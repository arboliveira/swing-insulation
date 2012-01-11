package br.com.arbo.swinginsulation.examples.numbers;

import java.util.concurrent.Executor;

class Dumb {

	Numbers make() {
		final String info = "This is why you don't do things in the Swing Thread.\n"
				+ "UI gets obstructed, can't repaint or process input events, will become unresponsive for a long time.";
		return new Numbers("Dumb", "Stay on the Swing Thread", info,
				new DumbExecutor());
	}

	static class DumbExecutor implements Executor {

		@Override
		public void execute(final Runnable command) {
			command.run();
		}

	}

}