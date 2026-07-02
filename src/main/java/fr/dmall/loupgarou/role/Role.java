package fr.dmall.loupgarou.role;

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

    public void onGameStart() {

    }

    public void onDeath() {

    }

    public void onDay() {

    }

    public void onNight() {

    }

}