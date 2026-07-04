package fr.dmall.loupgarou.player;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;

import java.util.UUID;

public class LGPlayer {

    private final UUID uuid;

    private boolean alive;
    private int kills;
    private int diamonds;
    private int honor;
    private boolean joined;

    private Role role;
    private RoleTeam teamOverride;

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

    public int getHonor() {
        return honor;
    }

    public void setHonor(int honor) {
        this.honor = Math.max(-3, Math.min(3, honor));
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public RoleTeam getEffectiveTeam() {

        if (teamOverride != null) {
            return teamOverride;
        }

        return (role != null) ? role.getTeam() : null;

    }

    public void setTeamOverride(RoleTeam teamOverride) {
        this.teamOverride = teamOverride;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public void resetStats() {
        this.alive = true;
        this.kills = 0;
        this.diamonds = 0;
        this.honor = 0;
        this.joined = false;
        this.role = null;
        this.teamOverride = null;
    }

}
