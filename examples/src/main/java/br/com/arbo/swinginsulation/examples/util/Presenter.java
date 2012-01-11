/*
 * Created on 06/06/2009
 */
package br.com.arbo.swinginsulation.examples.util;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Presenter {

	public static void showFrame(final String title, final Component content) {
		newFrame(title, content).setVisible(true);
	}

	public static JFrame newFrame(final String title, final Component content) {
		final JFrame frame = new JFrame(title);
		frame.setLayout(new GridLayout());
		frame.add(content);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		return frame;
	}

	public static void showFrames(final JFrame... frames) {
		int i = 0;
		for (final JFrame jFrame : frames) {
			jFrame.setLocation(i, 0);
			i += jFrame.getWidth() + 10;
			jFrame.setVisible(true);
		}
	}

}
