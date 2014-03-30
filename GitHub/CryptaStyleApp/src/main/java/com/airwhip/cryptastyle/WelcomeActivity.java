package com.airwhip.cryptastyle;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.airwhip.cryptastyle.anim.Fade;
import com.airwhip.cryptastyle.anim.Move;
import com.airwhip.cryptastyle.anim.Spin;
import com.airwhip.cryptastyle.getters.AccountInformation;
import com.airwhip.cryptastyle.getters.MusicInformation;
import com.airwhip.cryptastyle.misc.Constants;
import com.airwhip.cryptastyle.misc.CustomizeArrayAdapter;
import com.airwhip.cryptastyle.misc.Internet;
import com.airwhip.cryptastyle.parser.Characteristic;
import com.airwhip.cryptastyle.parser.InformationParser;


public class WelcomeActivity extends Activity {

    private FrameLayout startLayout;
    private ScrollView resultView;

    private ImageButton circle;
    private TextView startText;
    private TextView tipText;

    private ImageView plugImage;
    private ImageView socketImage;

    private ListView otherResults;

    private Characteristic characteristic = new Characteristic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        startLayout = (FrameLayout) findViewById(R.id.startLayout);
        resultView = (ScrollView) findViewById(R.id.resultView);

        circle = (ImageButton) findViewById(R.id.circle);
        startText = (TextView) findViewById(R.id.startText);
        tipText = (TextView) findViewById(R.id.tipText);

        plugImage = (ImageView) findViewById(R.id.plugImage);
        socketImage = (ImageView) findViewById(R.id.socketImage);

        otherResults = (ListView) findViewById(R.id.otherResults);

        circle.setOnClickListener(new StartButtonClick(ProgramState.START));
    }

    private enum ProgramState {
        START,
        NO_INTERNET
    }

    private class StartButtonAnimation implements Animation.AnimationListener {

        private ProgramState state;

        public StartButtonAnimation(ProgramState state) {
            this.state = state;
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            switch (state) {
                case START:
                    circle.setOnClickListener(null);
                    new ImageLoader().execute();
                    break;
                case NO_INTERNET:
                    plugImage.setAlpha(1f);
                    socketImage.setAlpha(1f);
                    findViewById(R.id.noInternetText).setAlpha(1f);
                    tipText.setText(getString(R.string.check_internet));
                    tipText.setAlpha(1f);
                    circle.setOnClickListener(new StartButtonClick(ProgramState.NO_INTERNET));
                    break;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    private class StartButtonClick implements View.OnClickListener {

        private ProgramState state;

        public StartButtonClick(ProgramState state) {
            this.state = state;
        }

        @Override
        public void onClick(View v) {
            Animation fadeOut = new Fade(startText, 0f);
            switch (state) {
                case START:
                    if (Internet.checkInternetConnection(getApplicationContext())) {
                        fadeOut.setAnimationListener(new StartButtonAnimation(ProgramState.START));
                        startText.startAnimation(fadeOut);
                    } else {
                        fadeOut.setAnimationListener(new StartButtonAnimation(ProgramState.NO_INTERNET));
                        startText.startAnimation(fadeOut);
                    }
                    tipText.setAlpha(0);
                    break;
                case NO_INTERNET:
                    if (!Internet.checkInternetConnection(getApplicationContext())) {
                        plugImage.startAnimation(new Move(-18, 18, false));
                        socketImage.startAnimation(new Move(18, -18, false));
                    } else {
                        plugImage.startAnimation(new Fade(plugImage, 0f));
                        socketImage.startAnimation(new Fade(socketImage, 0f));
                        findViewById(R.id.noInternetText).startAnimation(new Fade(findViewById(R.id.noInternetText), 0f));
                        tipText.startAnimation(new Fade(tipText, 0f));
                        fadeOut.setAnimationListener(new StartButtonAnimation(ProgramState.START));
                        circle.startAnimation(fadeOut);
                    }
            }

        }
    }

    private class ImageLoader extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            circle.setImageResource(R.drawable.loading_circle);
            circle.startAnimation(new Spin());
        }

        @Override
        protected Void doInBackground(Void... params) {
            publishProgress(0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Animation fadeIn = new Fade(startText, 1);
                    fadeIn.setDuration(200);
                    startText.startAnimation(fadeIn);
                }
            });

            InformationParser parser = new InformationParser(getApplicationContext(), AccountInformation.get(getApplicationContext()), InformationParser.ParserType.ACCOUNT);
            characteristic.addAll(parser.getAllWeight());
            publishProgress(20);
//            parser = new InformationParser(getApplicationContext(), ApplicationInformation.get(getApplicationContext()), InformationParser.ParserType.APPLICATION);
//            characteristic.addAll(parser.getAllWeight());
//            publishProgress(40);
//            parser = new InformationParser(getApplicationContext(), BrowserInformation.getHistory(getApplicationContext()), InformationParser.ParserType.HISTORY);
//            characteristic.addAll(parser.getAllWeight());
//            publishProgress(60);
//            parser = new InformationParser(getApplicationContext(), BrowserInformation.getBookmarks(getApplicationContext()), InformationParser.ParserType.BOOKMARKS);
//            characteristic.addAll(parser.getAllWeight());
//            publishProgress(80);
            parser = new InformationParser(getApplicationContext(), MusicInformation.get(getApplicationContext()), InformationParser.ParserType.MUSIC);
            characteristic.addAll(parser.getAllWeight());
            publishProgress(100);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            fillResult();
            resultView.setAlpha(1);
            startLayout.setVisibility(View.GONE);
        }

        private void fillResult() {
            int max = 0;
            for (int i = 0; i < characteristic.size(); i++) {
                if (characteristic.get(i) > characteristic.get(max)) {
                    max = i;
                }
            }
            ((TextView) findViewById(R.id.youAreText)).setText(getResources().getStringArray(R.array.types)[max].toUpperCase());
            ((ImageView) findViewById(R.id.avatar)).setImageResource(Constants.imgs[max]);
            otherResults.setFocusable(false);

            String[] types = new String[characteristic.size() - 1];
            int[] progress = new int[characteristic.size() - 1];
            for (int i = 0; i < characteristic.size(); i++) {
                if (i < max) {
                    types[i] = getResources().getStringArray(R.array.types)[i];
                    progress[i] = 0; // TODO get normal result
                }
                if (i > max) {
                    types[i - 1] = getResources().getStringArray(R.array.types)[i];
                    progress[i - 1] = 0; // TODO get normal result
                }
            }
            progress[4] = 20;
            progress[3] = 10;
            progress[5] = 100;
            ArrayAdapter<String> adapter = new CustomizeArrayAdapter(getApplicationContext(), types, progress);
            otherResults.setAdapter(adapter);

            int totalHeight = otherResults.getPaddingTop() + otherResults.getPaddingBottom();
            for (int i = 0; i < adapter.getCount(); i++) {
                View item = adapter.getView(i, null, otherResults);
                if (item != null) {
                    if (item instanceof ViewGroup) {
                        item.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                    item.measure(0, 0);
                    totalHeight += item.getMeasuredHeight();
                }
            }

            ViewGroup.LayoutParams params = otherResults.getLayoutParams();
            if (params != null) {
                params.height = totalHeight + (otherResults.getDividerHeight() * (adapter.getCount() - 1));
                otherResults.setLayoutParams(params);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            final int progress = values[0];

            startText.setText(String.valueOf(progress) + "%");
        }
    }
}
