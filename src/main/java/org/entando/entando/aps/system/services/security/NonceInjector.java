package org.entando.entando.aps.system.services.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NonceInjector {
    private static final String LIB_INJECTION = "<#assign wp=JspTaglibs[ \"/aps-core\"]>\n";
    private static final String NONCE_INJECTION = "nonce=\"<@wp.cspNonce />\"";
    private static final Pattern LIB_REGEX = Pattern.compile(
            "<#assign[\\t\\r\\n\\s]+wp=JspTaglibs\\[[\\t\\r\\n\\s]*\"\\/aps-core\"[\\t\\r\\n\\s]*\\]>[.\\t\\r\\n\\s]*");
    private static final Pattern SCRIPT_REGEX = Pattern.compile( // NOSONAR (added {0,8} to reduce risk of "catastrophic backtracking" situations)
            "(<script)(?:[\\t\\r\\n\\s]+([^\"=]*)=\"([^\"]*)\"[\\t\\r\\n\\s]*){0,8}([^>]*>)");

    private NonceInjector() {
        //Not used
    }

    public static String process(String source) {
        if (source == null) return null;

        Matcher scriptsMatcher = SCRIPT_REGEX.matcher(source);
        StringBuffer sb = new StringBuffer();
        boolean hasNonce = false;

        while (scriptsMatcher.find()) {
            hasNonce = true;
            String replacement;
            if (hasNonce(scriptsMatcher)) {
                replacement = "$0";
            } else {
                replacement = "$1 " + NONCE_INJECTION;
                if (scriptsMatcher.groupCount() == 4 && scriptsMatcher.group(2) != null && scriptsMatcher.group(3) != null) {
                    replacement += " $2=\"$3\" ";
                }
                replacement += "$4";
            }

            scriptsMatcher.appendReplacement(sb, replacement);
        }
        String processed = scriptsMatcher.appendTail(sb).toString();

        Matcher libMatcher = LIB_REGEX.matcher(processed);
        if (hasNonce && !libMatcher.find()) {
            processed = LIB_INJECTION + processed;
        }

        return processed;
    }

    public static boolean hasNonce(Matcher matcher) {
        return matcher.groupCount() == 4 && matcher.group(2) != null && matcher.group(2).equals("nonce");
    }

}
