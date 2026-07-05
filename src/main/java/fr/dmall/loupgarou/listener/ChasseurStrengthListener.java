package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.RoleTeam;
import fr.dmall.loupgarou.role.village.ChasseurRole;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ChasseurStrengthListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

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

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgAttacker = playerManager.get(attacker);

        if (lgAttacker == null || !(lgAttacker.getRole() instanceof ChasseurRole)) {
            return;
        }

        LGPlayer lgVictim = playerManager.get(victim);

        if (lgVictim == null || lgVictim.getEffectiveTeam() != RoleTeam.LOUP) {
            return;
        }

        double bonus = ((ChasseurRole) lgAttacker.getRole()).getWolfStrengthLevel() * 3.0;

        event.setDamage(event.getDamage() + bonus);

    }

}
