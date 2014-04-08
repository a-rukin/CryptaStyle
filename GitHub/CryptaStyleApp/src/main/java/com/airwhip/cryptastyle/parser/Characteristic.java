package com.airwhip.cryptastyle.parser;

import android.util.Log;

import com.airwhip.cryptastyle.misc.Constants;

/**
 * Created by Whiplash on 17.03.14.
 */
public class Characteristic {

    private static StringBuilder xml = new StringBuilder();

    private static double[] weight = new double[Constants.xmls.length];
    private static double[] max = new double[Constants.xmls.length];

    private Characteristic() {
    }

    public static void addAll(double[] values, double[] maxValues) {
        if (values.length != weight.length) {
            Log.e(Constants.ERROR_TAG, "INCORRECT ARRAY SIZE");
        }
        for (int i = 0; i < values.length; i++) {
            weight[i] += values[i];
            max[i] += maxValues[i];
        }
    }

    public static void append(StringBuilder newXml) {
        xml.append(newXml);
    }

    public static boolean isUFO() {
        // TODO check it
        return false;
    }

    public static int get(int i) {
        return max[i] != 0 ? Math.min((int) (100. * weight[i] / max[i]), 100) : 0;
    }

    public static StringBuilder getXml() {
        return xml;
    }

    public static int size() {
        return weight.length;
    }
}
