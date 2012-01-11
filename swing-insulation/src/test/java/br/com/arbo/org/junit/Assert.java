package br.com.arbo.org.junit;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.ComparisonFailure;

public final class Assert {

	@SuppressWarnings("unchecked")
	public static <T> void assertThat(final T actual,
			final Matcher<? super T> matcher) {
		if (actual instanceof String) {
			assertThat("", (String) actual, (Matcher<? super String>) matcher);
		} else {
			org.junit.Assert.assertThat(actual, matcher);
		}
	}

	public static void assertThat(final String reason, final String actual,
			final Matcher<? super String> matcher) {
		if (matcher.matches(actual)) {
			return;
		}
		final String cleanMessage = reason == null ? "" : reason;
		final Description expectedDescription = new StringDescription();
		expectedDescription.appendDescriptionOf(matcher);
		final Description actualDescription = new StringDescription();
		actualDescription.appendValue(actual);
		throw new ComparisonFailure(cleanMessage,
				expectedDescription.toString(), actualDescription.toString());
	}

}