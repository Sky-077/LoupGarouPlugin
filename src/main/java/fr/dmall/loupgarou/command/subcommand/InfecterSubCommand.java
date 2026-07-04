package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.DeathManager;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.game.VictoryChecker;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.loup.LoupGarouRole;
import fr.dmall.loupgarou.role.loup.PereDesLoupsRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class InfecterSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "infecter";
    }

    @Override
    public String getDescription() {
        return "Transforme votre victime en Loup-Garou au lieu de la laisser mourir (Père des Loups, une fois par partie).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player infector = (Player) sender;

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        DeathManager deathManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(DeathManager.class);

        LGPlayer lgInfector = playerManager.get(infector);

        if (lgInfector == null || !lgInfector.isAlive()) {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande.");
            return true;
        }

        Role role = lgInfector.getRole();

        if (!(role instanceof PereDesLoupsRole)) {
            sender.sendMessage("§cVous n'êtes pas le Père des Loups.");
            return true;
        }

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            sender.sendMessage("§cVous ne pouvez pas infecter en dehors d'une partie en cours.");
            return true;
        }

        if (!game.isRevealed()) {
            sender.sendMessage("§cLes rôles n'ont pas encore été révélés.");
            return true;
        }

        PereDesLoupsRole pereDesLoups = (PereDesLoupsRole) role;

        if (!pereDesLoups.isInfectionAvailable()) {
            sender.sendMessage("§cVous avez déjà utilisé votre pouvoir d'infection cette partie.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg infecter <joueur>");
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

        UUID pendingKiller = deathManager.getPendingKiller(target);

        if (pendingKiller == null || !pendingKiller.equals(infector.getUniqueId())) {
            sender.sendMessage("§cVous ne pouvez infecter que votre propre victime.");
            return true;
        }

        LGPlayer lgTarget = playerManager.get(target);

        if (lgTarget == null) {
            sender.sendMessage("§cCe joueur ne fait pas partie de la partie en cours.");
            return true;
        }

        pereDesLoups.consumeInfection();
        deathManager.revive(target);

        LoupGarouRole newRole = new LoupGarouRole();
        lgTarget.setRole(newRole);

        if (game.getState() == GameState.NIGHT) {
            newRole.onNight(target);
        }

        target.sendMessage("§4Le Père des Loups vous a infecté... Vous êtes désormais Loup-Garou !");
        infector.sendMessage("§4Vous avez infecté " + target.getName() + " ! Il rejoint la meute.");

        VictoryChecker.check();

        return true;
    }

}
