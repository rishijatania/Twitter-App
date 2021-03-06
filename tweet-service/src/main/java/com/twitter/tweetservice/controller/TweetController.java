package com.twitter.tweetservice.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.tweetservice.metrics.MetricsManager;
import com.twitter.tweetservice.metrics.SampleStore;
import com.twitter.tweetservice.models.Comment;
import com.twitter.tweetservice.models.File;
import com.twitter.tweetservice.models.Like;
import com.twitter.tweetservice.models.Tag;
import com.twitter.tweetservice.models.Tweet;
import com.twitter.tweetservice.models.User;
import com.twitter.tweetservice.payload.request.CommentRequest;
import com.twitter.tweetservice.payload.request.TweetRequest;
import com.twitter.tweetservice.payload.response.ErrorMessageResponse;
import com.twitter.tweetservice.payload.response.MessageResponse;
import com.twitter.tweetservice.payload.response.SearchTweetResponse;
import com.twitter.tweetservice.payload.response.TweetResponse;
import com.twitter.tweetservice.repository.CommentRepository;
import com.twitter.tweetservice.repository.TagRepository;
import com.twitter.tweetservice.repository.TweetRepository;
import com.twitter.tweetservice.service.ImageService;
import com.twitter.tweetservice.service.SlackReportService;
import com.twitter.tweetservice.service.TweetActionServiceImpl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Timer;

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
	private CommentRepository commentRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private TweetActionServiceImpl tweetActionServiceImpl;

	@Autowired
	SlackReportService slackReportService;

	@Autowired
	private MetricsManager metricsManager;

	@Autowired
	private SampleStore sampleStore;

	@GetMapping(value = "/testHealth")
	public ResponseEntity<?> userAccess(@RequestAttribute User user) {
		return ResponseEntity.ok(new MessageResponse("Tweet Service Up and running."));
	}

	@PostMapping(value = "")
	public ResponseEntity<?> createTweet(@RequestAttribute User user,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "tweetForm", required = true) @Valid String tweetForm) {

		if (file != null && !file.isEmpty()
				&& !((file.getContentType() != null && file.getContentType().startsWith("image"))
						|| getImageExt().contains(file.getOriginalFilename()
								.substring(file.getOriginalFilename().lastIndexOf(".") + 1)))) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "File not of type Image!", 400, "", "/tweet"),
					HttpStatus.BAD_REQUEST);
		}
		sampleStore.set(Timer.start(Clock.SYSTEM));
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
				fileToUpload.setUrl(pic == null || pic[1].isEmpty() ? null : pic[1]);
				fileToUpload.setFileName(pic == null || pic[0].isEmpty() ? null : pic[0]);
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
			slackReportService.report(e.getMessage(), user.getUsername(), "/tweet");
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Create failed!", 500, "", "/tweet"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/tweet", "method", "post");
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

		if (!t.get().getUser().getUsername().equals(user.getUsername())) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "User Not Authorised to Update Tweet", 404, "", "/tweet"),
					HttpStatus.UNAUTHORIZED);
		}
		sampleStore.set(Timer.start(Clock.SYSTEM));
		ObjectMapper mapper = new ObjectMapper();
		try {
			// Create Tweet
			TweetRequest tweetFormDTO = mapper.readValue(tweetForm, TweetRequest.class);
			t.get().setContent(tweetFormDTO.getContent());
			tweetRepository.save(t.get());
		} catch (Exception e) {
			slackReportService.report(e.getMessage(), user.getUsername(), "/tweet");
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Update failed!", 500, "", "/tweet"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/tweet", "method", "put");
		return ResponseEntity.ok(modelMapper.map(t.get(), TweetResponse.class));
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> deleteTweet(@RequestAttribute User user, @PathVariable String id) {
		try {
			sampleStore.set(Timer.start(Clock.SYSTEM));
			Optional<Tweet> t = tweetRepository.findById(id);
			if (!t.isPresent()) {
				return new ResponseEntity<>(
						new ErrorMessageResponse(DateToString(), "Tweet not found", 404, "", "/tweet"),
						HttpStatus.NOT_FOUND);
			}
			if (!t.get().getUser().getUsername().equals(user.getUsername())) {
				return new ResponseEntity<>(new ErrorMessageResponse(DateToString(),
						"User Not Authorised to Delete Tweet", 404, "", "/tweet"), HttpStatus.UNAUTHORIZED);
			}
			// Remove comments
			t.get().getComments().forEach(comment -> {
				commentRepository.delete(comment);
			});
			// Delete Tweet
			tweetRepository.delete(t.get());
		} catch (Exception e) {
			slackReportService.report(e.getMessage(), user.getUsername(), "/tweet");
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Delete failed!", 500, "", "/tweet"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/tweet", "method", "delete");
		return ResponseEntity.ok(new MessageResponse("Tweet Deleted"));
	}

	@PostMapping(value = "/{id}/like")
	public ResponseEntity<?> likeTweet(@RequestAttribute User user, @PathVariable String id) {
		try {
			sampleStore.set(Timer.start(Clock.SYSTEM));
			Optional<Tweet> t = tweetRepository.findById(id);
			if (!t.isPresent()) {
				return new ResponseEntity<>(
						new ErrorMessageResponse(DateToString(), "Tweet not found", 404, "", "/like"),
						HttpStatus.NOT_FOUND);
			}

			// Toggle Like
			Optional<Like> like = t.get().getLikes().stream()
					.filter(l -> l.getUser().getUsername().equals(user.getUsername())).findFirst();
			if (like.isPresent()) {
				t.get().getLikes().remove(like.get());
			} else {
				Like li = new Like();
				li.setUser(user);
				t.get().getLikes().add(li);
			}
			tweetRepository.save(t.get());
			metricsManager.trackTimeMetrics("http.requests", "uri", "/api/tweet/like", "method", "post");
			return ResponseEntity.ok(modelMapper.map(t.get(), TweetResponse.class));
		} catch (Exception e) {
			slackReportService.report(e.getMessage(), user.getUsername(), "/like");
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Like failed!", 500, "", "/like"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/search")
	public ResponseEntity<?> searchTweet(@RequestAttribute User user, @RequestParam String text) {
		if (text == null || text.isEmpty()) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Please enter search text!", 400, "", "/search"),
					HttpStatus.BAD_REQUEST);
		}
		sampleStore.set(Timer.start(Clock.SYSTEM));
		SearchTweetResponse result = new SearchTweetResponse();
		try {
			List<Tweet> tweets = tweetActionServiceImpl.findTweetByText(text);
			tweets.forEach(tweet -> {
				result.getTweets().add(modelMapper.map(tweet, TweetResponse.class));
			});
		} catch (Exception e) {
			slackReportService.report(e.getMessage(), user.getUsername(), "/search");
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Search failed!", 500, "", "/search"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/tweet/search", "method", "get");
		return ResponseEntity.ok(result);
	}

	@GetMapping("/searchTag")
	public ResponseEntity<?> searchTag(@RequestAttribute User user, @RequestParam String tag) {
		if (tag == null || tag.isEmpty()) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Please enter search tag!", 400, "", "/searchTag"),
					HttpStatus.BAD_REQUEST);
		}
		sampleStore.set(Timer.start(Clock.SYSTEM));
		SearchTweetResponse result = new SearchTweetResponse();
		try {
			Optional<Tag> tagResponse = tagRepository.findByTag(tag);
			if (tagResponse.isPresent()) {
				tagResponse.get().getTweets().forEach(tweet -> {
					result.getTweets().add(modelMapper.map(tweet, TweetResponse.class));
				});
			} else {
				return new ResponseEntity<>(
						new ErrorMessageResponse(DateToString(), "Tweet Tag Not Found!", 404, "", "/searchTag"),
						HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			slackReportService.report(e.getMessage(), user.getUsername(), "/searchTag");
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Tag Search failed!", 500, "", "/searchTag"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/tweet/searchTag", "method", "get");
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getTweetById(@PathVariable String id) {
		if (id == null || id.isEmpty()) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Please enter search id!", 400, "", "/tweet/{id}"),
					HttpStatus.BAD_REQUEST);
		}
		sampleStore.set(Timer.start(Clock.SYSTEM));
		Optional<Tweet> tweet = tweetActionServiceImpl.findTweetById(id);
		if (!tweet.isPresent()) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Not found!", 404, "", "/tweet/{id}"),
					HttpStatus.NOT_FOUND);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/tweet", "method", "get");
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
		sampleStore.set(Timer.start(Clock.SYSTEM));
		SearchTweetResponse result = new SearchTweetResponse();
		try {
			List<Tweet> tweets = tweetList.get();
			tweets.forEach(tweet -> {
				result.getTweets().add(modelMapper.map(tweet, TweetResponse.class));
			});
		} catch (Exception e) {
			slackReportService.report(e.getMessage(), user.getUsername(), "/search");
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Search failed!", 500, "", "/search"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/tweet", "method", "get");
		return ResponseEntity.ok(result);
	}

	@PostMapping(value = "/{id}/comment")
	public ResponseEntity<?> createComment(@RequestAttribute User user, @PathVariable String id,
			@RequestBody @Valid CommentRequest commentDTO) {
		Optional<Tweet> tweet;
		try {
			sampleStore.set(Timer.start(Clock.SYSTEM));
			tweet = tweetActionServiceImpl.findTweetById(id);
			if (!tweet.isPresent()) {
				return new ResponseEntity<>(
						new ErrorMessageResponse(DateToString(), "Tweet Not found!", 404, "", "/tweet/{id}/comment"),
						HttpStatus.NOT_FOUND);
			}
			// Create Comment
			Comment comment = new Comment();
			comment.setText(commentDTO.getText());
			comment.setUser(user);
			comment.setTweet(tweet.get());
			commentRepository.save(comment);
			tweet.get().getComments().add(comment);
			tweetRepository.save(tweet.get());
		} catch (Exception e) {
			slackReportService.report(e.getMessage(), user.getUsername(), "/comment");
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Comment Create failed!", 500, "", "/comment"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/tweet/comment", "method", "post");
		return ResponseEntity.ok(modelMapper.map(tweet.get(), TweetResponse.class));
	}

	@DeleteMapping(value = "/{id}/comment/{cid}")
	public ResponseEntity<?> deleteComment(@RequestAttribute User user, @PathVariable String id,
			@PathVariable String cid) {

		try {
			sampleStore.set(Timer.start(Clock.SYSTEM));
			Optional<Tweet> t = tweetRepository.findById(id);
			if (!t.isPresent()) {
				return new ResponseEntity<>(
						new ErrorMessageResponse(DateToString(), "Tweet Not found!", 404, "", "/Comment"),
						HttpStatus.NOT_FOUND);
			}
			if (!t.get().getComments().stream().anyMatch(com -> com.getId().equals(cid))) {
				return new ResponseEntity<>(
						new ErrorMessageResponse(DateToString(), "Comment Not found!", 404, "", "/Comment"),
						HttpStatus.NOT_FOUND);
			}
			Optional<Comment> comment = commentRepository.findById(cid);
			if (!comment.get().getUser().getUsername().equals(user.getUsername())) {
				return new ResponseEntity<>(new ErrorMessageResponse(DateToString(),
						"User Not Authorised to Delete Comment", 404, "", "/Comment"), HttpStatus.UNAUTHORIZED);
			}
			// Delete Comment
			commentRepository.delete(comment.get());
		} catch (Exception e) {
			slackReportService.report(e.getMessage(), user.getUsername(), "/Comment");
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Comment Delete failed!", 500, "", "/Comment"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/tweet/comment", "method", "delete");
		return ResponseEntity.ok(new MessageResponse("Comment Deleted"));
	}

	@GetMapping("/feed")
	public ResponseEntity<?> getTweetsFeedForUser(@RequestAttribute User user) {
		List<Tweet> tweetList = tweetActionServiceImpl.loadTweetFeedForUser(user);
		SearchTweetResponse result = new SearchTweetResponse();
		sampleStore.set(Timer.start(Clock.SYSTEM));
		try {
			tweetList.forEach(tweet -> {
				result.getTweets().add(modelMapper.map(tweet, TweetResponse.class));
			});
		} catch (Exception e) {
			slackReportService.report(e.getMessage(), user.getUsername(), "/feed");
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Tweet Feed failed!", 400, "", "/feed"),
					HttpStatus.BAD_REQUEST);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/tweet/feed", "method", "get");
		return ResponseEntity.ok(result);
	}

	public String DateToString() {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		return dateFormat.format(date);
	}

	public Set<String> getImageExt() {
		Set<String> imageExt = new HashSet<>();
		imageExt.add("jpeg");
		imageExt.add("png");
		imageExt.add("jpg");
		return imageExt;
	}
}
