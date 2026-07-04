package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.BountyManager;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ForcePvpSubCommand extends DebugSubCommand {

    @Override
    public String getName() {
        return "forcepvp";
    }

    @Override
    public String getDescription() {
        return "[Debug] Active immédiatement le PVP sans attendre le délai de 30 minutes.";
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

        if (game.isPvpEnabled()) {
            sender.sendMessage("§cLe PVP est déjà activé.");
            return true;
        }

        game.enablePvp();
        BountyManager.onPvpEnabled(game);

        Bukkit.broadcastMessage("§c⚔ Le PVP est désormais activé !");

        return true;
    }

}
