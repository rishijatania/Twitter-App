package com.twitter.tweetservice.models;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class Like {

	@DBRef
	@NotNull
	private User user;

	/**
	 * @return User return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
