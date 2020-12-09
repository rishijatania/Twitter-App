package com.twitter.twitterbackend.payload.request;

import java.util.ArrayList;
import java.util.List;

public class SlackReportRequest {

	List<SlackReportAttachment> attachments = new ArrayList<>();

	public List<SlackReportAttachment> getAttachments() {
		return attachments;
	}

	/**
	 * @param text the text to set
	 */
	public void setAttachments(List<SlackReportAttachment> attachments) {
		this.attachments = attachments;
	}
}
