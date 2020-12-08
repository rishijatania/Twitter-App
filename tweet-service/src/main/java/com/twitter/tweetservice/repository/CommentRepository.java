package com.twitter.tweetservice.repository;

import com.twitter.tweetservice.models.Comment;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, String>{
	
}
