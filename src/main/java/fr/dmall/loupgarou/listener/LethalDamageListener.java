package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.DeathManager;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import org.bukkit.Bukkit;
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
    @SuppressWarnings("deprecation")
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

        Player killer = resolveKiller(event);

        // Laisse le coup s'appliquer réellement (flash rouge, son, recul) au lieu de l'annuler
        // silencieusement, mais plafonne les dégâts pour ne jamais faire tomber la vie à 0 ici.
        double remainingHealth = (player.getHealth() > 1.0) ? player.getHealth() - 1.0 : player.getHealth() * 0.5;

        for (EntityDamageEvent.DamageModifier modifier : EntityDamageEvent.DamageModifier.values()) {

            if (modifier != EntityDamageEvent.DamageModifier.BASE && event.isApplicable(modifier)) {
                event.setDamage(modifier, 0.0);
            }

        }

        event.setDamage(EntityDamageEvent.DamageModifier.BASE, remainingHealth);

        // Le passage en agonie (mode spectateur) doit attendre le tick suivant : le faire pendant ce
        // tick-ci, avant que le serveur n'applique réellement les dégâts, empêcherait l'animation de coup
        // de s'afficher (un spectateur est immunisé aux dégâts).
        Bukkit.getScheduler().runTask(LoupGarouPlugin.getInstance(), () -> deathManager.startDying(player, killer));

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