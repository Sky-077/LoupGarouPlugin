package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.game.GameStarter;
import org.bukkit.command.CommandSender;

public class ForceStartSubCommand extends DebugSubCommand {

    @Override
    public String getName() {
        return "forcestart";
    }

    @Override
    public String getDescription() {
        return "[Debug] Force le lancement de la partie et la distribution des rôles sans vérifier le nombre minimum de joueurs.";
    }

    @Override
    protected boolean executeDebug(CommandSender sender, String[] args) {

        GameStarter.start(sender, true);

        return true;
    }

}
