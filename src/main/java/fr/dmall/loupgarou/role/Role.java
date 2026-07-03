package fr.dmall.loupgarou.role;

import org.bukkit.entity.Player;

public abstract class Role {

    private final String name;
    private final RoleTeam team;

    protected Role(String name, RoleTeam team) {
        this.name = name;
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public RoleTeam getTeam() {
        return team;
    }

    public void onGameStart(Player player) {

    }

    public void onDeath(Player player) {

    }

    public void onDay(Player player) {

    }

    public void onNight(Player player) {

    }

}