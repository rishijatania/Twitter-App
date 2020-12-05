package com.twitter.tweetservice.repository;

import java.util.List;
import java.util.Optional;

import com.twitter.tweetservice.models.Tweet;
import com.twitter.tweetservice.models.User;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TweetRepository extends MongoRepository<Tweet, String> {
	Optional<List<Tweet>> findByUser(User user);

	// Execute a full-text search and define sorting dynamically
	Optional<List<Tweet>> findAllBy(TextCriteria criteria,Sort sort);

	// Paginate over a full-text search result
	// Page<FullTextDocument> findAllBy(TextCriteria criteria, Pageable pageable);
}
