package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.RoleManager;
import fr.dmall.loupgarou.role.village.CupidonRole;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameStarter {

    private static final int MIN_PLAYERS = 3;
    private static final long INVINCIBILITY_DURATION_TICKS = 20L * 30L; // 30 secondes
    private static final long PVP_DELAY_TICKS = 20L * 60L * 30L; // 30 minutes
    private static final long ROLE_REVEAL_DELAY_TICKS = 20L * 60L * 10L; // 10 minutes

    private GameStarter() {
    }

    public static boolean start(CommandSender sender, boolean bypassMinPlayers) {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.WAITING) {
            sender.sendMessage("§cUne partie est déjà en cours.");
            return false;
        }

        List<LGPlayer> joinedPlayers = playerManager.getPlayers().stream()
                .filter(LGPlayer::isJoined)
                .collect(Collectors.toList());

        if (!bypassMinPlayers && joinedPlayers.size() < MIN_PLAYERS) {
            sender.sendMessage("§cIl faut au moins " + MIN_PLAYERS + " joueurs inscrits (/lg join) pour lancer une partie (actuellement "
                    + joinedPlayers.size() + ").");
            return false;
        }

        launchGame(joinedPlayers);

        sender.sendMessage("§7Joueurs : §e" + joinedPlayers.size());

        return true;

    }

    private static void launchGame(List<LGPlayer> joinedPlayers) {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        RoleManager roleManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(RoleManager.class);

        CycleManager cycleManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(CycleManager.class);

        WorldManager worldManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(WorldManager.class);

        Game game = gameManager.getCurrentGame();

        game.resetForNewMatch();
        game.clearPlayers();

        List<LGPlayer> players = new ArrayList<>(joinedPlayers);

        for (LGPlayer player : players) {
            game.addPlayer(player);
            player.setJoined(false);
        }

        roleManager.assignRoles(players);

        Bukkit.broadcastMessage("§7Génération du monde de jeu, veuillez patienter...");

        World gameWorld = worldManager.prepareGameWorld();

        game.setState(GameState.SCATTERING);

        for (LGPlayer lgPlayer : players) {

            Player scatterPlayer = Bukkit.getPlayer(lgPlayer.getUuid());

            if (scatterPlayer == null) {
                continue;
            }

            Location location = worldManager.findScatterLocation(gameWorld);
            scatterPlayer.getInventory().clear();
            scatterPlayer.getInventory().setArmorContents(null);
            scatterPlayer.setGameMode(GameMode.SURVIVAL);
            scatterPlayer.teleport(location);
            scatterPlayer.setInvulnerable(true);
            scatterPlayer.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));

            if (lgPlayer.getRole() instanceof CupidonRole) {
                scatterPlayer.getInventory().addItem(createCupidonBow());
                scatterPlayer.getInventory().addItem(new ItemStack(Material.ARROW, 64));
            }

        }

        for (LGPlayer lgPlayer : playerManager.getPlayers()) {

            if (players.contains(lgPlayer)) {
                continue;
            }

            Player spectator = Bukkit.getPlayer(lgPlayer.getUuid());

            if (spectator == null) {
                continue;
            }

            spectator.teleport(gameWorld.getSpawnLocation());
            spectator.setGameMode(GameMode.SPECTATOR);

        }

        game.setState(GameState.INVINCIBILITY);

        long invincibilitySeconds = INVINCIBILITY_DURATION_TICKS / 20L;

        Bukkit.broadcastMessage("§aLa partie a été lancée ! §7Vous êtes invulnérable pendant "
                + invincibilitySeconds + " secondes. Les rôles seront révélés dans 10 minutes.");

        Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> beginGame(game, players, cycleManager),
                INVINCIBILITY_DURATION_TICKS
        );

    }

    private static ItemStack createCupidonBow() {

        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.addEnchant(Enchantment.POWER, 5, true);
        bow.setItemMeta(meta);

        return bow;

    }

    private static void beginGame(Game game, List<LGPlayer> players, CycleManager cycleManager) {

        if (game.getState() != GameState.INVINCIBILITY) {
            return;
        }

        for (LGPlayer lgPlayer : players) {

            Player player = Bukkit.getPlayer(lgPlayer.getUuid());

            if (player != null) {
                player.setInvulnerable(false);
            }

        }

        game.markStarted();
        game.setState(cycleManager.getPhaseForCurrentTime());

        Bukkit.broadcastMessage("§aLa partie commence ! §7Les rôles seront révélés dans 10 minutes, le PVP activé dans 30 minutes.");

        long startedAt = game.getStartTimeMillis();

        Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> scheduledReveal(game, startedAt),
                ROLE_REVEAL_DELAY_TICKS
        );

        Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> enablePvp(game, startedAt),
                PVP_DELAY_TICKS
        );

    }

    private static void scheduledReveal(Game game, long startedAt) {

        if (game.getStartTimeMillis() != startedAt) {
            return;
        }

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            return;
        }

        if (game.isRevealed()) {
            return;
        }

        revealRoles(game);

    }

    public static void forceReveal(CommandSender sender) {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            sender.sendMessage("§cAucune partie en cours.");
            return;
        }

        if (game.isRevealed()) {
            sender.sendMessage("§cLes rôles sont déjà révélés.");
            return;
        }

        revealRoles(game);

    }

    private static void revealRoles(Game game) {

        game.reveal();

        boolean isNight = game.getState() == GameState.NIGHT;

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (lgPlayer.getRole() == null) {
                continue;
            }

            Player player = Bukkit.getPlayer(lgPlayer.getUuid());

            if (player == null) {
                continue;
            }

            lgPlayer.getRole().sendInstructions(player);

            if (isNight) {
                lgPlayer.getRole().onNight(player);
            } else {
                lgPlayer.getRole().onDay(player);
            }

        }

        Bukkit.broadcastMessage("§6Les rôles sont désormais révélés !");

    }

    private static void enablePvp(Game game, long startedAt) {

        if (game.getStartTimeMillis() != startedAt) {
            return;
        }

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            return;
        }

        if (game.isPvpEnabled()) {
            return;
        }

        game.enablePvp();

        Bukkit.broadcastMessage("§c⚔ Le PVP est désormais activé !");

    }

}
