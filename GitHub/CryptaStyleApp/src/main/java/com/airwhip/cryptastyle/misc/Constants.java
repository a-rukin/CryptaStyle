package com.airwhip.cryptastyle.misc;

import com.airwhip.cryptastyle.R;

/**
 * Created by Whiplash on 17.03.14.
 */
public class Constants {
    public static final String ERROR_TAG = "ERROR_TAG";
    public static final String DEBUG_TAG = "DEBUG_TAG";

    private static Constants ourInstance = new Constants();

    private Constants() {
    }

    public static Constants getInstance() {
        return ourInstance;
    }
}
