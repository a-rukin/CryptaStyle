package com.airwhip.cryptastyle;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airwhip.cryptastyle.anim.Fade;
import com.airwhip.cryptastyle.anim.Move;
import com.airwhip.cryptastyle.getters.AccountInformation;
import com.airwhip.cryptastyle.misc.Constants;
import com.airwhip.cryptastyle.misc.Internet;
import com.airwhip.cryptastyle.parser.InformationParser;


public class WelcomeActivity extends Activity {

    private TextView loading;
    private ImageView avatar;

    private ImageButton startButton;
    private TextView startText;

    private ImageView plugImage;
    private ImageView socketImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        InformationParser ap = new InformationParser(this, AccountInformation.get(this), InformationParser.ParserType.ACCOUNT);
        long[] test = ap.getAllWeight();
        for (Long l : test) {
            Log.d(Constants.DEBUG_TAG, String.valueOf(l));
        }

        loading = (TextView) findViewById(R.id.loading);
        avatar = (ImageView) findViewById(R.id.avatar);

        startButton = (ImageButton) findViewById(R.id.startButton);
        startText = (TextView) findViewById(R.id.startText);

        plugImage = (ImageView) findViewById(R.id.plugImage);
        socketImage = (ImageView) findViewById(R.id.socketImage);

        startButton.setOnClickListener(new StartButtonClick(ProgramState.START));
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
                    new ImageLoader().execute();
                    break;
                case NO_INTERNET:
                    plugImage.setAlpha(1f);
                    socketImage.setAlpha(1f);
                    findViewById(R.id.noInternetText).setAlpha(1f);
                    findViewById(R.id.checkInternetText).setAlpha(1f);
                    startButton.setOnClickListener(new StartButtonClick(ProgramState.NO_INTERNET));
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
                        startButton.startAnimation(new Fade(startText, 0f));
                        startText.startAnimation(fadeOut);
                    } else {
                        fadeOut.setAnimationListener(new StartButtonAnimation(ProgramState.NO_INTERNET));
                        startText.startAnimation(fadeOut);
                    }
                    break;
                case NO_INTERNET:
                    if (!Internet.checkInternetConnection(getApplicationContext())) {
                        plugImage.startAnimation(new Move(-18, 18, false));
                        socketImage.startAnimation(new Move(18, -18, false));
                    } else {
                        plugImage.startAnimation(new Fade(plugImage, 0f));
                        socketImage.startAnimation(new Fade(socketImage, 0f));
                        findViewById(R.id.noInternetText).startAnimation(new Fade(findViewById(R.id.noInternetText), 0f));
                        findViewById(R.id.checkInternetText).startAnimation(new Fade(findViewById(R.id.checkInternetText), 0f));
                        fadeOut.setAnimationListener(new StartButtonAnimation(ProgramState.START));
                        startButton.startAnimation(fadeOut);
                    }
            }

        }
    }

    private class ImageLoader extends AsyncTask<Void, Integer, Void> {

        private int height;

        @Override
        protected Void doInBackground(Void... params) {
            while (loading.getHeight() == 0) ;

            height = loading.getHeight() + avatar.getHeight();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loading.setHeight(height);
                    avatar.setAlpha(1f);
                }
            });

            for (int i = 0; i <= 100; i++) {
                publishProgress(i, height - (int) (avatar.getHeight() / 100. * i));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            int newHeight = values[1];

            loading.setText(String.valueOf(progress) + "%");
            loading.setHeight(newHeight);
            if (progress == 100) {
                loading.setVisibility(View.INVISIBLE);
            }

        }
    }
}
