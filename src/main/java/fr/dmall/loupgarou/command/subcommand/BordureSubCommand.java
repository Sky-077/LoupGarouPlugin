package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.HostManager;
import fr.dmall.loupgarou.game.WorldManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BordureSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "bordure";
    }

    @Override
    public String getDescription() {
        return "Configure la taille de la bordure de monde pour la prochaine partie.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        WorldManager worldManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(WorldManager.class);

        if (args.length < 2) {
            sender.sendMessage("§fBordure actuelle : §e" + (long) worldManager.getBorderSize() + " blocs");
            sender.sendMessage("§fUsage : /lg bordure <taille>");
            return true;
        }

        HostManager hostManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(HostManager.class);

        if (!(sender instanceof Player) || !hostManager.isActiveHost((Player) sender)) {
            sender.sendMessage("§cSeul l'hôte de la partie peut modifier la bordure.");
            return true;
        }

        double size;

        try {
            size = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cTaille invalide.");
            return true;
        }

        if (size < WorldManager.MIN_BORDER_SIZE) {
            sender.sendMessage("§cLa bordure doit faire au moins " + (long) WorldManager.MIN_BORDER_SIZE + " blocs.");
            return true;
        }

        worldManager.setBorderSize(size);

        sender.sendMessage("§aBordure réglée sur §e" + (long) size + " §ablocs pour la prochaine partie.");

        return true;
    }

}
