/*
 * Created on 15/08/2010
 */
package br.com.arbo.swinginsulation.examples.numbers;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executor;

import javax.swing.JFrame;

import br.com.arbo.swinginsulation.InsulatedProxyFactory;
import br.com.arbo.swinginsulation.Insulators;
import br.com.arbo.swinginsulation.examples.util.Presenter;

class Numbers implements ControllerActions {

	private final String name;
	private final NumbersPanel panel;
	private final ViewActions view;

	Numbers(final String name, final String description, final String info,
			final Executor executor) {
		this.name = name;
		final NumbersPanel p = makePanel(name, description, info,
				thisAsProxy(this, executor));
		this.view = panelAsProxy(p);
		this.panel = p;
	}

	private static NumbersPanel makePanel(final String name,
			final String description, final String info,
			final ControllerActions controllerActions) {
		return NumbersPanel.make(name + ": " + description, info,
				controllerActions);
	}

	private static ControllerActions thisAsProxy(final Numbers numbers,
			final Executor executor) {
		final InsulatedProxyFactory<ControllerActions> factory = InsulatedProxyFactory
				.with(ControllerActions.class, numbers, executor);
		factory.setUncaughtExceptionHandler(numbers.new ExceptionHandler());
		return factory.newProxyInstance();
	}

	private static ViewActions panelAsProxy(final NumbersPanel p) {
		return Insulators.swing(ViewActions.class, p);
	}

	JFrame newFrame() {
		return Presenter.newFrame("Number cruncher - " + name, panel);
	}

	@Override
	public void crunch(final String number) {
		final int n;
		n = Integer.parseInt(number);
		for (int i = 1; i <= n; i++) {
			view.updateProgress(i, n, Thread.currentThread().getName());
			sleep(0.5);
		}
		view.updateDone(n);
	}

	class ExceptionHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(final Thread t, final Throwable e) {
			displayError(e);
		}

	}

	void displayError(final Throwable ex) {
		view.displayError(ex);
	}

	private static void sleep(final double seconds) {
		try {
			Thread.sleep((int) (1000 * seconds));
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
