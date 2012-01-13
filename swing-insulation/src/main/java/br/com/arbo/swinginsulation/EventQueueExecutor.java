package br.com.arbo.swinginsulation;

import java.awt.EventQueue;
import java.util.concurrent.Executor;

final class EventQueueExecutor implements Executor {

	@Override
	public void execute(final Runnable command) {
		EventQueue.invokeLater(command);
	}

}