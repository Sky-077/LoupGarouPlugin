package fr.dmall.loupgarou.role.solo;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;

public class JoueurDeFluteRole extends Role {

    private boolean heart3Granted;
    private boolean strength6Granted;
    private boolean strength9Granted;
    private boolean speed9Granted;
    private boolean heart12Granted;
    private boolean resistanceGranted;

    public JoueurDeFluteRole() {
        super("Joueur de Flûte", RoleTeam.NEUTRAL);
    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "Vous devez gagner seul.",
                "Tant que vous portez une Flûte sur vous, vous charmez les joueurs à moins de 20 blocs (1% toutes les 4 secondes).",
                "Frapper un joueur au corps-à-corps augmente son charme de 10% (1 fois par joueur).",
                "Fabriquez des Flûtes (8 lingots d'or autour d'un bâton) et donnez-en une à un joueur visé par clic droit (portée 5 blocs, 1 flûte par joueur).",
                "Un joueur ayant reçu une Flûte charme les autres à son insu, 2 fois plus lentement que vous, sans pouvoir s'en débarrasser (notifié au début de l'épisode suivant).",
                "Paliers de joueurs charmés à 100% dans la partie (comptent même après leur mort) : 3 → +2 cœurs, 6 → Force 0.5, 9 → Force I + Speed 0.5, 12 → +1 cœur, tous les joueurs vivants (min. 6) → Résistance I.",
        };
    }

    public boolean isHeart3Granted() {
        return heart3Granted;
    }

    public void grantHeart3() {
        heart3Granted = true;
    }

    public boolean isStrength6Granted() {
        return strength6Granted;
    }

    public void grantStrength6() {
        strength6Granted = true;
    }

    public boolean isStrength9Granted() {
        return strength9Granted;
    }

    public void grantStrength9() {
        strength9Granted = true;
    }

    public boolean isSpeed9Granted() {
        return speed9Granted;
    }

    public void grantSpeed9() {
        speed9Granted = true;
    }

    public boolean isHeart12Granted() {
        return heart12Granted;
    }

    public void grantHeart12() {
        heart12Granted = true;
    }

    public boolean isResistanceGranted() {
        return resistanceGranted;
    }

    public void grantResistance() {
        resistanceGranted = true;
    }

}
