package br.com.arbo.swinginsulation;

class SingleInterfaceIntrospection {

	static <I, T extends I> Class<I> determineSingleInterface(final T decorated) {
		final Class<?>[] interfaces = decorated.getClass().getInterfaces();
		if (interfaces.length != 1) {
			throw new TooManyInterfacesException();
		}
		@SuppressWarnings("unchecked")
		final Class<I> single = (Class<I>) interfaces[0];
		return single;
	}

	static class TooManyInterfacesException extends RuntimeException {

		public TooManyInterfacesException() {
			super("Object must implement a single interface");
		}
	}

}
