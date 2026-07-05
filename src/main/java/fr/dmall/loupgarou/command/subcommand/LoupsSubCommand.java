package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.loup.LoupGarouRole;
import fr.dmall.loupgarou.role.loup.PereDesLoupsRole;
import fr.dmall.loupgarou.role.solo.LoupBlancRole;
import fr.dmall.loupgarou.role.village.PetiteFilleRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class LoupsSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "loups";
    }

    @Override
    public String getDescription() {
        return "Chat privé de la meute la nuit (Loup-Garou, Père des Loups, Loup Blanc) — la Petite Fille peut le lire, tous les pseudos sont masqués.";
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

        if (lgPlayer == null || !lgPlayer.isAlive()) {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande.");
            return true;
        }

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (!game.isRevealed()) {
            sender.sendMessage("§cLes rôles n'ont pas encore été révélés.");
            return true;
        }

        if (game.getState() != GameState.NIGHT) {
            sender.sendMessage("§cCe chat n'est disponible que la nuit.");
            return true;
        }

        Role role = lgPlayer.getRole();

        if (!(role instanceof LoupGarouRole) && !(role instanceof PereDesLoupsRole) && !(role instanceof LoupBlancRole)) {
            sender.sendMessage("§cVous n'avez pas accès à ce chat.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg loups <message>");
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        for (LGPlayer lgRecipient : game.getPlayers()) {

            if (!lgRecipient.isAlive()) {
                continue;
            }

            Role recipientRole = lgRecipient.getRole();

            boolean canSee = recipientRole instanceof LoupGarouRole
                    || recipientRole instanceof PereDesLoupsRole
                    || recipientRole instanceof LoupBlancRole
                    || recipientRole instanceof PetiteFilleRole;

            if (!canSee) {
                continue;
            }

            Player recipient = Bukkit.getPlayer(lgRecipient.getUuid());

            if (recipient != null) {
                recipient.sendMessage("§5[Meute] §7" + message);
            }

        }

        return true;
    }

}
