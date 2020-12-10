package com.twitter.twitterbackend.service;

import java.util.Arrays;

import com.twitter.twitterbackend.payload.request.SlackReportAttachment;
import com.twitter.twitterbackend.payload.request.SlackReportRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SlackReportService {

	@Value("${slack.webHookUri}")
	private String SLACK_URI;

	public void report(String message, String username, String api) {
		try {
			RestTemplate restTemplate = new RestTemplate();

			SlackReportRequest slackReportRequest = new SlackReportRequest();
			SlackReportAttachment slackReportAttachment = new SlackReportAttachment();
			slackReportAttachment.setFallback("Plain-text summary of the attachment.");
			slackReportAttachment.setPretext("HTTP 500 - Internal Server Error");
			slackReportAttachment.setTitle("API ERROR - " + api);
			slackReportAttachment.setAuthor_name("Username: " + username);
			slackReportAttachment.setText("`" + message + "`");
			slackReportRequest.getAttachments().add(slackReportAttachment);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<SlackReportRequest> entity = new HttpEntity<SlackReportRequest>(slackReportRequest, headers);
			ResponseEntity<String> result = restTemplate.exchange(SLACK_URI, HttpMethod.POST, entity, String.class);
			if (result.getStatusCode() != HttpStatus.OK) {
				System.out.println(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
