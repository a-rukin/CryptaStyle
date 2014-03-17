package com.airwhip.cryptastyle.parser;

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
    private long[] weight = new long[11];

    public void add(int position, long value) {
        weight[position] += value;
    }

    public boolean isUFO() {
        // TODO check it
        return true;
    }

    public double[] get() {
        // TODO get result
        return new double[0];
    }
}
