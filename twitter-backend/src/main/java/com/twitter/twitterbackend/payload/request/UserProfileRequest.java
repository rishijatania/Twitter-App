package com.twitter.twitterbackend.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserProfileRequest {
	@Size(min = 3, max = 20)
	private String username;

	@NotBlank
	private String firstname;

	@NotBlank
	private String lastname;

	@Size(max = 300)
	private String bio;

	@NotNull
	private boolean proficPicChanged;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
     * @return boolean return the proficPicChanged
     */
    public boolean isProficPicChanged() {
        return proficPicChanged;
    }

    /**
     * @param proficPicChanged the proficPicChanged to set
     */
    public void setProficPicChanged(boolean proficPicChanged) {
        this.proficPicChanged = proficPicChanged;
    }

}
