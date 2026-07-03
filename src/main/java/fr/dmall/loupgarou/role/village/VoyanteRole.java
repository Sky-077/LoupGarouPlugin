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

    public boolean isPowerAvailable() {
        return powerAvailable;
    }

    public void consumePower() {
        powerAvailable = false;
    }

}
