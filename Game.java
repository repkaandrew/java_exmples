package ua.repka.main;

import javax.swing.*;
import java.util.Random;

public class Game {
    private GameBoard board;
    private GamePlayer[] gamePlayers = new GamePlayer[2];
    private int playersTurn;

    public Game() {
        this.board = new GameBoard(this);
    }

    public void initialize() {
        gamePlayers[0] = new GamePlayer('X');
        gamePlayers[1] = new GamePlayer(false, 'O');
        playersTurn = 0;
        board.setCompsFirstTurn(false);
    }

    public void nextTurn() {
        this.playersTurn = (this.playersTurn == 0) ? 1 : 0;
    }

    public GamePlayer getCurrentGamePlayer() {
        return gamePlayers[playersTurn];
    }

    public GamePlayer getNextGamePlayer() {
        return (this.playersTurn == 0) ? gamePlayers[1] : gamePlayers[0];
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(board, message);
    }


    /**
     * Метод реализует ход компьютера. При этом учитывается три уровня сложности:
     * 1. Простой - ход генерируется случайно;
     * 2. Средний - ход генерируется с учетом выигрыша на следующем ходу либо по рейтингу ячеек
     * 3. Сложный - ход генерируется с учетом выигрыша соперника на два хода вперед.
     *
     * @param gameBoard
     */
    void nextCompTurn(GameBoard gameBoard) {
        char compSign = gameBoard.getCurrentGame().getCurrentGamePlayer().getPlayersSign();
        char humSign = gameBoard.getCurrentGame().getNextGamePlayer().getPlayersSign();
        if (gameBoard.getComputerLevel() == CompLevel.MEDIUM) {
            if (!findWinnerCell(compSign, compSign, gameBoard)) {
                if (!findWinnerCell(humSign, compSign, gameBoard)) {
                    findRatingCell(gameBoard);
                }
            }
        } else if (gameBoard.getComputerLevel() == CompLevel.HARD) {
            if (!findWinnerCell(compSign, compSign, gameBoard)) {
                if (!findWinnerCell(humSign, compSign, gameBoard)) {
                    if (!findDoubleWinCell(humSign, gameBoard, true))
                        if (!findDoubleWinCell(compSign, gameBoard, true))
                            findRatingCell(gameBoard);
                }
            }
        } else findRandCell(gameBoard);
    }

    /**
     * Метод для нахождения выигрышной комбинации
     *
     * @param winnerSign символ для которого нужно найти выигрышную комбинацию
     * @param setSign    символ, который будет записан на игральную доску(если выигрыш компьютера - ставим символ компа,
     *                   если выигрыш человека - ставим символ компьютера)
     * @return возвращает истину, если доска заполнена символом т.е. выигрышная комбинация найдена.
     */
    private boolean findWinnerCell(char winnerSign, char setSign, GameBoard gameBoard) {
        for (int i = 0; i < GameBoard.DIMENSION; i++) {
            for (int j = 0; j < GameBoard.DIMENSION; j++) {
                if (gameBoard.isAvailable(i, j)) {
                    gameBoard.setSignOnTheBoard(i, j, winnerSign);
                    if (gameBoard.isWinner(winnerSign)) {
                        gameBoard.setSignOnTheBoard(i, j, setSign);
                        setSymbolOnTheButton(gameBoard, i, j);
                        return true;
                    } else gameBoard.setSignOnTheBoard(i, j, GameBoard.EMPTY_SIGN);
                }
            }
        }
        return false;
    }

    /**
     * Метод нахождения хода для предотвращения двойной выигрышной комбинации
     *
     * @param winnerSign
     * @param gameBoard
     * @return
     */
    private boolean findDoubleWinCell(char winnerSign, GameBoard gameBoard, boolean setSign) {
        for (int i = 0; i < GameBoard.DIMENSION; i++) {
            for (int j = 0; j < GameBoard.DIMENSION; j++) {
                if (gameBoard.isAvailable(i, j)) {
                    gameBoard.setSignOnTheBoard(i, j, winnerSign);
                    int winCombinationCounter = findNumbOfWinComb(winnerSign, gameBoard);
                    if (winCombinationCounter == 2) {
                        gameBoard.setSignOnTheBoard(i, j, gameBoard.getCurrentGame().getCurrentGamePlayer().getPlayersSign());
                        if (!findDoubleWinCell(winnerSign, gameBoard, false)) {
                            if (setSign) setSymbolOnTheButton(gameBoard, i, j);
                            else gameBoard.setSignOnTheBoard(i, j, GameBoard.EMPTY_SIGN);
                        } else {
                            gameBoard.setSignOnTheBoard(i, j, GameBoard.EMPTY_SIGN);
                            findRandCell(gameBoard);
                        }
                        return true;
                    } else gameBoard.setSignOnTheBoard(i, j, GameBoard.EMPTY_SIGN);
                }
            }
        }
        return false;
    }

    /**
     * Finding number of win combinations
     *
     * @param winnerSign
     * @param gameBoard
     * @return
     */
    private int findNumbOfWinComb(char winnerSign, GameBoard gameBoard) {
        int winCombinationCounter = 0;
        for (int k = 0; k < GameBoard.DIMENSION; k++) {
            for (int l = 0; l < GameBoard.DIMENSION; l++) {
                if (gameBoard.isAvailable(k, l)) {
                    gameBoard.setSignOnTheBoard(k, l, winnerSign);
                    if (gameBoard.isWinner(winnerSign)) winCombinationCounter++;
                    gameBoard.setSignOnTheBoard(k, l, GameBoard.EMPTY_SIGN);
                }
            }
        }
        return winCombinationCounter;
    }

    /**
     * Метод выполнения хода по "рейтинговому" алгоритму - составляем массив, смежный с игральной доской.
     * для каждой ячейки считаем число соседних валидных ячеек. Если ячейка не пуста записываем 0.
     * Далее выбираем ячейку с максимальным числом соседей и заполняем соотв. символом.
     * При отсутствии соседей выбираем ячейку либо центр, либо 1 1, если эти заняты - выбираем случайно.
     */
    private void findRatingCell(GameBoard gameBoard) {
        int[][] moveRatingTable = new int[GameBoard.DIMENSION][GameBoard.DIMENSION];
        int maxRating = 0;
        char compSign = gameBoard.getCurrentGame().getCurrentGamePlayer().getPlayersSign();
        for (int i = 0; i < GameBoard.DIMENSION; i++) {
            for (int j = 0; j < GameBoard.DIMENSION; j++) {
                int numbOfCellsWithSign;
                if (gameBoard.isAvailable(i, j)) {
                    numbOfCellsWithSign = countNearCells(i, j, gameBoard);
                    if (numbOfCellsWithSign > maxRating) maxRating = numbOfCellsWithSign;
                } else numbOfCellsWithSign = -1;
                moveRatingTable[i][j] = numbOfCellsWithSign;
            }
        }
        if (maxRating > 0) {
            for (int i = 0; i < GameBoard.DIMENSION; i++) {
                for (int j = 0; j < GameBoard.DIMENSION; j++) {
                    if (moveRatingTable[i][j] == maxRating) {
                        gameBoard.setSignOnTheBoard(i, j, compSign);
                        setSymbolOnTheButton(gameBoard, i, j);
                        return;
                    }
                }
            }
        } else {
            if (gameBoard.isAvailable(1, 1)) {
                gameBoard.setSignOnTheBoard(1, 1, compSign);
                setSymbolOnTheButton(gameBoard, 1, 1);
            } else if (gameBoard.isAvailable(0, 0)) {
                gameBoard.setSignOnTheBoard(0, 0, compSign);
                setSymbolOnTheButton(gameBoard, 0, 0);
            } else if (gameBoard.isAvailable(0, 2)) {
                gameBoard.setSignOnTheBoard(0, 2, compSign);
                setSymbolOnTheButton(gameBoard, 0, 2);
            }else {
                findRandCell(gameBoard);
            }
        }
    }

    /**
     * Метод нахождения числа соседних ячеек
     *
     * @param x координата текущей проверяемой ячейки
     * @param y координата текущей проверяемой ячейки
     * @return возвращает число валидных соседей
     */
    private int countNearCells(int x, int y, GameBoard gameBoard) {
        int numbOfNearCellsWithTheSameSign = 0;
        for (int i = 0; i < GameBoard.DIMENSION; i++) {
            for (int j = 0; j < GameBoard.DIMENSION; j++) {
                if (gameBoard.getSymbolOnTheBoard(i, j) == gameBoard.getCurrentGame().getCurrentGamePlayer().getPlayersSign()
                        && Math.abs(i - x) <= 1 && Math.abs(j - y) <= 1) {
                    numbOfNearCellsWithTheSameSign++;
                }
            }
        }
        return numbOfNearCellsWithTheSameSign;
    }

    /**
     * Метод нахождения случайной ячейки
     *
     * @param gameBoard
     */
    private void findRandCell(GameBoard gameBoard) {
        int row;
        int column;
        char compSign = gameBoard.getCurrentGame().getCurrentGamePlayer().getPlayersSign();
        boolean permission;
        if (gameBoard.getComputerLevel() != CompLevel.HARD) {
            do {
                row = new Random().nextInt(GameBoard.DIMENSION);
                column = new Random().nextInt(GameBoard.DIMENSION);
                permission = gameBoard.isAvailable(row, column);
            } while (!permission);
        } else {
            do {
                row = new Random().nextInt(GameBoard.DIMENSION);
                column = new Random().nextInt(GameBoard.DIMENSION);
                if (gameBoard.getSymbolOnTheBoard(1, 1) == gameBoard.getCurrentGame().getCurrentGamePlayer().getPlayersSign())
                    permission = gameBoard.isAvailable(row, column) && !isCellInCorner(row, column);
                else permission = gameBoard.isAvailable(row, column) && isCellInCorner(row, column);
            } while (!permission);
        }
        gameBoard.setSignOnTheBoard(row, column, compSign);
        setSymbolOnTheButton(gameBoard, row, column);
    }

    private boolean isCellInCorner(int row, int column) {
        return ((row == 0 && column == 0) || (row == 0 && column == 2) || (row == 2 && column == 0) || (row == 2 && column == 2));
    }

    /**
     * Метод определения выигрыша, если выигрыш не найден, то перебрасывается ход.
     *
     * @param gameBoard
     * @return
     */
    boolean isWinOrTurn(GameBoard gameBoard) {
        if (gameBoard.isWinner(gameBoard.getCurrentGame().getCurrentGamePlayer().getPlayersSign())) {
            gameBoard.getCurrentGame().showMessage(gameBoard.getCurrentGame().getCurrentGamePlayer().getPlayersSign() + " WIN!!!");
            gameBoard.cleanBoard();
            return true;
        } else {
            gameBoard.getCurrentGame().nextTurn();
            return false;
        }
    }

    /**
     * Метод установки символа на графичкескую доску
     *
     * @param gameBoard
     * @param row
     * @param column
     */
    private void setSymbolOnTheButton(GameBoard gameBoard, int row, int column) {
        int buttonIndex = GameBoard.DIMENSION * row + column;
        gameBoard.getButtonByIndex(buttonIndex).setText(Character.toString(gameBoard.getCurrentGame().getCurrentGamePlayer().getPlayersSign()));
    }
}





