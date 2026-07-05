package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.manager.Manager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.loup.PereDesLoupsRole;
import fr.dmall.loupgarou.role.loup.WolfRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CorruptionManager implements Manager {

    private static final double RADIUS = 6.0;
    private static final double PERE_DES_LOUPS_RATE = 1.0;
    private static final double LOUP_GAROU_RATE = 1.0 / 5.0;

    private final Map<UUID, Double> corruption = new HashMap<>();

    private BukkitTask task;

    @Override
    public void enable() {

        task = Bukkit.getScheduler().runTaskTimer(
                LoupGarouPlugin.getInstance(),
                this::tick,
                20L,
                20L
        );

    }

    @Override
    public void disable() {

        if (task != null) {
            task.cancel();
        }

        corruption.clear();

    }

    public boolean isFullyCorrupted(UUID uuid) {
        return corruption.getOrDefault(uuid, 0.0) >= 100.0;
    }

    public void resetAll() {
        corruption.clear();
    }

    public void reset(UUID uuid) {
        corruption.remove(uuid);
    }

    private void tick() {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (!game.isRevealed()) {
            return;
        }

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            return;
        }

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        List<LGPlayer> wolves = new ArrayList<>();
        List<LGPlayer> targets = new ArrayList<>();

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (!lgPlayer.isAlive() || lgPlayer.getRole() == null) {
                continue;
            }

            if (lgPlayer.getRole() instanceof WolfRole) {
                wolves.add(lgPlayer);
            } else {
                targets.add(lgPlayer);
            }

        }

        if (wolves.isEmpty() || targets.isEmpty()) {
            return;
        }

        for (LGPlayer lgTarget : targets) {

            Player target = Bukkit.getPlayer(lgTarget.getUuid());

            if (target == null) {
                continue;
            }

            double gain = 0.0;

            for (LGPlayer lgWolf : wolves) {

                Player wolf = Bukkit.getPlayer(lgWolf.getUuid());

                if (wolf == null || !wolf.getWorld().equals(target.getWorld())) {
                    continue;
                }

                if (wolf.getLocation().distance(target.getLocation()) > RADIUS) {
                    continue;
                }

                gain += (lgWolf.getRole() instanceof PereDesLoupsRole) ? PERE_DES_LOUPS_RATE : LOUP_GAROU_RATE;

            }

            if (gain > 0.0) {
                double current = corruption.getOrDefault(target.getUniqueId(), 0.0);
                corruption.put(target.getUniqueId(), Math.min(100.0, current + gain));
            }

        }

    }

}
