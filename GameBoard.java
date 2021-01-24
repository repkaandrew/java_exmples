package ua.repka.main;

import javax.swing.*;
import java.awt.*;

public class GameBoard extends JFrame {
    static final char EMPTY_SIGN = Character.MIN_VALUE;
    static final int DIMENSION = 3;
    static final int CELL_SIZE = 150;
    static boolean isTwoPlayersGame = false;
    static boolean isCompFirstTurn = true;
    private char[][] gameBoard;
    private GameButton[] gameButtons;
    private Game currentGame;
    private JRadioButton setEasyLevel;
    private JRadioButton setMediumLevel;
    private JRadioButton setHardLevel;
    private JToggleButton compsFirstTurn;


    public GameBoard(Game game) {
        this.currentGame = game;
        this.initialize();
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    /**
     * Инициализация содержимого панели
     */
    public void initialize() {
        this.setTitle("TicTacToe");
        int paneSize = CELL_SIZE * DIMENSION;
        this.setBounds(paneSize, paneSize, 450, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        JButton newGameButton = new JButton("New Game");
        newGameButton.setFocusPainted(false);
        newGameButton.addActionListener(e -> this.cleanBoard());
        setEasyLevel = new JRadioButton("Easy", true);
        setMediumLevel = new JRadioButton("Medium", false);
        setHardLevel = new JRadioButton("Hard", false);

        JToggleButton twoPlayersGame = new JToggleButton("Два игрока", false);
        twoPlayersGame.setFocusPainted(false);
        twoPlayersGame.addItemListener(e -> isTwoPlayersGame = e.getStateChange() == e.SELECTED);

        compsFirstTurn = new JToggleButton(" Первый ход компа", false);
        compsFirstTurn.setFocusPainted(false);
        compsFirstTurn.addItemListener(e -> {
            if (e.getStateChange() == e.SELECTED){
                if (!twoPlayersGame.isSelected() && isEmptyBoard()){
                    currentGame.nextCompTurn(this);
                    currentGame.nextTurn();
                }
            }
        });

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(setEasyLevel);
        buttonGroup.add(setMediumLevel);
        buttonGroup.add(setHardLevel);
        controlPanel.setLayout(new GridLayout(1, 6));
        controlPanel.add(newGameButton);
        controlPanel.add(compsFirstTurn);
        controlPanel.add(twoPlayersGame);
        controlPanel.add(setEasyLevel);
        controlPanel.add(setMediumLevel);
        controlPanel.add(setHardLevel);
        controlPanel.setSize(paneSize, 300);

        JPanel mainBoardPanel = new JPanel();
        mainBoardPanel.setLayout(new GridLayout(DIMENSION, DIMENSION));
        mainBoardPanel.setSize(paneSize, paneSize);

        gameBoard = new char[DIMENSION][DIMENSION];
        gameButtons = new GameButton[(int) Math.pow(DIMENSION, 2)];

        for (int i = 0; i < gameButtons.length; i++) {
            GameButton gameButton = new GameButton(i, this);
            gameButton.setFocusPainted(false);
            gameButton.setFont(new Font("Arial", Font.BOLD, 50));
            gameButtons[i] = gameButton;
            mainBoardPanel.add(gameButton);
        }
        container.add(controlPanel, BorderLayout.NORTH);
        container.add(mainBoardPanel, BorderLayout.CENTER);
        this.setVisible(true);

    }

    public void cleanBoard() {
        for (int i = 0; i < gameButtons.length; i++) {
            gameButtons[i].setText(" ");
            int currRow = i / DIMENSION;
            int currColumn = i % DIMENSION;
            gameBoard[currRow][currColumn] = EMPTY_SIGN;
            currentGame.initialize();
        }
    }

    public boolean isAvailable(int currRow, int currColumn) {
        return gameBoard[currRow][currColumn] == EMPTY_SIGN;
    }

    /**
     * Метод записи символа на доску
     *
     * @param currRow
     * @param currColumn
     * @param sign
     */
    public void setSignOnTheBoard(int currRow, int currColumn, char sign) {
        gameBoard[currRow][currColumn] = sign;
    }

    /**
     * Метод проверяет, является ли текущая комбинация на доске выигрышной
     *
     * @param sign
     * @return
     */
    @SuppressWarnings("Duplicates")
    public boolean isWinner(char sign) {
        boolean isWin = false;
        for (int i = 0; i < DIMENSION; i++) {
            if ((gameBoard[i][0] == sign && gameBoard[i][1] == sign && gameBoard[i][2] == sign)
                    || (gameBoard[0][i] == sign && gameBoard[1][i] == sign && gameBoard[2][i] == sign)) {
                isWin = true;
                break;
            }
        }
        if (!isWin) {
            if ((gameBoard[0][0] == sign && gameBoard[1][1] == sign && gameBoard[2][2] == sign)
                    || (gameBoard[0][2] == sign && gameBoard[1][1] == sign && gameBoard[2][0] == sign)) {
                isWin = true;
            }
        }
        return isWin;
    }

    public boolean isFullBoard() {
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                if (gameBoard[i][j] == EMPTY_SIGN) return false;
            }
        }
        return true;
    }
    public boolean isEmptyBoard(){
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                if (gameBoard[i][j] != EMPTY_SIGN) return false;
            }
        }
        return true;
    }

    /**
     * Метод возвращает объект кнопки из одномерного массива по индексу
     *
     * @param index
     * @return
     */
    public GameButton getButtonByIndex(int index) {
        return gameButtons[index];
    }

    /**
     * Метод возвращает символ на доске из двумерного массива по индексам
     *
     * @param currRow
     * @param currColumn
     * @return
     */
    public char getSymbolOnTheBoard(int currRow, int currColumn) {
        return gameBoard[currRow][currColumn];
    }

    /**
     * Метод возвращает текущий уровень сложности
     *
     * @return
     */
    public CompLevel getComputerLevel() {
        if (setMediumLevel.isSelected()) return CompLevel.MEDIUM;
        else if (setHardLevel.isSelected()) return CompLevel.HARD;
        else return CompLevel.EASY;
    }

    public void setCompsFirstTurn(boolean state) {
        this.compsFirstTurn.setSelected(state);
    }
}
