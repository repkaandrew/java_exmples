package ua.repka.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameActionListener implements ActionListener {
    private int row;
    private int column;
    private GameButton gameButton;

    public GameActionListener(int row, int column, GameButton gameButton) {
        this.row = row;
        this.column = column;
        this.gameButton = gameButton;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    /**
     * Метод, который запускается при нажатии на кнопку поля человеком.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        GameBoard gameBoard = gameButton.getGameBoard();
        Game currentGame = gameBoard.getCurrentGame();
        if (gameBoard.isAvailable(row, column)) {
            nextHumTurn(gameBoard);
            if (!currentGame.isWinOrTurn(gameBoard)) {
                if (gameBoard.isFullBoard()) {
                    currentGame.showMessage("Ничья");
                    gameBoard.cleanBoard();
                } else {
                    if (!GameBoard.isTwoPlayersGame) {
                        currentGame.nextCompTurn(gameBoard);
                        currentGame.isWinOrTurn(gameBoard);
                        if (gameBoard.isFullBoard()) {
                            currentGame.showMessage("Ничья");
                            gameBoard.cleanBoard();
                        }
                    } else return;
                }
            }
        } else {
            currentGame.showMessage("Некоректный ввод");
        }
    }

    /**
     * Метод реализует ход человека
     *
     * @param gameBoard
     */
    private void nextHumTurn(GameBoard gameBoard) {
        char humSign = gameBoard.getCurrentGame().getCurrentGamePlayer().getPlayersSign();
        gameBoard.setSignOnTheBoard(row, column, humSign);
        gameButton.setText(Character.toString(humSign));
    }
}

