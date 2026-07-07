package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.HostManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HostSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "host";
    }

    @Override
    public String getDescription() {
        return "Gère l'hôte de la partie : add/remove/list (OP), claim/release (joueur éligible).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        HostManager hostManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(HostManager.class);

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg host <add|remove|list|claim|release> [joueur]");
            return true;
        }

        String action = args[1].toLowerCase();

        switch (action) {

            case "add":
                return handleAdd(sender, args, hostManager);

            case "remove":
                return handleRemove(sender, args, hostManager);

            case "list":
                return handleList(sender, hostManager);

            case "claim":
                return handleClaim(sender, hostManager);

            case "release":
                return handleRelease(sender, hostManager);

            default:
                sender.sendMessage("§cAction inconnue. Usage : /lg host <add|remove|list|claim|release> [joueur]");
                return true;

        }

    }

    private boolean handleAdd(CommandSender sender, String[] args, HostManager hostManager) {

        if (!sender.isOp()) {
            sender.sendMessage("§cCette action est réservée aux OP.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§cUsage : /lg host add <joueur>");
            return true;
        }

        String name = args[2];

        if (!hostManager.addEligible(name)) {
            sender.sendMessage("§c" + name + " est déjà éligible.");
            return true;
        }

        sender.sendMessage("§a" + name + " peut désormais devenir hôte.");

        return true;

    }

    private boolean handleRemove(CommandSender sender, String[] args, HostManager hostManager) {

        if (!sender.isOp()) {
            sender.sendMessage("§cCette action est réservée aux OP.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§cUsage : /lg host remove <joueur>");
            return true;
        }

        String name = args[2];

        if (name.equalsIgnoreCase(HostManager.PERMANENT_HOST)) {
            sender.sendMessage("§c" + HostManager.PERMANENT_HOST + " est hôte permanent, impossible de le retirer.");
            return true;
        }

        if (!hostManager.removeEligible(name)) {
            sender.sendMessage("§c" + name + " n'était pas éligible.");
            return true;
        }

        sender.sendMessage("§a" + name + " ne peut plus devenir hôte.");

        return true;

    }

    private boolean handleList(CommandSender sender, HostManager hostManager) {

        sender.sendMessage("§fJoueurs éligibles : §e" + String.join(", ", hostManager.listEligible()));
        sender.sendMessage(hostManager.hasActiveHost()
                ? "§fHôte actuel : §e" + hostManager.getActiveHostName()
                : "§fAucun hôte actif actuellement.");

        return true;

    }

    private boolean handleClaim(CommandSender sender, HostManager hostManager) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;

        if (hostManager.isActiveHost(player)) {
            sender.sendMessage("§fVous êtes déjà l'hôte.");
            return true;
        }

        if (!hostManager.isEligible(player.getName())) {
            sender.sendMessage("§cVous n'êtes pas éligible pour devenir hôte.");
            return true;
        }

        if (hostManager.hasActiveHost()) {
            sender.sendMessage("§c" + hostManager.getActiveHostName() + " est déjà l'hôte.");
            return true;
        }

        hostManager.claim(player);

        return true;

    }

    private boolean handleRelease(CommandSender sender, HostManager hostManager) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;

        if (!hostManager.release(player)) {
            sender.sendMessage("§cVous n'êtes pas l'hôte actuellement.");
        }

        return true;

    }

}
