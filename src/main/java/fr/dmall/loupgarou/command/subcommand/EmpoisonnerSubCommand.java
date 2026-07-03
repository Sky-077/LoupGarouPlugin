package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.DeathManager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.village.SorciereRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EmpoisonnerSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "empoisonner";
    }

    @Override
    public String getDescription() {
        return "Tue instantanément un joueur avec votre potion de mort (Sorcière, une fois par partie).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg empoisonner <joueur>");
            return true;
        }

        Player poisoner = (Player) sender;

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        DeathManager deathManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(DeathManager.class);

        LGPlayer lgPoisoner = playerManager.get(poisoner);

        if (lgPoisoner == null || !lgPoisoner.isAlive()) {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande.");
            return true;
        }

        Role role = lgPoisoner.getRole();

        if (!(role instanceof SorciereRole)) {
            sender.sendMessage("§cVous n'êtes pas la Sorcière.");
            return true;
        }

        SorciereRole sorciere = (SorciereRole) role;

        if (!sorciere.isPoisonAvailable()) {
            sender.sendMessage("§cVous avez déjà utilisé votre potion de mort cette partie.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        if (target.equals(poisoner)) {
            sender.sendMessage("§cVous ne pouvez pas vous empoisonner vous-même.");
            return true;
        }

        LGPlayer lgTarget = playerManager.get(target);

        if (lgTarget == null || !lgTarget.isAlive()) {
            sender.sendMessage("§cCe joueur ne fait pas partie de la partie en cours.");
            return true;
        }

        if (deathManager.isDying(target)) {
            sender.sendMessage("§cCe joueur est déjà en train de mourir.");
            return true;
        }

        sorciere.consumePoison();
        poisoner.sendMessage("§5Vous avez empoisonné " + target.getName() + " !");

        deathManager.killInstantly(target, poisoner);

        return true;
    }

}
