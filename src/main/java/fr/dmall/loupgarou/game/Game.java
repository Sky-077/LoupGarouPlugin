package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.player.LGPlayer;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private GameState state;
    private final List<LGPlayer> players;

    public Game() {
        this.state = GameState.WAITING;
        this.players = new ArrayList<>();
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public List<LGPlayer> getPlayers() {
        return players;
    }

    public void addPlayer(LGPlayer player) {
        players.add(player);
    }

    public void removePlayer(LGPlayer player) {
        players.remove(player);
    }
}