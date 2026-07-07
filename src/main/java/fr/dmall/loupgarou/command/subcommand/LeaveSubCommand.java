package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Annule votre inscription pour la prochaine partie.";
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

        if (lgPlayer == null || !lgPlayer.isJoined()) {
            sender.sendMessage("§fVous n'êtes pas inscrit.");
            return true;
        }

        lgPlayer.setJoined(false);

        sender.sendMessage("§fVous n'êtes plus inscrit pour la prochaine partie.");

        return true;
    }

}
