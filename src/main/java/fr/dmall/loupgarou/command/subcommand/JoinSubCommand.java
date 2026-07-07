package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "S'inscrit pour la prochaine partie.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.WAITING) {
            sender.sendMessage("§cImpossible de s'inscrire, une partie est déjà en cours.");
            return true;
        }

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgPlayer = playerManager.get(player);

        if (lgPlayer == null) {
            sender.sendMessage("§cVous n'êtes pas reconnu par le plugin.");
            return true;
        }

        if (lgPlayer.isJoined()) {
            sender.sendMessage("§fVous êtes déjà inscrit.");
            return true;
        }

        lgPlayer.setJoined(true);

        sender.sendMessage("§aVous êtes inscrit pour la prochaine partie !");

        return true;
    }

}
