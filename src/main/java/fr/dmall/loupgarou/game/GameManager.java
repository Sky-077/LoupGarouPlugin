package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.manager.Manager;

public class GameManager implements Manager {

    private Game currentGame;

    @Override
    public void enable() {
        currentGame = new Game();
    }

    @Override
    public void disable() {
        currentGame = null;
    }

    public Game getCurrentGame() {
        return currentGame;
    }
}