package com.airwhip.cryptastyle.misc;

import com.airwhip.cryptastyle.R;

/**
 * Created by Whiplash on 17.03.14.
 */
public class Constants {
    public static final String ERROR_TAG = "ERROR_TAG";
    public static final String DEBUG_TAG = "DEBUG_TAG";

    public static final int[] xmls = {
            R.xml.geek,
            R.xml.housewife,
            R.xml.trendy,
            R.xml.student,
            R.xml.child,
            R.xml.traveler,
            R.xml.anime_addicted,
            R.xml.music_lover,
            R.xml.stalin,
            R.xml.cat_lady,
            R.xml.dog_lover};

    private static Constants ourInstance = new Constants();

    private Constants() {
    }

    public static Constants getInstance() {
        return ourInstance;
    }
}
