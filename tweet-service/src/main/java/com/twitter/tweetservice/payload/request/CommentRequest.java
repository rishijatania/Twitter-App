package com.twitter.tweetservice.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CommentRequest {
	@NotBlank
	@Size(max = 300)
	private String text;

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

}
