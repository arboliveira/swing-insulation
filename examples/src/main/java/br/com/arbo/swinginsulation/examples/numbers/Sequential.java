package br.com.arbo.swinginsulation.examples.numbers;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class Sequential {

	Sequential(final ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}

	Numbers make() {
		final String info = "Long running operations are sent to a different work thread.\n"
				+ "The work thread sends feedback on progress back to the Swing thread.\n"
				+ "UI repaints normally, unobstructed.\n"
				+ "Progress seems incremental because there is just one work thread doing everything.";
		return new Numbers("Sequential",
				"Enqueue onto a single separate Thread", info,
				Executors.newSingleThreadExecutor(threadFactory));
	}

	private final ThreadFactory threadFactory;

}