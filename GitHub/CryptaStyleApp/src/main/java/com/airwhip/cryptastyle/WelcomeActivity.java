package com.airwhip.cryptastyle;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airwhip.cryptastyle.anim.Fade;
import com.airwhip.cryptastyle.misc.Internet;


public class WelcomeActivity extends Activity {

    private TextView loading;
    private ImageView avatar;

    private ImageButton imageButton;
    private TextView startText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loading = (TextView) findViewById(R.id.loading);
        avatar = (ImageView) findViewById(R.id.avatar);

        imageButton = (ImageButton) findViewById(R.id.startButton);
        startText = (TextView) findViewById(R.id.startText);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeOut = new Fade(startText, 0f);

                if (Internet.checkInternetConnection(getApplicationContext())) {
                    fadeOut.setAnimationListener(new StartButtonAnimation(false));
                    imageButton.startAnimation(new Fade(startText, 0f));
                    startText.startAnimation(fadeOut);
                } else {
                    fadeOut.setAnimationListener(new StartButtonAnimation(true));
                    startText.startAnimation(fadeOut);
                }
            }
        });
    }

    private class StartButtonAnimation implements Animation.AnimationListener {

        private boolean isConnectionFailed;

        public StartButtonAnimation(boolean isConnectionFailed) {
            this.isConnectionFailed = isConnectionFailed;
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (isConnectionFailed) {
                findViewById(R.id.noConnection).setAlpha(1f);
                findViewById(R.id.noInternetText).setAlpha(1f);
                findViewById(R.id.checkInternetText).setAlpha(1f);
                imageButton.setOnClickListener(null);
            } else {
                new ImageLoader().execute();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
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
