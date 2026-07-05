package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.DeathManager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.loup.PereDesLoupsRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LaisserMourirSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "laissermourir";
    }

    @Override
    public String getDescription() {
        return "Refuse d'infecter un joueur corrompu à 100% et le laisse mourir (Père des Loups).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player pere = (Player) sender;

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgPere = playerManager.get(pere);

        if (lgPere == null || !lgPere.isAlive()) {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande.");
            return true;
        }

        Role role = lgPere.getRole();

        if (!(role instanceof PereDesLoupsRole)) {
            sender.sendMessage("§cVous n'êtes pas le Père des Loups.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg laissermourir <joueur>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        DeathManager deathManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(DeathManager.class);

        if (!deathManager.hasConversionOffer(target)) {
            sender.sendMessage("§cCe joueur n'attend pas de décision d'infection.");
            return true;
        }

        deathManager.declineConversion(target);

        sender.sendMessage("§7Vous avez laissé " + target.getName() + " mourir.");

        return true;
    }

}
