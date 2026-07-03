package fr.dmall.loupgarou.role.loup;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LoupGarouRole extends Role {

    public LoupGarouRole() {
        super("Loup-Garou", RoleTeam.LOUP);
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

}