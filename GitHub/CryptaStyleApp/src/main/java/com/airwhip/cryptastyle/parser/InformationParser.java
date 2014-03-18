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
import java.util.List;

/**
 * Created by Whiplash on 17.03.14.
 */
public class InformationParser {

    private static final String WEIGHT_ARRAY_TAG = "weight-array";
    private static final String ITEM_TAG = "item";
    private Context context;
    private ParserType type;
    private List<String> storage = new ArrayList<>();

    public InformationParser(Context context, StringBuilder xml, ParserType type) {
        this.context = context;
        this.type = type;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(xml.toString()));
            int eventType = xpp.getEventType();

            boolean isCorrectTag = false;
            StringBuilder text = new StringBuilder();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals(ITEM_TAG)) {
                            isCorrectTag = true;
                            text = new StringBuilder();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (xpp.getName().equals(ITEM_TAG)) {
                            isCorrectTag = false;
                            storage.add(text.toString());
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (isCorrectTag) {
                            text.append(xpp.getText().toLowerCase());
                        }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            Log.e(Constants.ERROR_TAG, e.getMessage() + " " + type.toString());
        }

//        for (String str : storage) {
//            Log.d(Constants.DEBUG_TAG, str);
//        }
    }

    public long[] getAllWeight() {
        long[] result = new long[Constants.xmls.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getWeight(Constants.xmls[i]);
        }
        return result;
    }

    public long getWeight(int xml) {
        long resultWeight = 0;
        boolean[] isUsed = new boolean[storage.size()];
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
                        if (currentTag.equals(type.toString())) {
                            isCorrectTag = true;
                        }
                        if (isCorrectTag && xrp.getName().equals(WEIGHT_ARRAY_TAG)) {
                            currentWeight = xrp.getAttributeIntValue(0, 0);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (isCorrectTag && currentTag.equals(ITEM_TAG)) {
                            resultWeight += numberOfEntries(xrp.getText(), isUsed) * currentWeight;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        currentTag = "";
                        if (isCorrectTag && xrp.getName().equals(type.toString())) {
                            isCorrectTag = false;
                        }
                        break;
                }
                eventType = xrp.next();
            }

        } catch (XmlPullParserException | IOException | NullPointerException e) {
            Log.e(Constants.ERROR_TAG, e.getMessage() + " " + type.toString());
        }
        return resultWeight;
    }

    private int numberOfEntries(String str, boolean[] isUsed) {
        str = str.toLowerCase();
        int result = 0;
        for (int i = 0; i < storage.size(); i++) {
            if (!isUsed[i] && storage.get(i).contains(str)) {
                isUsed[i] = true;
                result++;
            }
        }
        return result;
    }

    public enum ParserType {
        ACCOUNT, APPLICATION,
        HISTORY, BOOKMARKS,
        MUSIC;

        @Override
        public String toString() {
            switch (this) {
                case ACCOUNT:
                    return "account";
                case APPLICATION:
                    return "application";
                case HISTORY:
                    return "history";
                case BOOKMARKS:
                    return "bookmarks";
                case MUSIC:
                    return "music";
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

}
