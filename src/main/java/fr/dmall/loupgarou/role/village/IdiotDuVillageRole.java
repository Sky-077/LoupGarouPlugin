package fr.dmall.loupgarou.role.village;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;

public class IdiotDuVillageRole extends Role {

    private boolean reviveAvailable = true;

    public IdiotDuVillageRole() {
        super("Idiot du Village", RoleTeam.VILLAGE);
    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "Vous devez gagner avec le Village.",
                "Si un joueur qui n'appartient pas au camp des Loups-Garous vous tue, vous survivez (1 fois par partie) avec 8 cœurs de vie, et tout le monde apprend que vous êtes l'Idiot du Village.",
                "Face à un Loup-Garou ou un Père des Loups, vous mourez normalement.",
        };
    }

    public boolean isReviveAvailable() {
        return reviveAvailable;
    }

    public void consumeRevive() {
        reviveAvailable = false;
    }

}
