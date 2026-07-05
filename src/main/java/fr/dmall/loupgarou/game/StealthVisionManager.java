package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.manager.Manager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.role.loup.LoupGarouPerfideRole;
import fr.dmall.loupgarou.role.solo.FeuFolletRole;
import fr.dmall.loupgarou.role.village.PetiteFilleRole;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class StealthVisionManager implements Manager {

    private static final long PERIOD_TICKS = 10L; // 0.5 seconde

    private BukkitTask task;

    @Override
    public void enable() {

        task = Bukkit.getScheduler().runTaskTimer(
                LoupGarouPlugin.getInstance(),
                this::tick,
                PERIOD_TICKS,
                PERIOD_TICKS
        );

    }

    @Override
    public void disable() {

        if (task != null) {
            task.cancel();
            task = null;
        }

    }

    private void tick() {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (!game.isRevealed() || (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT)) {
            return;
        }

        List<Player> stealthers = new ArrayList<>();
        List<Player> observers = new ArrayList<>();

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (!lgPlayer.isAlive()) {
                continue;
            }

            boolean isStealthRole = lgPlayer.getRole() instanceof FeuFolletRole
                    || lgPlayer.getRole() instanceof PetiteFilleRole
                    || lgPlayer.getRole() instanceof LoupGarouPerfideRole;

            if (!isStealthRole) {
                continue;
            }

            Player player = Bukkit.getPlayer(lgPlayer.getUuid());

            if (player == null) {
                continue;
            }

            observers.add(player);

            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                stealthers.add(player);
            }

        }

        for (Player stealther : stealthers) {

            for (Player observer : observers) {

                if (observer.equals(stealther) || !observer.getWorld().equals(stealther.getWorld())) {
                    continue;
                }

                observer.spawnParticle(Particle.WITCH, stealther.getLocation().add(0, 1, 0), 3, 0.2, 0.3, 0.2, 0.0);

            }

        }

    }

}
