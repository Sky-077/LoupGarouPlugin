package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.CorruptionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfectionSubCommand extends DebugSubCommand {

    @Override
    public String getName() {
        return "infection";
    }

    @Override
    public String getDescription() {
        return "[Debug] Affiche le taux de corruption (%) d'un joueur (/lg infection <joueur>).";
    }

    @Override
    protected boolean executeDebug(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg infection <joueur>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        CorruptionManager corruptionManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(CorruptionManager.class);

        double corruption = corruptionManager.getCorruption(target.getUniqueId());

        sender.sendMessage("§fCorruption de " + target.getName() + " : §e" + String.format("%.1f", corruption) + "%");

        return true;
    }

}
