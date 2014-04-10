package com.airwhip.cryptastyle.misc;

/**
 * Created by Whiplash on 18.03.14.
 */
public class XmlHelper {
    private static final String ERROR_SYMBOLS = "[&\\?<>\"\'\\{}@]";

    private XmlHelper() {
    }

    public static String removeXmlBadSymbols(String str) {
        return str.replaceAll(ERROR_SYMBOLS, " ");
    }
}
