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
                "Votre victoire est strictement individuelle.",
                "Porter une Flûte sur vous charme automatiquement les joueurs situés à moins de 20 blocs, à raison de +1% toutes les 4 secondes.",
                "Un coup au corps-à-corps ajoute instantanément 10% de charme à la cible touchée (une seule fois par joueur).",
                "Craftez des Flûtes supplémentaires (8 lingots d'or autour d'un bâton) et transmettez-en une par clic droit sur un joueur visé (portée 5 blocs, une seule par joueur).",
                "Le joueur qui reçoit une Flûte charme à son tour les autres, sans le savoir, deux fois plus lentement que vous, et ne peut plus s'en séparer (il ne l'apprendra qu'au début de l'épisode suivant).",
                "Des bonus cumulatifs se débloquent selon le nombre total de joueurs charmés à 100% (comptés même après leur mort) : 3 → +2 cœurs, 6 → Force 0.5, 9 → Force I + Speed 0.5, 12 → +1 cœur, la totalité des vivants (au moins 6) → Résistance I.",
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
