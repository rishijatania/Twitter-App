package com.twitter.twitterbackend.metrics;

public interface MetricsManager
{
    /**
     * To monitor counts & time
     * @param metricName
     * @param tags
     */
    void trackTimeMetrics(String metricName, String... tags);

    /**
     * To monitor incremental count
     * @param metricName
     * @param tags
     */
    void trackCounterMetrics(String metricName, double value, String... tags);
}
