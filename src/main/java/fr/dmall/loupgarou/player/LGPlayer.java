package fr.dmall.loupgarou.player;

import java.util.UUID;

public class LGPlayer {

    private final UUID uuid;

    private boolean alive;
    private int kills;

    public LGPlayer(UUID uuid) {
        this.uuid = uuid;
        this.alive = true;
        this.kills = 0;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        this.kills++;
    }
}