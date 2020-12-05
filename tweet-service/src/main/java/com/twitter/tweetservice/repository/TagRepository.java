package com.twitter.tweetservice.repository;

import java.util.Optional;

import com.twitter.tweetservice.models.Tag;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TagRepository extends MongoRepository<Tag, String> {
	Optional<Tag> findByTag(String tag);
}
