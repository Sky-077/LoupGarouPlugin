package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.HostManager;
import fr.dmall.loupgarou.game.SettingsMenuBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "menu";
    }

    @Override
    public String getDescription() {
        return "Ouvre le menu de paramètres de la partie (réservé à l'hôte).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;

        HostManager hostManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(HostManager.class);

        if (!hostManager.isActiveHost(player)) {
            sender.sendMessage("§cSeul l'hôte de la partie peut ouvrir ce menu.");
            return true;
        }

        SettingsMenuBuilder.openMain(player);

        return true;
    }

}
