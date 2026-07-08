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
                "Votre camp est le Village, et sa victoire est la vôtre.",
                "Tant que celui qui vous porte le coup fatal n'appartient pas au camp des Loups, vous survivez automatiquement (une fois par partie) avec 8 cœurs de vie restants, et votre identité d'Idiot du Village est alors révélée à tous.",
                "Un Loup-Garou ou un Père des Loups, en revanche, vous tue sans recours.",
        };
    }

    public boolean isReviveAvailable() {
        return reviveAvailable;
    }

    public void consumeRevive() {
        reviveAvailable = false;
    }

}
