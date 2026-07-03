package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.DeathManager;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.village.ChasseurRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TirerSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "tirer";
    }

    @Override
    public String getDescription() {
        return "Riposte une dernière fois avant de mourir (Chasseur, une fois par partie).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg tirer <joueur>");
            return true;
        }

        Player hunter = (Player) sender;

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        DeathManager deathManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(DeathManager.class);

        LGPlayer lgHunter = playerManager.get(hunter);

        if (lgHunter == null || !lgHunter.isAlive()) {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande.");
            return true;
        }

        Role role = lgHunter.getRole();

        if (!(role instanceof ChasseurRole)) {
            sender.sendMessage("§cVous n'êtes pas le Chasseur.");
            return true;
        }

        if (!deathManager.isDying(hunter)) {
            sender.sendMessage("§cVous ne pouvez utiliser ce pouvoir qu'en train de mourir.");
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

        ChasseurRole chasseur = (ChasseurRole) role;

        if (!chasseur.isShotAvailable()) {
            sender.sendMessage("§cVous avez déjà utilisé votre riposte cette partie.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        if (target.equals(hunter)) {
            sender.sendMessage("§cVous ne pouvez pas vous tirer dessus vous-même.");
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

        chasseur.consumeShot();
        hunter.sendMessage("§6Vous avez tiré une dernière fois sur " + target.getName() + " !");

        deathManager.killInstantly(target, hunter);

        return true;
    }

}
