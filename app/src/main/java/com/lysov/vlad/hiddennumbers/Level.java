package com.lysov.vlad.hiddennumbers;


import java.util.HashMap;
import java.util.Map;

public class Level {

    private int levelNumber;
    private int rowsCount;
    private int columnsCount;
    private int hiddenNumbersCount;
    private int timeToShowHiddenCells;
    private Map<Integer, HiddenNumberCell> hiddenNumbersPositions= new HashMap<>();

    public Level(int rowsCount, int columnsCount, int hiddenNumbersCount) {
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
        this.hiddenNumbersCount = hiddenNumbersCount;
    }

    public Level(int rowsCount, int columnsCount, int hiddenNumbersCount, Map<Integer, HiddenNumberCell> hiddenNumbersPositions) {
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
        this.hiddenNumbersCount = hiddenNumbersCount;
        this.hiddenNumbersPositions = hiddenNumbersPositions;
    }

    public Level(int levelNumber, int rowsCount, int columnsCount, int hiddenNumbersCount, Map<Integer, HiddenNumberCell> hiddenNumbersPositions) {
        this.levelNumber = levelNumber;
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
        this.hiddenNumbersCount = hiddenNumbersCount;
        this.hiddenNumbersPositions = hiddenNumbersPositions;
    }

    public Level(int levelNumber, int rowsCount, int columnsCount, int hiddenNumbersCount, int timeToShowHiddenCells, Map<Integer, HiddenNumberCell> hiddenNumbersPositions) {
        this.levelNumber = levelNumber;
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
        this.hiddenNumbersCount = hiddenNumbersCount;
        this.timeToShowHiddenCells = timeToShowHiddenCells;
        this.hiddenNumbersPositions = hiddenNumbersPositions;
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
        this.rowsCount = rowsCount;
    }

    public int getColumnsCount() {
        return columnsCount;
    }

    public void setColumnsCount(int columnsCount) {
        this.columnsCount = columnsCount;
    }

    public int getHiddenNumbersCount() {
        return hiddenNumbersCount;
    }

    public void setHiddenNumbersCount(int hiddenNumbersCount) {
        this.hiddenNumbersCount = hiddenNumbersCount;
    }

    public Map<Integer, HiddenNumberCell> getHiddenNumbersPositions() {
        return hiddenNumbersPositions;
    }

    public void setHiddenNumbersPositions(Map<Integer, HiddenNumberCell> hiddenNumbersPositions) {
        this.hiddenNumbersPositions = hiddenNumbersPositions;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public int getTimeToShowHiddenCells() {
        return timeToShowHiddenCells;
    }

    public void setTimeToShowHiddenCells(int timeToShowHiddenCells) {
        this.timeToShowHiddenCells = timeToShowHiddenCells;
    }
}
