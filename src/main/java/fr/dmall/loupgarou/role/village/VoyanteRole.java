package fr.dmall.loupgarou.role.village;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.entity.Player;

public class VoyanteRole extends Role {

    private boolean powerAvailable = true;

    public VoyanteRole() {
        super("Voyante", RoleTeam.VILLAGE);
    }

    @Override
    public void onNight(Player player) {
        powerAvailable = true;
    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "/lg sonder <joueur> dévoile le rôle d'un joueur, de jour comme de nuit.",
                "Ce pouvoir se recharge une fois par cycle jour/nuit.",
        };
    }

    public boolean isPowerAvailable() {
        return powerAvailable;
    }

    public void consumePower() {
        powerAvailable = false;
    }

}
