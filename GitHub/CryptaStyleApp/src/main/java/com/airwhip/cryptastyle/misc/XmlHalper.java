package com.airwhip.cryptastyle.misc;

/**
 * Created by Whiplash on 18.03.14.
 */
public class XmlHalper {
    private static final String ERROR_SYMBOLS = "[&\\?<>\"\'\\{}@]";
    private static XmlHalper ourInstance = new XmlHalper();

    private XmlHalper() {
    }

    public static XmlHalper getInstance() {
        return ourInstance;
    }

    public static String removeXmlBadSymbols(String str) {
        return str.replaceAll(ERROR_SYMBOLS, " ");
    }
}
