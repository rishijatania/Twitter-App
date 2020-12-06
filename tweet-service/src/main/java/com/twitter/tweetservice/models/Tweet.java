package com.twitter.tweetservice.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tweets")
public class Tweet {
	@Id
	private String id;

	@DBRef
	@NotNull
	private User user;

	@NotBlank
	@Size(max = 300)
	private String text;

	private List<String> tags = new ArrayList<>();

	private File file;

	@DBRef
	private List<Comment> comments = new ArrayList<>();

	@DBRef
	private List<Like> likes = new ArrayList<>();

	@DBRef
	private List<Retweet> retweets = new ArrayList<>();

	@CreatedDate
	private Date createdDate;

	@LastModifiedDate
	private Date lastModifiedDate;

	private Integer retweetsCount;
	private Integer commentsCount;
	private Integer likesCount;

	public Tweet() {
	}

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
	 * @return User return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return String return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return List<String> return the tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/**
	 * @return File return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return List<Comment> return the comments
	 */
	public List<Comment> getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	/**
	 * @return List<Like> return the likes
	 */
	public List<Like> getLikes() {
		return likes;
	}

	/**
	 * @param likes the likes to set
	 */
	public void setLikes(List<Like> likes) {
		this.likes = likes;
	}

	/**
	 * @return List<Retweet> return the retweets
	 */
	public List<Retweet> getRetweets() {
		return retweets;
	}

	/**
	 * @param retweets the retweets to set
	 */
	public void setRetweets(List<Retweet> retweets) {
		this.retweets = retweets;
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

	/**
	 * @return Integer return the tweetsCount
	 */
	public Integer getRetweetsCount() {
		return this.retweets.size();
	}

	/**
	 * @return Integer return the commentsCount
	 */
	public Integer getCommentsCount() {
		return this.comments.size();
	}

	/**
	 * @return Integer return the likesCount
	 */
	public Integer getLikesCount() {
		return this.likes.size();
	}

}
