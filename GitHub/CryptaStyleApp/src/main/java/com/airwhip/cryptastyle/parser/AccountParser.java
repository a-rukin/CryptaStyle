package com.airwhip.cryptastyle.parser;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.airwhip.cryptastyle.misc.Constants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Whiplash on 17.03.14.
 */
public class AccountParser {

    private Context context;

    private HashSet<String> accountStorage = new HashSet<>();

    public AccountParser(Context context, StringBuilder xml) {
        this.context = context;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(xml.toString()));
            int eventType = xpp.getEventType();
            String currentTag = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTag = xpp.getName();
                        break;
                    case XmlPullParser.END_TAG:
                        currentTag = "";
                        break;
                    case XmlPullParser.TEXT:
                        if (currentTag.equals("type")) {
                            accountStorage.add(xpp.getText().toLowerCase());
                        }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            Log.e(Constants.ERROR_TAG, e.getMessage());
        }

        for (String str : accountStorage) {
            Log.d(Constants.DEBUG_TAG, str);
        }
    }

    public long getWeight(int xml) {
        long resultWeight = 0;
        try {
            XmlResourceParser xrp = context.getResources().getXml(xml);

            int eventType = xrp.getEventType();

            String currentTag = "";
            long currentWeight = 0;
            boolean isCorrectTag = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTag = xrp.getName();
                        if (currentTag.equals("account")) {
                            isCorrectTag = true;
                        }
                        if (isCorrectTag && xrp.getName().equals("weight-array")) {
                            currentWeight = xrp.getAttributeIntValue(0, 0);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (isCorrectTag && currentTag.equals("item")) {
                            resultWeight += numberOfEntries(xrp.getText()) * currentWeight;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        currentTag = "";
                        if (isCorrectTag && xrp.getName().equals("account")) {
                            isCorrectTag = false;
                        }
                        break;
                }
                eventType = xrp.next();
            }

        } catch (XmlPullParserException | IOException | NullPointerException e) {
            Log.e(Constants.ERROR_TAG, e.getMessage());
        }
        return resultWeight;
    }

    private int numberOfEntries(String str) {
        str = str.toLowerCase();
        List<String> removeList = new ArrayList<>();
        for (String s : accountStorage) {
            if (s.contains(str)) {
                removeList.add(s);
            }
        }
        int result = removeList.size();
        for (String s : removeList) {
            accountStorage.remove(s);
        }
        return result;
    }

}
