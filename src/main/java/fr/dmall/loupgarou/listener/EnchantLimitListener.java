package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.RoleTeam;
import fr.dmall.loupgarou.role.village.ChasseurRole;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EnchantLimitListener implements Listener {

    private final Map<UUID, int[]> shownLevels = new HashMap<>();

    @EventHandler
    public void onPrepare(PrepareItemEnchantEvent event) {

        ItemStack item = event.getItem();
        Enchantment allowed = getCategoryEnchant(item.getType());
        Enchantment shown = (allowed != null) ? allowed : Enchantment.UNBREAKING;
        int cap = getCap(shown, item.getType(), event.getEnchanter());

        EnchantmentOffer[] offers = event.getOffers();
        int[] levels = new int[offers.length];

        for (int i = 0; i < offers.length; i++) {

            EnchantmentOffer offer = offers[i];

            if (offer == null) {
                continue;
            }

            int level = Math.min(Math.max(offer.getEnchantmentLevel(), 1), cap);

            offer.setEnchantment(shown);
            offer.setEnchantmentLevel(level);
            levels[i] = level;

        }

        shownLevels.put(event.getEnchanter().getUniqueId(), levels);

    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {

        Enchantment allowed = getCategoryEnchant(event.getItem().getType());
        int categoryCap = (allowed != null) ? getCap(allowed, event.getItem().getType(), event.getEnchanter()) : 0;

        int[] levels = shownLevels.remove(event.getEnchanter().getUniqueId());
        int index = event.whichButton();
        int exactLevel = (levels != null && index >= 0 && index < levels.length && levels[index] > 0)
                ? levels[index]
                : categoryCap;

        Map<Enchantment, Integer> filtered = new HashMap<>();

        Integer unbreakingLevel = event.getEnchantsToAdd().get(Enchantment.UNBREAKING);

        if (unbreakingLevel != null) {
            filtered.put(Enchantment.UNBREAKING, Math.min(unbreakingLevel, 3));
        }

        if (allowed != null) {
            // Toujours forcer l'enchant de catégorie au niveau exact affiché dans l'aperçu :
            // l'algorithme interne de Minecraft ne garantit pas de l'inclure lui-même dans le résultat.
            filtered.put(allowed, Math.min(exactLevel, categoryCap));
        }

        if (filtered.isEmpty()) {
            event.setCancelled(true);
            event.getEnchanter().sendMessage("§cCet objet ne peut recevoir aucun enchantement autorisé dans cette partie.");
            return;
        }

        event.getEnchantsToAdd().clear();
        event.getEnchantsToAdd().putAll(filtered);

    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {

        ItemStack result = event.getResult();

        if (result == null || result.getType() == Material.AIR) {
            return;
        }

        HumanEntity viewer = event.getView().getPlayer();

        if (!(viewer instanceof Player)) {
            return;
        }

        if (applyEnchantCaps(result, (Player) viewer)) {
            event.setResult(result);
        }

    }

    private boolean applyEnchantCaps(ItemStack item, Player player) {

        boolean isBook = item.getType() == Material.ENCHANTED_BOOK;
        Enchantment allowed = getCategoryEnchant(item.getType());

        Map<Enchantment, Integer> current = isBook
                ? ((EnchantmentStorageMeta) item.getItemMeta()).getStoredEnchants()
                : item.getEnchantments();

        Map<Enchantment, Integer> filtered = new HashMap<>();

        for (Map.Entry<Enchantment, Integer> entry : current.entrySet()) {

            if (entry.getKey().equals(Enchantment.UNBREAKING)) {
                filtered.put(Enchantment.UNBREAKING, Math.min(entry.getValue(), 3));
            } else if (allowed != null && entry.getKey().equals(allowed)) {
                filtered.put(allowed, Math.min(entry.getValue(), getCap(allowed, item.getType(), player)));
            }

        }

        if (filtered.equals(current)) {
            return false;
        }

        if (isBook) {

            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

            for (Enchantment enchant : new HashSet<>(meta.getStoredEnchants().keySet())) {
                meta.removeStoredEnchant(enchant);
            }

            for (Map.Entry<Enchantment, Integer> entry : filtered.entrySet()) {
                meta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
            }

            item.setItemMeta(meta);

        } else {

            Set<Enchantment> toRemove = new HashSet<>(item.getEnchantments().keySet());

            for (Enchantment enchant : toRemove) {
                item.removeEnchantment(enchant);
            }

            for (Map.Entry<Enchantment, Integer> entry : filtered.entrySet()) {
                item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
            }

        }

        return true;

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
