package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameEnder;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.game.HostManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Arrête la partie (réservé à l'hôte, ou à un OP en secours).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        HostManager hostManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(HostManager.class);

        boolean isHost = sender instanceof Player && hostManager.isActiveHost((Player) sender);

        if (!sender.isOp() && !isHost) {
            sender.sendMessage("§cSeul l'hôte de la partie (ou un OP) peut utiliser cette commande.");
            return true;
        }

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() == GameState.WAITING && !game.isLaunching()) {
            sender.sendMessage("§cAucune partie n'est en cours.");
            return true;
        }

        GameEnder.end("§cLa partie a été arrêtée.");

        return true;
    }
}
