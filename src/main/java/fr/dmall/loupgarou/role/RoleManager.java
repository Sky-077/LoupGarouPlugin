package fr.dmall.loupgarou.role;

import fr.dmall.loupgarou.manager.Manager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.role.village.VillageoisRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoleManager implements Manager {

    private final List<Role> roles = new ArrayList<>();

    @Override
    public void enable() {

        register(new VillageoisRole());

    }

    @Override
    public void disable() {

        roles.clear();

    }

    public void register(Role role) {
        roles.add(role);
    }

    public List<Role> getRoles() {
        return Collections.unmodifiableList(roles);
    }

    public void assignDefaultRole(LGPlayer player) {
        player.setRole(new VillageoisRole());
    }

}