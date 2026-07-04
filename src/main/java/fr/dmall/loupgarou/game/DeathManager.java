package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathManager implements Manager {

    private static final long DYING_DURATION_TICKS = 20L * 60L; // 1 minute

    private final Map<UUID, BukkitTask> pendingTasks = new HashMap<>();
    private final Map<UUID, UUID> pendingKillers = new HashMap<>();
    private final Map<UUID, ItemStack[]> hiddenArmor = new HashMap<>();
    private final Map<UUID, ItemStack> hiddenMainHand = new HashMap<>();

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

        for (BukkitTask task : pendingTasks.values()) {
            task.cancel();
        }

        pendingTasks.clear();
        pendingKillers.clear();
        hiddenArmor.clear();
        hiddenMainHand.clear();

    }

    public boolean isDying(Player player) {
        return pendingTasks.containsKey(player.getUniqueId());
    }

    public void startDying(Player player, Player killer) {

        if (isDying(player)) {
            return;
        }

        UUID uuid = player.getUniqueId();

        if (killer != null) {
            pendingKillers.put(uuid, killer.getUniqueId());
        }

        player.setInvulnerable(true);

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) DYING_DURATION_TICKS, 0, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, (int) DYING_DURATION_TICKS, 3, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (int) DYING_DURATION_TICKS, 0, false, false));

        hiddenArmor.put(uuid, player.getInventory().getArmorContents());
        hiddenMainHand.put(uuid, player.getInventory().getItemInMainHand());
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setItemInMainHand(null);

        player.sendTitle("§4Vous agonisez...", "§7Vous allez mourir dans une minute", 10, 60, 10);

        BukkitTask task = Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> finalizeDeath(player),
                DYING_DURATION_TICKS
        );

        pendingTasks.put(uuid, task);

    }

    public void revive(Player player) {

        UUID uuid = player.getUniqueId();

        BukkitTask task = pendingTasks.remove(uuid);

        if (task != null) {
            task.cancel();
        }

        pendingKillers.remove(uuid);

        player.setInvulnerable(false);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        restoreEquipment(player);
        player.sendTitle("§aVous avez été sauvé !", "", 5, 40, 10);

    }

    public UUID consumeKiller(Player player) {
        return pendingKillers.remove(player.getUniqueId());
    }

    public UUID getPendingKiller(Player player) {
        return pendingKillers.get(player.getUniqueId());
    }

    public void killInstantly(Player player, Player killer) {

        if (killer != null) {
            pendingKillers.put(player.getUniqueId(), killer.getUniqueId());
        }

        player.setHealth(0.0);

    }

    private void finalizeDeath(Player player) {

        pendingTasks.remove(player.getUniqueId());

        player.setInvulnerable(false);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        restoreEquipment(player);

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (!game.isPvpEnabled()) {

            pendingKillers.remove(player.getUniqueId());

            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
            player.setFireTicks(0);
            player.setRemainingAir(player.getMaximumAir());

            player.sendTitle("§aVous avez survécu !", "§7Le PVP n'est pas encore activé", 5, 40, 10);

            return;

        }

        player.setHealth(0.0);

    }

    private void restoreEquipment(Player player) {

        UUID uuid = player.getUniqueId();

        ItemStack[] armor = hiddenArmor.remove(uuid);
        ItemStack mainHand = hiddenMainHand.remove(uuid);

        if (armor != null) {
            player.getInventory().setArmorContents(armor);
        }

        if (mainHand != null) {
            player.getInventory().setItemInMainHand(mainHand);
        }

    }

}