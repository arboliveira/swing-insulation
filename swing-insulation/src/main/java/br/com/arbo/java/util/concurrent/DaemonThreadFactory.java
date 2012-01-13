package br.com.arbo.java.util.concurrent;

import java.util.concurrent.ThreadFactory;

public final class DaemonThreadFactory implements ThreadFactory {

	private static final ThreadFactory DELEGATE = java.util.concurrent.Executors
			.defaultThreadFactory();

	@Override
	public Thread newThread(final Runnable r) {
		final Thread t = DELEGATE.newThread(r);
		t.setDaemon(true);
		return t;
	}
}