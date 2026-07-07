package fr.dmall.loupgarou.role.village;

import fr.dmall.loupgarou.role.NightInvisibilityRole;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PetiteFilleRole extends Role implements NightInvisibilityRole {

    public static final int INVISIBILITY_DURATION_TICKS = 20 * 60 * 5; // 5 minutes

    private boolean powerAvailable = true;

    public PetiteFilleRole() {
        super("Petite Fille", RoleTeam.VILLAGE);
    }

    @Override
    public void onNight(Player player) {

        powerAvailable = true;

        // Vérification au tout début de la nuit, au cas où elle serait déjà sans armure.
        if (hasNoArmor(player)) {
            tryActivateInvisibility(player);
        }

    }

    @Override
    public void onDay(Player player) {
        removeInvisibility(player);
    }

    @Override
    public void tryActivateInvisibility(Player player) {

        if (!powerAvailable) {
            player.sendMessage("§cVous avez déjà utilisé votre pouvoir cette nuit.");
            return;
        }

        powerAvailable = false;

        applyInvisibility(player);

    }

    public void applyInvisibility(Player player) {

        boolean alreadyInvisible = player.hasPotionEffect(PotionEffectType.INVISIBILITY);

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.INVISIBILITY,
                INVISIBILITY_DURATION_TICKS,
                0,
                false,
                false
        ));

        if (!alreadyInvisible) {
            player.sendMessage("§fVous êtes désormais invisible.");
        }

    }

    @Override
    public void removeInvisibility(Player player) {

        boolean wasInvisible = player.hasPotionEffect(PotionEffectType.INVISIBILITY);

        player.removePotionEffect(PotionEffectType.INVISIBILITY);

        if (wasInvisible) {
            player.sendMessage("§fVous n'êtes plus invisible.");
        }

    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "Retirez toute votre armure pendant la nuit pour devenir invisible durant 5 minutes.",
                "Remettre une pièce d'armure annule l'effet immédiatement.",
                "Ce pouvoir n'est utilisable qu'une fois par nuit.",
        };
    }

    @Override
    public boolean hasNoArmor(Player player) {

        PlayerInventory inventory = player.getInventory();

        return isEmpty(inventory.getHelmet())
                && isEmpty(inventory.getChestplate())
                && isEmpty(inventory.getLeggings())
                && isEmpty(inventory.getBoots());

    }

    private boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

}