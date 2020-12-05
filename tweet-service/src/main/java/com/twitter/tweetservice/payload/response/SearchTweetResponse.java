package com.twitter.tweetservice.payload.response;

import java.util.ArrayList;
import java.util.List;

public class SearchTweetResponse {
	List<TweetResponse> tweets=new ArrayList<>();
	/**
     * @param tweets the tweets to set
     */
    public void setTweets(List<TweetResponse> tweets) {
        this.tweets = tweets;
    }

    /**
     * @return List<TweetResponse> return the tweets
     */
    public List<TweetResponse> getTweets() {
        return tweets;
    }

}
