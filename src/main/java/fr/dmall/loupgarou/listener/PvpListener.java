package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
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
import org.bukkit.projectiles.ProjectileSource;

public class PvpListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = resolveAttacker(event);

        if (attacker == null || attacker.equals(victim)) {
            return;
        }

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            return;
        }

        if (game.isPvpEnabled()) {
            return;
        }

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgVictim = playerManager.get(victim);
        LGPlayer lgAttacker = playerManager.get(attacker);

        if (lgVictim == null || lgAttacker == null
                || !game.getPlayers().contains(lgVictim) || !game.getPlayers().contains(lgAttacker)) {
            return;
        }

        event.setCancelled(true);
        attacker.sendMessage("§cLe PVP n'est pas encore activé.");

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
