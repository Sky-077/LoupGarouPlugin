package fr.dmall.loupgarou.role.village;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;

public class ChasseurRole extends Role {

    private static final double MAX_WOLF_STRENGTH = 1.0;
    private static final double WOLF_STRENGTH_STEP = 0.1;

    private boolean shotAvailable = true;
    private double wolfStrengthLevel = 0.5;

    public ChasseurRole() {
        super("Chasseur", RoleTeam.VILLAGE);
    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "Un arc enchanté Puissance IV, accompagné de flèches, vous est fourni.",
                "Contre le camp des Loups, vos coups portent un bonus de dégâts (Force 0.5 de base), qui progresse de 0.1 à chaque Loup abattu, jusqu'à un plafond de Force I.",
                "Un coup normalement fatal vous laisse 15 secondes de sursis avant la mort définitive.",
                "Durant ce sursis, /lg tirer <joueur> permet une dernière riposte sur une cible différente de votre tueur (une seule fois par partie) :",
                "  - Contre un Loup-Garou/Père des Loups : sa vie maximale chute de moitié, de façon irréversible.",
                "  - Contre un solitaire : sa vie maximale diminue d'un quart, de façon irréversible.",
                "  - Contre un villageois : il perd 6 cœurs de vie actuelle (récupérables).",
                "Vous éliminer ne procure à un Loup-Garou ou un Père des Loups aucun de leurs bonus habituels de Speed/Absorption.",
        };
    }

    public boolean isShotAvailable() {
        return shotAvailable;
    }

    public void consumeShot() {
        shotAvailable = false;
    }

    public double getWolfStrengthLevel() {
        return wolfStrengthLevel;
    }

    public void increaseWolfStrength() {
        wolfStrengthLevel = Math.min(MAX_WOLF_STRENGTH, Math.round((wolfStrengthLevel + WOLF_STRENGTH_STEP) * 10.0) / 10.0);
    }

}
