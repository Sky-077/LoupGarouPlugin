package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.player.LGPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {

    private GameState state;
    private final List<LGPlayer> players;
    private long startTimeMillis;
    private int episode;
    private boolean pvpEnabled;
    private boolean revealed;

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

    public void markStarted() {
        this.startTimeMillis = System.currentTimeMillis();
        this.episode = 1;
        this.pvpEnabled = false;
        this.revealed = false;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void reveal() {
        revealed = true;
    }

    public boolean isPvpEnabled() {
        return pvpEnabled;
    }

    public void enablePvp() {
        pvpEnabled = true;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public int getEpisode() {
        return episode;
    }

    public void incrementEpisode() {
        episode++;
    }

    public List<LGPlayer> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public void addPlayer(LGPlayer player) {
        if (!players.contains(player)) {
            players.add(player);
        }
    }

    public void removePlayer(LGPlayer player) {
        players.remove(player);
    }

    public void clearPlayers() {
        players.clear();
    }
}