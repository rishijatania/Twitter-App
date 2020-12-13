package com.twitter.tweetservice.metrics;

import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class SampleStore {
	private static final InheritableThreadLocal<Timer.Sample> THREAD_LOCAL = new InheritableThreadLocal<>();

	public void set(Timer.Sample sample) {
		THREAD_LOCAL.set(sample);
	}

	public Timer.Sample get() {
		return THREAD_LOCAL.get();
	}
}
