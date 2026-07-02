package fr.dmall.loupgarou.role;

import fr.dmall.loupgarou.manager.Manager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.role.village.VillageoisRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoleManager implements Manager {

    private final List<Role> registeredRoles = new ArrayList<>();
    private final List<Role> gameRoles = new ArrayList<>();

    @Override
    public void enable() {

        register(new VillageoisRole());

    }

    @Override
    public void disable() {

        registeredRoles.clear();
        gameRoles.clear();

    }

    public void register(Role role) {
        registeredRoles.add(role);
    }

    public List<Role> getRegisteredRoles() {
        return Collections.unmodifiableList(registeredRoles);
    }

    public List<Role> getGameRoles() {
        return Collections.unmodifiableList(gameRoles);
    }

    public void clearGameRoles() {
        gameRoles.clear();
    }

    public void addGameRole(Role role) {
        gameRoles.add(role);
    }

    public void assignRoles(List<LGPlayer> players) {

        Collections.shuffle(gameRoles);

        for (int i = 0; i < players.size(); i++) {

            Role role;

            if (i < gameRoles.size()) {
                role = RoleFactory.create(gameRoles.get(i));
            } else {
                role = new VillageoisRole();
            }

            players.get(i).setRole(role);

        }

    }

}