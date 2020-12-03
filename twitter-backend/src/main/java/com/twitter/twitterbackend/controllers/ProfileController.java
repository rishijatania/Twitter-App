package com.twitter.twitterbackend.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.twitterbackend.models.User;
import com.twitter.twitterbackend.payload.request.UserProfileRequest;
import com.twitter.twitterbackend.payload.response.ErrorMessageResponse;
import com.twitter.twitterbackend.payload.response.UserResponse;
import com.twitter.twitterbackend.repository.UserRepository;
import com.twitter.twitterbackend.security.services.UserDetailsServiceImpl;
import com.twitter.twitterbackend.service.ImageService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

		if(file != null && !file.getContentType().startsWith("image")){
			return new ResponseEntity<>(new ErrorMessageResponse(DateToString(),"File not of type Image!",400,"","/profile"), HttpStatus.BAD_REQUEST);
		}
		User user = userServiceImpl.getCurrentUserFromSession();
		ObjectMapper mapper = new ObjectMapper();
		try {
			UserProfileRequest userFormDTO = mapper.readValue(userForm, UserProfileRequest.class);
			user.setFirstname(userFormDTO.getFirstname());
			user.setLastname(userFormDTO.getLastname());
			user.setBio(userFormDTO.getBio());
			//upload to s3 here
			if(userFormDTO.isProficPicChanged()){
				String[] pic = imageService.updateImage(file,user.getProfilePicName());
				user.setProfilePicUrl(pic[1].isEmpty()? null:pic[1]);
				user.setProfilePicName(pic[0].isEmpty()? null:pic[0]);
			}
			userRepository.save(user);
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorMessageResponse(DateToString(),"User update failed!",400,"","/profile"), HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity.ok(modelMapper.map(user, UserResponse.class));
	}

	// @GetMapping("/followers")
	// @PreAuthorize("hasRole('USER')")
	// public ResponseEntity<?> getFollowers() {
	// 	User user = userServiceImpl.getCurrentUserFromSession();
	// 	return ResponseEntity.ok(modelMapper.map(user, UserResponse.class));
	// }

	// @GetMapping("/following")
	// @PreAuthorize("hasRole('USER')")
	// public ResponseEntity<?> getFollowing() {
	// 	User user = userServiceImpl.getCurrentUserFromSession();
	// 	return ResponseEntity.ok(modelMapper.map(user, UserResponse.class));
	// }

	public String DateToString() {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		return dateFormat.format(date);
	}
}
