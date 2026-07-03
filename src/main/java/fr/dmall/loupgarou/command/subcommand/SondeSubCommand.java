package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.village.VoyanteRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SondeSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "sonder";
    }

    @Override
    public String getDescription() {
        return "Sonde un joueur pour connaître son rôle (Voyante, la nuit uniquement).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg sonder <joueur>");
            return true;
        }

        Player viewer = (Player) sender;

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        LGPlayer lgViewer = playerManager.get(viewer);

        if (lgViewer == null || !lgViewer.isAlive()) {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande.");
            return true;
        }

        Role role = lgViewer.getRole();

        if (!(role instanceof VoyanteRole)) {
            sender.sendMessage("§cVous n'êtes pas Voyante.");
            return true;
        }

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.NIGHT) {
            sender.sendMessage("§cVous ne pouvez sonder un joueur que la nuit.");
            return true;
        }

        VoyanteRole voyante = (VoyanteRole) role;

        if (!voyante.isPowerAvailable()) {
            sender.sendMessage("§cVous avez déjà utilisé votre pouvoir cette nuit.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        if (target.equals(viewer)) {
            sender.sendMessage("§cVous ne pouvez pas vous sonder vous-même.");
            return true;
        }

        LGPlayer lgTarget = playerManager.get(target);

        if (lgTarget == null || !lgTarget.isAlive() || lgTarget.getRole() == null) {
            sender.sendMessage("§cCe joueur ne fait pas partie de la partie en cours.");
            return true;
        }

        voyante.consumePower();

        sender.sendMessage("§d" + target.getName() + " est §f" + lgTarget.getRole().getName()
                + " §d(équipe " + lgTarget.getRole().getTeam() + ").");

        return true;
    }

}
