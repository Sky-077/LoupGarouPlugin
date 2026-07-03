package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.game.GameStarter;
import org.bukkit.command.CommandSender;

public class ForceStartSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "forcestart";
    }

    @Override
    public String getDescription() {
        return "[Debug] Force le lancement de la partie et la distribution des rôles sans vérifier le nombre minimum de joueurs.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        GameStarter.start(sender, true);

        return true;
    }

}
