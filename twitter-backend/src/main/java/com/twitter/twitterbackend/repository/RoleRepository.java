package com.twitter.twitterbackend.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.twitter.twitterbackend.models.ERole;
import com.twitter.twitterbackend.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
