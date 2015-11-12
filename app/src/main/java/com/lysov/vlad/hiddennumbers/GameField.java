package com.lysov.vlad.hiddennumbers;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Map;
import java.util.TreeMap;


public class GameField extends ActionBarActivity {

    private static final Map<Integer, Level> LEVELS = generateLevels();

    private Map<Integer, Button> hiddenButtons = new TreeMap<>();

    private static CountDownTimer showTimer;
    private static CountDownTimer gameTimer;

    private static int currentLevelNumber = 0;
//    private static Level currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_field);

        initGameFieldTableLayout(LEVELS.get(currentLevelNumber));
        initShowTimer(LEVELS.get(currentLevelNumber));
        showTimer.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_field, menu);
        return true;
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
                try {
                    Thread.sleep(currentLevel.getTimeToShowHiddenCells());
                } catch (InterruptedException e) {
                    // DO NOTHING
                }
//                hideHiddenNumbers();
            }
        };
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

    private void initHiddenNumbersButton(Level currentLevel) {
        TableLayout gameField = (TableLayout) findViewById(R.id.game_field);
        for (Map.Entry<Integer, HiddenNumberCell> hiddenNumberCellEntry : currentLevel.getHiddenNumbersPositions().entrySet()) {
            HiddenNumberCell hiddenNumberCell = hiddenNumberCellEntry.getValue();

            TableRow tableRow = (TableRow) gameField.getChildAt(hiddenNumberCell.getRow());
            Button hiddenButton = (Button) tableRow.getChildAt(hiddenNumberCell.getColumn());

            hiddenButton.setTypeface(null, Typeface.BOLD);
            hiddenButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40); // TODO replace text size to sp

            hiddenButton.setText(String.valueOf(hiddenNumberCell.getNumber()));
            hiddenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = 1;
                }
            });
            hiddenButtons.put(hiddenNumberCellEntry.getKey(), hiddenButton);
        }
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
            levels.put(levelNumber, LevelUtils.generateLevel(levelNumber, 3, 3, 1, 2000));
        }

        for (; levelNumber < 9; levelNumber++) {
            levels.put(levelNumber, LevelUtils.generateLevel(levelNumber, 4, 4, 2, 3000));
        }

        for (; levelNumber < 13; levelNumber++) {
            levels.put(levelNumber, LevelUtils.generateLevel(levelNumber, 5, 5, 3, 4000));
        }

        return levels;
    }

    private float convertDpToPixes(float px) {
        Resources r = getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, r.getDisplayMetrics());
    }
}
