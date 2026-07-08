package fr.dmall.loupgarou.role.village;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;

public class CupidonRole extends Role {

    private boolean powerAvailable = true;

    public CupidonRole() {
        super("Cupidon", RoleTeam.VILLAGE);
    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "Votre équipement de départ comprend un arc, des flèches et un livre enchanté Puissance III + Punch I, à assembler sur une enclume.",
                "/lg lier <joueur1> <joueur2> unit deux joueurs par les liens de l'amour, une seule fois par partie.",
                "La mort de l'un entraîne immédiatement celle de l'autre, de chagrin.",
                "Quel que soit leur camp d'origine, un camp Amoureux distinct se forme avec vous dès ce moment, et l'emporte en restant le dernier camp en lice.",
        };
    }

    public boolean isPowerAvailable() {
        return powerAvailable;
    }

    public void consumePower() {
        powerAvailable = false;
    }

}
