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
                "Vous recevez un arc enchanté Puissance IV et des flèches.",
                "Vous infligez un bonus de dégâts contre le camp des Loups-Garous (Force 0.5), qui augmente de 0.1 à chaque Loup tué, jusqu'à Force I maximum.",
                "Quand vous recevez un coup mortel, vous disposez de 15 secondes avant de mourir réellement.",
                "Pendant ce délai, ripostez avec /lg tirer <joueur> (autre que votre tueur) pour lui faire perdre 6 cœurs (1 fois par partie).",
                "Si un Loup-Garou ou un Père des Loups vous tue, il ne reçoit pas son bonus de Speed/Absorption habituel.",
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
