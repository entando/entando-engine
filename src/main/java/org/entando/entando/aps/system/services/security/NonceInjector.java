package org.entando.entando.aps.system.services.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NonceInjector {
    private static final String NONCE_INJECTION = "nonce=\"<@wp.cspNonce />\"";

    public static String process(String source) {
        if (source == null) return null;

        Pattern pattern = Pattern.compile("(<script)(?:[\\t\\r\\n\\s]+([^\"=]*)=\"([^\"]*)\"[\\t\\r\\n\\s]*)*([^>]*>)");
        Matcher matcher = pattern.matcher(source);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String replacement;
            if (hasNonce(matcher)) {
                //Already has nonce
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

    public static void main(String[] args) {
        System.out.println(NonceInjector.process(
                "<html><body>\n"
                        + "<script nonce=\"what\" key=\"value\" another_key=\"value\">\n"
                        + "<script   key=\"value\"  \t   another_key=\"value\">\n"
                        + "<script nonce=\"what\">\n"
                        + "<script key=\"value\" >\n"
                        + "<script >\n"
                        + "<script>\n"
                        + "</body></html>"));
    }

}
