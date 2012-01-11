/*
 * Created on 14/08/2010
 */
package br.com.arbo.swinginsulation;

import static br.com.arbo.org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.io.Serializable;

import org.junit.Test;

import br.com.arbo.swinginsulation.SingleInterfaceIntrospection;
import br.com.arbo.swinginsulation.SingleInterfaceIntrospection.TooManyInterfacesException;

public class SingleInterfaceIntrospectionTest {

	@Test
	public void happyDay() {
		class One implements Serializable {
			//
		}
		final Class<?> i = SingleInterfaceIntrospection
				.determineSingleInterface(new One());
		assertThat(i, is(Serializable.class));
	}

	@Test(expected = TooManyInterfacesException.class)
	public void sorryDay() {
		class Two implements Serializable, Cloneable {
			//
		}
		SingleInterfaceIntrospection.determineSingleInterface(new Two());
	}

}
