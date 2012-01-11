package br.com.arbo.swinginsulation.examples.numbers;

import java.util.concurrent.ThreadFactory;

import br.com.arbo.java.util.concurrent.DaemonThreadFactory;
import br.com.arbo.swinginsulation.examples.util.Presenter;

public class Main {

	public static void main(final String... args) {
		new Main().run();
	}

	private Main() {
		final ThreadFactory threadFactory = new DaemonThreadFactory();
		sequential = new Sequential(threadFactory).make();
		parallel = new Parallel(threadFactory).make();
		dumb = new Dumb().make();
	}

	private void run() {
		Presenter.showFrames(sequential.newFrame(), parallel.newFrame(),
				dumb.newFrame());
	}

	private final Numbers sequential;
	private final Numbers parallel;
	private final Numbers dumb;

}
