package com.golfscriptcompiler.parser;

public class BadParseException extends RuntimeException {

	/**
	 * This exception is thrown when the code is unparseable because it was badly written.
	 */
	
	private static final long serialVersionUID = 1L;

	public BadParseException() {
	}

	public BadParseException(String arg0) {
		super(arg0);
	}

	public BadParseException(Throwable arg0) {
		super(arg0);
	}

	public BadParseException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public BadParseException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
