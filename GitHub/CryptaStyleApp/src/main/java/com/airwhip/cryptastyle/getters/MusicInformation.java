package com.airwhip.cryptastyle.getters;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.airwhip.cryptastyle.misc.Constants;
import com.airwhip.cryptastyle.misc.XmlHelper;
import com.airwhip.cryptastyle.parser.Characteristic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Whiplash on 05.03.14.
 */
public class MusicInformation {

    private static final String IS_MUSIC = MediaStore.Audio.Media.IS_MUSIC + " != 0";
    private static final String[] PROJECTION = {MediaStore.Audio.Media.ARTIST};

    private static final String MAIN_TAG_BEGIN = "<music>\n";
    private static final String MAIN_TAG_END = "</music>\n";

    private static final String ITEM_TAG_BEGIN = "\t<item>\n";
    private static final String ITEM_TAG_END = "\t</item>\n";

    private static final String NAME_TAG_BEGIN = "\t\t<name>";
    private static final String NAME_TAG_END = "</name>\n";

    private static final String COUNT_TAG_BEGIN = "\t\t<count>";
    private static final String COUNT_TAG_END = "</count>\n";

    private static final String TOP_FANS = "http://ws.audioscrobbler.com/2.0/?method=artist.gettopfans&artist=";
    private static final String FANS_INFO = "http://ws.audioscrobbler.com/2.0/?method=user.getinfo&user=";

    private static final String OAUTH_KEY = "&api_key=aa3caf28fe64c9efa190bdd0d253a707&format=json";

    private static final int FANS_PER_ARTIST = 10;

    public static StringBuilder get(Context context) {
        StringBuilder result = new StringBuilder(MAIN_TAG_BEGIN);

        List<String> artistsList = new ArrayList<>();
        Map<String, Integer> artistsStorage = new HashMap<>();

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION, IS_MUSIC, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String artist = cursor.getString(0);
                if (!artistsStorage.containsKey(artist)) {
                    artistsList.add(artist);
                    artistsStorage.put(artist, 1);
                } else {
                    int count = artistsStorage.remove(artist);
                    artistsStorage.put(artist, count + 1);
                }
            }
        }

        Log.d(Constants.DEBUG_TAG, "GET MUSIC");

        Set<String> fans = new HashSet<>();
        Map<String, Integer> fansCount = new HashMap<>();
        for (String artistName : artistsList) {
            try {
                URLConnection connection = new URL(TOP_FANS + artistName.replace(" ", "%20") + OAUTH_KEY).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder jsonText = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonText.append(line);
                }

                JSONObject jsonObject = new JSONObject(jsonText.toString());
                JSONArray usersArray = jsonObject.getJSONObject("topfans").getJSONArray("user");
                for (int i = 0; i < FANS_PER_ARTIST && i < usersArray.length(); i++) {
                    String fanName = usersArray.getJSONObject(i).getString("name");
                    if (!fans.contains(fanName)) {
                        fans.add(fanName);
                        fansCount.put(fanName, artistsStorage.get(artistName));
                    } else {
                        int songs = fansCount.remove(fanName);
                        fansCount.put(fanName, songs + artistsStorage.get(artistName));
                    }
                }
            } catch (IOException e) {
                Log.e(Constants.ERROR_TAG, "Get information about fans failed because of IOException");
            } catch (JSONException e) {
                Log.e(Constants.ERROR_TAG, "Get information about fans failed because of JSONException");
            }
        }

        Log.d(Constants.DEBUG_TAG, "GET FANS");

        int index = 1;
        int maleCounter = 0, femaleCounter = 0;
        double sum = 0;
        int ageCounter = 0;

        for (String user : fans) {
            try {
                URLConnection connection = new URL(FANS_INFO + user.replace(" ", "%20") + OAUTH_KEY).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder jsonText = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonText.append(line);
                }

                JSONObject jsonObject = new JSONObject(jsonText.toString());
                String gender = jsonObject.getJSONObject("user").getString("gender");
                if (gender.equals("m")) {
                    maleCounter += fansCount.get(user);
                }
                if (gender.equals("f")) {
                    femaleCounter += fansCount.get(user);
                }

                String age = jsonObject.getJSONObject("user").getString("age");
                if (!age.equals("")) {
                    sum += Integer.parseInt(age) * fansCount.get(user);
                    ageCounter += fansCount.get(user);
                }
            } catch (IOException e) {
                Log.e(Constants.ERROR_TAG, "Get information about fans' gender failed because of IOException");
            } catch (JSONException e) {
                Log.e(Constants.ERROR_TAG, "Get information about fans' gender because of JSONException");
            }
            Log.d(Constants.DEBUG_TAG, "LOADING: " + (index++ / (double) fans.size() * 100));
        }

        if (maleCounter != 0 && femaleCounter != 0) {
            Characteristic.addMale((double) maleCounter / (maleCounter + femaleCounter));
            Characteristic.addFemale(1. - (double) maleCounter / (maleCounter + femaleCounter));
            Characteristic.addAge((int) Math.floor(sum / ageCounter));
        }

        for (String artist : artistsList) {
            result.append(ITEM_TAG_BEGIN);
            result.append(NAME_TAG_BEGIN + XmlHelper.removeXmlBadSymbols(artist) + NAME_TAG_END);
            result.append(COUNT_TAG_BEGIN + artistsStorage.get(artist) + COUNT_TAG_END);
            result.append(ITEM_TAG_END);
        }

        return result.append(MAIN_TAG_END);
    }

}
