package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import org.bukkit.command.CommandSender;

public class InfoSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Affiche les informations de la partie.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        sender.sendMessage("§6===== Informations =====");
        sender.sendMessage("§eÉtat : §f" + game.getState());
        sender.sendMessage("§eJoueurs : §f" + game.getPlayers().size());

        return true;
    }
}