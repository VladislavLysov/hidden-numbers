package com.lysov.vlad.hiddennumbers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LevelUtils {

    public static Level generateLevel(int levelNumber, int rowsCount, int columnsCount, int hiddenNumbersCount,
                                      int timeToShowHiddenCells) {
        Map<Integer, HiddenNumberCell> hiddenNumbersPositions = new HashMap<>();
        List<Integer> ocuppiedRows = new ArrayList<>();
        List<Integer> ocuppiedColumns = new ArrayList<>();
        for (int i = 1; i < hiddenNumbersCount + 1; i++) {
            HiddenNumberCell hiddenNumber = generateHiddenNumberCell(i, ocuppiedRows, ocuppiedColumns,
                                                                     rowsCount, columnsCount);
            hiddenNumbersPositions.put(i, hiddenNumber);
        }

        return new Level(levelNumber, rowsCount, columnsCount, hiddenNumbersCount, timeToShowHiddenCells,
                         hiddenNumbersPositions);
    }

    private static HiddenNumberCell generateHiddenNumberCell(int number, List<Integer> ocuppiedRows, List<Integer> ocuppiedColumns,
                             int rowsCount, int columnsCount) {
        int row = -1;
        int column = -1;
        while ((row == -1 && column == -1) || (ocuppiedRows.contains(row) && ocuppiedColumns.contains(column))) {
            row = new Random().nextInt(rowsCount);
            column = new Random().nextInt(columnsCount);
        }
        return new HiddenNumberCell(number, column, row);
    }
}
