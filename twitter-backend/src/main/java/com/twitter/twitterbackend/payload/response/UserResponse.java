package com.twitter.twitterbackend.payload.response;

import java.util.Set;

public class UserResponse {

	private String id;
	private String username;
	private String email;
	// private String firstName;
	// private String lastName;
	// private Long phoneNo;
	private Set<RoleResponse> roles;

	/**
	 * @return Long return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return String return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return String return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return Set<RoleResponse> return the roles
	 */
	public Set<RoleResponse> getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(Set<RoleResponse> roles) {
		this.roles = roles;
	}

}