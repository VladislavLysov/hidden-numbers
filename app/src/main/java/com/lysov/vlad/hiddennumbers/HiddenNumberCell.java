package com.lysov.vlad.hiddennumbers;


public class HiddenNumberCell {
    private int number;
    private int column;
    private int row;

    public HiddenNumberCell(int number, int column, int row) {
        this.number = number;
        this.column = column;
        this.row = row;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
