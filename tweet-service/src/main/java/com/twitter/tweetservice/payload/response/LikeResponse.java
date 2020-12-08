package com.twitter.tweetservice.payload.response;

public class LikeResponse {
	private UserDetailMinResponse user;

	/**
	 * @return String return the username
	 */
	public UserDetailMinResponse getUser() {
		return user;
	}

	/**
	 * @param username the username to set
	 */
	public void setUser(UserDetailMinResponse user) {
		this.user = user;
	}

}
