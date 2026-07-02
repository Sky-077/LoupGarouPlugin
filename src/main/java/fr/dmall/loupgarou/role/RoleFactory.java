package fr.dmall.loupgarou.role;

import fr.dmall.loupgarou.role.village.VillageoisRole;

public class RoleFactory {

    private RoleFactory() {
    }

    public static Role create(Role role) {

        if (role instanceof VillageoisRole) {
            return new VillageoisRole();
        }

        throw new IllegalArgumentException("Rôle inconnu : " + role.getClass().getSimpleName());
    }

}