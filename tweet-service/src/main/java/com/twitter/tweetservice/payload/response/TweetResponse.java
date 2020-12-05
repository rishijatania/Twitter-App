package com.twitter.tweetservice.payload.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TweetResponse {
	private String id;
	private UserDetailMinResponse user;
	private String text;
	private List<String> tags = new ArrayList<>();
	private FileResponse file;
	private List<CommentResponse> comments = new ArrayList<>();
	private List<LikeResponse> likes = new ArrayList<>();
	private List<RetweetResponse> retweets = new ArrayList<>();
	private Date createdDate;
	private Date lastModifiedDate;
	private Integer retweetsCount;
	private Integer commentsCount;
	private Integer likesCount;

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
     * @return UserResponse return the user
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
     * @return FileResponse return the file
     */
    public FileResponse getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(FileResponse file) {
        this.file = file;
    }

    /**
     * @return List<CommentResponse> return the comments
     */
    public List<CommentResponse> getComments() {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(List<CommentResponse> comments) {
        this.comments = comments;
    }

    /**
     * @return List<LikeResponse> return the likes
     */
    public List<LikeResponse> getLikes() {
        return likes;
    }

    /**
     * @param likes the likes to set
     */
    public void setLikes(List<LikeResponse> likes) {
        this.likes = likes;
    }

    /**
     * @return List<RetweetResponse> return the retweets
     */
    public List<RetweetResponse> getRetweets() {
        return retweets;
    }

    /**
     * @param retweets the retweets to set
     */
    public void setRetweets(List<RetweetResponse> retweets) {
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
     * @return Integer return the retweetsCount
     */
    public Integer getRetweetsCount() {
        return retweetsCount;
    }

    /**
     * @param retweetsCount the retweetsCount to set
     */
    public void setRetweetsCount(Integer retweetsCount) {
        this.retweetsCount = retweetsCount;
    }

    /**
     * @return Integer return the commentsCount
     */
    public Integer getCommentsCount() {
        return commentsCount;
    }

    /**
     * @param commentsCount the commentsCount to set
     */
    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    /**
     * @return Integer return the likesCount
     */
    public Integer getLikesCount() {
        return likesCount;
    }

    /**
     * @param likesCount the likesCount to set
     */
    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

}
