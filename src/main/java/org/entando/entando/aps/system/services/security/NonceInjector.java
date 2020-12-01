package org.entando.entando.aps.system.services.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NonceInjector {
    private static final String NONCE_INJECTION = "nonce=\"<@wp.cspNonce />\"";
    private static final Pattern SCRIPT_REGEX = Pattern.compile(
            "(<script)(?:[\\t\\r\\n\\s]+([^\"=]*)=\"([^\"]*)\"[\\t\\r\\n\\s]*)*([^>]*>)");

    private NonceInjector() {
        //Not used
    }

    public static String process(String source) {
        if (source == null) return null;

        Matcher matcher = SCRIPT_REGEX.matcher(source);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String replacement;
            if (hasNonce(matcher)) {
                replacement = "$0";
            } else {
                replacement = "$1 " + NONCE_INJECTION;
                if (matcher.groupCount() == 4 && matcher.group(2) != null && matcher.group(3) != null) {
                    replacement += " $2=\"$3\" ";
                }
                replacement += "$4";
            }

            matcher.appendReplacement(sb, replacement);
        }
        return matcher.appendTail(sb).toString();
    }

    public static boolean hasNonce(Matcher matcher) {
        return matcher.groupCount() == 4 && matcher.group(2) != null && matcher.group(2).equals("nonce");
    }

}
