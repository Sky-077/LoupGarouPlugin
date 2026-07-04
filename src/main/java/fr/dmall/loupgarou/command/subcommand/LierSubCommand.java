package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.LoveManager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.village.CupidonRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LierSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "lier";
    }

    @Override
    public String getDescription() {
        return "Lie deux joueurs par l'amour (Cupidon, une fois par partie).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player cupidonPlayer = (Player) sender;

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgCupidon = playerManager.get(cupidonPlayer);

        if (lgCupidon == null || !lgCupidon.isAlive()) {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande.");
            return true;
        }

        Role role = lgCupidon.getRole();

        if (!(role instanceof CupidonRole)) {
            sender.sendMessage("§cVous n'êtes pas Cupidon.");
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

        CupidonRole cupidon = (CupidonRole) role;

        if (!cupidon.isPowerAvailable()) {
            sender.sendMessage("§cVous avez déjà lié deux joueurs cette partie.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§cUsage : /lg lier <joueur1> <joueur2>");
            return true;
        }

        Player target1 = Bukkit.getPlayer(args[1]);
        Player target2 = Bukkit.getPlayer(args[2]);

        if (target1 == null || target2 == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        if (target1.equals(target2)) {
            sender.sendMessage("§cVous devez choisir deux joueurs différents.");
            return true;
        }

        LGPlayer lgTarget1 = playerManager.get(target1);
        LGPlayer lgTarget2 = playerManager.get(target2);

        if (lgTarget1 == null || !lgTarget1.isAlive() || lgTarget2 == null || !lgTarget2.isAlive()) {
            sender.sendMessage("§cLes deux joueurs doivent faire partie de la partie en cours et être vivants.");
            return true;
        }

        LoveManager loveManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(LoveManager.class);

        cupidon.consumePower();
        loveManager.link(lgTarget1, lgTarget2, lgCupidon);

        target1.sendMessage("§dCupidon vous a lié à " + target2.getName() + " par l'amour ! Si l'un de vous meurt, l'autre mourra aussi.");
        target2.sendMessage("§dCupidon vous a lié à " + target1.getName() + " par l'amour ! Si l'un de vous meurt, l'autre mourra aussi.");
        cupidonPlayer.sendMessage("§dVous avez lié " + target1.getName() + " et " + target2.getName() + " par l'amour !");

        if (loveManager.isCrossedCamps()) {
            target1.sendMessage("§5Vous formez un nouveau camp avec Cupidon : les Amoureux doivent éliminer tout le monde pour gagner !");
            target2.sendMessage("§5Vous formez un nouveau camp avec Cupidon : les Amoureux doivent éliminer tout le monde pour gagner !");
            cupidonPlayer.sendMessage("§5Vos amoureux sont de camps opposés : vous rejoignez leur camp pour les aider à survivre !");
        }

        return true;
    }

}
