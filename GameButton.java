package ua.repka.main;

import javax.swing.*;

public class GameButton extends JButton {
    private int buttonIndex;
    private GameBoard gameBoard;
    private int rowNumb;
    private int columnNumb;

    public GameButton(int buttonIndex, GameBoard gameBoard) {
        this.buttonIndex = buttonIndex;
        this.gameBoard = gameBoard;
        this.rowNumb = this.buttonIndex / GameBoard.DIMENSION;
        this.columnNumb = this.buttonIndex % GameBoard.DIMENSION;
        this.setSize(GameBoard.CELL_SIZE - 5, GameBoard.CELL_SIZE - 5);
        addActionListener(new GameActionListener(this.rowNumb, this.columnNumb, this));
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }
}
