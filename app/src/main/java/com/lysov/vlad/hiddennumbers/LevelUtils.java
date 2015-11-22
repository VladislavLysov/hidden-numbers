package com.lysov.vlad.hiddennumbers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LevelUtils {

    public static Level generateLevel(int levelNumber, int rowsCount, int columnsCount, int hiddenNumbersCount,
                                      int timeToShowHiddenCells, int textSize, String name, int pictureId) {
        Map<Integer, HiddenNumberCell> hiddenNumbersPositions = new HashMap<>();
        List<Integer> ocuppiedRows = new ArrayList<>();
        List<Integer> ocuppiedColumns = new ArrayList<>();
        for (int i = 1; i < hiddenNumbersCount + 1; i++) {
            HiddenNumberCell hiddenNumber = generateHiddenNumberCell(i, ocuppiedRows, ocuppiedColumns,
                                                                     rowsCount, columnsCount);
            hiddenNumbersPositions.put(i, hiddenNumber);
        }

        return new Level(levelNumber, rowsCount, columnsCount, hiddenNumbersCount, timeToShowHiddenCells,
                         hiddenNumbersPositions, textSize, name, pictureId);
    }

    private static HiddenNumberCell generateHiddenNumberCell(int number, List<Integer> ocuppiedRows, List<Integer> ocuppiedColumns,
                             int rowsCount, int columnsCount) {
        while (true) {
            int row = new Random().nextInt(rowsCount);
            int column = new Random().nextInt(columnsCount);
            if (!(ocuppiedRows.contains(row) && ocuppiedColumns.contains(column))) {
                ocuppiedRows.add(row);
                ocuppiedColumns.add(column);
                return new HiddenNumberCell(number, column, row);
            }
        }
    }
}
