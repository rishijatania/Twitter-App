package com.twitter.twitterbackend.repository;

import java.util.List;
import java.util.Optional;

import com.twitter.twitterbackend.models.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);

	@Query("{'username':{'$regex':'?0','$options':'i'}}")
	Optional<List<User>> searchByUsername(String username);
}
