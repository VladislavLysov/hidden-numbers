package com.lysov.vlad.hiddennumbers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;


public class GameField extends ActionBarActivity {

    private Map<Integer, Button> hiddenButtons = new TreeMap<>();

    private static CountDownTimer showTimer;
    private static CountDownTimer hideTimer;
    private static CountDownTimer gameTimer;

    private static final int TIMER = 10;
    private static final int MAX_ERROR_NUMBERS = 3;
    private static final int MAX_LEVEL_NUMBER = 33;
    private static final int INTERSTITIAL_SHOW_COUNT = 3;

    private static boolean isGameStarted;
    private static InterstitialAd mInterstitialAd;

    private static int errors = 0;

    private TreeSet<Integer> clickedNumbers = new TreeSet<>();

    protected static int currentLevelNumber = 0;
    private static int gameOversCount;

    private static MediaPlayer rightClick;
    private static MediaPlayer wrongClick;

    private static Typeface tf;
    private static Vibrator vibrator;

    private Tracker tracker;

    private int timer = TIMER;
    private boolean isGameOver;

    private Level currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_field);

        initVibrationAndClicksAudio();

        if (tracker == null) {
            AnalyticsApplication application = (AnalyticsApplication) getApplication();
            tracker = application.getDefaultTracker();
        }

        clickedNumbers.clear();
        isGameStarted = false;
        initTopMenu();

        currentLevel = generateLevel();

        timer = TIMER;
        initFont();
        initGameFieldTableLayout(currentLevel);
        initGameTimer(timer);
        initShowTimer(currentLevel);
        initInterstitial();
        requestNewInterstitial();
        showTimer.start();
    }

    private void initVibrationAndClicksAudio() {

        if (vibrator == null) {
            vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        }

        if (rightClick == null) {
            rightClick = MediaPlayer.create(this, R.raw.game_field_click);
        }

        if (wrongClick == null) {
            wrongClick = MediaPlayer.create(this, R.raw.button_sound_wrong);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initGameTimer(int timer) {
        final TextView time = (TextView) findViewById(R.id.time);
        gameTimer = new CountDownTimer(timer * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                gameOver();
            }
        };
    }

    private void initShowTimer(final Level currentLevel) {
        showTimer = new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // DO NOTHING
            }

            @Override
            public void onFinish() {
                showHiddenNumbers();
                initHideTimer(currentLevel.getTimeToShowHiddenCells());
                hideTimer.start();
            }
        };
    }

    private void initHideTimer(int time) {
        hideTimer = new CountDownTimer(time, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // DO NOTHING
            }

            @Override
            public void onFinish() {
                hideHiddenNumbers();
                isGameStarted = true;
                gameTimer.start();
            }
        };
    }

    private void initTopMenu() {
        errors = 0;
        final TextView scores = (TextView) findViewById(R.id.scores);
        final TextView errorsTxt = (TextView) findViewById(R.id.errors);
        final TextView time = (TextView) findViewById(R.id.time);
        scores.setText(String.valueOf(currentLevelNumber));
        errorsTxt.setText(String.valueOf(errors));
        time.setText(String.valueOf(TIMER));
    }

    private void initGameFieldTableLayout(Level currentLevel) {
        int columnNums = currentLevel.getColumnsCount();
        int rowNums = currentLevel.getRowsCount();
        TableLayout gridLayout = (TableLayout) findViewById(R.id.game_field);
        for (int i = 0; i < columnNums; i++) {
            TableRow tableRow = initTableRow();
            gridLayout.addView(tableRow);
            initTableRowLayoutParams(tableRow);
            for (int j = 0; j < rowNums; j++) {
                View button = initButton();
                tableRow.addView(button);
                initLayoutParams(button);
            }
        }
        initHiddenNumbersButton(currentLevel);
    }

    private void showHiddenNumbers() {
        for (Map.Entry<Integer, Button> hiddenButtonEntry : hiddenButtons.entrySet()) {
            hiddenButtonEntry.getValue().setTextSize(TypedValue.COMPLEX_UNIT_SP, currentLevel.getTextSize());
        }
    }

    private void hideHiddenNumbers() {
        for (Map.Entry<Integer, Button> hiddenButtonEntry : hiddenButtons.entrySet()) {
            hiddenButtonEntry.getValue().setTextSize(0);
        }
    }

    private void initHiddenNumbersButton(final Level currentLevel) {
        TableLayout gameField = (TableLayout) findViewById(R.id.game_field);
        for (Map.Entry<Integer, HiddenNumberCell> hiddenNumberCellEntry : currentLevel.getHiddenNumbersPositions().entrySet()) {
            HiddenNumberCell hiddenNumberCell = hiddenNumberCellEntry.getValue();

            TableRow tableRow = (TableRow) gameField.getChildAt(hiddenNumberCell.getRow());
            final Button hiddenButton = (Button) tableRow.getChildAt(hiddenNumberCell.getColumn());

            hiddenButton.setTypeface(tf);
            hiddenButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 0);

            hiddenButton.setText(String.valueOf(hiddenNumberCell.getNumber()));
            hiddenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isGameStarted) {
                        return;
                    }
                    Integer clickedHiddenNumber = Integer.valueOf(hiddenButton.getText().toString());
                    if ((clickedNumbers.isEmpty() && clickedHiddenNumber == 1) ||
                            (!clickedNumbers.isEmpty() && clickedNumbers.last() == clickedHiddenNumber - 1)) {
                        rightClick.start();
                        clickedNumbers.add(clickedHiddenNumber);
                        hiddenButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentLevel.getTextSize());
                        if (currentLevel.getHiddenNumbersCount() == clickedNumbers.size()) {
                            if (currentLevelNumber == MAX_LEVEL_NUMBER) {
                                gameOver();
                            } else {
                                gameTimer.cancel();
                                currentLevelNumber++;
                                GameField.this.recreate();
                            }
                        }
                    } else {
                        updateErrors();
                    }
                }
            });
            hiddenButtons.put(hiddenNumberCellEntry.getKey(), hiddenButton);
        }
    }

    private void updateErrors() {
        if (isGameStarted) {
            errors++;
            TextView errorsTxt = (TextView) findViewById(R.id.errors);
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(100);
            }
            wrongClick.start();
            errorsTxt.setText(String.valueOf(errors));
            if (errors >= MAX_ERROR_NUMBERS) {
                gameOver();
            }
        }
    }

    private void gameOver() {
        isGameOver = true;
        gameTimer.cancel();
        gameTimer = null;
        gameOversCount++;
        showGameOverDialog();
    }

    private TableRow initTableRow() {
        TableRow tableRow = new TableRow(this);
        tableRow.setGravity(Gravity.CENTER);
        return tableRow;
    }

    private void initTableRowLayoutParams(TableRow tableRow) {
        ((TableLayout.LayoutParams) tableRow.getLayoutParams()).weight = 1.0f;
    }

    private View initButton() {
        Button button = new Button(this);
        button.setBackgroundResource(R.drawable.game_button);
        button.setTextColor(Color.rgb(0, 0, 139));
        button.setTypeface(tf);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateErrors();
            }
        });
        return button;
    }

    private void initLayoutParams(View view) {
        TableRow.LayoutParams buttonGridLayoutParams = (TableRow.LayoutParams) view.getLayoutParams();
        int margin = Math.round(convertDpToPixes(5));
        buttonGridLayoutParams.bottomMargin = margin;
        buttonGridLayoutParams.leftMargin = margin;
        buttonGridLayoutParams.rightMargin = margin;
        buttonGridLayoutParams.topMargin = margin;
        buttonGridLayoutParams.weight = 1.0f;
    }

    private Level generateLevel() {
        if (currentLevelNumber < 2) {
            return LevelUtils.generateLevel(currentLevelNumber, 3, 3, 2, 700, 36, "Ludvig Van Beethoven", R.drawable.ludvig_van_beethoven);
        }

        if (currentLevelNumber >= 2 && currentLevelNumber < 5) {
            return LevelUtils.generateLevel(currentLevelNumber, 4, 4, 4, 1100, 32, "William Shakespeare", R.drawable.william_shakespeare);
        }

        if (currentLevelNumber >= 5 && currentLevelNumber < 9) {
            return LevelUtils.generateLevel(currentLevelNumber, 5, 5, 5, 1600, 28, "Benjamin Franklin", R.drawable.benfranklin);
        }

        if (currentLevelNumber >= 9 && currentLevelNumber < 13) {
            return LevelUtils.generateLevel(currentLevelNumber, 6, 6, 6, 3000, 24, "Socrates", R.drawable.socrates);
        }

        if (currentLevelNumber >= 13 && currentLevelNumber < 18) {
            return LevelUtils.generateLevel(currentLevelNumber, 7, 7, 8, 3500, 22, "Galileo Galilei", R.drawable.galileo);
        }

        if (currentLevelNumber >= 18 && currentLevelNumber < 24) {
            return LevelUtils.generateLevel(currentLevelNumber, 7, 7, 10, 4500, 16, "Leonardo Da Vinci", R.drawable.da_vinci);
        }

        if (currentLevelNumber >= 24 && currentLevelNumber < 28) {
            return LevelUtils.generateLevel(currentLevelNumber, 7, 7, 12, 5000, 16, "Isaac Newton", R.drawable.newton);
        }

        if (currentLevelNumber >= 28 && currentLevelNumber < 34) {
            return LevelUtils.generateLevel(currentLevelNumber, 7, 7, 15, 5500, 16, "Albert Einstein", R.drawable.einstein);
        }

        return null;
    }

    private float convertDpToPixes(float px) {
        Resources r = getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, r.getDisplayMetrics());
    }

    private void setFonts(String font) {
        if (tf == null) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/" + font);
        }
        List<TextView> textViews = new ArrayList<>();
        textViews.add((TextView) findViewById(R.id.time_txt));
        textViews.add((TextView) findViewById(R.id.time));
        textViews.add((TextView) findViewById(R.id.errors_txt));
        textViews.add((TextView) findViewById(R.id.errors));
        textViews.add((TextView) findViewById(R.id.scores_txt));
        textViews.add((TextView) findViewById(R.id.scores));
        for (TextView textView : textViews) {
            textView.setTypeface(tf);
        }
    }

    private void initFont() {
        setFonts("Chunkfive.otf");
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater  = this.getLayoutInflater();
        View scores_layout = inflater.inflate(R.layout.game_over, null);
        builder.setView(scores_layout);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/" + "Sail-Regular.otf");

        TextView gameOverScoresText = (TextView) scores_layout.findViewById(R.id.game_over_scores_text);
        gameOverScoresText.setTypeface(tf);

        TextView gameOverScores = (TextView) scores_layout.findViewById(R.id.game_over_scores);
        gameOverScores.setTypeface(tf);
        gameOverScores.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        gameOverScores.setText(String.format(getResources().getString(R.string.game_over_points), String.valueOf(currentLevelNumber)));

        TextView gameOverPoints = (TextView) scores_layout.findViewById(R.id.game_over_before_name_text);
        gameOverPoints.setTypeface(tf);

        TextView gameOverName = (TextView) scores_layout.findViewById(R.id.game_over_name);
        gameOverName.setTypeface(tf);
        gameOverName.setText(currentLevel.getName());

        final AlertDialog gameOverDialog = builder.create();
        ImageButton dialogButton = (ImageButton) scores_layout.findViewById(R.id.game_over_dialog_ok_btn);

        ImageView gameOverPicture = (ImageView) scores_layout.findViewById(R.id.game_over_picture);
        gameOverPicture.setImageResource(currentLevel.getPictureId());

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Game Over. New game click")
                        .build());
                currentLevelNumber = 0;
                rightClick.start();
                gameOverDialog.dismiss();
                recreate();
            }
        });

//        ImageButton shareButton = (ImageButton) scores_layout.findViewById(R.id.game_over_share_btn);
//        shareButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tracker.send(new HitBuilders.EventBuilder()
//                        .setCategory("Action")
//                        .setAction("Invite and share")
//                        .build());
//                inviteAndShare();
//            }
//        });

        gameOverDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    gameOverDialog.dismiss();
                    finish();
                }
                return true;
            }
        });
        showInterstitial();
        gameOverDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveAndCancelTimerState();
        if (!MainMenu.isMainMenuVisible) {
            MainMenu.mainMenuTheme.pause();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveAndCancelTimerState();
        MainMenu.mainMenuTheme.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimerAfterGamePause();
        if (!MainMenu.mainMenuTheme.isPlaying()) {
            MainMenu.mainMenuTheme.start();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTimerAfterGamePause();
        if (!MainMenu.mainMenuTheme.isPlaying()) {
            MainMenu.mainMenuTheme.start();
        }
    }

    private void startTimerAfterGamePause() {
        if (gameTimer == null && !isGameOver) {
            initGameTimer(timer);
            gameTimer.start();
        }
    }

    private void saveAndCancelTimerState() {
        if (gameTimer != null && !isGameOver && isGameStarted) {
            gameTimer.cancel();
            gameTimer = null;
            TextView time = (TextView) findViewById(R.id.time);
            timer = Integer.valueOf(String.valueOf(time.getText()));
        }
    }

    private void inviteAndShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "https://goo.gl/jLbpx6 \n\n" +  "Сheck Your Memory in \"Hidden numbers\" game!\n I've scored " + currentLevelNumber + " scores!");
        startActivity(Intent.createChooser(intent, "Share with"));
    }

    private void showInterstitial() {
        if (gameOversCount == INTERSTITIAL_SHOW_COUNT) {
            gameOversCount = 0;
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

    private void initInterstitial() {
        if (mInterstitialAd == null) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-3582002686029130/9690306000");
//            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // This is a test AdUnitId
            requestNewInterstitial();
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("Exit game from interstitial")
                            .build());
                    requestNewInterstitial();
                }
            });
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
