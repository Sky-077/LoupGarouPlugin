package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.role.village.SalvateurRole;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class SalvateurProtectionListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent event) {

        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

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

        if (!game.isRevealed()) {
            return;
        }

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (lgPlayer.getRole() instanceof SalvateurRole
                    && ((SalvateurRole) lgPlayer.getRole()).isProtecting(player.getUniqueId())) {
                event.setDamage(event.getDamage() * 0.5);
                return;
            }

        }

    }

}
