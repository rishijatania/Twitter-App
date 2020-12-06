package com.twitter.tweetservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.twitter.tweetservice.models.Tweet;
import com.twitter.tweetservice.models.User;
import com.twitter.tweetservice.repository.TweetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TweetActionServiceImpl {

	@Autowired
	TweetRepository tweetRepository;

	@Transactional
	public List<Tweet> findTweetByText(String text) {

		Sort sort = Sort.by(Sort.Direction.DESC, "lastModifiedDate");

		TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(text).caseSensitive(false);
		Optional<List<Tweet>> result = tweetRepository.findAllBy(criteria, sort);

		// criteria = TextCriteria.forDefaultLanguage().matching("film");
		// Page<FullTextDocument> page = repository.findAllBy(criteria,
		// PageRequest.of(1, 1, sort));

		return result.isPresent() ? result.get() : new ArrayList<>();
	}

	@Transactional
	public Optional<Tweet> findTweetById(String id) {
		return tweetRepository.findById(id);
	}

	public Optional<List<Tweet>> findTweetByUser(User user) {
		return tweetRepository.findByUser(user);
	}
}
