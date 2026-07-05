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

        VoteManager voteManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(VoteManager.class);

        CorruptionManager corruptionManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(CorruptionManager.class);

        Game game = gameManager.getCurrentGame();

        for (String message : messages) {
            Bukkit.broadcastMessage(message);
        }

        // Passé à WAITING avant les respawns forcés, pour que PlayerDeathListener.onRespawn()
        // sache que la partie est terminée et renvoie directement au lobby.
        game.setState(GameState.WAITING);

        Location lobbySpawn = lobbySpawnManager.getSpawn();

        for (LGPlayer lgPlayer : playerManager.getPlayers()) {

            Player player = Bukkit.getPlayer(lgPlayer.getUuid());

            if (player != null) {

                player.setInvulnerable(false);
                HonorManager.clearModifier(player);
                AngeManager.clearHearts(player);
                PoisonManager.clearPoison(player);

                if (player.isDead()) {
                    player.spigot().respawn();
                }

                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(lobbySpawn);

            }

            lgPlayer.resetStats();

        }

        game.clearPlayers();
        loveManager.reset();
        voteManager.reset();
        corruptionManager.resetAll();

    }

}
