package br.com.arbo.swinginsulation;

import static org.junit.Assert.assertTrue;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.junit.Test;

public class EventQueueExecutorTest {

	@Test
	public void execute_shouldRunInSwingThread() throws InterruptedException,
			ExecutionException {
		final FutureTask<Boolean> isSwingThread = new FutureTask<Boolean>(
				new IsSwingThread());
		new EventQueueExecutor().execute(isSwingThread);
		assertTrue(isSwingThread.get().booleanValue());
	}

	static class IsSwingThread implements Callable<Boolean> {

		@Override
		public Boolean call() {
			return Boolean.valueOf(EventQueue.isDispatchThread());
		}

	}

}
