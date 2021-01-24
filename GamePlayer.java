package ua.repka.main;

public class GamePlayer {
    private boolean isPlayerReal;
    private char playersSign;

    GamePlayer(char playersSign) {
        this(true, playersSign);
    }

    GamePlayer(boolean isPlayerReal, char playersSign) {
        this.isPlayerReal = isPlayerReal;
        this.playersSign = playersSign;
    }

    public boolean isPlayerReal() {
        return isPlayerReal;
    }

    char getPlayersSign() {
        return playersSign;
    }
}
