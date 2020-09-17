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
package org.entando.entando.ent.util;

import org.entando.entando.ent.util.EntLogging.EntLogger.SanitizationLevel;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * Sanitizes and Logs Data
 * <p>
 * Set of classes to help logging in Entando.
 * <p>
 * The classes supports simple sanitization in a scanner-friendly way.
 */
public class EntLogging {

	/**
	 * The main Entando logging class
	 * <pre>
	 * Use this class instead of directly using slf4j.
	 *
	 * Please check the methods;
	 *
	 *  - from
	 *  - withXXX
	 *  </pre>
	 */
	public static class EntLogger implements org.slf4j.Logger {

		protected final org.slf4j.Logger delegate;

		private final SanitizationLevel sanitizationLevel;

		public SanitizationLevel getSanitizationLevel() {
			return sanitizationLevel;
		}

		/**
		 * Returns a (cached) clone of the object with the same logger but BASIC_SANITIZATION level
		 */
		public EntLogger withBasicSan() {
			if (this.basicSan == null) {
				this.basicSan = EntLogFactory.from(SanitizationLevel.BASIC_SANITIZATION, delegate);
			}
			return this.basicSan;
		}

		/**
		 * Returns a (cached) clone of the object with the same logger but FULL_SANITIZATION level
		 */
		public EntLogger withFullSan() {
			if (this.fullySan == null) {
				this.fullySan = EntLogFactory.from(SanitizationLevel.FULL_SANITIZATION, delegate);
			}
			return this.fullySan;
		}

		/**
		 * Returns a (cached) clone of the object with the same logger but NO_SANITIZATION level
		 */
		public EntLogger withNoSan() {
			if (this.noSan == null) {
				this.noSan = EntLogFactory.from(SanitizationLevel.NO_SANITIZATION, delegate);
			}
			return this.noSan;
		}

		private EntLogger noSan = null;
		private EntLogger basicSan = null;
		private EntLogger fullySan = null;

		public enum SanitizationLevel {
			/**
			 * Nothing is sanitized
			 */
			NO_SANITIZATION,
			/**
			 * Only fields that are supposed to carry user contents are sanitized. Format string and exceptions are not
			 * sanitized
			 */
			BASIC_SANITIZATION,
			/**
			 * Everything is sanitized, including the format strings and the exception
			 */
			FULL_SANITIZATION,
		}

		private EntLogger(org.slf4j.Logger logger, SanitizationLevel sanitizationLevel) {
			this.delegate = logger;
			this.sanitizationLevel = sanitizationLevel;

			switch (sanitizationLevel) {
				case NO_SANITIZATION:
					noSan = this;
					break;
				case BASIC_SANITIZATION:
					basicSan = this;
					break;
				case FULL_SANITIZATION:
					fullySan = this;
					break;
			}
		}

		/**
		 * Return the name of this <code>Logger</code> instance.
		 *
		 * @return name of this logger instance
		 */
		@Override
		public String getName() {
			return delegate.getName();
		}

		/**
		 * Is the logger instance enabled for the TRACE level?
		 *
		 * @return True if this Logger is enabled for the TRACE level, false otherwise.
		 * @since 1.4
		 */
		@Override
		public boolean isTraceEnabled() {
			return delegate.isTraceEnabled();
		}

		/**
		 * Log a message at the TRACE level.
		 *
		 * @param msg the message string to be logged
		 * @since 1.4
		 */
		@Override
		public void trace(String msg) {
			if (delegate.isTraceEnabled()) {
				delegate.trace(sanitize(msg));
			}
		}

		/**
		 * Log a message at the TRACE level according to the specified format and argument.
		 * <p/>
		 * <p>This form avoids superfluous object creation when the logger
		 * is disabled for the TRACE level. </p>
		 *
		 * @param format the format string
		 * @param arg    the argument
		 * @since 1.4
		 */
		@Override
		public void trace(String format, Object arg) {
			if (delegate.isTraceEnabled()) {
				delegate.trace(sanitizeSpec(format), sanitize(arg));
			}
		}

		/**
		 * Log a message at the TRACE level according to the specified format and arguments.
		 * <p/>
		 * <p>This form avoids superfluous object creation when the logger
		 * is disabled for the TRACE level. </p>
		 *
		 * @param format the format string
		 * @param arg1   the first argument
		 * @param arg2   the second argument
		 * @since 1.4
		 */
		@Override
		public void trace(String format, Object arg1, Object arg2) {
			if (delegate.isTraceEnabled()) {
				delegate.trace(sanitizeSpec(format), sanitize(arg1), sanitize(arg2));
			}
		}

		/**
		 * Log a message at the TRACE level according to the specified format and arguments.
		 * <p/>
		 * <p>This form avoids superfluous string concatenation when the logger
		 * is disabled for the TRACE level. However, this variant incurs the hidden (and relatively small) cost of
		 * creating an <code>Object[]</code> before invoking the method, even if this logger is disabled for TRACE. The
		 * variants taking {@link #trace(String, Object) one} and {@link #trace(String, Object, Object) two} arguments
		 * exist solely in order to avoid this hidden cost.</p>
		 *
		 * @param format    the format string
		 * @param arguments a list of 3 or more arguments
		 * @since 1.4
		 */
		@Override
		public void trace(String format, Object... arguments) {
			if (delegate.isTraceEnabled()) {
				delegate.trace(sanitizeSpec(format), sanitize(arguments));
			}
		}

		/**
		 * Log an exception (throwable) at the TRACE level with an accompanying message.
		 *
		 * @param msg the message accompanying the exception
		 * @param t   the exception (throwable) to log
		 * @since 1.4
		 */
		@Override
		public void trace(String msg, Throwable t) {
			if (delegate.isTraceEnabled()) {
				delegate.trace(sanitizeSpec(msg), t);
			}
		}

		/**
		 * Similar to {@link #isTraceEnabled()} method except that the marker data is also taken into account.
		 *
		 * @param marker The marker data to take into consideration
		 * @return True if this Logger is enabled for the TRACE level, false otherwise.
		 * @since 1.4
		 */
		@Override
		public boolean isTraceEnabled(Marker marker) {
			return delegate.isTraceEnabled(marker);
		}

		/**
		 * Log a message with the specific Marker at the TRACE level.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param msg    the message string to be logged
		 * @since 1.4
		 */
		@Override
		public void trace(Marker marker, String msg) {
			if (delegate.isTraceEnabled()) {
				delegate.trace(marker, sanitize(msg));
			}
		}

		/**
		 * This method is similar to {@link #trace(String, Object)} method except that the marker data is also taken
		 * into consideration.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param format the format string
		 * @param arg    the argument
		 * @since 1.4
		 */
		@Override
		public void trace(Marker marker, String format, Object arg) {
			if (delegate.isTraceEnabled()) {
				delegate.trace(marker, sanitizeSpec(format), sanitize(arg));
			}
		}

		/**
		 * This method is similar to {@link #trace(String, Object, Object)} method except that the marker data is also
		 * taken into consideration.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param format the format string
		 * @param arg1   the first argument
		 * @param arg2   the second argument
		 * @since 1.4
		 */
		@Override
		public void trace(Marker marker, String format, Object arg1, Object arg2) {
			if (delegate.isTraceEnabled()) {
				delegate.trace(marker, sanitizeSpec(format), sanitize(arg1), sanitize(arg2));
			}
		}

		/**
		 * This method is similar to {@link #trace(String, Object...)} method except that the marker data is also taken
		 * into consideration.
		 *
		 * @param marker   the marker data specific to this log statement
		 * @param format   the format string
		 * @param argArray an array of arguments
		 * @since 1.4
		 */
		@Override
		public void trace(Marker marker, String format, Object... argArray) {
			if (delegate.isTraceEnabled()) {
				delegate.trace(marker, sanitizeSpec(format), sanitize(argArray));
			}
		}

		/**
		 * This method is similar to {@link #trace(String, Throwable)} method except that the marker data is also taken
		 * into consideration.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param msg    the message accompanying the exception
		 * @param t      the exception (throwable) to log
		 * @since 1.4
		 */
		@Override
		public void trace(Marker marker, String msg, Throwable t) {
			if (delegate.isTraceEnabled()) {
				delegate.trace(marker, sanitize(msg), sanitizeSpec(t));
			}
		}

		/**
		 * Is the logger instance enabled for the DEBUG level?
		 *
		 * @return True if this Logger is enabled for the DEBUG level, false otherwise.
		 */
		@Override
		public boolean isDebugEnabled() {
			return delegate.isDebugEnabled();
		}

		/**
		 * Log a message at the DEBUG level.
		 *
		 * @param msg the message string to be logged
		 */
		@Override
		public void debug(String msg) {
			if (delegate.isDebugEnabled()) {
				delegate.debug(sanitize(msg));
			}
		}

		/**
		 * Log a message at the DEBUG level according to the specified format and argument.
		 * <p/>
		 * <p>This form avoids superfluous object creation when the logger
		 * is disabled for the DEBUG level. </p>
		 *
		 * @param format the format string
		 * @param arg    the argument
		 */
		@Override
		public void debug(String format, Object arg) {
			if (delegate.isDebugEnabled()) {
				delegate.debug(sanitizeSpec(format), sanitize(arg));
			}
		}

		/**
		 * Log a message at the DEBUG level according to the specified format and arguments.
		 * <p/>
		 * <p>This form avoids superfluous object creation when the logger
		 * is disabled for the DEBUG level. </p>
		 *
		 * @param format the format string
		 * @param arg1   the first argument
		 * @param arg2   the second argument
		 */
		@Override
		public void debug(String format, Object arg1, Object arg2) {
			if (delegate.isDebugEnabled()) {
				delegate.debug(sanitizeSpec(format), sanitize(arg1), sanitize(arg2));
			}
		}

		/**
		 * Log a message at the DEBUG level according to the specified format and arguments.
		 * <p/>
		 * <p>This form avoids superfluous string concatenation when the logger
		 * is disabled for the DEBUG level. However, this variant incurs the hidden (and relatively small) cost of
		 * creating an <code>Object[]</code> before invoking the method, even if this logger is disabled for DEBUG. The
		 * variants taking {@link #debug(String, Object) one} and {@link #debug(String, Object, Object) two} arguments
		 * exist solely in order to avoid this hidden cost.</p>
		 *
		 * @param format    the format string
		 * @param arguments a list of 3 or more arguments
		 */
		@Override
		public void debug(String format, Object... arguments) {
			if (delegate.isDebugEnabled()) {
				delegate.debug(sanitizeSpec(format), sanitize(arguments));
			}
		}

		/**
		 * Log an exception (throwable) at the DEBUG level with an accompanying message.
		 *
		 * @param msg the message accompanying the exception
		 * @param t   the exception (throwable) to log
		 */
		@Override
		public void debug(String msg, Throwable t) {
			if (delegate.isDebugEnabled()) {
				delegate.debug(sanitizeSpec(msg), sanitizeSpec(t));
			}
		}

		/**
		 * Similar to {@link #isDebugEnabled()} method except that the marker data is also taken into account.
		 *
		 * @param marker The marker data to take into consideration
		 * @return True if this Logger is enabled for the DEBUG level, false otherwise.
		 */
		@Override
		public boolean isDebugEnabled(Marker marker) {
			return delegate.isDebugEnabled(marker);
		}

		/**
		 * Log a message with the specific Marker at the DEBUG level.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param msg    the message string to be logged
		 */
		@Override
		public void debug(Marker marker, String msg) {
			if (delegate.isDebugEnabled()) {
				delegate.debug(marker, sanitize(msg));
			}
		}

		/**
		 * This method is similar to {@link #debug(String, Object)} method except that the marker data is also taken
		 * into consideration.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param format the format string
		 * @param arg    the argument
		 */
		@Override
		public void debug(Marker marker, String format, Object arg) {
			if (delegate.isDebugEnabled()) {
				delegate.debug(marker, sanitizeSpec(format), sanitize(arg));
			}
		}

		/**
		 * This method is similar to {@link #debug(String, Object, Object)} method except that the marker data is also
		 * taken into consideration.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param format the format string
		 * @param arg1   the first argument
		 * @param arg2   the second argument
		 */
		@Override
		public void debug(Marker marker, String format, Object arg1, Object arg2) {
			if (delegate.isDebugEnabled()) {
				delegate.debug(marker, sanitizeSpec(format), sanitize(arg1), sanitize(arg2));
			}
		}

		/**
		 * This method is similar to {@link #debug(String, Object...)} method except that the marker data is also taken
		 * into consideration.
		 *
		 * @param marker    the marker data specific to this log statement
		 * @param format    the format string
		 * @param arguments a list of 3 or more arguments
		 */
		@Override
		public void debug(Marker marker, String format, Object... arguments) {
			if (delegate.isDebugEnabled()) {
				delegate.debug(marker, sanitizeSpec(format), sanitize(arguments));
			}
		}

		/**
		 * This method is similar to {@link #debug(String, Throwable)} method except that the marker data is also taken
		 * into consideration.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param msg    the message accompanying the exception
		 * @param t      the exception (throwable) to log
		 */
		@Override
		public void debug(Marker marker, String msg, Throwable t) {
			if (delegate.isDebugEnabled()) {
				delegate.debug(marker, sanitize(msg), sanitizeSpec(t));
			}
		}

		/**
		 * Is the logger instance enabled for the INFO level?
		 *
		 * @return True if this Logger is enabled for the INFO level, false otherwise.
		 */
		@Override
		public boolean isInfoEnabled() {
			return delegate.isInfoEnabled();
		}

		/**
		 * Log a message at the INFO level.
		 *
		 * @param msg the message string to be logged
		 */
		@Override
		public void info(String msg) {
			if (delegate.isInfoEnabled()) {
				delegate.info(sanitize(msg));
			}
		}

		/**
		 * Log a message at the INFO level according to the specified format and argument.
		 * <p/>
		 * <p>This form avoids superfluous object creation when the logger
		 * is disabled for the INFO level. </p>
		 *
		 * @param format the format string
		 * @param arg    the argument
		 */
		@Override
		public void info(String format, Object arg) {
			if (delegate.isInfoEnabled()) {
				delegate.info(sanitizeSpec(format), sanitize(arg));
			}
		}

		/**
		 * Log a message at the INFO level according to the specified format and arguments.
		 * <p/>
		 * <p>This form avoids superfluous object creation when the logger
		 * is disabled for the INFO level. </p>
		 *
		 * @param format the format string
		 * @param arg1   the first argument
		 * @param arg2   the second argument
		 */
		@Override
		public void info(String format, Object arg1, Object arg2) {
			if (delegate.isInfoEnabled()) {
				delegate.info(sanitizeSpec(format), sanitize(arg1), sanitize(arg2));
			}
		}

		/**
		 * Log a message at the INFO level according to the specified format and arguments.
		 * <p/>
		 * <p>This form avoids superfluous string concatenation when the logger
		 * is disabled for the INFO level. However, this variant incurs the hidden (and relatively small) cost of
		 * creating an <code>Object[]</code> before invoking the method, even if this logger is disabled for INFO. The
		 * variants taking {@link #info(String, Object) one} and {@link #info(String, Object, Object) two} arguments
		 * exist solely in order to avoid this hidden cost.</p>
		 *
		 * @param format    the format string
		 * @param arguments a list of 3 or more arguments
		 */
		@Override
		public void info(String format, Object... arguments) {
			if (delegate.isInfoEnabled()) {
				delegate.info(sanitizeSpec(format), sanitize(arguments));
			}
		}

		/**
		 * Log an exception (throwable) at the INFO level with an accompanying message.
		 *
		 * @param msg the message accompanying the exception
		 * @param t   the exception (throwable) to log
		 */
		@Override
		public void info(String msg, Throwable t) {
			if (delegate.isInfoEnabled()) {
				delegate.info(sanitizeSpec(msg), sanitizeSpec(t));
			}
		}

		/**
		 * Similar to {@link #isInfoEnabled()} method except that the marker data is also taken into consideration.
		 *
		 * @param marker The marker data to take into consideration
		 * @return true if this logger is warn enabled, false otherwise
		 */
		@Override
		public boolean isInfoEnabled(Marker marker) {
			return delegate.isInfoEnabled(marker);
		}

		/**
		 * Log a message with the specific Marker at the INFO level.
		 *
		 * @param marker The marker specific to this log statement
		 * @param msg    the message string to be logged
		 */
		@Override
		public void info(Marker marker, String msg) {
			if (delegate.isInfoEnabled()) {
				delegate.info(marker, sanitize(msg));
			}
		}

		/**
		 * This method is similar to {@link #info(String, Object)} method except that the marker data is also taken into
		 * consideration.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param format the format string
		 * @param arg    the argument
		 */
		@Override
		public void info(Marker marker, String format, Object arg) {
			if (delegate.isInfoEnabled()) {
				delegate.info(marker, sanitizeSpec(format), sanitize(arg));
			}
		}

		/**
		 * This method is similar to {@link #info(String, Object, Object)} method except that the marker data is also
		 * taken into consideration.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param format the format string
		 * @param arg1   the first argument
		 * @param arg2   the second argument
		 */
		@Override
		public void info(Marker marker, String format, Object arg1, Object arg2) {
			if (delegate.isInfoEnabled()) {
				delegate.info(marker, sanitizeSpec(format), sanitize(arg1), sanitize(arg2));
			}
		}

		/**
		 * This method is similar to {@link #info(String, Object...)} method except that the marker data is also taken
		 * into consideration.
		 *
		 * @param marker    the marker data specific to this log statement
		 * @param format    the format string
		 * @param arguments a list of 3 or more arguments
		 */
		@Override
		public void info(Marker marker, String format, Object... arguments) {
			if (delegate.isInfoEnabled()) {
				delegate.info(marker, sanitizeSpec(format), sanitize(arguments));
			}
		}

		/**
		 * This method is similar to {@link #info(String, Throwable)} method except that the marker data is also taken
		 * into consideration.
		 *
		 * @param marker the marker data for this log statement
		 * @param msg    the message accompanying the exception
		 * @param t      the exception (throwable) to log
		 */
		@Override
		public void info(Marker marker, String msg, Throwable t) {
			if (delegate.isInfoEnabled()) {
				delegate.info(marker, sanitize(msg), sanitizeSpec(t));
			}
		}

		/**
		 * Is the logger instance enabled for the WARN level?
		 *
		 * @return True if this Logger is enabled for the WARN level, false otherwise.
		 */
		@Override
		public boolean isWarnEnabled() {
			return delegate.isWarnEnabled();
		}

		/**
		 * Log a message at the WARN level.
		 *
		 * @param msg the message string to be logged
		 */
		@Override
		public void warn(String msg) {
			if (delegate.isWarnEnabled()) {
				delegate.warn(sanitize(msg));
			}
		}

		/**
		 * Log a message at the WARN level according to the specified format and argument.
		 * <p/>
		 * <p>This form avoids superfluous object creation when the logger
		 * is disabled for the WARN level. </p>
		 *
		 * @param format the format string
		 * @param arg    the argument
		 */
		@Override
		public void warn(String format, Object arg) {
			if (delegate.isWarnEnabled()) {
				delegate.warn(sanitizeSpec(format), sanitize(arg));
			}
		}

		/**
		 * Log a message at the WARN level according to the specified format and arguments.
		 * <p/>
		 * <p>This form avoids superfluous string concatenation when the logger
		 * is disabled for the WARN level. However, this variant incurs the hidden (and relatively small) cost of
		 * creating an <code>Object[]</code> before invoking the method, even if this logger is disabled for WARN. The
		 * variants taking {@link #warn(String, Object) one} and {@link #warn(String, Object, Object) two} arguments
		 * exist solely in order to avoid this hidden cost.</p>
		 *
		 * @param format    the format string
		 * @param arguments a list of 3 or more arguments
		 */
		@Override
		public void warn(String format, Object... arguments) {
			if (delegate.isWarnEnabled()) {
				delegate.warn(sanitizeSpec(format), sanitize(arguments));
			}
		}

		/**
		 * Log a message at the WARN level according to the specified format and arguments.
		 * <p/>
		 * <p>This form avoids superfluous object creation when the logger
		 * is disabled for the WARN level. </p>
		 *
		 * @param format the format string
		 * @param arg1   the first argument
		 * @param arg2   the second argument
		 */
		@Override
		public void warn(String format, Object arg1, Object arg2) {
			if (delegate.isWarnEnabled()) {
				delegate.warn(sanitizeSpec(format), sanitize(arg1), sanitize(arg2));
			}
		}

		/**
		 * Log an exception (throwable) at the WARN level with an accompanying message.
		 *
		 * @param msg the message accompanying the exception
		 * @param t   the exception (throwable) to log
		 */
		@Override
		public void warn(String msg, Throwable t) {
			if (delegate.isWarnEnabled()) {
				delegate.warn(sanitizeSpec(msg), t);
			}
		}

		/**
		 * Similar to {@link #isWarnEnabled()} method except that the marker data is also taken into consideration.
		 *
		 * @param marker The marker data to take into consideration
		 * @return True if this Logger is enabled for the WARN level, false otherwise.
		 */
		@Override
		public boolean isWarnEnabled(Marker marker) {
			return delegate.isWarnEnabled(marker);
		}

		/**
		 * Log a message with the specific Marker at the WARN level.
		 *
		 * @param marker The marker specific to this log statement
		 * @param msg    the message string to be logged
		 */
		@Override
		public void warn(Marker marker, String msg) {
			if (delegate.isWarnEnabled()) {
				delegate.warn(marker, sanitize(msg));
			}
		}

		/**
		 * This method is similar to {@link #warn(String, Object)} method except that the marker data is also taken into
		 * consideration.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param format the format string
		 * @param arg    the argument
		 */
		@Override
		public void warn(Marker marker, String format, Object arg) {
			if (delegate.isWarnEnabled()) {
				delegate.warn(marker, sanitizeSpec(format), sanitize(arg));
			}
		}

		/**
		 * This method is similar to {@link #warn(String, Object, Object)} method except that the marker data is also
		 * taken into consideration.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param format the format string
		 * @param arg1   the first argument
		 * @param arg2   the second argument
		 */
		@Override
		public void warn(Marker marker, String format, Object arg1, Object arg2) {
			if (delegate.isWarnEnabled()) {
				delegate.warn(marker, sanitizeSpec(format), sanitize(arg1), sanitize(arg2));
			}
		}

		/**
		 * This method is similar to {@link #warn(String, Object...)} method except that the marker data is also taken
		 * into consideration.
		 *
		 * @param marker    the marker data specific to this log statement
		 * @param format    the format string
		 * @param arguments a list of 3 or more arguments
		 */
		@Override
		public void warn(Marker marker, String format, Object... arguments) {
			if (delegate.isWarnEnabled()) {
				delegate.warn(marker, sanitizeSpec(format), sanitize(arguments));
			}
		}

		/**
		 * This method is similar to {@link #warn(String, Throwable)} method except that the marker data is also taken
		 * into consideration.
		 *
		 * @param marker the marker data for this log statement
		 * @param msg    the message accompanying the exception
		 * @param t      the exception (throwable) to log
		 */
		@Override
		public void warn(Marker marker, String msg, Throwable t) {
			if (delegate.isWarnEnabled()) {
				delegate.warn(marker, sanitize(msg), sanitizeSpec(t));
			}
		}

		/**
		 * Is the logger instance enabled for the ERROR level?
		 *
		 * @return True if this Logger is enabled for the ERROR level, false otherwise.
		 */
		@Override
		public boolean isErrorEnabled() {
			return delegate.isErrorEnabled();
		}

		/**
		 * Log a message at the ERROR level.
		 *
		 * @param msg the message string to be logged
		 */
		@Override
		public void error(String msg) {
			if (delegate.isErrorEnabled()) {
				delegate.error(sanitize(msg));
			}
		}

		/**
		 * Log a message at the ERROR level according to the specified format and argument.
		 * <p/>
		 * <p>This form avoids superfluous object creation when the logger
		 * is disabled for the ERROR level. </p>
		 *
		 * @param format the format string
		 * @param arg    the argument
		 */
		@Override
		public void error(String format, Object arg) {
			if (delegate.isErrorEnabled()) {
				delegate.error(sanitizeSpec(format), sanitize(arg));
			}
		}

		/**
		 * Log a message at the ERROR level according to the specified format and arguments.
		 * <p/>
		 * <p>This form avoids superfluous object creation when the logger
		 * is disabled for the ERROR level. </p>
		 *
		 * @param format the format string
		 * @param arg1   the first argument
		 * @param arg2   the second argument
		 */
		@Override
		public void error(String format, Object arg1, Object arg2) {
			if (delegate.isErrorEnabled()) {
				delegate.error(sanitizeSpec(format), sanitize(arg1), sanitize(arg2));
			}
		}

		/**
		 * Log a message at the ERROR level according to the specified format and arguments.
		 * <p/>
		 * <p>This form avoids superfluous string concatenation when the logger
		 * is disabled for the ERROR level. However, this variant incurs the hidden (and relatively small) cost of
		 * creating an <code>Object[]</code> before invoking the method, even if this logger is disabled for ERROR. The
		 * variants taking {@link #error(String, Object) one} and {@link #error(String, Object, Object) two} arguments
		 * exist solely in order to avoid this hidden cost.</p>
		 *
		 * @param format    the format string
		 * @param arguments a list of 3 or more arguments
		 */
		@Override
		public void error(String format, Object... arguments) {
			if (delegate.isErrorEnabled()) {
				delegate.error(sanitizeSpec(format), sanitize(arguments));
			}
		}

		/**
		 * Log an exception (throwable) at the ERROR level with an accompanying message.
		 *
		 * @param msg the message accompanying the exception
		 * @param t   the exception (throwable) to log
		 */
		@Override
		public void error(String msg, Throwable t) {
			if (delegate.isErrorEnabled()) {
				delegate.error(sanitizeSpec(msg), sanitizeSpec(t));
			}
		}

		/**
		 * Similar to {@link #isErrorEnabled()} method except that the marker data is also taken into consideration.
		 *
		 * @param marker The marker data to take into consideration
		 * @return True if this Logger is enabled for the ERROR level, false otherwise.
		 */
		@Override
		public boolean isErrorEnabled(Marker marker) {
			return delegate.isErrorEnabled(marker);
		}

		/**
		 * Log a message with the specific Marker at the ERROR level.
		 *
		 * @param marker The marker specific to this log statement
		 * @param msg    the message string to be logged
		 */
		@Override
		public void error(Marker marker, String msg) {
			if (delegate.isErrorEnabled()) {
				delegate.error(marker, sanitize(msg));
			}
		}

		/**
		 * This method is similar to {@link #error(String, Object)} method except that the marker data is also taken
		 * into consideration.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param format the format string
		 * @param arg    the argument
		 */
		@Override
		public void error(Marker marker, String format, Object arg) {
			if (delegate.isErrorEnabled()) {
				delegate.error(marker, sanitizeSpec(format), sanitize(arg));
			}
		}

		/**
		 * This method is similar to {@link #error(String, Object, Object)} method except that the marker data is also
		 * taken into consideration.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param format the format string
		 * @param arg1   the first argument
		 * @param arg2   the second argument
		 */
		@Override
		public void error(Marker marker, String format, Object arg1, Object arg2) {
			if (delegate.isErrorEnabled()) {
				delegate.error(marker, sanitizeSpec(format), sanitize(arg1), sanitize(arg2));
			}
		}

		/**
		 * This method is similar to {@link #error(String, Object...)} method except that the marker data is also taken
		 * into consideration.
		 *
		 * @param marker    the marker data specific to this log statement
		 * @param format    the format string
		 * @param arguments a list of 3 or more arguments
		 */
		@Override
		public void error(Marker marker, String format, Object... arguments) {
			if (delegate.isErrorEnabled()) {
				delegate.error(marker, sanitizeSpec(format), sanitize(arguments));
			}
		}

		/**
		 * This method is similar to {@link #error(String, Throwable)} method except that the marker data is also taken
		 * into consideration.
		 *
		 * @param marker the marker data specific to this log statement
		 * @param msg    the message accompanying the exception
		 * @param t      the exception (throwable) to log
		 */
		@Override
		public void error(Marker marker, String msg, Throwable t) {
			if (delegate.isErrorEnabled()) {
				delegate.error(marker, sanitize(msg), sanitizeSpec(t));
			}
		}

		// LOCAL UTILS
		protected String sanitize(String s) {
			return maySanitize(sanitizationLevel, s);
		}

		protected Object sanitize(Object o) {
			return maySanitize(sanitizationLevel, o);
		}

		protected Object[] sanitize(Object... a) {
			return maySanitize(sanitizationLevel, a);
		}

		protected String sanitizeSpec(String s) {
			return (sanitizationLevel == SanitizationLevel.FULL_SANITIZATION)
					? sanitize(s)
					: s;
		}

		protected Object sanitizeSpec(Throwable t) {
			return (sanitizationLevel == SanitizationLevel.FULL_SANITIZATION)
					? sanitizeSpec(t.toString())
					: t;
		}

		// LOCAL STATIC UTILS
		public static String maySanitize(SanitizationLevel sanitizationLevel, String s) {
			return sanitizationLevel == SanitizationLevel.NO_SANITIZATION
					? s
					: sanitizeString(s);
		}

		public static Object[] maySanitize(SanitizationLevel sanitizationLevel, Object... arguments) {
			return sanitizationLevel == SanitizationLevel.NO_SANITIZATION
					? arguments
					: Stream.of(arguments).map(a -> maySanitize(sanitizationLevel, a)).toArray();
		}

		public static Object maySanitize(SanitizationLevel sanitizationLevel, Object o) {
			switch (sanitizationLevel) {
				case BASIC_SANITIZATION:
					if (o instanceof Throwable) {
						return o;
					} else {
						return sanitizeString(o.toString());
					}
				case FULL_SANITIZATION:
					return sanitizeString(o.toString());
				case NO_SANITIZATION:
				default:
					return o;
			}
		}

		private static String sanitizeString(String s) {
			return s.replace("\n", "_").replace("\r", "_").replace("\t", "_");
		}
	}

	/**
	 * Shortcut for building ApsLogger objects with BASIC_SANITIZATION level
	 */
	public interface SanitizedLogger {

		static EntLogger from(Logger logger) {
			return EntLogFactory.from(SanitizationLevel.BASIC_SANITIZATION, logger);
		}
	}

	/**
	 * Standard Entando Logger factory
	 */
	public interface EntLogFactory {

		static EntLogger getSantiziedLogger(Class<?> clazz) {
			return from(SanitizationLevel.BASIC_SANITIZATION, LoggerFactory.getLogger(clazz));
		}

		static EntLogger getLogger(SanitizationLevel sanitizationLevel, Class<?> clazz) {
			return from(sanitizationLevel, LoggerFactory.getLogger(clazz));
		}

		static EntLogger from(SanitizationLevel sanitizationLevel, Logger logger) {
			return new EntLogger(logger, sanitizationLevel);
		}
	}

	private EntLogging() {
	}
}
