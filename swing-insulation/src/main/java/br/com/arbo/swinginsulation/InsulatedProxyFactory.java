package br.com.arbo.swinginsulation;

import java.awt.EventQueue;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.Executor;

public final class InsulatedProxyFactory<T> {

	@SuppressWarnings("unchecked")
	public static <I, T extends I> InsulatedProxyFactory<I> swing(
			final T decorated) {
		return (InsulatedProxyFactory<I>) with(decorated,
				EventQueueExecutor.SINGLETON);
	}

	public static <T> InsulatedProxyFactory<T> swing(final Class<T> single,
			final T decorated) {
		return with(single, decorated, EventQueueExecutor.SINGLETON);
	}

	public static <T> InsulatedProxyFactory<T> swing(final T decorated,
			final ClassLoader classLoader, final Class<?>[] interfaces) {
		return with(decorated, EventQueueExecutor.SINGLETON, classLoader,
				interfaces);
	}

	public static <I, T extends I> InsulatedProxyFactory<I> with(
			final T decorated, final Executor executor) {
		@SuppressWarnings("unchecked")
		final Class<I> single = (Class<I>) SingleInterfaceIntrospection
				.determineSingleInterface(decorated);
		return InsulatedProxyFactory.with(single, decorated, executor);
	}

	public static <I> InsulatedProxyFactory<I> with(final Class<I> single,
			final I decorated, final Executor executor) {
		final ClassLoader classLoader = decorated.getClass().getClassLoader();
		final Class<?>[] interfaces = new Class<?>[] { single };
		final InsulatedProxyFactory<I> factory = InsulatedProxyFactory.with(
				decorated, executor, classLoader, interfaces);
		return factory;
	}

	public static <I> InsulatedProxyFactory<I> with(final I decorated,
			final Executor executor, final ClassLoader classLoader,
			final Class<?>[] interfaces) {
		final InsulatedProxyFactory<I> factory = new InsulatedProxyFactory<I>(
				decorated, classLoader, interfaces);
		factory.setExecutor(executor);
		return factory;
	}

	public T newProxyInstance() {
		@SuppressWarnings("unchecked")
		final T proxyInstance = (T) Proxy.newProxyInstance(classLoader,
				interfaces, newInvocationHandler());
		return proxyInstance;
	}

	public void setExecutor(final Executor executor) {
		this.executor = executor;
	}

	public void setUncaughtExceptionHandler(final UncaughtExceptionHandler eh) {
		uncaughtExceptionHandler = eh;
	}

	private InsulatedInvocationHandler newInvocationHandler() {
		final InsulatedInvocationHandler h = new InsulatedInvocationHandler(
				decorated, executor);
		h.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
		return h;
	}

	private InsulatedProxyFactory(final T decorated,
			final ClassLoader classLoader, final Class<?>[] interfaces) {
		this.decorated = decorated;
		this.classLoader = classLoader;
		this.interfaces = interfaces;
	}

	static final class EventQueueExecutor implements Executor {

		static final EventQueueExecutor SINGLETON = new EventQueueExecutor();

		@Override
		public void execute(final Runnable command) {
			EventQueue.invokeLater(command);
		}

	}

	private final T decorated;
	private final ClassLoader classLoader;
	private final Class<?>[] interfaces;
	private Executor executor;
	private UncaughtExceptionHandler uncaughtExceptionHandler;
}
