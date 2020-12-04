package com.twitter.twitterbackend.service;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.query.Criteria;

public class CustomAggregationOperation implements AggregationOperation {
	private int size;

	public CustomAggregationOperation(int size) {
		this.size = size;
	}

	public AggregationOperation setSize(int size) {
		this.size = size;
		return this;
	}

	@Override
	public Document toDocument(AggregationOperationContext context) {
		return new Document("$sample", context.getMappedObject(Criteria.where("size").is(size).getCriteriaObject()));
	}
}
