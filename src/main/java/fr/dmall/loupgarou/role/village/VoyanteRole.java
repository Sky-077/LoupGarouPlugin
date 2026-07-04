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
                "Vous pouvez sonder un joueur, de jour comme de nuit, pour découvrir son rôle avec /lg sonder <joueur>.",
                "Ce pouvoir n'est utilisable qu'une fois par cycle jour/nuit.",
        };
    }

    public boolean isPowerAvailable() {
        return powerAvailable;
    }

    public void consumePower() {
        powerAvailable = false;
    }

}
