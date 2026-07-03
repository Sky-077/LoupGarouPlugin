package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.RoleManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameStarter {

    private static final int MIN_PLAYERS = 3;

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

        game.markStarted();
        game.setState(cycleManager.getPhaseForCurrentTime());

        sender.sendMessage("§aLa partie a été lancée !");
        sender.sendMessage("§7Joueurs : §e" + players.size());

        return true;

    }

}
