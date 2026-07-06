package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.HostManager;
import fr.dmall.loupgarou.player.LGPlayer;
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

        HostManager hostManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(HostManager.class);

        sender.sendMessage("§6===== Informations =====");
        sender.sendMessage("§eHôte : §f" + (hostManager.hasActiveHost() ? hostManager.getActiveHostName() : "aucun"));
        sender.sendMessage("§eÉtat : §f" + game.getState());
        sender.sendMessage("§eJoueurs : §f" + game.getPlayers().size());

        for (LGPlayer player : game.getPlayers()) {
            if (player.getRole() != null) {
                sender.sendMessage(" §7- §f" + player.getRole().getName());
            }
        }

        return true;
    }
}