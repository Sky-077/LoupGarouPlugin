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
                "Aucun pouvoir spécial ne vous est attribué.",
                "Votre seule arme est la survie : repérez les Loups-Garous et affrontez-les directement.",
        };
    }

}