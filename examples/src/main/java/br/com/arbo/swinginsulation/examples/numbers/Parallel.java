package br.com.arbo.swinginsulation.examples.numbers;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class Parallel {

	Parallel(final ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}

	Numbers make() {
		final String info = "Long running operations are sent to multiple different work threads.\n"
				+ "All of them send feedback on progress back simultaneously, which are enqueued onto the Swing thread.\n"
				+ "UI repaints normally, unobstructed.\n"
				+ "Progress seems erratic because multiple work threads are sharing the same user interface.\n"
				+ "Multiple windows would be more adequate to report progress in this scenario.";
		return new Numbers("Parallel", "Distribute into a Thread Pool", info,
				Executors.newFixedThreadPool(3, threadFactory));
	}

	private final ThreadFactory threadFactory;
}