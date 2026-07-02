package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
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

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.WAITING) {
            sender.sendMessage("§cUne partie est déjà en cours.");
            return true;
        }

        game.setState(GameState.PLAYING);

        sender.sendMessage("§aLa partie a été lancée !");

        return true;
    }
}