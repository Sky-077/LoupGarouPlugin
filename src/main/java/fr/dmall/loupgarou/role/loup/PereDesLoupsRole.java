package fr.dmall.loupgarou.role.loup;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PereDesLoupsRole extends Role {

    private boolean infectionAvailable = true;

    public PereDesLoupsRole() {
        super("Père des Loups", RoleTeam.LOUP);
    }

    @Override
    public void onNight(Player player) {

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.STRENGTH,
                PotionEffect.INFINITE_DURATION,
                0,
                false,
                false
        ));

    }

    @Override
    public void onDay(Player player) {

        player.removePotionEffect(PotionEffectType.STRENGTH);

    }

    public boolean isInfectionAvailable() {
        return infectionAvailable;
    }

    public void consumeInfection() {
        infectionAvailable = false;
    }

}
