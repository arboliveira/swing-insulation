package br.com.arbo.swinginsulation.examples.numbers;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;

import br.com.arbo.swinginsulation.Insulators;

class Flash {

	public interface Unflash {

		void unflash();
	}

	void flash(final JComponent c) {
		green(c);
		class BackToNormal implements Unflash {

			@Override
			public void unflash() {
				black(c);
			}
		}
		scheduleUnflash(new BackToNormal());
	}

	private void scheduleUnflash(final Unflash unflash) {
		final Unflash insulated = Insulators.swing(unflash);
		class Delayed extends TimerTask {

			@Override
			public void run() {
				insulated.unflash();
			}

		}
		flash.schedule(new Delayed(), 50);
	}

	static void green(final JComponent c) {
		c.setForeground(Color.GREEN);
	}

	static void black(final JComponent c) {
		c.setForeground(Color.BLACK);
	}

	private final Timer flash = new Timer("Flash", true);
}