/*
 * Created on 15/08/2010
 */
package br.com.arbo.swinginsulation.examples.numbers;

import java.awt.Component;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executor;

import br.com.arbo.swinginsulation.InsulatedProxyFactory;
import br.com.arbo.swinginsulation.Insulators;

class Numbers implements OutsideUI {

	private final String name;
	private final Component component;
	private final InsideUI insideUI;

	Numbers(final String name, final String description, final String info,
			final Executor executor) {
		this.name = name;
		final NumbersPanel p = makePanel(name, description, info,
				thisAsProxy(executor));
		this.component = p;
		this.insideUI = panelAsProxy(p);
	}

	String getName() {
		return name;
	}

	Component getComponent() {
		return component;
	}

	private static NumbersPanel makePanel(final String name,
			final String description, final String info,
			final OutsideUI outsideUI) {
		return NumbersPanel.make(name + ": " + description, info,
				outsideUI);
	}

	private OutsideUI thisAsProxy(final Executor executor) {
		final InsulatedProxyFactory<OutsideUI> factory = InsulatedProxyFactory
				.with(OutsideUI.class, this, executor);
		factory.setUncaughtExceptionHandler(new ExceptionHandler());
		return factory.newProxyInstance();
	}

	private static InsideUI panelAsProxy(final NumbersPanel p) {
		return Insulators.swing(InsideUI.class, p);
	}

	@Override
	public void crunch(final String number) {
		final int n;
		n = Integer.parseInt(number);
		for (int i = 1; i <= n; i++) {
			insideUI.updateProgress(i, n, Thread.currentThread().getName());
			sleep(0.5);
		}
		insideUI.updateDone(n);
	}

	class ExceptionHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(final Thread t, final Throwable e) {
			displayError(e);
		}

	}

	void displayError(final Throwable ex) {
		insideUI.displayError(ex);
	}

	private static void sleep(final double seconds) {
		try {
			Thread.sleep((int) (1000 * seconds));
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
