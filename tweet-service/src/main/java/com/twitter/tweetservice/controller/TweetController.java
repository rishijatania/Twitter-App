package com.twitter.tweetservice.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.tweetservice.models.File;
import com.twitter.tweetservice.models.Tag;
import com.twitter.tweetservice.models.Tweet;
import com.twitter.tweetservice.models.User;
import com.twitter.tweetservice.payload.request.TweetRequest;
import com.twitter.tweetservice.payload.response.ErrorMessageResponse;
import com.twitter.tweetservice.payload.response.TweetResponse;
import com.twitter.tweetservice.repository.TagRepository;
import com.twitter.tweetservice.repository.TweetRepository;
import com.twitter.tweetservice.service.ImageService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
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
	ImageService imageService;

	@Autowired
	TweetRepository tweetRepository;

	@Autowired
	TagRepository tagRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@RequestMapping(value = "/testHealth", method = RequestMethod.GET)
	public String userAccess(@RequestAttribute User user) {
		return "Tweet Service Up and running.";
	}

	@PostMapping(value = "")
	public ResponseEntity<?> createTweet(@RequestAttribute User user,@RequestParam(value = "file", required = false) MultipartFile file,
		@RequestParam(value = "tweetForm", required = true) @Valid String tweetForm) {

		if (file != null && !file.getOriginalFilename().isEmpty() && !file.getContentType().startsWith("image")) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "File not of type Image!", 400, "", "/tweet"),
					HttpStatus.BAD_REQUEST);
		}	
		ObjectMapper mapper = new ObjectMapper();
		Tweet tweet = new Tweet();
		try {
			//Create Tweet
			TweetRequest tweetFormDTO = mapper.readValue(tweetForm, TweetRequest.class);
			tweet.setText(tweetFormDTO.getText());
			if(!tweetFormDTO.getTags().isEmpty())
				tweet.setTags(tweetFormDTO.getTags());
			tweet.setUser(user);
			tweetRepository.save(tweet);

			// upload to s3 here
			if (tweetFormDTO.isFileAttached() && file != null && !file.getOriginalFilename().isEmpty()) {
				String[] pic = imageService.updateImage(file,"",tweet.getId());
				File fileToUpload =new File();
				fileToUpload.setUrl(pic[1]);
				fileToUpload.setFileName(pic[0]);
				tweet.setFile(fileToUpload);
				tweetRepository.save(tweet);
			}

			//Add Tags
			if(!tweet.getTags().isEmpty()){
				tweet.getTags().forEach(tag-> {
					Tag t = tagRepository.findByTag(tag).isPresent()? tagRepository.findByTag(tag).get():new Tag();
					t.setTag(tag);
					t.getTweets().add(tweet);
					tagRepository.save(t);
				});
			}
		} catch (Exception e) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Twee Create sfailed!", 400, "", "/tweet"),
					HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(modelMapper.map(tweet, TweetResponse.class));
	}

	public String DateToString() {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		return dateFormat.format(date);
	}
}
