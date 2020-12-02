package com.twitter.twitterbackend.payload.response;

import java.util.HashSet;
import java.util.Set;

public class UserResponse {

	private String id;
	private String username;
	private String email;
	private String firstname;
	private String lastname;
	private String profilePic;
	private String bio;
	private Set<RoleResponse> roles;
	private Set<UserResponse> followers = new HashSet<>();
	private Set<UserResponse> following = new HashSet<>();

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

	/**
	 * @return String return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return String return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return String return the profilePic
	 */
	public String getProfilePic() {
		return profilePic;
	}

	/**
	 * @param profilePic the profilePic to set
	 */
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	/**
	 * @return String return the bio
	 */
	public String getBio() {
		return bio;
	}

	/**
	 * @param bio the bio to set
	 */
	public void setBio(String bio) {
		this.bio = bio;
	}

	/**
	 * @return Set<UserResponse> return the followers
	 */
	public Set<UserResponse> getFollowers() {
		return followers;
	}

	/**
	 * @param followers the followers to set
	 */
	public void setFollowers(Set<UserResponse> followers) {
		this.followers = followers;
	}

	/**
	 * @return Set<UserResponse> return the following
	 */
	public Set<UserResponse> getFollowing() {
		return following;
	}

	/**
	 * @param following the following to set
	 */
	public void setFollowing(Set<UserResponse> following) {
		this.following = following;
	}

}