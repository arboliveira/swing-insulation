package br.com.arbo.swinginsulation.examples.numbers;

public interface ViewActions {

	void updateProgress(int current, int total, String thread);

	void updateDone(int number);

	void displayError(Throwable ex);
}
