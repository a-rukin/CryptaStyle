package com.airwhip.cryptastyle.parser;

import android.content.Context;
import android.util.Log;

import com.airwhip.cryptastyle.R;
import com.airwhip.cryptastyle.misc.Constants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;

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
                            accountStorage.add(xpp.getText());
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


        return 0;
    }
}
