package fr.dmall.loupgarou.role.village;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;

public class ChasseurRole extends Role {

    private boolean shotAvailable = true;

    public ChasseurRole() {
        super("Chasseur", RoleTeam.VILLAGE);
    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "Vous recevez un arc enchanté Puissance IV et des flèches.",
                "Quand vous recevez un coup mortel, vous disposez de 15 secondes avant de mourir réellement.",
                "Pendant ce délai, ripostez une dernière fois avec /lg tirer <joueur> (1 fois par partie).",
        };
    }

    public boolean isShotAvailable() {
        return shotAvailable;
    }

    public void consumeShot() {
        shotAvailable = false;
    }

}
