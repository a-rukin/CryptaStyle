package com.airwhip.cryptastyle.getters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.airwhip.cryptastyle.misc.XmlHalper;

import java.util.List;

/**
 * Created by Whiplash on 05.03.14.
 */
public class ApplicationInformation {

    private static final String MAIN_TAG_BEGIN = "<application>\n";
    private static final String MAIN_TAG_END = "</application>\n";

    private static final String NAME_TAG_BEGIN = "\t<name>";
    private static final String NAME_TAG_END = "</name>\n";

    public static StringBuilder get(Context context) {
        StringBuilder result = new StringBuilder(MAIN_TAG_BEGIN);

        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            result.append(NAME_TAG_BEGIN + XmlHalper.removeXmlBadSymbols(packageInfo.packageName) + NAME_TAG_END);
        }

        return result.append(MAIN_TAG_END);
    }

}
