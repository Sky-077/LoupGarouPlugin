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
                "Vous spawnez avec un arc, un livre enchanté Puissance V et des flèches : combinez-les sur une enclume.",
                "Une fois par partie, liez deux joueurs par l'amour avec /lg lier <joueur1> <joueur2>.",
                "Si l'un des deux meurt, l'autre meurt aussi de chagrin.",
                "S'ils sont de camps opposés, ils forment avec vous un camp Amoureux à part, qui gagne en étant le dernier camp survivant.",
        };
    }

    public boolean isPowerAvailable() {
        return powerAvailable;
    }

    public void consumePower() {
        powerAvailable = false;
    }

}
