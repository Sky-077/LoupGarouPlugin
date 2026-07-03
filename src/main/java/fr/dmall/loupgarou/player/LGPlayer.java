package fr.dmall.loupgarou.player;

import fr.dmall.loupgarou.role.Role;

import java.util.UUID;

public class LGPlayer {

    private final UUID uuid;

    private boolean alive;
    private int kills;
    private int diamonds;
    private boolean joined;

    private Role role;

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
        kills++;
    }

    public int getDiamonds() {
        return diamonds;
    }

    public void addDiamonds(int amount) {
        diamonds += amount;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

}