package org.entando.entando.aps.util;

public class UrlUtils {

    /**
     * Return true if the proper configuration var is set to true:
     * <ul>
     *     <li>ENTANDO_APP_USE_TLS must be used</li>
     *     <li>FORCE_HTTPS deprecated but kept for backport compatibility</li>
     * </ul>
     */
    public static boolean determineForceHttps() {
        return Boolean.parseBoolean(System.getenv("ENTANDO_APP_USE_TLS")) ||
                Boolean.parseBoolean(System.getenv("FORCE_HTTPS"));
    }

}
