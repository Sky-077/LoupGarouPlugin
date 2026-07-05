package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.HonorManager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HonneurSubCommand extends DebugSubCommand {

    @Override
    public String getName() {
        return "honneur";
    }

    @Override
    public String getDescription() {
        return "[Debug] Affiche ou modifie l'honneur d'un joueur (/lg honneur <joueur> [valeur]).";
    }

    @Override
    protected boolean executeDebug(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg honneur <joueur> [valeur]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgTarget = playerManager.get(target);

        if (lgTarget == null) {
            sender.sendMessage("§cCe joueur n'est pas suivi par le plugin.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§7Honneur de " + target.getName() + " : §f" + lgTarget.getHonor());
            return true;
        }

        int value;

        try {
            value = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cValeur invalide, ça doit être un nombre entre -3 et 3.");
            return true;
        }

        HonorManager.setHonor(lgTarget, target, value);

        sender.sendMessage("§aHonneur de " + target.getName() + " réglé sur §f" + lgTarget.getHonor() + "§a.");

        return true;
    }

}
