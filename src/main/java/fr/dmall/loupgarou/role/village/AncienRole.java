package fr.dmall.loupgarou.role.village;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;

public class AncienRole extends Role {

    private boolean resistanceAvailable = true;
    private boolean reviveAvailable = true;

    public AncienRole() {
        super("Ancien", RoleTeam.VILLAGE);
    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "Vous devez gagner avec le Village.",
                "Vous disposez de l'effet Résistance 0.5 (-10% de dégâts subis) en permanence.",
                "Si un Loup-Garou ou un Père des Loups vous tue, vous ressuscitez (1 fois par partie) avec votre vie actuelle, mais perdez définitivement votre Résistance 0.5.",
                "Si un villageois (ni Loup, ni solitaire) vous tue, vous mourez normalement, mais votre tueur perd la moitié de sa vie maximale, définitivement.",
                "Face à un solitaire, vous mourez normalement, sans effet particulier.",
        };
    }

    public boolean isResistanceAvailable() {
        return resistanceAvailable;
    }

    public void loseResistance() {
        resistanceAvailable = false;
    }

    public boolean isReviveAvailable() {
        return reviveAvailable;
    }

    public void consumeRevive() {
        reviveAvailable = false;
    }

}
