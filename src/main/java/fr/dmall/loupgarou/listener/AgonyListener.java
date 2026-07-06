package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.DeathManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class AgonyListener implements Listener {

    // Le mode spectateur (DeathManager.startDying) empêche déjà nativement de subir/infliger des dégâts ;
    // seul le blocage de position reste nécessaire (sinon le mode spectateur permettrait de voler/espionner).
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

}
