package com.ias.s7reader.core;

public class S7ReadException extends Exception {

	private static final long serialVersionUID = 1L;
	private final int errorCode;

    public S7ReadException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
