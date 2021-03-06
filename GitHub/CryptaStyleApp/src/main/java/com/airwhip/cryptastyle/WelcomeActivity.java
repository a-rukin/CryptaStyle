package com.airwhip.cryptastyle;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airwhip.cryptastyle.anim.Fade;
import com.airwhip.cryptastyle.anim.Move;
import com.airwhip.cryptastyle.anim.Spin;
import com.airwhip.cryptastyle.getters.AccountInformation;
import com.airwhip.cryptastyle.getters.ApplicationInformation;
import com.airwhip.cryptastyle.getters.BrowserInformation;
import com.airwhip.cryptastyle.getters.MusicInformation;
import com.airwhip.cryptastyle.getters.SMSInformation;
import com.airwhip.cryptastyle.misc.Internet;
import com.airwhip.cryptastyle.misc.Names;
import com.airwhip.cryptastyle.parser.Characteristic;
import com.airwhip.cryptastyle.parser.InformationParser;

public class WelcomeActivity extends Activity {

    private static Names names;

    private ImageButton circle;
    private TextView startText;
    private TextView tipText;
    private ImageView plugImage;
    private ImageView socketImage;

    public static boolean isContainsName(String name) {
        return names.contains(name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        circle = (ImageButton) findViewById(R.id.circle);
        startText = (TextView) findViewById(R.id.startText);
        tipText = (TextView) findViewById(R.id.tipText);

        plugImage = (ImageView) findViewById(R.id.plugImage);
        socketImage = (ImageView) findViewById(R.id.socketImage);

        circle.setOnClickListener(new StartButtonClick(ProgramState.START));

        names = new Names(this);
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
                    if (Internet.checkInternetConnection(getApplicationContext())) {
                        plugImage.startAnimation(new Fade(plugImage, 0f));
                        socketImage.startAnimation(new Fade(socketImage, 0f));
                        findViewById(R.id.noInternetText).startAnimation(new Fade(findViewById(R.id.noInternetText), 0f));
                        tipText.startAnimation(new Fade(tipText, 0f));
                        fadeOut.setAnimationListener(new StartButtonAnimation(ProgramState.START));
                        circle.startAnimation(fadeOut);
                    } else {
                        plugImage.startAnimation(new Move(-18, 18, false));
                        socketImage.startAnimation(new Move(18, -18, false));
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

            StringBuilder partOfXml = AccountInformation.get(getApplicationContext());
            SMSInformation.get(getApplicationContext());
            InformationParser parser = new InformationParser(getApplicationContext(), partOfXml, InformationParser.ParserType.ACCOUNT);
            Characteristic.addAll(parser.getAllWeight(), parser.getAllMax());
            Characteristic.append(partOfXml);
            publishProgress(20);
            partOfXml = ApplicationInformation.get(getApplicationContext());
            parser = new InformationParser(getApplicationContext(), partOfXml, InformationParser.ParserType.APPLICATION);
            Characteristic.addAll(parser.getAllWeight(), parser.getAllMax());
            Characteristic.append(partOfXml);
            publishProgress(40);
            partOfXml = BrowserInformation.getHistory(getApplicationContext());
            parser = new InformationParser(getApplicationContext(), partOfXml, InformationParser.ParserType.HISTORY);
            Characteristic.addAll(parser.getAllWeight(), parser.getAllMax());
            Characteristic.append(partOfXml);
            publishProgress(60);
            partOfXml = BrowserInformation.getBookmarks(getApplicationContext());
            parser = new InformationParser(getApplicationContext(), partOfXml, InformationParser.ParserType.BOOKMARKS);
            Characteristic.addAll(parser.getAllWeight(), parser.getAllMax());
            Characteristic.append(partOfXml);
            publishProgress(80);
            partOfXml = MusicInformation.get(getApplicationContext());
            parser = new InformationParser(getApplicationContext(), partOfXml, InformationParser.ParserType.MUSIC);
            Characteristic.addAll(parser.getAllWeight(), parser.getAllMax());
            Characteristic.append(partOfXml);
            publishProgress(100);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(getApplicationContext(), PreviewActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            startText.setText(String.valueOf(progress) + "%");
        }
    }
}
