package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.game.VoteManager;
import org.bukkit.command.CommandSender;

public class ForceVoteSubCommand extends DebugSubCommand {

    @Override
    public String getName() {
        return "forcevote";
    }

    @Override
    public String getDescription() {
        return "[Debug] Ouvre immédiatement le vote sans attendre le délai de 45 minutes.";
    }

    @Override
    protected boolean executeDebug(CommandSender sender, String[] args) {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            sender.sendMessage("§cAucune partie en cours.");
            return true;
        }

        VoteManager voteManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(VoteManager.class);

        if (voteManager.isActive()) {
            sender.sendMessage("§cLe vote est déjà ouvert.");
            return true;
        }

        voteManager.startVoting(game);

        return true;
    }

}
