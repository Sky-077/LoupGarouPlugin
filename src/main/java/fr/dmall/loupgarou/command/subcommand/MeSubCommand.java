package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MeSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "me";
    }

    @Override
    public String getDescription() {
        return "Affiche votre rôle actuel.";
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

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (!game.isRevealed()) {
            sender.sendMessage("§7Les rôles n'ont pas encore été révélés.");
            return true;
        }

        sender.sendMessage("§6===== Votre rôle =====");
        sender.sendMessage("§eRôle : §f" + role.getName());
        sender.sendMessage("§eÉquipe : §f" + role.getTeam());

        return true;
    }

}