package com.lysov.vlad.hiddennumbers;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;


public class GameField extends ActionBarActivity {

    private static final Map<Integer, Level> LEVELS = generateLevels();

    private Map<Integer, Button> hiddenButtons = new TreeMap<>();

    private static CountDownTimer showTimer;
    private static CountDownTimer hideTimer;
    private static CountDownTimer gameTimer;

    private static boolean isGameStarted;

    private static int errors = 0;

    private TreeSet<Integer> clickedNumbers = new TreeSet<>();

    private static int currentLevelNumber = 0;

    private static MediaPlayer rightClick;
    private static MediaPlayer wrongClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_field);

        if (rightClick == null) {
            rightClick = MediaPlayer.create(this, R.raw.game_field_click);
        }

        if (wrongClick == null) {
            wrongClick = MediaPlayer.create(this, R.raw.button_sound_wrong);
        }
        clickedNumbers.clear();
        isGameStarted = false;
        initTopMenu();

        initGameFieldTableLayout(LEVELS.get(currentLevelNumber));
        initShowTimer(LEVELS.get(currentLevelNumber));
        initGameTimer();
        gameTimer.start();
        showTimer.start();
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

    private void initGameTimer() {
        final TextView time = (TextView) findViewById(R.id.time);
        gameTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                currentLevelNumber++;
                GameField.this.recreate();
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
                initHideTimer(currentLevel, currentLevel.getTimeToShowHiddenCells());
                hideTimer.start();
            }
        };
    }

    private void initHideTimer(final Level currentLevel, int time) {
        hideTimer = new CountDownTimer(time, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // DO NOTHING
            }

            @Override
            public void onFinish() {
                hideHiddenNumbers();
                isGameStarted = true;
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
        time.setText(String.valueOf(30));
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
            hiddenButtonEntry.getValue().setTextColor(Color.RED);
        }
    }

    private void hideHiddenNumbers() {
        for (Map.Entry<Integer, Button> hiddenButtonEntry : hiddenButtons.entrySet()) {
            hiddenButtonEntry.getValue().setTextColor(Color.GRAY);
        }
    }

    private void initHiddenNumbersButton(final Level currentLevel) {
        TableLayout gameField = (TableLayout) findViewById(R.id.game_field);
        for (Map.Entry<Integer, HiddenNumberCell> hiddenNumberCellEntry : currentLevel.getHiddenNumbersPositions().entrySet()) {
            HiddenNumberCell hiddenNumberCell = hiddenNumberCellEntry.getValue();

            TableRow tableRow = (TableRow) gameField.getChildAt(hiddenNumberCell.getRow());
            final Button hiddenButton = (Button) tableRow.getChildAt(hiddenNumberCell.getColumn());

            hiddenButton.setTypeface(null, Typeface.BOLD);
            hiddenButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40); // TODO replace text size to sp

            hiddenButton.setText(String.valueOf(hiddenNumberCell.getNumber()));
            hiddenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isGameStarted) {
                        return;
                    }
                    Integer clickedHiddenNumber = Integer.valueOf(hiddenButton.getText().toString());
                    if ((clickedNumbers.isEmpty() && clickedHiddenNumber == 1) ||
                            (clickedNumbers.last() == clickedHiddenNumber - 1)) {
                        rightClick.start();
                        clickedNumbers.add(clickedHiddenNumber);
                        hiddenButton.setTextColor(Color.RED);
                        if (currentLevel.getHiddenNumbersCount() == clickedNumbers.size()) {
                            currentLevelNumber++;
                            GameField.this.recreate();
                        }
                    } else {
                        updateErrors();
                    }

                    if (errors == 3) {
                        //gameOver();
                    }
                }
            });
            hiddenButtons.put(hiddenNumberCellEntry.getKey(), hiddenButton);
        }
    }

    private void updateErrors() {
        TextView errorsTxt = (TextView) findViewById(R.id.errors);
        errors++;
        wrongClick.start();
        errorsTxt.setText(String.valueOf(errors));
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
        button.setText("Button test");
        button.setTextColor(Color.GRAY);
        button.setBackgroundColor(Color.GRAY);
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
        buttonGridLayoutParams.width = TableLayout.LayoutParams.MATCH_PARENT;
        buttonGridLayoutParams.height = TableLayout.LayoutParams.MATCH_PARENT;
        int margin = Math.round(convertDpToPixes(5));
        buttonGridLayoutParams.bottomMargin = margin;
        buttonGridLayoutParams.leftMargin = margin;
        buttonGridLayoutParams.rightMargin = margin;
        buttonGridLayoutParams.topMargin = margin;
        buttonGridLayoutParams.weight = 1.0f;
    }

    private static Map<Integer, Level> generateLevels() {
        Map<Integer, Level> levels = new TreeMap<>();
        int levelNumber = 0;

        for (; levelNumber < 2; levelNumber++) {
            levels.put(levelNumber, LevelUtils.generateLevel(levelNumber, 2, 2, 1, 1000));
        }

        for (; levelNumber < 5; levelNumber++) {
            levels.put(levelNumber, LevelUtils.generateLevel(levelNumber, 3, 3, 3, 2000));
        }

        for (; levelNumber < 9; levelNumber++) {
            levels.put(levelNumber, LevelUtils.generateLevel(levelNumber, 4, 4, 4, 3000));
        }

        for (; levelNumber < 13; levelNumber++) {
            levels.put(levelNumber, LevelUtils.generateLevel(levelNumber, 5, 5, 5, 4000));
        }

        return levels;
    }

    private float convertDpToPixes(float px) {
        Resources r = getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, r.getDisplayMetrics());
    }
}
