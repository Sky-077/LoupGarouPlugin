package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameStarter;
import fr.dmall.loupgarou.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class ExperienceLockListener implements Listener {

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent event) {

        if (event.getNewLevel() >= GameStarter.LOCKED_EXPERIENCE_LEVEL) {
            return;
        }

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() == GameState.WAITING) {
            return;
        }

        event.getPlayer().setLevel(GameStarter.LOCKED_EXPERIENCE_LEVEL);

    }

}
