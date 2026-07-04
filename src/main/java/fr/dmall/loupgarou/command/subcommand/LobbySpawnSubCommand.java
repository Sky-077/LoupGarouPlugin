package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.LobbySpawnManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbySpawnSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "lobbyspawn";
    }

    @Override
    public String getDescription() {
        return "Définit votre position actuelle comme spawn du lobby en fin de partie (OP).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!sender.isOp()) {
            sender.sendMessage("§cCette commande est réservée aux OP.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;

        LobbySpawnManager lobbySpawnManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(LobbySpawnManager.class);

        lobbySpawnManager.setSpawn(player.getLocation());

        sender.sendMessage("§aSpawn du lobby défini à votre position actuelle.");

        return true;
    }

}
