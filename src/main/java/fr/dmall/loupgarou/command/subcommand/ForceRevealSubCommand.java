package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.game.GameStarter;
import org.bukkit.command.CommandSender;

public class ForceRevealSubCommand extends DebugSubCommand {

    @Override
    public String getName() {
        return "forcereveal";
    }

    @Override
    public String getDescription() {
        return "[Debug] Force la révélation immédiate des rôles sans attendre le délai de 10 minutes.";
    }

    @Override
    protected boolean executeDebug(CommandSender sender, String[] args) {

        GameStarter.forceReveal(sender);

        return true;
    }

}
