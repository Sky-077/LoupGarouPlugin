package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.village.VillageoisRole;
import org.bukkit.command.CommandSender;

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

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.WAITING) {
            sender.sendMessage("§cUne partie est déjà en cours.");
            return true;
        }

        game.clearPlayers();

        for (LGPlayer player : playerManager.getPlayers()) {
            player.setRole(new VillageoisRole());
            game.addPlayer(player);
        }

        game.setState(GameState.PLAYING);

        sender.sendMessage("§aLa partie a été lancée !");
        sender.sendMessage("§7Joueurs : §e" + game.getPlayers().size());

        return true;
    }
}