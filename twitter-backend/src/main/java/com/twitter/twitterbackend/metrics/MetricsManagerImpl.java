package com.twitter.twitterbackend.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetricsManagerImpl implements MetricsManager
{
    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private SampleStore sampleStore;

    @Override
    public void trackTimeMetrics(String metricName, String... tags)
    {
        Timer timer = meterRegistry.timer(metricName, tags);
        sampleStore.get().stop(timer);
    }

    @Override
    public void trackCounterMetrics(String metricName, double value, String... tags)
    {
        meterRegistry.counter(metricName, tags).increment(value);
    }
}
