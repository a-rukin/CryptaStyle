package com.airwhip.cryptastyle.misc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Whiplash on 17.03.14.
 */
public class Internet {
    private static Internet ourInstance = new Internet();

    private Internet() {
    }

    public static Internet getInstance() {
        return ourInstance;
    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
