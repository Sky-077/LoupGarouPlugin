package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.DeathManager;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

public class LethalDamageListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            return;
        }

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgPlayer = playerManager.get(player);

        if (lgPlayer == null || !game.getPlayers().contains(lgPlayer) || !lgPlayer.isAlive()) {
            return;
        }

        DeathManager deathManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(DeathManager.class);

        if (deathManager.isDying(player)) {
            event.setCancelled(true);
            return;
        }

        if (event.getFinalDamage() < player.getHealth()) {
            return;
        }

        event.setCancelled(true);

        Player killer = resolveKiller(event);

        deathManager.startDying(player, killer);

    }

    private Player resolveKiller(EntityDamageEvent event) {

        if (!(event instanceof EntityDamageByEntityEvent)) {
            return null;
        }

        EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) event;

        if (entityEvent.getDamager() instanceof Player) {
            return (Player) entityEvent.getDamager();
        }

        if (entityEvent.getDamager() instanceof Projectile) {

            ProjectileSource shooter = ((Projectile) entityEvent.getDamager()).getShooter();

            if (shooter instanceof Player) {
                return (Player) shooter;
            }

        }

        return null;

    }

}