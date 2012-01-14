package br.com.arbo.swinginsulation.examples.numbers;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executor;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;

import br.com.arbo.swinginsulation.InsulatedProxyFactory;
import br.com.arbo.swinginsulation.examples.util.Presenter;

public class NumbersPanelTest {

	@Test
	public void crunchButton_shouldSplitTextAndCrunchEachNumber()
			throws Throwable {
		m.checking(new Expectations() {
			{
				for (final String one : new String[] { "1", "foo", "bar", "2" }) {
					oneOf(outsideUI).crunch(one);
				}
			}
		});
		Presenter.showFrame(name, panel);
		final JFrameOperator frame = new JFrameOperator();
		final JTextFieldOperator text = new JTextFieldOperator(frame);
		text.clearText();
		text.typeText("1 foo bar 2");
		new JButtonOperator(frame).push();
		captureErrors.assertNoErrors();
		m.assertIsSatisfied();
	}

	public NumbersPanelTest() {
		this.panel = NumbersPanel.make(name, name, newProxy());
	}

	private OutsideUI newProxy() {
		final InsulatedProxyFactory<OutsideUI> factory = InsulatedProxyFactory
				.with(OutsideUI.class, outsideUI,
						new ActuallySwingThreadImmediate());
		factory.setUncaughtExceptionHandler(captureErrors);
		return factory.newProxyInstance();
	}

	static class ActuallySwingThreadImmediate implements Executor {

		@Override
		public void execute(final Runnable command) {
			command.run();
		}

	}

	static class CaptureErrors implements UncaughtExceptionHandler {

		Throwable exception;

		@Override
		public void uncaughtException(final Thread t, final Throwable e) {
			this.exception = e;
		}

		public void assertNoErrors() throws Throwable {
			if (exception != null) {
				throw exception;
			}
		}

	}

	final Mockery m = new JUnit4Mockery();
	final OutsideUI outsideUI = m.mock(OutsideUI.class);
	private final String name = this.getClass().getName();
	private final CaptureErrors captureErrors = new CaptureErrors();
	private final NumbersPanel panel;

}
