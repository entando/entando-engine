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
package com.agiletec.aps.system;

import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

/**
 * Utility class for system logger
 *
 * @author E.Santoboni
 */
public class ApsSystemUtils {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(ApsSystemUtils.class);

    private static final boolean ENABLE_DIRECT_STDOUT_TRACE =
            ("" + System.getProperty("org.entando.enableDirectStdoutTrace")).equals("true");
    
    private ApsSystemUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static org.slf4j.Logger getLogger() {
        return logger;
    }

    /**
     * Draw an exception on the context logger.
     *
     * @param t The exception to trace
     * @param caller The caller class
     * @param methodName The method in which the error occurred.
     * @param message The message to include
     */
    public static void logThrowable(Throwable t, Object caller,
            String methodName, String message) {
        String className = null;
        if (caller != null) {
            className = caller.getClass().getName();
        }
        logger.error("{} in {}.{}", message, className, methodName, t);
    }

    /**
     * Draw an exception on the context logger.
     *
     * @param t The exception to trace
     * @param caller The caller class
     * @param methodName The method in which the error occurred.
     */
    public static void logThrowable(Throwable t, Object caller, String methodName) {
        logThrowable(t, caller, methodName, "Exception");
    }

    public static boolean directStdoutTrace(String str) {
        return directStdoutTrace(str, false);
    }

    public static boolean directStdoutTrace(String str, boolean force) {
        if (ENABLE_DIRECT_STDOUT_TRACE || force) {
            System.out.println(str);    //NOSONAR
            return true;
        } else {
            return false;
        }
    }

}
