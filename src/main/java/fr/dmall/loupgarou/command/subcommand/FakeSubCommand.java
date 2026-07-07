package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.FakePlayerManager;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleFactory;
import fr.dmall.loupgarou.role.RoleManager;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

// [Debug] Faux joueurs de test (FakePlayerManager) : /lg fake spawn|remove|list|clear.
// Le rôle est donné une fois pour toutes au spawn (jamais réassigné par RoleManager.assignRoles,
// le bot n'entrant pas dans son tirage), pour tester les rôles qui ont besoin de beaucoup de joueurs
// proches (corruption, charme, votes...) sans avoir besoin de plusieurs comptes réels.
public class FakeSubCommand extends DebugSubCommand {

    @Override
    public String getName() {
        return "fake";
    }

    @Override
    public String getDescription() {
        return "[Debug] Faux joueurs de test : /lg fake spawn|remove|list|clear.";
    }

    // Désactivée temporairement : /lg fake spawn a coïncidé avec un arrêt propre du serveur (probablement
    // une protection anti-abus de l'hébergeur détectant la fausse connexion NMS locale), pas confirmé.
    // Le code reste en place pour une reprise ultérieure, voir GUIDE_TEST.md.
    private static final boolean ENABLED = false;

    @Override
    protected boolean executeDebug(CommandSender sender, String[] args) {

        if (!ENABLED) {
            sender.sendMessage("§cCette commande est temporairement désactivée.");
            return true;
        }

        if (args.length < 2) {
            sendUsage(sender);
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "spawn":
                return spawn(sender, args);
            case "remove":
                return remove(sender, args);
            case "list":
                return list(sender);
            case "clear":
                return clear(sender);
            default:
                sendUsage(sender);
                return true;
        }

    }

    private boolean spawn(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur en jeu peut utiliser cette commande (spawn à sa position).");
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage("§cUsage : /lg fake spawn <nom> <role>");
            return true;
        }

        String name = args[2];

        if (Bukkit.getPlayer(name) != null || Bukkit.getOfflinePlayer(name).hasPlayedBefore()) {
            sender.sendMessage("§cCe nom est déjà utilisé par un vrai joueur, choisis-en un autre.");
            return true;
        }

        String roleName = args[3];

        if (!RoleFactory.exists(roleName)) {
            sender.sendMessage("§cRôle inconnu : " + roleName + ". Utilise /lg role available pour voir les noms exacts.");
            return true;
        }

        FakePlayerManager fakePlayerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(FakePlayerManager.class);

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        RoleManager roleManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(RoleManager.class);

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Player bot = fakePlayerManager.spawn(name, ((Player) sender).getLocation());

        playerManager.add(bot);

        Role role = RoleFactory.create(roleName);
        LGPlayer lgPlayer = playerManager.get(bot);
        lgPlayer.setRole(role);

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.WAITING) {

            game.addPlayer(lgPlayer);

            if (role.getTeam() == RoleTeam.LOUP || roleName.equalsIgnoreCase("loup-blanc")) {
                roleManager.assignKnownWolves(game.getPlayers());
            }

        }

        sender.sendMessage("§aBot §e" + name + " §aspawné avec le rôle §e" + roleName + "§a"
                + (game.getState() != GameState.WAITING ? " et ajouté à la partie en cours." : " (aucune partie en cours, pas ajouté à Game)."));

        return true;

    }

    private boolean remove(CommandSender sender, String[] args) {

        if (args.length < 3) {
            sender.sendMessage("§cUsage : /lg fake remove <nom>");
            return true;
        }

        UUID uuid = findBotUuid(args[2]);

        if (uuid == null) {
            sender.sendMessage("§cAucun bot avec ce nom.");
            return true;
        }

        despawn(uuid);

        sender.sendMessage("§aBot §e" + args[2] + " §asupprimé.");

        return true;

    }

    private boolean list(CommandSender sender) {

        FakePlayerManager fakePlayerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(FakePlayerManager.class);

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        Map<UUID, String> bots = fakePlayerManager.getBots();

        if (bots.isEmpty()) {
            sender.sendMessage("§fAucun bot actif.");
            return true;
        }

        sender.sendMessage("§6Bots actifs (" + bots.size() + ") :");

        for (Map.Entry<UUID, String> entry : bots.entrySet()) {

            Player bot = Bukkit.getPlayer(entry.getKey());
            LGPlayer lgPlayer = (bot != null) ? playerManager.get(bot) : null;
            String roleName = (lgPlayer != null && lgPlayer.getRole() != null) ? lgPlayer.getRole().getName() : "?";

            sender.sendMessage("§f- §e" + entry.getValue() + " §f(" + roleName + ")");

        }

        return true;

    }

    private boolean clear(CommandSender sender) {

        FakePlayerManager fakePlayerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(FakePlayerManager.class);

        int count = fakePlayerManager.getBots().size();

        for (UUID uuid : fakePlayerManager.getBots().keySet().toArray(new UUID[0])) {
            despawn(uuid);
        }

        sender.sendMessage("§a" + count + " bot(s) supprimé(s).");

        return true;

    }

    private void despawn(UUID uuid) {

        FakePlayerManager fakePlayerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(FakePlayerManager.class);

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Player bot = Bukkit.getPlayer(uuid);
        LGPlayer lgPlayer = (bot != null) ? playerManager.get(bot) : null;

        if (lgPlayer != null) {
            gameManager.getCurrentGame().removePlayer(lgPlayer);
        }

        fakePlayerManager.remove(uuid);
        playerManager.forget(uuid);

    }

    private UUID findBotUuid(String name) {

        FakePlayerManager fakePlayerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(FakePlayerManager.class);

        for (Map.Entry<UUID, String> entry : fakePlayerManager.getBots().entrySet()) {

            if (entry.getValue().equalsIgnoreCase(name)) {
                return entry.getKey();
            }

        }

        return null;

    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§eUsage : /lg fake spawn <nom> <role> §f| §e/lg fake remove <nom> §f| §e/lg fake list §f| §e/lg fake clear");
    }

}
