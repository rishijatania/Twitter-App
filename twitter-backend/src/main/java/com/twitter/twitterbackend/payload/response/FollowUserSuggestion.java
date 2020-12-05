package com.twitter.twitterbackend.payload.response;

import java.util.ArrayList;
import java.util.List;

public class FollowUserSuggestion {

	private List<UserDetailMinResponse> mappedResults;

	public FollowUserSuggestion() {
		this.mappedResults = new ArrayList<>();
	}

	public void setMappedResults(List<UserDetailMinResponse> mappedResults) {
		this.mappedResults = mappedResults;
	}

	public List<UserDetailMinResponse> getMappedResults() {
		return mappedResults;
	}

}
