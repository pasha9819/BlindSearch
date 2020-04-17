package ru.pasha.pt.lab3.game;

public class State {
    private String[] playersInfo;
    private String winner;

    public State(int playersCount) {
        playersInfo = new String[playersCount];
    }

    public String[] getPlayersInfo() {
        return playersInfo;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void set(int i, String info) {
        playersInfo[i] = info;
    }
}
