/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.ent.exception;

/**
 * Generic Entando Runtime Exception
 */
public class EntRuntimeException extends RuntimeException implements IEntException {

	/**
	 * @see RuntimeException#RuntimeException(String)
	 */
	public EntRuntimeException(String message) {
		super(message);
	}

	/**
	 * @see RuntimeException#RuntimeException(String, Throwable)
	 */
	public EntRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Formatted ent runtime exception
	 * <p>Syntax 1:</p>
	 * <pre>
	 * # Simple exception
	 *
	 * throw new EntRuntimeException("Invalid value %s", var);
	 * </pre>
	 * <p>Syntax 2:</p>
	 * <pre>
	 * # Exception with cause
	 *
	 * try {
	 * 	  ...
	 * } catch (Exception e) {
	 * 	 throw new EntRuntimeException("Invalid value %s", var, e);
	 * }
	 * </pre>
	 * <p>Syntax 3:</p>
	 * <pre>
	 * # Simple formatted exception with a terminating throwable as last parameter of the message
	 * # The terminating throwable is interpreted a argument and TERM is just ignored
	 *
	 * throw new EntRuntimeException("Invalid value %s %s", new Exception("Hey"), IEntException.TERM);
	 * </pre>
	 * <p>~~~</p>
	 * @param format the format string like in {@link String#format}
	 * @param args   a list of printable info plus the optional cause (a throwable object)
	 */
	public EntRuntimeException(String format, Object... args) {
		super(String.format(format,
				IEntException.extractActualArgs(args)),
				IEntException.extractActualCause(args));
	}

	/**
	 * @see RuntimeException#RuntimeException(Throwable)
	 */
	public EntRuntimeException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @see RuntimeException#RuntimeException(String, Throwable, boolean, boolean)
	 */
	public EntRuntimeException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
