package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.RoleManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class StartSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Lance une partie.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        RoleManager roleManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(RoleManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.WAITING) {
            sender.sendMessage("§cUne partie est déjà en cours.");
            return true;
        }

        game.clearPlayers();

        List<LGPlayer> players = new ArrayList<>();

        for (LGPlayer player : playerManager.getPlayers()) {
            game.addPlayer(player);
            players.add(player);
        }

        roleManager.assignRoles(players);

        game.setState(GameState.PLAYING);

        sender.sendMessage("§aLa partie a été lancée !");
        sender.sendMessage("§7Joueurs : §e" + players.size());

        return true;
    }

}