package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathManager implements Manager {

    private static final long DYING_DURATION_TICKS = 20L * 60L; // 1 minute

    private final Map<UUID, BukkitTask> pendingTasks = new HashMap<>();
    private final Map<UUID, UUID> pendingKillers = new HashMap<>();

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
        player.setHealth(0.0);

    }

}