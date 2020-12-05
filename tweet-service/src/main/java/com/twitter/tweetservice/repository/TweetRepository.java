package com.twitter.tweetservice.repository;

import java.util.List;
import java.util.Optional;

import com.twitter.tweetservice.models.Tweet;
import com.twitter.tweetservice.models.User;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TweetRepository extends MongoRepository<Tweet, String> {
	Optional<List<Tweet>> findByUser(User user);
}
