package com.lysov.vlad.hiddennumbers;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class MainMenu extends ActionBarActivity {

    public static MediaPlayer mainMenuTheme;
    private static MediaPlayer click;

    protected static boolean isMainMenuVisible;

    private Tracker tracker;

    private boolean isNewGamePressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        GoogleAnalytics.getInstance(this).setLocalDispatchPeriod(15);
        tracker = application.getDefaultTracker();
//        LeakCanary.install(this.getApplication());
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main_menu);
        setupButtonsOnClick();
        click = MediaPlayer.create(this.getBaseContext(), R.raw.game_field_click);
        mainMenuTheme = MediaPlayer.create(this.getBaseContext(), R.raw.main_menu_music);
        mainMenuTheme.setLooping(true);
        mainMenuTheme.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupButtonsOnClick() {
        final Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        ImageButton startGame = (ImageButton) findViewById(R.id.start_game_btn);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameField.currentLevelNumber = 0;
                isNewGamePressed = true;
                Intent intent = new Intent(MainMenu.this, GameField.class);
                v.startAnimation(shake);
                click.start();
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Start game")
                        .build());
                MainMenu.this.startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mainMenuTheme.pause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isMainMenuVisible = true;
        isNewGamePressed = false;
        mainMenuTheme.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMainMenuVisible = true;
        isNewGamePressed = false;
        mainMenuTheme.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isMainMenuVisible = false;
        if (!isNewGamePressed) {
            mainMenuTheme.pause();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isMainMenuVisible = false;
        if (!isNewGamePressed) {
            mainMenuTheme.pause();
        }
    }
}
