package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.GameStarter;
import fr.dmall.loupgarou.game.HostManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelaisSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "delais";
    }

    @Override
    public String getDescription() {
        return "Configure les délais de partie et le minimum de joueurs (réservé à l'hôte).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sendCurrentValues(sender);
            return true;
        }

        String key = args[1].toLowerCase();

        if (args.length < 3) {

            if (!key.equals("minjoueurs") && !key.equals("invincibilite") && !key.equals("revelation")
                    && !key.equals("pvp") && !key.equals("vote") && !key.equals("rapide")) {
                sendUsage(sender);
                return true;
            }

            sendCurrentValues(sender);
            return true;

        }

        HostManager hostManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(HostManager.class);

        if (!(sender instanceof Player) || !hostManager.isActiveHost((Player) sender)) {
            sender.sendMessage("§cSeul l'hôte de la partie peut modifier ces paramètres.");
            return true;
        }

        int value;

        try {
            value = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cValeur invalide.");
            return true;
        }

        switch (key) {

            case "minjoueurs":
                GameStarter.setMinPlayers(value);
                sender.sendMessage("§aMinimum de joueurs réglé sur §e" + GameStarter.getMinPlayers());
                return true;

            case "invincibilite":
                GameStarter.setInvincibilityMinutes(value);
                sender.sendMessage("§aInvincibilité réglée sur §e" + GameStarter.getInvincibilityMinutes() + " min");
                return true;

            case "revelation":
                GameStarter.setRoleRevealMinutes(value);
                sender.sendMessage("§aRévélation des rôles réglée sur §e" + GameStarter.getRoleRevealMinutes() + " min");
                return true;

            case "pvp":
                GameStarter.setPvpDelayMinutes(value);
                sender.sendMessage("§aActivation du PVP réglée sur §e" + GameStarter.getPvpDelayMinutes() + " min");
                return true;

            case "vote":
                GameStarter.setVoteStartMinutes(value);
                sender.sendMessage("§aOuverture du vote réglée sur §e" + GameStarter.getVoteStartMinutes() + " min");
                return true;

            case "rapide":
                GameStarter.setQuickModeMinutes(value);
                sender.sendMessage("§aDélai PVP/vote du mode rapide réglé sur §e" + GameStarter.getQuickModeMinutes() + " min");
                return true;

            default:
                sendUsage(sender);
                return true;

        }

    }

    private void sendCurrentValues(CommandSender sender) {

        sender.sendMessage("§6===== Délais de partie =====");
        sender.sendMessage("§fMinimum de joueurs : §e" + GameStarter.getMinPlayers());
        sender.sendMessage("§fInvincibilité : §e" + GameStarter.getInvincibilityMinutes() + " min");
        sender.sendMessage("§fRévélation des rôles : §e" + GameStarter.getRoleRevealMinutes() + " min");
        sender.sendMessage("§fActivation du PVP : §e" + GameStarter.getPvpDelayMinutes() + " min");
        sender.sendMessage("§fOuverture du vote : §e" + GameStarter.getVoteStartMinutes() + " min");
        sender.sendMessage("§fMode rapide : §e" + (GameStarter.isQuickMode() ? "activé" : "désactivé") + " §f(bascule via /lg menu)");
        sender.sendMessage("§fDélai PVP/vote en mode rapide : §e" + GameStarter.getQuickModeMinutes() + " min");
        sendUsage(sender);

    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§fUsage : /lg delais <minjoueurs|invincibilite|revelation|pvp|vote|rapide> [valeur]");
    }

}
