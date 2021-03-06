package br.com.arbo.swinginsulation;

import static br.com.arbo.org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public final class InsulatedInvocationHandlerTest {

	@Test
	public void methodWithoutReturnWontBlock() throws InterruptedException {

		final Semaphore lockedOutside = new Semaphore(0);
		final Semaphore lockedInside = new Semaphore(0);

		class My implements MethodWithoutReturn {

			String where;

			@Override
			public void methodWithoutReturn() {
				where = Thread.currentThread().getName();
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
		final My my = new My();
		final MethodWithoutReturn proxy = newProxy(my);
		proxy.methodWithoutReturn();
		lockedOutside.release();
		if (!lockedInside.tryAcquire(1, TimeUnit.SECONDS)) {
			throw new RuntimeException(
					"Inside never unlocked. Am I holding the outside lock?");
		}
		assertThat(my.where, equalTo(expectedThreadName()));
	}

	public interface MethodWithoutReturn {

		void methodWithoutReturn();
	}

	public interface MethodWithReturn {

		Object methodWithReturn();
	}

	@Test
	public void methodWithReturnWillBlock() {

		final Object toBeReturned = new Object();

		class My implements MethodWithReturn {

			String where;

			@Override
			public Object methodWithReturn() {
				where = Thread.currentThread().getName();
				return toBeReturned;
			}

		}
		final My my = new My();
		final MethodWithReturn proxy = newProxy(my);
		final Object returned = proxy.methodWithReturn();
		assertThat(returned, is(toBeReturned));
		assertThat(my.where, equalTo(expectedThreadName()));
	}

	public static class CrashException extends RuntimeException {
		//
	}

	public interface CrashDeclare {

		void crash() throws CrashException;

	}

	public interface CrashDontDeclare {

		void crash();

	}

	@Test
	public void methodWithDeclaredException_shouldBlock() throws CrashException {
		final Object toBeReturned = new Object();
		class DontActuallyCrashJustDeclareExceptionInSignature implements
				CrashDeclare {

			String where;
			Object returned;

			@Override
			public void crash() throws CrashException {
				where = Thread.currentThread().getName();
				returned = toBeReturned;
			}

		}
		final DontActuallyCrashJustDeclareExceptionInSignature my = new DontActuallyCrashJustDeclareExceptionInSignature();
		final CrashDeclare proxy = newProxy(my);
		proxy.crash();
		assertThat(my.returned, is(toBeReturned));
		assertThat(my.where, equalTo(expectedThreadName()));
	}

	@Test(expected = CrashException.class)
	public void declaredException_shouldPropagate() throws CrashException {
		class DeclareExceptionInSignature implements CrashDeclare {

			@Override
			public void crash() throws CrashException {
				throw new CrashException();
			}

		}
		final CrashDeclare proxy = newProxy(new DeclareExceptionInSignature());
		proxy.crash();
	}

	@Test
	public void undeclaredException_shouldNotPropagate() {
		class DontDeclareException implements CrashDontDeclare {

			@Override
			public void crash() {
				throw new CrashException();
			}

		}
		final CrashDontDeclare proxy = newProxy(new DontDeclareException());
		proxy.crash();
	}

	private <T> T newProxy(final T decorated) {
		return Insulators.with(decorated,
				Executors.newSingleThreadExecutor(threadFactory));
	}

	private String expectedThreadName() {
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
