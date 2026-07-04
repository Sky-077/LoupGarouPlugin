package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GameEnder {

    private GameEnder() {
    }

    public static void end(String... messages) {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LoveManager loveManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(LoveManager.class);

        LobbySpawnManager lobbySpawnManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(LobbySpawnManager.class);

        Game game = gameManager.getCurrentGame();

        for (String message : messages) {
            Bukkit.broadcastMessage(message);
        }

        Location lobbySpawn = lobbySpawnManager.getSpawn();

        for (LGPlayer lgPlayer : playerManager.getPlayers()) {

            Player player = Bukkit.getPlayer(lgPlayer.getUuid());

            if (player != null) {
                player.setInvulnerable(false);
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(lobbySpawn);
            }

            lgPlayer.resetStats();

        }

        game.clearPlayers();
        game.setState(GameState.WAITING);
        loveManager.reset();

    }

}
