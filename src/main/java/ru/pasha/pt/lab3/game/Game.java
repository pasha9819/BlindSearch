package ru.pasha.pt.lab3.game;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

import static java.lang.Math.*;

@Component
public class Game {
    private class Point {
        private double x, y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        private void go(double angle) {
            x += cos(angle / 180 * PI);
            y += sin(angle / 180 * PI);
        }

        private double getDistance() {
            return Math.sqrt(x * x + y * y);
        }
    }

    private static final double MAX_DISTANCE = 15;
    private static final double MIN_DISTANCE = 10;

    private static final int MAX_PLAYERS_COUNT = 2;
    private static final int STEP_TIME = 10000;
    private static final double WINNER_DISTANCE = 1.0;

    private long finishTime;
    private long lastStepTime;

    private int connectedPlayers = 0;

    private HashMap<String, Integer> idMap;

    private String winner;

    private Point[] points;

    /**
     * Флаг, отвечающий за состояние игры.
     * Если значение флага ложно -> ожидается подключение игроков.
     */
    private boolean onGame = false;

    /**
     * id игрока, который должен делать мледующий ход
     */
    private int nextPlayerId;

    private int lastPlayerId;

    public Game() {
        idMap = new HashMap<>();
        initPoints();
    }

    private void initPoints() {
        points = new Point[MAX_PLAYERS_COUNT];
        double x = MIN_DISTANCE + random() * (MAX_DISTANCE - MIN_DISTANCE);
        double y = MIN_DISTANCE + random() * (MAX_DISTANCE - MIN_DISTANCE);
        double t, x1, y1;
        for (int i = 0; i < points.length; i++) {
            t = i * PI / points.length;
            x1 = x * cos(t) - y * sin(t);
            y1 = -x * sin(t) + y * cos(t);
            points[i] = new Point(x1, y1);
        }
    }

    private static long now() {
        return System.currentTimeMillis();
    }

    public Token addNewPlayer() throws GameException {
        checkFinish();
        if (connectedPlayers == MAX_PLAYERS_COUNT) {
            throw new GameException("Все игровые места заняты, попробуйте позже");
        }
        String token = UUID.randomUUID().toString();
        idMap.put(token, connectedPlayers);
        connectedPlayers++;

        // начинаем игру
        if (connectedPlayers == MAX_PLAYERS_COUNT) {
            onGame = true;
            nextPlayerId = 0;
            lastStepTime = now();
        }

        return new Token(token);
    }

    public State step(String token, double angle) throws GameException {
        checkFinish();
        int id = getIdByTokenAndUpdateNextPlayerId(token);

        if (id != nextPlayerId) {
            throw new GameException("Сейчас ходит игрок " + (nextPlayerId + 1));
        }

        lastStepTime = now();

        Point p = points[id];
        p.go(angle);
        if (p.getDistance() < WINNER_DISTANCE) {
            winner = "Выиграл игрок " + (id + 1);
            finishTime = now();
        }
        lastPlayerId = nextPlayerId;
        nextPlayerId = (nextPlayerId + 1) % MAX_PLAYERS_COUNT;
        return getState(token);
    }

    public State getState(String token) throws GameException {
        checkFinish();
        int id = getIdByToken(token);

        State s = new State(MAX_PLAYERS_COUNT);
        for (int i = 0; i < MAX_PLAYERS_COUNT; i++) {
            String you = id == i ? "(Вы)" : "";
            String str = String.format("Игрок %d%s: %.3f", i + 1, you, points[i].getDistance());
            s.set(i, str);
        }
        if (winner != null) {
            s.setWinner(winner);
        }
        return s;
    }

    public boolean canStep(String token) throws GameException {
        checkFinish();
        int id = getIdByTokenAndUpdateNextPlayerId(token);
        return winner == null && id == nextPlayerId;
    }

    public boolean validateToken(String token) {
        return idMap.get(token) != null;
    }

    private int getIdByToken(String token) throws GameException {
        if (!onGame) {
            throw new GameException("В данный момент игра не проводится (завершена / еще не начата)");
        }

        Integer id = idMap.get(token);
        if (id == null) {
            throw new GameException("Вы не подключены к игре");
        }
        return id;
    }

    private int getIdByTokenAndUpdateNextPlayerId(String token) throws GameException {
        int id = getIdByToken(token);

        long timeBetweenSteps = now() - lastStepTime;
        if (timeBetweenSteps > STEP_TIME) {
            long t = timeBetweenSteps / STEP_TIME;
            nextPlayerId = (int) ((lastPlayerId + t) % MAX_PLAYERS_COUNT);
        }
        return id;
    }

    private void checkFinish() {
        if ((winner != null && now() - finishTime > STEP_TIME)
                || (onGame && now() - lastStepTime > 2 * MAX_PLAYERS_COUNT * STEP_TIME)) {
            finish();
        }
    }

    private void finish() {
        idMap.clear();
        nextPlayerId = 0;
        initPoints();
        winner = null;
        connectedPlayers = 0;
        onGame = false;
    }

}
