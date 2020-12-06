package com.twitter.tweetservice.payload.response;

import java.util.HashSet;
import java.util.Set;

public class UserResponse {

	private String id;
	private String username;
	private String email;
	private String firstname;
	private String lastname;
	private String profilePicUrl;
	private String profilePicName;
	private String bio;
	private Set<RoleResponse> roles;
	private Set<UserDetailMinResponse> followers = new HashSet<>();
	private Set<UserDetailMinResponse> following = new HashSet<>();
	private Integer followersCount;
	private Integer followingCount;

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
	 * @return String return the profilePicUrl
	 */
	public String getProfilePicUrl() {
		return profilePicUrl;
	}

	/**
	 * @param profilePicUrl the profilePicUrl to set
	 */
	public void setProfilePicUrl(String profilePicUrl) {
		this.profilePicUrl = profilePicUrl;
	}

	/**
	 * @return String return the profilePicName
	 */
	public String getProfilePicName() {
		return profilePicName;
	}

	/**
	 * @param profilePicName the profilePicName to set
	 */
	public void setProfilePicName(String profilePicName) {
		this.profilePicName = profilePicName;
	}

	/**
	 * @return Set<UserDetailMinResponse> return the followers
	 */
	public Set<UserDetailMinResponse> getFollowers() {
		return followers;
	}

	/**
	 * @param followers the followers to set
	 */
	public void setFollowers(Set<UserDetailMinResponse> followers) {
		this.followers = followers;
	}

	/**
	 * @return Set<UserDetailMinResponse> return the following
	 */
	public Set<UserDetailMinResponse> getFollowing() {
		return following;
	}

	/**
	 * @param following the following to set
	 */
	public void setFollowing(Set<UserDetailMinResponse> following) {
		this.following = following;
	}

	/**
	 * @return Integer return the followersCount
	 */
	public Integer getFollowersCount() {
		return followersCount;
	}

	/**
	 * @param followersCount the followersCount to set
	 */
	public void setFollowersCount(Integer followersCount) {
		this.followersCount = followersCount;
	}

	/**
	 * @return Integer return the followingCount
	 */
	public Integer getFollowingCount() {
		return followingCount;
	}

	/**
	 * @param followingCount the followingCount to set
	 */
	public void setFollowingCount(Integer followingCount) {
		this.followingCount = followingCount;
	}

}