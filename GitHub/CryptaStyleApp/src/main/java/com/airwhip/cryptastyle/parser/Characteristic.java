package com.airwhip.cryptastyle.parser;

import android.util.Log;

import com.airwhip.cryptastyle.misc.Constants;

/**
 * Created by Whiplash on 17.03.14.
 */
public class Characteristic {

    /*  0 - Geek
        1 - Housewife
        2 - Trendy
        3 - Student
        4 - Child
        5 - Traveler
        6 - Anime addicted
        7 - Music lover
        8 - Stalin
        9 - Cat lady
        10 - Dog lover
        Extra - UFO */
    private long[] weight = new long[Constants.xmls.length];

    public void add(int position, long value) {
        weight[position] += value;
    }

    public void addAll(long[] values) {
        if (values.length != weight.length) {
            Log.e(Constants.ERROR_TAG, "INCORRECT ARRAY SIZE");
        }
        for (int i = 0; i < values.length; i++) {
            weight[i] += values[i];
        }
    }

    public boolean isUFO() {
        // TODO check it
        return true;
    }

    public long[] get() {
        long[] clone = new long[weight.length];
        for (int i = 0; i < weight.length; i++) {
            clone[i] = weight[i];
        }
        return clone;
    }
}
