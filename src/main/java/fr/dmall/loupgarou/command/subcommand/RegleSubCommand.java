package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegleSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "regle";
    }

    @Override
    public String getDescription() {
        return "Réaffiche l'explication de votre rôle et de ses commandes.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgPlayer = playerManager.get(player);

        if (lgPlayer == null) {
            sender.sendMessage("§cVous n'êtes pas reconnu par le plugin.");
            return true;
        }

        Role role = lgPlayer.getRole();

        if (role == null) {
            sender.sendMessage("§7Vous n'avez pas encore de rôle (la partie n'a peut-être pas démarré).");
            return true;
        }

        role.sendInstructions(player);

        return true;
    }

}
