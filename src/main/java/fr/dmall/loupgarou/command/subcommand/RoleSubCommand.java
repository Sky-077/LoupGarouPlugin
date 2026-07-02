package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.role.RoleManager;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class RoleSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "role";
    }

    @Override
    public String getDescription() {
        return "Configure les rôles de la partie.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        RoleManager roleManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(RoleManager.class);

        if (args.length < 2) {
            sendUsage(sender);
            return true;
        }

        switch (args[1].toLowerCase()) {

            case "add": {

                if (args.length < 4) {
                    sender.sendMessage("§cUsage : /lg role add <role> <nombre>");
                    return true;
                }

                int amount;

                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cLe nombre doit être un entier.");
                    return true;
                }

                if (amount <= 0) {
                    sender.sendMessage("§cLe nombre doit être supérieur à 0.");
                    return true;
                }

                try {
                    roleManager.addGameRole(args[2], amount);
                    sender.sendMessage("§aAjouté : §e" + amount + " §a" + args[2]);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§c" + e.getMessage());
                }

                return true;
            }

            case "remove": {

                if (args.length < 3) {
                    sender.sendMessage("§cUsage : /lg role remove <role>");
                    return true;
                }

                if (roleManager.removeGameRole(args[2])) {
                    sender.sendMessage("§aRôle retiré : §e" + args[2]);
                } else {
                    sender.sendMessage("§cCe rôle n'était pas dans la configuration.");
                }

                return true;
            }

            case "clear": {
                roleManager.clearGameRoles();
                sender.sendMessage("§aConfiguration des rôles réinitialisée.");
                return true;
            }

            case "list": {

                Map<String, Integer> gameRoles = roleManager.getGameRoles();

                sender.sendMessage("§6===== Rôles configurés =====");

                if (gameRoles.isEmpty()) {
                    sender.sendMessage("§7Aucun rôle configuré (tout le monde sera Villageois).");
                } else {
                    for (Map.Entry<String, Integer> entry : gameRoles.entrySet()) {
                        sender.sendMessage(" §7- §e" + entry.getValue() + " §f" + entry.getKey());
                    }
                }

                return true;
            }

            default:
                sendUsage(sender);
                return true;
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§6===== /lg role =====");
        sender.sendMessage("§e/lg role add <role> <nombre>");
        sender.sendMessage("§e/lg role remove <role>");
        sender.sendMessage("§e/lg role list");
        sender.sendMessage("§e/lg role clear");
    }

}