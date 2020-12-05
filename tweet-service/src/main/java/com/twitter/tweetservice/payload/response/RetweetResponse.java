package com.twitter.tweetservice.payload.response;

import java.util.Date;

public class RetweetResponse {
	private String id;
	private UserDetailMinResponse user;
	private Date createdDate;
	private Date lastModifiedDate;

    /**
     * @return String return the id
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
     * @return UserDetailMinResponse return the user
     */
    public UserDetailMinResponse getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(UserDetailMinResponse user) {
        this.user = user;
    }

    /**
     * @return Date return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return Date return the lastModifiedDate
     */
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * @param lastModifiedDate the lastModifiedDate to set
     */
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
