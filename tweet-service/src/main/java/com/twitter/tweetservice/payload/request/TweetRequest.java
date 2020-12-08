package com.twitter.tweetservice.payload.request;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TweetRequest {
	
	@NotBlank
	@Size(max = 300)
	private String content;

	private List<String> tags = new ArrayList<>();

	@NotNull
	private boolean fileAttached;

    /**
     * @return String return the text
     */
    public String getContent() {
        return content;
    }

    /**
     * @param text the text to set
     */
    public void setContent(String content) {
        this.content = content;
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
     * @return boolean return the fileAttached
     */
    public boolean isFileAttached() {
        return fileAttached;
    }

    /**
     * @param fileAttached the fileAttached to set
     */
    public void setFileAttached(boolean fileAttached) {
        this.fileAttached = fileAttached;
    }

}
