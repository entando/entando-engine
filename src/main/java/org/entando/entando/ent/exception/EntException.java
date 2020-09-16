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
 * Entando Standard Exception for generic conditions
 */
public class EntException extends Exception implements IEntException {

	/**
	 * @see Exception#Exception(String)
	 */
	public EntException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public EntException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Formatted ent exception
	 * <p>Syntax 1:</p>
	 * <pre>
	 * # Simple Exception
	 *
	 * throw new EntException("Invalid value %s", var);
	 * </pre>
	 * <p>Syntax 2:</p>
	 * <pre>
	 * # Exception with cause
	 *
	 * try {
	 * 	  ...
	 * } catch (Exception e) {
	 * 	 throw new EntException("Invalid value %s %s", var, e);
	 * }
	 * </pre>
	 * <p>Syntax 3:</p>
	 * <pre>
	 * # Simple formatted exception with a terminating throwable as last parameter of the message
	 * # The terminating throwable is interpreted a argument and TERM is just ignored
	 *
	 * throw new EntException("Invalid value %s", var, new Exception("Hey"), IEntException.TERM);
	 * </pre>
	 * <p>~~~</p>
	 * @param format the format string like in {@link String#format}
	 * @param args   a list of printable info plus the optional cause (a throwable object)
	 */
	public EntException(String format, Object... args) {
		super(
				String.format(format, IEntException.extractActualArgs(args)),
				IEntException.extractActualCause(args));
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public EntException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @see Exception#Exception(String, Throwable, boolean, boolean)
	 */
	public EntException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}
}
