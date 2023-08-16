package com.food.lite.nckh.detection;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.food.lite.nckh.detection.sqlite.sqlFood;

import org.tensorflow.lite.examples.detection.R;

import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    protected Button buttonDetect;
    protected Button cameraButton;
    protected Button buttonListView;
    RadioGroup radioGroup;
    RadioButton radioEng, radioVN;
    public int selected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        buttonDetect = findViewById(R.id.buttonDetect);
        buttonDetect.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity2.this, DetectGallery.class);
            startActivity(intent);
        });

        cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(v -> startActivity(new Intent(MainActivity2.this, DetectorActivity.class)));

        buttonListView = findViewById(R.id.buttonListView);
        buttonListView.setOnClickListener(v -> startActivity(new Intent(MainActivity2.this, ListFood.class)));

        //Assign
        radioGroup = findViewById(R.id.rg_language);
        radioEng = findViewById(R.id.radioEng);
        radioVN = findViewById(R.id.radioVN);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioVN:
                        setLocale("vi-rVN");
                        break;
                    case R.id.radioEng:
                        setLocale("en");
                        break;
                }
            }
        });
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity2.class);
        selected = -1;
        finish();
        startActivity(refresh);
    };

    public Locale getLocale() {
        return getResources().getConfiguration().locale;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}


