package br.com.arbo.swinginsulation;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import br.com.arbo.swinginsulation.InsulatedProxyFactory;

public class ControllerProxyFactoryTest extends Fixture {

	@Test
	public void undeclaredExceptionWithUncaughtExceptionHandler_shouldCapture()
			throws InterruptedException {
		final Semaphore lockedOutside = new Semaphore(0);
		final Semaphore lockedInside = new Semaphore(0);

		class DontDeclareException implements CrashDontDeclare {

			@Override
			public void crash() {
				throw new CrashException();
			}

		}

		class Handler implements UncaughtExceptionHandler {

			@Override
			public void uncaughtException(final Thread t, final Throwable e) {
				expectOutsideToProceedAndUnlock();
				lockedInside.release();
			}

			private void expectOutsideToProceedAndUnlock() {
				try {
					if (!lockedOutside.tryAcquire(1, TimeUnit.SECONDS)) {
						throw new RuntimeException(
								"Outside never unlocked. Am I blocking?");
					}
				} catch (final InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

		}

		final InsulatedProxyFactory<DontDeclareException> factory = newProxyFactory(new DontDeclareException());
		factory.setUncaughtExceptionHandler(new Handler());
		final CrashDontDeclare proxy = factory.newProxyInstance();
		proxy.crash();
		lockedOutside.release();
		if (!lockedInside.tryAcquire(1, TimeUnit.SECONDS)) {
			throw new RuntimeException(
					"Inside never unlocked. Am I holding the outside lock?");
		}
	}

	@Override
	protected <T> InsulatedProxyFactory<T> newProxyFactory(final T decorated) {
		final Executor executor = Executors
				.newSingleThreadExecutor(threadFactory);
		return InsulatedProxyFactory.with(decorated, executor);
	}

	@Override
	protected String expectedThreadName() {
		return threadFactory.nameOfLastCreatedThread;
	}

	static class ThreadFactoryForTest implements ThreadFactory {

		String nameOfLastCreatedThread;

		@Override
		public Thread newThread(final Runnable r) {
			final Thread t = Executors.defaultThreadFactory().newThread(r);
			this.nameOfLastCreatedThread = t.getName();
			return t;
		}
	}

	private final ThreadFactoryForTest threadFactory = new ThreadFactoryForTest();

}
