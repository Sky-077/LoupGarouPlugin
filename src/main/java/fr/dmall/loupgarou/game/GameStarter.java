package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.RoleManager;
import fr.dmall.loupgarou.role.RoleTeam;
import fr.dmall.loupgarou.role.solo.FeuFolletRole;
import fr.dmall.loupgarou.role.solo.LoupBlancRole;
import fr.dmall.loupgarou.role.village.BienfaiteurRole;
import fr.dmall.loupgarou.role.village.ChasseurRole;
import fr.dmall.loupgarou.role.village.CupidonRole;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameStarter {

    private static int minPlayers = 3;
    private static int invincibilityMinutes = 3; // laisse le temps à la pré-génération du monde de finir en fond
    private static int roleRevealMinutes = 10;
    private static int pvpDelayMinutes = 30;
    private static int voteStartMinutes = 45;

    private GameStarter() {
    }

    public static int getMinPlayers() {
        return minPlayers;
    }

    public static void setMinPlayers(int value) {
        minPlayers = Math.max(1, value);
    }

    public static int getInvincibilityMinutes() {
        return invincibilityMinutes;
    }

    public static void setInvincibilityMinutes(int value) {
        invincibilityMinutes = Math.max(0, Math.min(value, roleRevealMinutes));
    }

    public static int getRoleRevealMinutes() {
        return roleRevealMinutes;
    }

    public static void setRoleRevealMinutes(int value) {
        roleRevealMinutes = Math.max(Math.max(1, invincibilityMinutes), Math.min(value, pvpDelayMinutes - 1));
    }

    public static int getPvpDelayMinutes() {
        return pvpDelayMinutes;
    }

    public static void setPvpDelayMinutes(int value) {
        pvpDelayMinutes = Math.max(roleRevealMinutes + 1, Math.min(value, voteStartMinutes - 1));
    }

    public static int getVoteStartMinutes() {
        return voteStartMinutes;
    }

    public static void setVoteStartMinutes(int value) {
        voteStartMinutes = Math.max(pvpDelayMinutes + 1, value);
    }

    private static long minutesToTicks(int minutes) {
        return 20L * 60L * minutes;
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

        if (!bypassMinPlayers && joinedPlayers.size() < minPlayers) {
            sender.sendMessage("§cIl faut au moins " + minPlayers + " joueurs inscrits (/lg join) pour lancer une partie (actuellement "
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
            scatterPlayer.setHealth(scatterPlayer.getAttribute(Attribute.MAX_HEALTH).getValue());
            scatterPlayer.setFoodLevel(20);
            scatterPlayer.setSaturation(20f);
            scatterPlayer.setFireTicks(0);
            scatterPlayer.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));

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

        long invincibilityTicks = minutesToTicks(invincibilityMinutes);

        Bukkit.broadcastMessage("§aLa partie a été lancée ! §7Vous êtes invulnérable pendant "
                + (invincibilityTicks / 20L) + " secondes. Les rôles seront révélés dans " + roleRevealMinutes + " minutes.");

        Bukkit.broadcastMessage("§7Le centre de la zone (maisons de vote) se trouve en X: "
                + worldManager.getCenterX() + ", Z: " + worldManager.getCenterZ() + ".");

        Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> beginGame(game, players, cycleManager),
                invincibilityTicks
        );

    }

    private static ItemStack createPowerBook() {

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(Enchantment.POWER, 4, true);
        meta.addStoredEnchant(Enchantment.PUNCH, 1, true);
        book.setItemMeta(meta);

        return book;

    }

    private static ItemStack createPowerBow() {

        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.addEnchant(Enchantment.POWER, 4, true);
        bow.setItemMeta(meta);

        return bow;

    }

    private static ItemStack createSharpnessBook() {

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(Enchantment.SHARPNESS, 4, true);
        book.setItemMeta(meta);

        return book;

    }

    private static ItemStack createProtectionBook() {

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(Enchantment.PROTECTION, 2, true);
        book.setItemMeta(meta);

        return book;

    }

    private static ItemStack createFeuFolletFeather() {

        ItemStack feather = new ItemStack(Material.FEATHER);
        ItemMeta meta = feather.getItemMeta();
        meta.setDisplayName("§bPlume du Feu Follet");
        feather.setItemMeta(meta);

        return feather;

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

        Bukkit.broadcastMessage("§aLa partie commence ! §7Les rôles seront révélés dans " + roleRevealMinutes
                + " minutes, le PVP activé dans " + pvpDelayMinutes + " minutes.");

        long startedAt = game.getStartTimeMillis();

        Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> scheduledReveal(game, startedAt),
                minutesToTicks(roleRevealMinutes)
        );

        Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> enablePvp(game, startedAt),
                minutesToTicks(pvpDelayMinutes)
        );

        Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> startVoting(game, startedAt),
                minutesToTicks(voteStartMinutes)
        );

    }

    private static void startVoting(Game game, long startedAt) {

        if (game.getStartTimeMillis() != startedAt) {
            return;
        }

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            return;
        }

        VoteManager voteManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(VoteManager.class);

        voteManager.startVoting(game);

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
            giveRoleItems(lgPlayer, player);

            if (lgPlayer.getRole() instanceof LoupBlancRole) {
                LoupBlancManager.applyHearts(player);
            }

            if (lgPlayer.getRole() instanceof FeuFolletRole) {
                ((FeuFolletRole) lgPlayer.getRole()).checkInitialInvisibility(player);
            }

            if (isNight) {
                lgPlayer.getRole().onNight(player);
            } else {
                lgPlayer.getRole().onDay(player);
            }

        }

        Bukkit.broadcastMessage("§6Les rôles sont désormais révélés !");

    }

    public static void giveRoleItems(LGPlayer lgPlayer, Player player) {

        if (lgPlayer.getRole() instanceof CupidonRole) {
            player.getInventory().addItem(new ItemStack(Material.BOW));
            player.getInventory().addItem(createPowerBook());
            player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
        } else if (lgPlayer.getRole() instanceof ChasseurRole) {
            player.getInventory().addItem(createPowerBow());
            player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
        } else if (lgPlayer.getRole() instanceof BienfaiteurRole) {
            player.getInventory().addItem(createProtectionBook());
            player.getInventory().addItem(createProtectionBook());
        } else if (lgPlayer.getRole().getTeam() == RoleTeam.NEUTRAL) {
            player.getInventory().addItem(createSharpnessBook());
        }

        if (lgPlayer.getRole() instanceof FeuFolletRole) {
            player.getInventory().addItem(createFeuFolletFeather());
        }

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
        BountyManager.onPvpEnabled(game);

        Bukkit.broadcastMessage("§c⚔ Le PVP est désormais activé !");

    }

}
