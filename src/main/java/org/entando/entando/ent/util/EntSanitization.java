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

/**
 * Simple Sanitization helper
 */
public interface EntSanitization {

	interface EntSanitizer<T> {

		String sanitize(T value);
	}

	class JavaSecS5145<T> implements EntSanitizer<T> {

		public String sanitize(T value) {
			return fixJavaSecS5145("" + value);
		}
	}

	class NopSanitizer<T> implements EntSanitizer<T> {

		public String sanitize(T value) {
			return "" + value;
		}
	}

	static String fixJavaSecS5145(String s) {
		return (s == null) ? null : s.replace("\n", "_").replace("\r", "_").replace("\t", "_");
	}
}
