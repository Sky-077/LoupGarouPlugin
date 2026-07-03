package fr.dmall.loupgarou.role.village;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PetiteFilleRole extends Role {

    public static final int INVISIBILITY_DURATION_TICKS = 20 * 60 * 5; // 5 minutes

    public PetiteFilleRole() {
        super("Petite Fille", RoleTeam.VILLAGE);
    }

    @Override
    public void onNight(Player player) {

        // Vérification au tout début de la nuit, au cas où elle serait déjà sans armure.
        if (hasNoArmor(player)) {
            applyInvisibility(player);
        }

    }

    @Override
    public void onDay(Player player) {
        removeInvisibility(player);
    }

    public void applyInvisibility(Player player) {

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.INVISIBILITY,
                INVISIBILITY_DURATION_TICKS,
                0,
                false,
                false
        ));

    }

    public void removeInvisibility(Player player) {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    public boolean hasNoArmor(Player player) {

        PlayerInventory inventory = player.getInventory();

        return inventory.getHelmet() == null
                && inventory.getChestplate() == null
                && inventory.getLeggings() == null
                && inventory.getBoots() == null;

    }

}