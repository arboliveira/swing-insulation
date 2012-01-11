package br.com.arbo.swinginsulation;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

final class InsulatedInvocationHandler implements InvocationHandler {

	InsulatedInvocationHandler(final Object decorated, final Executor executor) {
		this.decorated = decorated;
		this.executor = executor;
	}

	@Override
	public Object invoke(final Object proxy, final Method method,
			final Object[] args) throws Throwable {
		if (Void.TYPE.equals(method.getReturnType())
				&& method.getExceptionTypes().length == 0) {
			invokeLater(method, args);
			return null;
		}
		return invokeAndWait(method, args);
	}

	void setUncaughtExceptionHandler(final UncaughtExceptionHandler eh) {
		uncaughtExceptionHandler = eh;
	}

	private void invokeLater(final Method method, final Object[] args) {
		final Runnable runnable = new Runnable() {

			@Override
			public void run() {
				try {
					invokeOnDecorated(method, args);
				} catch (final IllegalAccessException e) {
					dispatchUncaughtException(e);
				} catch (final InvocationTargetException e) {
					final Throwable targetException = e.getTargetException();
					dispatchUncaughtException(targetException);
				}
			}

		};
		execute(runnable);
	}

	private Object invokeAndWait(final Method method, final Object[] args)
			throws Throwable {
		final FutureTask<Object> futureTask = new FutureTask<Object>(
				new Callable<Object>() {

					@Override
					public Object call() throws Exception {
						return invokeOnDecorated(method, args);
					}

				});
		execute(futureTask);
		try {
			return futureTask.get();
		} catch (final ExecutionException e) {
			final Throwable cause = e.getCause();
			if (cause instanceof InvocationTargetException) {
				throw cause.getCause();
			}
			throw cause;
		}
	}

	Object invokeOnDecorated(final Method method, final Object[] args)
			throws IllegalAccessException, InvocationTargetException {
		return method.invoke(decorated, args);
	}

	void dispatchUncaughtException(final Throwable e) {
		if (uncaughtExceptionHandler != null) {
			uncaughtExceptionHandler.uncaughtException(Thread.currentThread(),
					e);
			return;
		}
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		}
		throw new RuntimeException(e);
	}

	private void execute(final Runnable runnable) {
		executor.execute(runnable);
	}

	private final Object decorated;
	private final Executor executor;
	private UncaughtExceptionHandler uncaughtExceptionHandler;

}
