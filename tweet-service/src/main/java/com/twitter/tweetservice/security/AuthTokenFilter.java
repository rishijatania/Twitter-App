package com.twitter.tweetservice.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.tweetservice.models.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

public class AuthTokenFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Value("${twitter.backend.url}")
	private String BACKEND_SEVICE_URL;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			logger.info("inside token filter");
			logger.info(" Request URL " + request.getRequestURI());
			logger.info(" Request URL " + request.getHeader("HOST"));
			if (request.getRequestURI() !=null && request.getRequestURI().contains("/actuator")) {
				logger.info("inside if for actuator");
				filterChain.doFilter(request, response);
			}
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", request.getHeader("Authorization"));
			HttpEntity<String> entity = new HttpEntity<String>("body", headers);
			ResponseEntity<String> authResponse = restTemplate.exchange(BACKEND_SEVICE_URL + "/api/user/profile",
					HttpMethod.GET, entity, String.class);
			ObjectMapper objectMapper = new ObjectMapper();
			User user = objectMapper.readValue(authResponse.getBody(), User.class);
			if (user == null) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid.");
				return;
			}
			request.setAttribute("user", user);
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid.");
			return;
		}

		filterChain.doFilter(request, response);
	}
}
