package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.RoleManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameStarter {

    private static final int MIN_PLAYERS = 3;
    private static final long INVINCIBILITY_DURATION_TICKS = 20L * 30L; // 30 secondes
    private static final long PVP_DELAY_TICKS = 20L * 60L * 30L; // 30 minutes

    private GameStarter() {
    }

    public static boolean start(CommandSender sender, boolean bypassMinPlayers) {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        RoleManager roleManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(RoleManager.class);

        CycleManager cycleManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(CycleManager.class);

        WorldManager worldManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(WorldManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.WAITING) {
            sender.sendMessage("§cUne partie est déjà en cours.");
            return false;
        }

        int connectedPlayers = playerManager.getPlayers().size();

        if (!bypassMinPlayers && connectedPlayers < MIN_PLAYERS) {
            sender.sendMessage("§cIl faut au moins " + MIN_PLAYERS + " joueurs pour lancer une partie (actuellement "
                    + connectedPlayers + ").");
            return false;
        }

        game.clearPlayers();

        List<LGPlayer> players = new ArrayList<>();

        for (LGPlayer player : playerManager.getPlayers()) {
            game.addPlayer(player);
            players.add(player);
        }

        roleManager.assignRoles(players);

        for (LGPlayer lgPlayer : players) {

            Player rolePlayer = Bukkit.getPlayer(lgPlayer.getUuid());

            if (rolePlayer != null && lgPlayer.getRole() != null) {
                lgPlayer.getRole().sendInstructions(rolePlayer);
            }

        }

        Bukkit.broadcastMessage("§7Génération du monde de jeu, veuillez patienter...");

        World gameWorld = worldManager.prepareGameWorld();

        game.setState(GameState.SCATTERING);

        for (LGPlayer lgPlayer : players) {

            Player scatterPlayer = Bukkit.getPlayer(lgPlayer.getUuid());

            if (scatterPlayer == null) {
                continue;
            }

            Location location = worldManager.findScatterLocation(gameWorld);
            scatterPlayer.teleport(location);
            scatterPlayer.setInvulnerable(true);

        }

        game.setState(GameState.INVINCIBILITY);

        long invincibilitySeconds = INVINCIBILITY_DURATION_TICKS / 20L;

        Bukkit.broadcastMessage("§aLa partie a été lancée ! §7Vous êtes invulnérable pendant "
                + invincibilitySeconds + " secondes.");
        sender.sendMessage("§7Joueurs : §e" + players.size());

        Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> beginGame(game, players, cycleManager),
                INVINCIBILITY_DURATION_TICKS
        );

        return true;

    }

    private static void beginGame(Game game, List<LGPlayer> players, CycleManager cycleManager) {

        if (game.getState() != GameState.INVINCIBILITY) {
            return;
        }

        for (LGPlayer lgPlayer : players) {

            Player player = Bukkit.getPlayer(lgPlayer.getUuid());

            if (player != null) {
                player.setInvulnerable(false);
            }

        }

        game.markStarted();
        game.setState(cycleManager.getPhaseForCurrentTime());

        Bukkit.broadcastMessage("§aLa partie commence ! §7Le PVP sera activé dans 30 minutes.");

        long startedAt = game.getStartTimeMillis();

        Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> enablePvp(game, startedAt),
                PVP_DELAY_TICKS
        );

    }

    private static void enablePvp(Game game, long startedAt) {

        if (game.getStartTimeMillis() != startedAt) {
            return;
        }

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            return;
        }

        if (game.isPvpEnabled()) {
            return;
        }

        game.enablePvp();

        Bukkit.broadcastMessage("§c⚔ Le PVP est désormais activé !");

    }

}
