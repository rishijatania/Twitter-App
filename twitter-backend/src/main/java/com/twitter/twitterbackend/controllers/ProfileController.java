package com.twitter.twitterbackend.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.twitterbackend.metrics.MetricsManager;
import com.twitter.twitterbackend.metrics.SampleStore;
import com.twitter.twitterbackend.models.User;
import com.twitter.twitterbackend.payload.request.FollowRequest;
import com.twitter.twitterbackend.payload.request.UserProfileRequest;
import com.twitter.twitterbackend.payload.response.ErrorMessageResponse;
import com.twitter.twitterbackend.payload.response.FollowUserSuggestion;
import com.twitter.twitterbackend.payload.response.MessageResponse;
import com.twitter.twitterbackend.payload.response.SearchUserResponse;
import com.twitter.twitterbackend.payload.response.UserDetailMinResponse;
import com.twitter.twitterbackend.payload.response.UserResponse;
import com.twitter.twitterbackend.repository.UserRepository;
import com.twitter.twitterbackend.security.services.UserDetailsServiceImpl;
import com.twitter.twitterbackend.service.CustomAggregationOperation;
import com.twitter.twitterbackend.service.ImageService;
import com.twitter.twitterbackend.service.SlackReportService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Timer;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class ProfileController {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	UserDetailsServiceImpl userServiceImpl;

	@Autowired
	ImageService imageService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	SlackReportService slackReportService;

	@Autowired
	private MetricsManager metricsManager;

	@Autowired
	private SampleStore sampleStore;

	@GetMapping("/verify")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> userAccessVerify() {
		return ResponseEntity.ok("User Verified");
	}

	@GetMapping("/profile")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> viewProfile() {
		sampleStore.set(Timer.start(Clock.SYSTEM));
		User user = userServiceImpl.getCurrentUserFromSession();
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/user/profile", "method", "get");
		return ResponseEntity.ok(modelMapper.map(user, UserResponse.class));
	}

	@PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> updateProfile(@RequestParam(value = "profilePic", required = false) MultipartFile file,
			@RequestParam(value = "userForm", required = true) @Valid String userForm) {

		if (file != null && !file.isEmpty()
				&& !((file.getContentType() != null && file.getContentType().startsWith("image"))
						|| getImageExt().contains(file.getOriginalFilename()
								.substring(file.getOriginalFilename().lastIndexOf(".") + 1)))) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "File not of type Image!", 400, "", "/profile"),
					HttpStatus.BAD_REQUEST);
		}
		sampleStore.set(Timer.start(Clock.SYSTEM));
		User user = userServiceImpl.getCurrentUserFromSession();
		ObjectMapper mapper = new ObjectMapper();
		try {
			UserProfileRequest userFormDTO = mapper.readValue(userForm, UserProfileRequest.class);
			user.setFirstname(userFormDTO.getFirstname());
			user.setLastname(userFormDTO.getLastname());
			user.setBio(userFormDTO.getBio());
			// upload to s3 here
			if (userFormDTO.isProficPicChanged()) {
				String[] pic = imageService.updateImage(file, user.getProfilePicName());
				user.setProfilePicUrl(pic == null || pic[1].isEmpty() ? null : pic[1]);
				user.setProfilePicName(pic == null || pic[0].isEmpty() ? null : pic[0]);
			}
			userRepository.save(user);
		} catch (Exception e) {
			slackReportService.report(e.getMessage(), user.getUsername(), "/profile");
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "User update failed!", 500, "", "/profile"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/user/profile", "method", "put");
		return ResponseEntity.ok(modelMapper.map(user, UserResponse.class));
	}

	@GetMapping("/search")
	public ResponseEntity<?> searchTweet(@RequestParam String username) {
		if (username == null || username.isEmpty()) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "Please enter search text!", 400, "", "/search"),
					HttpStatus.BAD_REQUEST);
		}
		sampleStore.set(Timer.start(Clock.SYSTEM));
		User user = userServiceImpl.getCurrentUserFromSession();
		SearchUserResponse result = new SearchUserResponse();
		try {
			Optional<List<User>> users = userRepository.searchByUsername(username);
			if (users.isPresent()) {
				users.get().forEach(usert -> {
					result.getUsers().add(modelMapper.map(usert, UserResponse.class));
				});
			}
		} catch (Exception e) {
			slackReportService.report(e.getMessage(), user.getUsername(), "/search");
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "User Search failed!", 500, "", "/search"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/user/search", "method", "get");
		return ResponseEntity.ok(result);
	}

	@PostMapping("/followers")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> addFollowers(@RequestBody FollowRequest follow) {
		sampleStore.set(Timer.start(Clock.SYSTEM));
		User user = userServiceImpl.getCurrentUserFromSession();

		if (user.getUsername().equals(follow.getUsername())) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "User Cannot follow themselves!", 400, "", "/followers"),
					HttpStatus.BAD_REQUEST);
		}

		if (user.getFollowing().stream().anyMatch(u -> u.getUsername().equals(follow.getUsername()))) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "User Already Followed!", 400, "", "/followers"),
					HttpStatus.BAD_REQUEST);
		}

		Optional<User> followUser = userRepository.findByUsername(follow.getUsername());
		if (!followUser.isPresent()) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "User Not Found!", 400, "", "/followers"),
					HttpStatus.BAD_REQUEST);
		}
		try {
			user.getFollowing().add(followUser.get());
			followUser.get().getFollowers().add(user);
			userRepository.save(user);
			userRepository.save(followUser.get());
		} catch (Exception e) {
			slackReportService.report(e.getMessage(), user.getUsername(), "/followers");
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "User Follow failed!", 500, "", "/followers"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/user/followers", "method", "post");
		return ResponseEntity.ok(new MessageResponse("User Followed!"));
	}

	@DeleteMapping("/followers/{username}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> removeFollowers(@PathVariable(value = "username") String username) {
		sampleStore.set(Timer.start(Clock.SYSTEM));
		User user = userServiceImpl.getCurrentUserFromSession();

		if (user.getUsername().equals(username)) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "User Cannot unfollow themselves!", 400, "", "/followers"),
					HttpStatus.BAD_REQUEST);
		}

		if (user.getFollowing().stream().allMatch(u -> !u.getUsername().equals(username))) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "User Already unFollowed!", 400, "", "/followers"),
					HttpStatus.BAD_REQUEST);
		}

		Optional<User> followUser = userRepository.findByUsername(username);
		if (!followUser.isPresent()) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "User Not Found!", 400, "", "/followers"),
					HttpStatus.BAD_REQUEST);
		}
		try {
			user.getFollowing().removeIf(u -> u.getUsername().equals(username));
			followUser.get().getFollowers().removeIf(u -> u.getUsername().equals(user.getUsername()));
			userRepository.save(user);
			userRepository.save(followUser.get());
		} catch (Exception e) {
			slackReportService.report(e.getMessage(), user.getUsername(), "/followers");
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "User Unfollow failed!", 500, "", "/followers"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/user/followers", "method", "delete");
		return ResponseEntity.ok(new MessageResponse("User unFollowed!"));
	}

	@GetMapping("/followSuggestions")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> getFollowSuggestions() {
		sampleStore.set(Timer.start(Clock.SYSTEM));
		User user = userServiceImpl.getCurrentUserFromSession();
		FollowUserSuggestion result = new FollowUserSuggestion();
		try {
			Aggregation aggregation = Aggregation.newAggregation(new CustomAggregationOperation(4));
			AggregationResults<User> output = mongoTemplate.aggregate(aggregation, "users", User.class);
			List<User> mappedResults = output.getMappedResults().stream()
					.filter(u -> !(u.getUsername().equals(user.getUsername())
							|| u.getFollowers().stream().anyMatch(i -> i.getUsername().equals(user.getUsername()))))
					.collect(Collectors.toList());

			mappedResults
					.forEach(item -> result.getMappedResults().add(modelMapper.map(item, UserDetailMinResponse.class)));
		} catch (Exception e) {
			slackReportService.report(e.getMessage(), user.getUsername(), "/followsuggestions");
			return new ResponseEntity<>(new ErrorMessageResponse(DateToString(), "User followsuggestions failed!", 500,
					"", "/followsuggestions"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		metricsManager.trackTimeMetrics("http.requests", "uri", "/api/user/search", "method", "get");
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
