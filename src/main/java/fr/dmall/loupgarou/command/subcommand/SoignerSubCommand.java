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

public class SoignerSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "soigner";
    }

    @Override
    public String getDescription() {
        return "Sauve un joueur en train de mourir avec votre potion de vie (Sorcière, une fois par partie).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg soigner <joueur>");
            return true;
        }

        Player healer = (Player) sender;

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        DeathManager deathManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(DeathManager.class);

        LGPlayer lgHealer = playerManager.get(healer);

        if (lgHealer == null || !lgHealer.isAlive()) {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande.");
            return true;
        }

        Role role = lgHealer.getRole();

        if (!(role instanceof SorciereRole)) {
            sender.sendMessage("§cVous n'êtes pas la Sorcière.");
            return true;
        }

        SorciereRole sorciere = (SorciereRole) role;

        if (!sorciere.isHealAvailable()) {
            sender.sendMessage("§cVous avez déjà utilisé votre potion de vie cette partie.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        if (!deathManager.isDying(target)) {
            sender.sendMessage("§cCe joueur n'est pas en train de mourir.");
            return true;
        }

        sorciere.consumeHeal();
        deathManager.revive(target);

        target.sendMessage("§dLa Sorcière vous a sauvé avec sa potion de vie !");
        healer.sendMessage("§dVous avez sauvé " + target.getName() + " avec votre potion de vie !");

        return true;
    }

}
