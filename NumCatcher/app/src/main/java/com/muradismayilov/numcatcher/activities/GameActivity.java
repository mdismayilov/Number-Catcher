package com.muradismayilov.numcatcher.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.muradismayilov.numcatcher.R;

import java.util.Random;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameActivity extends AppCompatActivity {

    // UI Components
    // TextView
    @BindView(R.id.activity_game_numberTV)
    TextView activity_game_numberTV;
    @BindView(R.id.activity_game_catchableNumberTV)
    TextView activity_game_catchableNumberTV;
    @BindView(R.id.activity_game_bestScoreTV)
    TextView activity_game_bestScoreTV;
    // FrameLayout
    @BindView(R.id.activity_game_bannerAdFL)
    FrameLayout activity_game_bannerAdFL;

    // String
    @BindString(R.string.catch_the_number)
    String catchTheNumber;
    @BindString(R.string.best_score)
    String best_score;
    @BindString(R.string.your_score)
    String your_score;
    @BindString(R.string.numbercatcher_game_banner)
    String numbercatcher_game_banner;

    // Variables
    // Integer
    private int number, catchableNumber, bestScore;
    // Handler
    private Handler mainHandler;
    // RotateAnimation
    private RotateAnimation rotateAnimation;
    // String
    private String catchableNumberString;
    //Boolean
    private boolean clicked;
    // Final
    private final String SHARED_PREFERENCES = "bestScore";
    // Runnable
    private Runnable mainRunnable;
    // MediaPlayer
    private MediaPlayer mediaPlayer, mediaPlayerClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        initialFunctions();
    }

    private void initialFunctions() {
        declareVariables();
        getBestScore();
        startTheNumber();
        setTheAnimation();
        activity_game_catchableNumberTV.setText(catchableNumberString);
        setMusic();
        setAds();
    }

    private void setAds() {
        MobileAds.initialize(this);
        AdView adView = new AdView(this);
        adView.setAdUnitId(numbercatcher_game_banner);
        activity_game_bannerAdFL.addView(adView);

        AdRequest bannerAdRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(bannerAdRequest);
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void setMusic() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.music);
        mediaPlayer.setLooping(true);

        mediaPlayerClick = new MediaPlayer();
        mediaPlayerClick = MediaPlayer.create(getApplicationContext(), R.raw.click);

        if (mediaPlayer != null) {

            mediaPlayer.start();
        }
    }

    private void declareVariables() {
        // Integer
        number = -1;
        catchableNumber = 6;
        // Handler
        mainHandler = new Handler();
        // RotateAnimation
        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // String
        catchableNumberString = catchTheNumber + ": " + catchableNumber;
        // Boolean
        clicked = false;
    }

    private void getBestScore() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        bestScore = sharedPreferences.getInt("bestScore", 0);
        String bestScoreText = best_score + ": " + bestScore;
        activity_game_bestScoreTV.setText(bestScoreText);
    }

    private void startTheNumber() {
        mainRunnable = new Runnable() {
            @Override
            public void run() {

                Random random = new Random();
                int randomDelay = random.nextInt((1000 - 500) + 1) + 500;

                doInEveryChange();

                mainHandler.postDelayed(this, randomDelay);
            }
        };
        mainHandler.post(mainRunnable);
    }

    private void doInEveryChange() {
        number++;

        activity_game_numberTV.setText(String.valueOf(number));
        startTheAnimation();

        if (number >= catchableNumber) {

            new Handler().postDelayed(() -> {
                if (!clicked) {
                    showFinishDialog();
                }

                clicked = false;

                setNewCatchableNumber();
            }, 500);
        }
    }

    private void setNewCatchableNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt((9 - 4) + 1) + 4;

        catchableNumber = number + randomNumber;
        catchableNumberString = catchTheNumber + ": " + catchableNumber;
        activity_game_catchableNumberTV.setText(catchableNumberString);
    }

    private void setTheAnimation() {
        rotateAnimation.setDuration(500);
        rotateAnimation.setInterpolator(new LinearInterpolator());

        activity_game_numberTV.setAnimation(rotateAnimation);
    }

    private void startTheAnimation() {
        activity_game_numberTV.startAnimation(rotateAnimation);
    }

    @OnClick(R.id.activity_game_numberTV)
    public void activity_game_numberTVClicked() {
        if (mediaPlayerClick != null) {

            mediaPlayerClick.start();
        }

        if (activity_game_numberTV.getText() != null && activity_game_catchableNumberTV.getText() != null) {
            String currentNumber = activity_game_numberTV.getText().toString();

            if (Integer.parseInt(currentNumber) == catchableNumber) {
                clicked = true;
            } else {
                showFinishDialog();
            }
        }
    }

    private void showFinishDialog() {
        stopRunnable();

        final AlertDialog dialog_finish = new AlertDialog.Builder(this).create();
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.dialog_finish, null);

        TextView dialog_finish_yourScoreTV = view.findViewById(R.id.dialog_finish_yourScoreTV);
        TextView dialog_finish_bestScoreTV = view.findViewById(R.id.dialog_finish_bestScoreTV);
        Button dialog_finish_restartBTN = view.findViewById(R.id.dialog_finish_restartBTN);
        Button dialog_finish_homeBTN = view.findViewById(R.id.dialog_finish_homeBTN);

        String yourScoreText = your_score + ": " + number;
        dialog_finish_yourScoreTV.setText(yourScoreText);

        if (number > bestScore) {
            saveBestScore();

            String bestScoreText = best_score + ": " + number;
            dialog_finish_bestScoreTV.setText(bestScoreText);
        } else {

            String bestScoreText = best_score + ": " + bestScore;
            dialog_finish_bestScoreTV.setText(bestScoreText);
        }

        dialog_finish_restartBTN.setOnClickListener(v -> {
            Intent intent = getIntent();
            startActivity(intent);
            GameActivity.this.finish();
        });

        dialog_finish_homeBTN.setOnClickListener(v -> GameActivity.this.finish());

        dialog_finish.setView(view);
        dialog_finish.setCanceledOnTouchOutside(false);
        dialog_finish.show();
    }

    private void saveBestScore() {
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).edit();
        editor.putInt("bestScore", number);
        editor.apply();
    }

    private void stopRunnable() {
        mainHandler.removeCallbacks(mainRunnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRunnable();
        mediaPlayer.stop();
        mediaPlayerClick.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
