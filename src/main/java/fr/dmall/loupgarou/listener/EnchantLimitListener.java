package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.RoleTeam;
import fr.dmall.loupgarou.role.village.ChasseurRole;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EnchantLimitListener implements Listener {

    @EventHandler
    public void onPrepare(PrepareItemEnchantEvent event) {

        ItemStack item = event.getItem();
        Enchantment allowed = getCategoryEnchant(item.getType());
        Enchantment shown = (allowed != null) ? allowed : Enchantment.UNBREAKING;
        int cap = getCap(shown, item.getType(), event.getEnchanter());

        for (EnchantmentOffer offer : event.getOffers()) {

            if (offer == null) {
                continue;
            }

            offer.setEnchantment(shown);
            offer.setEnchantmentLevel(Math.min(Math.max(offer.getEnchantmentLevel(), 1), cap));

        }

    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {

        Enchantment allowed = getCategoryEnchant(event.getItem().getType());
        int categoryCap = (allowed != null) ? getCap(allowed, event.getItem().getType(), event.getEnchanter()) : 0;

        Map<Enchantment, Integer> filtered = new HashMap<>();

        for (Map.Entry<Enchantment, Integer> entry : event.getEnchantsToAdd().entrySet()) {

            if (entry.getKey().equals(Enchantment.UNBREAKING)) {
                filtered.put(Enchantment.UNBREAKING, Math.min(entry.getValue(), 3));
            } else if (allowed != null && entry.getKey().equals(allowed)) {
                filtered.put(allowed, Math.min(entry.getValue(), categoryCap));
            }

        }

        if (filtered.isEmpty()) {
            event.setCancelled(true);
            event.getEnchanter().sendMessage("§cCet objet ne peut recevoir aucun enchantement autorisé dans cette partie.");
            return;
        }

        event.getEnchantsToAdd().clear();
        event.getEnchantsToAdd().putAll(filtered);

    }

    private Enchantment getCategoryEnchant(Material material) {

        String name = material.name();

        if (isArmor(name)) {
            return Enchantment.PROTECTION;
        }

        if (isSwordOrAxe(name)) {
            return Enchantment.SHARPNESS;
        }

        if (material == Material.BOW) {
            return Enchantment.POWER;
        }

        return null;

    }

    private int getCap(Enchantment enchant, Material material, Player enchanter) {

        if (enchant == Enchantment.PROTECTION) {
            return material.name().startsWith("DIAMOND_") ? 2 : 3;
        }

        if (enchant == Enchantment.SHARPNESS) {
            return isSoloRole(enchanter) ? 4 : 3;
        }

        if (enchant == Enchantment.POWER) {
            return (isSoloRole(enchanter) || isChasseur(enchanter)) ? 4 : 3;
        }

        return 3; // Solidité, seul enchant autorisé en dehors armure/épée-hache/arc

    }

    private boolean isArmor(String name) {
        return name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS");
    }

    private boolean isSwordOrAxe(String name) {
        return name.endsWith("_SWORD") || name.endsWith("_AXE");
    }

    private boolean isSoloRole(Player player) {

        LGPlayer lgPlayer = getLgPlayer(player);
        return lgPlayer != null && lgPlayer.getRole() != null && lgPlayer.getRole().getTeam() == RoleTeam.NEUTRAL;

    }

    private boolean isChasseur(Player player) {

        LGPlayer lgPlayer = getLgPlayer(player);
        return lgPlayer != null && lgPlayer.getRole() instanceof ChasseurRole;

    }

    private LGPlayer getLgPlayer(Player player) {

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        return playerManager.get(player);

    }

}
