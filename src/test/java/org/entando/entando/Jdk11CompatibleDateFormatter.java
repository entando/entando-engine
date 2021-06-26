package org.entando.entando;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class was introduced to accommodate inconsistencies between Java 8 and Java 11 in the way they implemented
 * the medium date style formatting in the Italian locale. Java 8 uses dashes to separate words, Java 11 uses spaces
 */
public class Jdk11CompatibleDateFormatter {
    public static String formatMediumDate(String input) {
        try {
            Date date = new SimpleDateFormat("dd-MMM-yyyy", Locale.forLanguageTag("it")).parse(input);
            return DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.forLanguageTag("it")).format(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
