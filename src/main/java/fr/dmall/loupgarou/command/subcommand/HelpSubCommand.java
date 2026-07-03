package fr.dmall.loupgarou.command.subcommand;

import org.bukkit.command.CommandSender;

public class HelpSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Affiche la liste des commandes.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        sender.sendMessage("§6========== LoupGarou ==========");
        sender.sendMessage("§e/lg help §7- Affiche cette aide");
        sender.sendMessage("§e/lg info §7- Informations de la partie");
        sender.sendMessage("§e/lg start §7- Lance une partie");
        sender.sendMessage("§e/lg stop §7- Arrête la partie");
        sender.sendMessage("§e/lg role §7- Configure les rôles (add/remove/list/clear)");
        sender.sendMessage("§e/lg me §7- Affiche votre rôle actuel");
        sender.sendMessage("§e/lg sonder <joueur> §7- Sonde un joueur (Voyante, la nuit)");
        sender.sendMessage("§e/lg infecter <joueur> §7- Infecte votre victime (Père des Loups)");

        return true;
    }
}