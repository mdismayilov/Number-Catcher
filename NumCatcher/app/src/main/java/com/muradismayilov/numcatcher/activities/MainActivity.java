package com.muradismayilov.numcatcher.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.muradismayilov.numcatcher.R;

import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    // String
    @BindString(R.string.app_link)
    String app_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.activity_main_startBTN)
    public void activity_main_startBTNClicked() {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @OnClick(R.id.activity_main_aboutTV)
    public void activity_main_aboutTVClicked() {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @OnClick(R.id.activity_main_rateUsTV)
    public void activity_main_rateUsTVClicked() {
        Uri open_uri = Uri.parse(app_link);
        Intent open_intent = new Intent(Intent.ACTION_VIEW, open_uri);
        startActivity(open_intent);
    }
}