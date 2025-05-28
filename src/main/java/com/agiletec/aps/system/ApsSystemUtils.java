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

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Utility class for system logger
 *
 * @author E.Santoboni
 */
public class ApsSystemUtils {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(ApsSystemUtils.class);
    private static final Marker startupMarker = MarkerFactory.getMarker("STARTUP");

    /**
     * Comma-separated list of feature flags tags that are enabled.
     * See {@link ApsSystemUtils#isTagEnabled} for details about the rules.
     */
    public static final String ENTANDO_FEATURE_FLAGS = System.getenv("ENTANDO_FEATURE_FLAGS");

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
    public static void logThrowable(Throwable t, Object caller, String methodName, String message) {
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

    public static void markedTrace(String str) {
        logger.info(startupMarker, str);
    }

    /**
     * ApsDeepDebug is a utility class for managing and printing deep debug messages.
     * This class provides methods to log messages conditionally based on specified tags,
     * leveraging the environment variable "ENTANDO_FF_DEEP_DEBUG" to control the output.
     */
    public static class ApsDeepDebug {
        public static final String FORCE_TAG_PREFIX = "::";
        private static final String SEP_LW = "▔▔▔▔▔▔▔▔▔▔";
        private static final String SEP_UP = "▁▁▁▁▁▁▁▁▁▁";
        private static final String SEP_FL = "▒";
        public static final String ENTANDO_FF_DEEP_DEBUG = System.getenv("ENTANDO_FF_DEEP_DEBUG");

        /**
         * Prints - if allowed by the implicit tag "print" - a single deep debug message
         */
        public static boolean print(String message) {
            return print(null, "", message);
        }

        /**
         * Prints - if allowed by the given tag - a single deep debug message
         */
        public static boolean print(String tag, String message) {
            return print(tag, "", message);
        }

        /**
         * Prints - if allowed by the given tag - multiple deep messages
         */
        public static boolean print(String tag, String prefix, String... messages) {
            return print(tag, prefix, false, messages);
        }

        /**
         * Prints - if allowed by the given tag - multiple deep messages
         */
        public synchronized static boolean print(String tag, String prefix, boolean suppressTitle, String... messages) {
            if (isTagEnabled(tag)) {
                if (prefix == null) prefix = "├ ";
                if (tag == null) suppressTitle = true;

                try {
                    StringBuilder sb = new StringBuilder();
                    if (!suppressTitle) addHeader(tag, sb);
                    for (String str : messages) {
                        if (!suppressTitle) {
                            sb.append(prefix).append(" ");
                        }
                        sb.append(str).append("\n");
                    }
                    if (!suppressTitle) {
                        addFooter(sb);
                    }
                    System.out.println(sb);
                } catch (Throwable t) {
                    logger.warn(String.format("Error printing the deep debug message"), t);
                }
                return true;
            } else {
                return false;
            }
        }

        public static void printHttpRequest(String tag, HttpServletRequest request) {
            if (isTagEnabled(tag)) {
                Enumeration<String> headerNames = request.getHeaderNames();
                StringBuilder sb = new StringBuilder();
                addHeader(tag, sb).append("~ ").append(request.getRequestURL()).append("\n");
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    Enumeration<String> headerValues = request.getHeaders(headerName);
                    while (headerValues.hasMoreElements()) {
                        String headerValue = headerValues.nextElement();
                        sb.append("~ ").append(headerName).append(": ").append(headerValue).append("\n");
                    }
                }
                addFooter(sb);
                print(sb.toString());
            }
        }

        private static void addFooter(StringBuilder s) {
            s.append(SEP_LW + SEP_LW + SEP_LW + SEP_LW + SEP_LW + SEP_LW + SEP_LW + SEP_LW + SEP_LW + SEP_LW);
        }

        private static StringBuilder addHeader(String title, StringBuilder s) {
            return s.append("\n" + SEP_UP + SEP_UP + SEP_UP + SEP_UP + SEP_UP + SEP_UP + SEP_UP + SEP_UP + SEP_UP + SEP_UP + "\n")
                    .append(title).append("\n");
        }

        /**
         * Checks if a tag is enabled according to ENTANDO_FF_DEEP_DEBUG
         * Check also {@link ApsSystemUtils#isTagEnabled}
         */
        public static boolean isTagEnabled(String tag) {
            return ApsSystemUtils.isTagEnabled(getDeepDebugFF(), tag);
        }

        /**
         * Prints a feature flare, which is a signal that a feature was enabled
         */
        public static void printFeatureFlare(String tag, String flareName, boolean alwaysPrint) {
            String sep = SEP_FL.repeat(tag.length() + 4 + 4);
            print(
                    ((alwaysPrint) ? FORCE_TAG_PREFIX : "") + "FLARE" + ((flareName != null) ? ":" + tag : ""),
                    null,
                    true,
                    "", sep, SEP_FL + SEP_FL + SEP_FL + " " + tag + " " + SEP_FL + SEP_FL + SEP_FL, sep, ""
            );
        }

        public static void printFeatureFlare(String flareName) {
            printFeatureFlare(null, flareName, false);
        }
    }

    public static String getEnv(String name, String def) {
        String res = System.getenv(name);
        return (res != null) ? res : def;
    }

    public static boolean getEnvFlag(String name, boolean def) {
        String res = System.getenv(name);
        return res != null ? Boolean.parseBoolean(res) : def;
    }

    /**
     * Checks if a FEAURE is enabled according to ENTANDO_FF_TAGS
     * Check also {@link ApsSystemUtils#isTagEnabled}
     */
    public static boolean isFeatureEnabled(String tag) {
        return ApsSystemUtils.isTagEnabled(getFeatureFlags(), tag);
    }

    /**
     * Checks if a tag is enabled.
     * <pre>
     * A tag is enabled if "enabledTags" lists it or one of its subtags.
     * A tag is divided in subtags by the char ":"</p>
     *
     * For instance, if the tag "A-FLAG" is present in enabledTags it would enable:
     * - The tag "A-FLAG"
     * - The tags like "A-FLAG:SOMETHING" (note that the subtags order doesn't matter)
     * </pre>
     */
    public static boolean isTagEnabled(String enabledTags, String tag) {
        if (enabledTags == null) return false;
        if (enabledTags.equals("*") || enabledTags.equals("true")) return true;
        if (tag == null) return false;
        enabledTags = "," + enabledTags + ",";
        if (enabledTags.contains("," + tag + ",")) return true;
        for (String subtag : tag.split(":")) {
            if (enabledTags.contains("," + subtag + ",")) return true;
        }
        return false;
    }

    /* Mockable getter of the constant */
    public static String getFeatureFlags() {
        return ENTANDO_FEATURE_FLAGS;
    }

    static public String getDeepDebugFF() {
        return ApsDeepDebug.ENTANDO_FF_DEEP_DEBUG;
    }

}
