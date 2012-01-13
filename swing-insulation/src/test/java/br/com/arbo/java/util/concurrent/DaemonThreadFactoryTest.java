package br.com.arbo.java.util.concurrent;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import org.junit.Test;

public class DaemonThreadFactoryTest {

	@Test
	@SuppressWarnings("boxing")
	public void newSingleDaemonThreadExecutor() throws InterruptedException,
			ExecutionException {
		final Executor executor = java.util.concurrent.Executors
				.newSingleThreadExecutor(new DaemonThreadFactory());
		class SniffThread implements Callable<Boolean> {

			@Override
			public Boolean call() {
				return Thread.currentThread().isDaemon();
			}

		}
		final FutureTask<Boolean> sniffIsDaemon = new FutureTask<Boolean>(
				new SniffThread());
		executor.execute(sniffIsDaemon);
		assertTrue(sniffIsDaemon.get());
	}

}
