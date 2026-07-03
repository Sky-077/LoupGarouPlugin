package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import org.bukkit.command.CommandSender;

public class StopSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Arrête la partie.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() == GameState.WAITING) {
            sender.sendMessage("§cAucune partie n'est en cours.");
            return true;
        }

        game.setState(GameState.WAITING);

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        for (LGPlayer lgPlayer : playerManager.getPlayers()) {
            lgPlayer.setJoined(false);
        }

        sender.sendMessage("§cLa partie a été arrêtée.");

        return true;
    }
}