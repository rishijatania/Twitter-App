package com.twitter.twitterbackend.payload.request;

public class SlackReportAttachment {

	private String fallback;
	private final String color = "#FF0000";
	private String pretext;
	private String author_name;
	private String title;
	private String text;

	/**
	 * @return String return the fallback
	 */
	public String getFallback() {
		return fallback;
	}

	/**
	 * @param fallback the fallback to set
	 */
	public void setFallback(String fallback) {
		this.fallback = fallback;
	}

	/**
	 * @return String return the pretext
	 */
	public String getPretext() {
		return pretext;
	}

	/**
	 * @param pretext the pretext to set
	 */
	public void setPretext(String pretext) {
		this.pretext = pretext;
	}

	/**
	 * @return String return the author_name
	 */
	public String getAuthor_name() {
		return author_name;
	}

	/**
	 * @param author_name the author_name to set
	 */
	public void setAuthor_name(String author_name) {
		this.author_name = author_name;
	}

	/**
	 * @return String return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
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

}
