package br.com.arbo.swinginsulation;

import java.util.concurrent.Executor;

public final class Insulators {

	public static <T> T swing(final T decorated) {
		return InsulatedProxyFactory.swing(decorated).newProxyInstance();
	}

	public static <T> T swing(final Class<T> single, final T decorated) {
		return InsulatedProxyFactory
				.swing(single, decorated)
				.newProxyInstance();
	}

	public static <T> T swing(final T decorated, final ClassLoader classLoader,
			final Class< ? >[] interfaces) {
		return InsulatedProxyFactory
				.swing(decorated, classLoader, interfaces)
				.newProxyInstance();
	}

	public static <T> T with(final T decorated, final Executor executor) {
		return InsulatedProxyFactory
				.with(decorated, executor)
				.newProxyInstance();
	}

	public static <T> T with(final Class<T> single, final T decorated,
			final Executor executor) {
		return InsulatedProxyFactory
				.with(single, decorated, executor)
				.newProxyInstance();
	}

	public static <T> T with(final T decorated, final Executor executor,
			final ClassLoader classLoader, final Class< ? >[] interfaces) {
		return InsulatedProxyFactory.with(decorated, executor, classLoader,
				interfaces).newProxyInstance();
	}
}
