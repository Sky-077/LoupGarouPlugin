package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.manager.Manager;

public class GameManager implements Manager {

    private GameState gameState;

    @Override
    public void enable() {
        gameState = GameState.WAITING;
    }

    @Override
    public void disable() {

    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}