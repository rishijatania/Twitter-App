package com.twitter.tweetservice.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.tweetservice.models.File;
import com.twitter.tweetservice.models.Tag;
import com.twitter.tweetservice.models.Tweet;
import com.twitter.tweetservice.models.User;
import com.twitter.tweetservice.payload.request.TweetRequest;
import com.twitter.tweetservice.payload.response.ErrorMessageResponse;
import com.twitter.tweetservice.payload.response.SearchTweetResponse;
import com.twitter.tweetservice.payload.response.TweetResponse;
import com.twitter.tweetservice.repository.TagRepository;
import com.twitter.tweetservice.repository.TweetRepository;
import com.twitter.tweetservice.service.ImageService;
import com.twitter.tweetservice.service.TweetActionServiceImpl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tweet")
public class TweetController {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ImageService imageService;

	@Autowired
	private TweetRepository tweetRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private TweetActionServiceImpl tweetActionServiceImpl;

	@RequestMapping(value = "/testHealth", method = RequestMethod.GET)
	public String userAccess(@RequestAttribute User user) {
		return "Tweet Service Up and running.";
	}

	@PostMapping(value = "")
	public ResponseEntity<?> createTweet(@RequestAttribute User user,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "tweetForm", required = true) @Valid String tweetForm) {

		if (file != null && !file.getOriginalFilename().isEmpty() && !file.getContentType().startsWith("image")) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "File not of type Image!", 400, "", "/tweet"),
					HttpStatus.BAD_REQUEST);
		}
		ObjectMapper mapper = new ObjectMapper();
		Tweet tweet = new Tweet();
		try {
			// Create Tweet
			TweetRequest tweetFormDTO = mapper.readValue(tweetForm, TweetRequest.class);
			tweet.setContent(tweetFormDTO.getContent());
			if (!tweetFormDTO.getTags().isEmpty())
				tweet.setTags(tweetFormDTO.getTags());
			tweet.setUser(user);
			tweetRepository.save(tweet);

			// upload to s3 here
			if (tweetFormDTO.isFileAttached() && file != null && !file.getOriginalFilename().isEmpty()) {
				String[] pic = imageService.updateImage(file, "", tweet.getId());
				File fileToUpload = new File();
				fileToUpload.setUrl(pic[1]);
				fileToUpload.setFileName(pic[0]);
				tweet.setFile(fileToUpload);
				tweetRepository.save(tweet);
			}

			// Add Tags
			if (!tweet.getTags().isEmpty()) {
				tweet.getTags().forEach(tag -> {
					Tag t = tagRepository.findByTag(tag).isPresent() ? tagRepository.findByTag(tag).get() : new Tag();
					t.setTag(tag);
					t.getTweets().add(tweet);
					tagRepository.save(t);
				});
			}
		} catch (Exception e) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Create sfailed!", 400, "", "/tweet"),
					HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(modelMapper.map(tweet, TweetResponse.class));
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<?> updateTweet(@RequestAttribute User user, @PathVariable String id,
			@RequestParam(value = "tweetForm", required = true) @Valid String tweetForm) {
		Optional<Tweet> t = tweetRepository.findById(id);
		if (!t.isPresent()) {
			return new ResponseEntity<>(new ErrorMessageResponse(DateToString(), "Tweet not found", 404, "", "/tweet"),
					HttpStatus.NOT_FOUND);
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			// Create Tweet
			TweetRequest tweetFormDTO = mapper.readValue(tweetForm, TweetRequest.class);
			t.get().setContent(tweetFormDTO.getContent());
			tweetRepository.save(t.get());
		} catch (Exception e) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Update failed!", 400, "", "/tweet"),
					HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(modelMapper.map(t.get(), TweetResponse.class));
	}

	@GetMapping("/search")
	public ResponseEntity<?> searchTweet(@RequestParam String text) {
		if (text == null || text.isEmpty()) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Please enter search text!", 400, "", "/search"),
					HttpStatus.BAD_REQUEST);
		}
		SearchTweetResponse result = new SearchTweetResponse();
		try {
			List<Tweet> tweets = tweetActionServiceImpl.findTweetByText(text);
			tweets.forEach(tweet -> {
				result.getTweets().add(modelMapper.map(tweet, TweetResponse.class));
			});
		} catch (Exception e) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Search failed!", 400, "", "/search"),
					HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getTweetById(@PathVariable String id) {
		if (id == null || id.isEmpty()) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Please enter search id!", 400, "", "/tweet/{id}"),
					HttpStatus.BAD_REQUEST);
		}
		Optional<Tweet> tweet = tweetActionServiceImpl.findTweetById(id);
		if (!tweet.isPresent()) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Not found!", 404, "", "/tweet/{id}"),
					HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(modelMapper.map(tweet.get(), TweetResponse.class));
	}

	@GetMapping("")
	public ResponseEntity<?> getTweetsByUser(@RequestAttribute User user) {
		Optional<List<Tweet>> tweetList = tweetActionServiceImpl.findTweetByUser(user);
		if (!tweetList.isPresent()) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Not found!", 404, "", "/tweet/{id}"),
					HttpStatus.NOT_FOUND);
		}
		SearchTweetResponse result = new SearchTweetResponse();
		try {
			List<Tweet> tweets = tweetList.get();
			tweets.forEach(tweet -> {
				result.getTweets().add(modelMapper.map(tweet, TweetResponse.class));
			});
		} catch (Exception e) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Search failed!", 400, "", "/search"),
					HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(result);
	}

	public String DateToString() {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		return dateFormat.format(date);
	}
}
