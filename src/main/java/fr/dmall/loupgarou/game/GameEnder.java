package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.village.SalvateurRole;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class GameEnder {

    private static final PotionEffectType[] GAME_POTION_EFFECTS = {
            PotionEffectType.STRENGTH,
            PotionEffectType.SPEED,
            PotionEffectType.ABSORPTION,
            PotionEffectType.WEAKNESS,
            PotionEffectType.REGENERATION,
            PotionEffectType.BLINDNESS,
            PotionEffectType.SLOWNESS,
            PotionEffectType.INVISIBILITY,
    };

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

        DeathManager deathManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(DeathManager.class);

        WorldManager worldManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(WorldManager.class);

        Game game = gameManager.getCurrentGame();

        for (String message : messages) {
            Bukkit.broadcastMessage(message);
        }

        // Passé à WAITING avant les respawns forcés, pour que PlayerDeathListener.onRespawn()
        // sache que la partie est terminée et renvoie directement au lobby.
        game.setState(GameState.WAITING);

        Location lobbySpawn = lobbySpawnManager.getSpawn();

        for (LGPlayer lgPlayer : playerManager.getPlayers()) {

            if (lgPlayer.getRole() instanceof SalvateurRole) {

                UUID protectedUuid = ((SalvateurRole) lgPlayer.getRole()).getProtectedUuid();
                Player protege = (protectedUuid != null) ? Bukkit.getPlayer(protectedUuid) : null;

                if (protege != null) {
                    protege.removePotionEffect(PotionEffectType.RESISTANCE);
                }

            }

            Player player = Bukkit.getPlayer(lgPlayer.getUuid());

            if (player != null) {

                player.setInvulnerable(false);
                HonorManager.clearModifier(player);
                AngeManager.clearHearts(player);
                PoisonManager.clearPoison(player);
                LoupBlancManager.clearHearts(player);

                for (PotionEffectType effect : GAME_POTION_EFFECTS) {
                    player.removePotionEffect(effect);
                }

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
        deathManager.resetAll();
        worldManager.clearGameWorld();

    }

}
