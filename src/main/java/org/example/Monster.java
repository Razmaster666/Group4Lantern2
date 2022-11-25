package org.example;

public class Monster {
    private int col;
    private int row;

    private char symbol;
    private int previousCol;
    private int previousRow;

    public Monster(int col, int row, char symbol) {
        this.col = col;
        this.row = row;
        this.symbol = symbol;
        this.previousCol = col;
        this.previousRow = row;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public char getSymbol() {
        return symbol;
    }

    public int getPreviousCol() {
        return previousCol;
    }

    public int getPreviousRow() {
        return previousRow;
    }

    public void moveTowards(Position position) {

        previousCol = col;
        previousRow = row;

        int diffCol = this.col - position.getCol();
        int absDiffCol = Math.abs(diffCol);
        int diffRow = this.row - position.getRow();
        int absDiffRow = Math.abs(diffRow);

        if (absDiffCol > absDiffRow) {
            if (diffCol < 0) {
                this.col += 1;
            } else {
                this.col -= 1;
            }
        } else if(absDiffCol < absDiffRow){
            if(diffRow < 0){
                this.col +=1;
            }else {
                this.col -= 1;
            }
        } else {
            if(diffCol < 0){
                this.col +=1;
            } else {
                this.col -= 1;
            }
            if(diffRow < 0){
                this.row += 1;
            }else {
                this.row -= 1;
            }
        }

    }
    @Override
    public String toString() {
        return "Monster{" +
                "col=" + col +
                ", row=" + row +
                ", symbol=" + symbol +
                ", previousCol=" + previousCol +
                ", previousRow=" + previousRow +
                '}';
    }
}
