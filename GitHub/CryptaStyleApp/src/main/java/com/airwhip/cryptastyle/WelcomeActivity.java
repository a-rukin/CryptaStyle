package com.airwhip.cryptastyle;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class WelcomeActivity extends Activity {

    private TextView loading;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loading = (TextView) findViewById(R.id.loading);
        avatar = (ImageView) findViewById(R.id.avatar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new ImageLoader().execute();
    }

    private class ImageLoader extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (loading.getHeight() == 0) ;

            int height = loading.getHeight() + avatar.getHeight();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loading.setHeight(loading.getHeight() + avatar.getHeight());
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

            final int progress = values[0];
            final int newHeight = values[1];

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loading.setText(String.valueOf(progress) + "%");
                    loading.setHeight(newHeight);

                    if (progress == 100) {
                        loading.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }
}
