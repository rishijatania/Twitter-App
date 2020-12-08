package com.twitter.twitterbackend.payload.response;

import java.util.ArrayList;
import java.util.List;

public class SearchUserResponse {
	List<UserResponse> users = new ArrayList<>();

	public void setUsers(List<UserResponse> users) {
		this.users = users;
	}

	public List<UserResponse> getUsers() {
		return users;
	}
}
