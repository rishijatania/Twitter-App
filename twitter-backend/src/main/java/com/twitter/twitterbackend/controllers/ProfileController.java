package com.twitter.twitterbackend.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.twitterbackend.models.User;
import com.twitter.twitterbackend.payload.request.FollowRequest;
import com.twitter.twitterbackend.payload.request.UserProfileRequest;
import com.twitter.twitterbackend.payload.response.ErrorMessageResponse;
import com.twitter.twitterbackend.payload.response.FollowUserSuggestion;
import com.twitter.twitterbackend.payload.response.MessageResponse;
import com.twitter.twitterbackend.payload.response.UserDetailMinResponse;
import com.twitter.twitterbackend.payload.response.UserResponse;
import com.twitter.twitterbackend.repository.UserRepository;
import com.twitter.twitterbackend.security.services.UserDetailsServiceImpl;
import com.twitter.twitterbackend.service.CustomAggregationOperation;
import com.twitter.twitterbackend.service.ImageService;

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

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/profile")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> viewProfile() {
		User user = userServiceImpl.getCurrentUserFromSession();
		return ResponseEntity.ok(modelMapper.map(user, UserResponse.class));
	}

	@PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> updateProfile(@RequestParam(value = "profilePic", required = false) MultipartFile file,
			@RequestParam(value = "userForm", required = true) @Valid String userForm) {

		if (file != null && !file.isEmpty() && !file.getContentType().startsWith("image")) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "File not of type Image!", 400, "", "/profile"),
					HttpStatus.BAD_REQUEST);
		}
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
				user.setProfilePicUrl(pic[1].isEmpty() ? null : pic[1]);
				user.setProfilePicName(pic[0].isEmpty() ? null : pic[0]);
			}
			userRepository.save(user);
		} catch (Exception e) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), "User update failed!", 400, "", "/profile"),
					HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity.ok(modelMapper.map(user, UserResponse.class));
	}

	@PostMapping("/followers")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> addFollowers(@RequestBody FollowRequest follow) {
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
		user.getFollowing().add(followUser.get());
		followUser.get().getFollowers().add(user);
		userRepository.save(user);
		userRepository.save(followUser.get());

		return ResponseEntity.ok(new MessageResponse("User Followed!"));
	}

	@DeleteMapping("/followers/{username}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> removeFollowers(@PathVariable(value = "username") String username) {
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
		user.getFollowing().removeIf(u -> u.getUsername().equals(username));
		followUser.get().getFollowers().removeIf(u -> u.getUsername().equals(user.getUsername()));
		userRepository.save(user);
		userRepository.save(followUser.get());

		return ResponseEntity.ok(new MessageResponse("User unFollowed!"));
	}

	@GetMapping("/followsuggestions")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> getFollowing() {
		User user = userServiceImpl.getCurrentUserFromSession();
		Aggregation aggregation = Aggregation.newAggregation(new CustomAggregationOperation(4));
		AggregationResults<User> output = mongoTemplate.aggregate(aggregation, "users", User.class);
		List<User> mappedResults = output.getMappedResults().stream()
				.filter(u -> !(
							u.getUsername().equals(user.getUsername()) ||
							u.getFollowers().stream().anyMatch(i -> i.getUsername().equals(user.getUsername()))
						 )
				)
				.collect(Collectors.toList());
		FollowUserSuggestion result = new FollowUserSuggestion();
		mappedResults
				.forEach(item -> result.getMappedResults().add(modelMapper.map(item, UserDetailMinResponse.class)));
		return ResponseEntity.ok(result);
	}

	public String DateToString() {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		return dateFormat.format(date);
	}
}
