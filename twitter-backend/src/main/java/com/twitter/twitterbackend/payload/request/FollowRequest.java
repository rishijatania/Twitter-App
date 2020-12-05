package com.twitter.twitterbackend.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class FollowRequest {
	@Size(min = 3, max = 20)
	@NotBlank
	private String username;

	/**
	 * @return String return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

}
