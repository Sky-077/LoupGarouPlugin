package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.GameStarter;
import fr.dmall.loupgarou.game.HostManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Lance une partie (réservé à l'hôte).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        HostManager hostManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(HostManager.class);

        if (!hostManager.isActiveHost((Player) sender)) {
            sender.sendMessage("§cSeul l'hôte de la partie peut utiliser cette commande.");
            return true;
        }

        GameStarter.start(sender, false);

        return true;
    }

}
