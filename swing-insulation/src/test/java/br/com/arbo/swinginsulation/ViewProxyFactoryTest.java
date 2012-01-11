/*
 * Created on 29/04/2009
 */
package br.com.arbo.swinginsulation;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.swing.SwingUtilities;

import br.com.arbo.swinginsulation.InsulatedProxyFactory;

public class ViewProxyFactoryTest extends Fixture {

	private static final String swingThreadName = discoverSwingThreadName();

	@Override
	protected <T> InsulatedProxyFactory<T> newProxyFactory(final T decorated) {
		return InsulatedProxyFactory.swing(decorated);
	}

	@Override
	public String expectedThreadName() {
		return swingThreadName;
	}

	private static String discoverSwingThreadName() {
		class DiscoverSwingThreadName implements Callable<String> {

			@Override
			public String call() {
				return Thread.currentThread().getName();
			}

		}
		final FutureTask<String> task = new FutureTask<String>(
				new DiscoverSwingThreadName());
		try {
			SwingUtilities.invokeAndWait(task);
			return task.get();
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		} catch (final InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (final ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

}
