package fr.dmall.loupgarou.role.village;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;

public class VillageoisRole extends Role {

    public VillageoisRole() {
        super("Villageois", RoleTeam.VILLAGE);
    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "Vous n'avez aucun pouvoir spécial.",
                "Survivez et tentez de démasquer les Loups-Garous en combat direct.",
        };
    }

}