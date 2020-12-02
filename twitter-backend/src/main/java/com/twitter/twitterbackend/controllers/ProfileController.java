package com.twitter.twitterbackend.controllers;

import com.twitter.twitterbackend.models.User;
import com.twitter.twitterbackend.payload.response.UserResponse;
import com.twitter.twitterbackend.security.services.UserDetailsServiceImpl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class ProfileController {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	UserDetailsServiceImpl userServiceImpl;
	
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/profile")
	@PreAuthorize("hasRole('USER') or hasRole('GRADMIN') or hasRole('ADMIN')")
	public ResponseEntity<?> viewProfile() {
		User user = userServiceImpl.getCurrentUserFromSession();
		return ResponseEntity.ok(modelMapper.map(user, UserResponse.class));
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}
}
