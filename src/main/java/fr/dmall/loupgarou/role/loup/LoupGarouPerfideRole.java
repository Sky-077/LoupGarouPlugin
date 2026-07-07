package fr.dmall.loupgarou.role.loup;

import fr.dmall.loupgarou.role.NightInvisibilityRole;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class LoupGarouPerfideRole extends WolfRole implements NightInvisibilityRole {

    public static final int INVISIBILITY_DURATION_TICKS = 20 * 60 * 5; // 5 minutes

    private boolean powerAvailable = true;

    public LoupGarouPerfideRole() {
        super("Loup-Garou Perfide");
    }

    @Override
    public void onNight(Player player) {

        super.onNight(player);

        powerAvailable = true;

        // Vérification au tout début de la nuit, au cas où il serait déjà sans armure.
        if (hasNoArmor(player)) {
            tryActivateInvisibility(player);
        }

    }

    @Override
    public void onDay(Player player) {
        super.onDay(player);
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

    @Override
    public String[] getInstructions() {

        List<String> lines = new ArrayList<>();
        lines.add("Vous recevez Force I chaque nuit, comme un loup.");
        lines.add("Éliminez les villageois en combat direct (PVP libre, pas de vote ni de ciblage).");
        lines.add("En restant à proximité d'un joueur, vous le corrompez (1% toutes les 5 secondes).");
        lines.add("Retirez toute votre armure pendant la nuit pour devenir invisible durant 5 minutes (1 fois par nuit).");
        lines.add("Remettre une pièce d'armure annule l'effet pour le reste de la nuit.");
        lines.add("Pendant l'invisibilité, vous générez des particules visibles par la Petite Fille et le Feu Follet, et vous voyez aussi les leurs.");
        lines.addAll(getWolfPackLines());

        return lines.toArray(new String[0]);

    }

}
