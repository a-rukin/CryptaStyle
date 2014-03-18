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
import com.airwhip.cryptastyle.getters.ApplicationInformation;
import com.airwhip.cryptastyle.getters.BrowserInformation;
import com.airwhip.cryptastyle.getters.MusicInformation;
import com.airwhip.cryptastyle.misc.Constants;
import com.airwhip.cryptastyle.misc.Internet;
import com.airwhip.cryptastyle.parser.Characteristic;
import com.airwhip.cryptastyle.parser.InformationParser;


public class WelcomeActivity extends Activity {

    private TextView loading;
    private TextView loadingAnimation;
    private ImageView avatar;

    private ImageButton startButton;
    private TextView startText;

    private ImageView plugImage;
    private ImageView socketImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loading = (TextView) findViewById(R.id.loading);
        loadingAnimation = (TextView) findViewById(R.id.loadingAnimation);
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
                    startButton.setOnClickListener(null);
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
        private int curProgress = 0;

        private boolean canContinue = true;

        @Override
        protected Void doInBackground(Void... params) {
            while (loading.getHeight() == 0) ;

            height = loading.getHeight() + avatar.getHeight();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loading.setHeight(height);
                    loadingAnimation.setHeight(height);
                    loadingAnimation.setVisibility(View.INVISIBLE);
                    avatar.setAlpha(1f);
                }
            });

            Characteristic characteristic = new Characteristic();

            publishProgress(0, countHeight(0));
            InformationParser parser = new InformationParser(getApplicationContext(), AccountInformation.get(getApplicationContext()), InformationParser.ParserType.ACCOUNT);
            characteristic.addAll(parser.getAllWeight());
            publishProgress(20, countHeight(20));
            parser = new InformationParser(getApplicationContext(), ApplicationInformation.get(getApplicationContext()), InformationParser.ParserType.APPLICATION);
            characteristic.addAll(parser.getAllWeight());
            while (!canContinue) ;
            publishProgress(40, countHeight(40));
            parser = new InformationParser(getApplicationContext(), BrowserInformation.getHistory(getApplicationContext()), InformationParser.ParserType.HISTORY);
            characteristic.addAll(parser.getAllWeight());
            while (!canContinue) ;
            publishProgress(60, countHeight(60));
            parser = new InformationParser(getApplicationContext(), BrowserInformation.getBookmarks(getApplicationContext()), InformationParser.ParserType.BOOKMARKS);
            characteristic.addAll(parser.getAllWeight());
            while (!canContinue) ;
            publishProgress(80, countHeight(80));
            parser = new InformationParser(getApplicationContext(), MusicInformation.get(getApplicationContext()), InformationParser.ParserType.MUSIC);
            characteristic.addAll(parser.getAllWeight());
            while (!canContinue) ;
            publishProgress(100, countHeight(100));

            //TODO determine who is who
            long[] test = characteristic.get();
            for (int i = 0; i < test.length; i++) {
                Log.d(Constants.DEBUG_TAG, String.valueOf(test[i]));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    avatar.setImageResource(R.drawable.geek);
                }
            });

            return null;
        }

        private int countHeight(int i) {
            return (int) (avatar.getHeight() / 100. * (i - curProgress));
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            canContinue = false;
            final int progress = values[0];
            final int deltaHeight = values[1];
            curProgress = progress;

            loading.setText(String.valueOf(progress) + "%");
            loadingAnimation.setText(String.valueOf(progress) + "%");
            Animation move = new Move(0, -deltaHeight, true);
            move.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    loadingAnimation.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    loading.setY(loading.getY() - deltaHeight);
                    loadingAnimation.setVisibility(View.INVISIBLE);
                    loading.setVisibility(View.VISIBLE);
                    loadingAnimation.setY(loading.getY());
                    canContinue = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            loadingAnimation.startAnimation(move);
            if (progress == 100) {
                loading.setVisibility(View.INVISIBLE);
                loadingAnimation.setVisibility(View.INVISIBLE);
            }

        }
    }
}
