package com.airwhip.cryptastyle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airwhip.cryptastyle.parser.Characteristic;


public class PreviewActivity extends Activity {

    TextView maleText;
    TextView femaleText;

    TextView maleValueText;
    TextView femaleValueText;

    ImageView maleValueImage;
    ImageView femaleValueImage;

    TextView ageText;

    TextView continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        maleText = (TextView) findViewById(R.id.maleText);
        femaleText = (TextView) findViewById(R.id.femaleText);

        maleValueText = (TextView) findViewById(R.id.maleValueText);
        femaleValueText = (TextView) findViewById(R.id.femaleValueText);

        maleValueImage = (ImageView) findViewById(R.id.maleValueImage);
        femaleValueImage = (ImageView) findViewById(R.id.femaleValueImage);

        ageText = (TextView) findViewById(R.id.ageText);

        continueButton = (TextView) findViewById(R.id.continueButton);

        if (Characteristic.isMale()) {
            femaleText.setAlpha(.3f);
        } else {
            maleText.setAlpha(.3f);
        }

        maleValueText.setText(Characteristic.getMale() + "%");
        femaleValueText.setText(Characteristic.getFemale() + "%");
        maleValueImage.getLayoutParams().width = Characteristic.getMale();
        femaleValueImage.getLayoutParams().width = Characteristic.getFemale();
        ageText.setText(String.valueOf(Characteristic.getAge()));

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
