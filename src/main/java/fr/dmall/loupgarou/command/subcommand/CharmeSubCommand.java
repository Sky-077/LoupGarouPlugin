package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.CharmManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CharmeSubCommand extends DebugSubCommand {

    @Override
    public String getName() {
        return "charme";
    }

    @Override
    public String getDescription() {
        return "[Debug] Affiche le taux de charme (%) actuel d'un joueur (Joueur de Flûte).";
    }

    @Override
    protected boolean executeDebug(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg charme <joueur>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        CharmManager charmManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(CharmManager.class);

        double charm = charmManager.getCharm(target.getUniqueId());

        sender.sendMessage("§7Charme de " + target.getName() + " : §f" + String.format("%.1f", charm) + "%");

        return true;
    }

}
