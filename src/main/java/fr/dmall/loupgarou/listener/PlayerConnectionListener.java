package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.scoreboard.ScoreboardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final PlayerManager playerManager;

    public PlayerConnectionListener() {
        this.playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playerManager.add(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        playerManager.remove(event.getPlayer());

        ScoreboardManager scoreboardManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(ScoreboardManager.class);

        scoreboardManager.removePlayer(event.getPlayer().getUniqueId());

    }
}