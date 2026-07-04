package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.DeathManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.projectiles.ProjectileSource;

public class AgonyListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
            return;
        }

        Player player = event.getPlayer();

        DeathManager deathManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(DeathManager.class);

        if (!deathManager.isDying(player)) {
            return;
        }

        Location locked = from.clone();
        locked.setYaw(to.getYaw());
        locked.setPitch(to.getPitch());

        event.setTo(locked);

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {

        Player attacker = resolveAttacker(event);

        if (attacker == null) {
            return;
        }

        DeathManager deathManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(DeathManager.class);

        if (deathManager.isDying(attacker)) {
            event.setCancelled(true);
        }

    }

    private Player resolveAttacker(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player) {
            return (Player) event.getDamager();
        }

        if (event.getDamager() instanceof Projectile) {

            ProjectileSource shooter = ((Projectile) event.getDamager()).getShooter();

            if (shooter instanceof Player) {
                return (Player) shooter;
            }

        }

        return null;

    }

}
