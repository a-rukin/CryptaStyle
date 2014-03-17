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
import com.airwhip.cryptastyle.getters.BrowserInformation;


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
                Animation fade = new Fade(imageButton, 0f);
                fade.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        new ImageLoader().execute();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                startText.startAnimation(new Fade(startText, 0f));
                imageButton.startAnimation(fade);
            }
        });
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
