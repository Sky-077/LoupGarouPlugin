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

    public static final int LOCKED_EXPERIENCE_LEVEL = 30;
    private static final int DIAMOND_LIMIT_NORMAL = 18;
    private static final int DIAMOND_LIMIT_QUICK = 0;
    // Laisse la pré-génération asynchrone du monde avancer avant de forcer la construction des maisons de vote
    // et le scattering (bloquants), pour éviter un gel du serveur assez long pour faire timeout les joueurs.
    private static final long WORLD_PREP_DELAY_TICKS = 20L * 15L;

    private static int minPlayers = 3;
    private static int invincibilityMinutes = 3; // laisse le temps à la pré-génération du monde de finir en fond
    private static int roleRevealMinutes = 10;
    private static int pvpDelayMinutes = 30;
    private static int voteStartMinutes = 45;
    private static boolean quickMode = false;
    private static int quickModeMinutes = 10; // délai commun PVP + vote en mode rapide

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

    public static boolean isQuickMode() {
        return quickMode;
    }

    public static void setQuickMode(boolean value) {
        quickMode = value;
    }

    public static int getQuickModeMinutes() {
        return quickModeMinutes;
    }

    public static void setQuickModeMinutes(int value) {
        quickModeMinutes = Math.max(1, value);
    }

    public static int getDiamondLimit() {
        return quickMode ? DIAMOND_LIMIT_QUICK : DIAMOND_LIMIT_NORMAL;
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

        if (game.getState() != GameState.WAITING || game.isLaunching()) {
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

        game.setLaunching(true);

        roleManager.assignRoles(players);

        Bukkit.broadcastMessage("§7Génération du monde de jeu, veuillez patienter...");

        World gameWorld = worldManager.prepareGameWorld();

        Bukkit.broadcastMessage("§7Préparation de la zone de jeu, encore " + (WORLD_PREP_DELAY_TICKS / 20L)
                + " secondes avant le scattering...");

        Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> continueLaunch(game, players, playerManager, cycleManager, worldManager, gameWorld),
                WORLD_PREP_DELAY_TICKS
        );

    }

    private static void continueLaunch(Game game, List<LGPlayer> players, PlayerManager playerManager,
                                        CycleManager cycleManager, WorldManager worldManager, World gameWorld) {

        // La partie peut avoir été arrêtée pendant le délai de préparation du monde.
        if (!game.getPlayers().equals(players)) {
            return;
        }

        game.setLaunching(false);

        worldManager.buildVoteHouses(gameWorld);

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
            scatterPlayer.setLevel(LOCKED_EXPERIENCE_LEVEL);
            scatterPlayer.setExp(0f);
            scatterPlayer.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));

            if (quickMode) {
                giveQuickModeGear(scatterPlayer, lgPlayer);
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

    private static void applyCupidonKitBowEnchants(Player player) {

        for (ItemStack item : player.getInventory().getContents()) {

            if (item == null || item.getType() != Material.BOW) {
                continue;
            }

            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(Enchantment.POWER, 4, true);
            meta.addEnchant(Enchantment.PUNCH, 1, true);
            item.setItemMeta(meta);

            return;

        }

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

    private static void giveQuickModeGear(Player player, LGPlayer lgPlayer) {

        boolean solo = lgPlayer.getRole() != null && lgPlayer.getRole().getTeam() == RoleTeam.NEUTRAL;
        boolean chasseur = lgPlayer.getRole() instanceof ChasseurRole;

        player.getInventory().setHelmet(enchantedItem(Material.IRON_HELMET, Enchantment.PROTECTION, 3));
        player.getInventory().setChestplate(enchantedItem(Material.DIAMOND_CHESTPLATE, Enchantment.PROTECTION, 2));
        player.getInventory().setLeggings(enchantedItem(Material.IRON_LEGGINGS, Enchantment.PROTECTION, 3));
        player.getInventory().setBoots(enchantedItem(Material.IRON_BOOTS, Enchantment.PROTECTION, 3));

        player.getInventory().addItem(enchantedItem(Material.DIAMOND_SWORD, Enchantment.SHARPNESS, solo ? 4 : 3));
        player.getInventory().addItem(enchantedItem(Material.BOW, Enchantment.POWER, (solo || chasseur) ? 4 : 3));

        player.getInventory().addItem(new ItemStack(Material.OAK_LEAVES, 128));
        player.getInventory().addItem(new ItemStack(Material.ARROW, 128));
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 10));
        player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 32));

    }

    private static ItemStack enchantedItem(Material material, Enchantment enchantment, int level) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(enchantment, level, true);
        item.setItemMeta(meta);

        return item;

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

        long startedAt = game.getStartTimeMillis();

        if (quickMode) {

            Bukkit.broadcastMessage("§aLa partie commence (mode rapide) ! §7Les rôles sont révélés immédiatement, "
                    + "le PVP et le vote seront activés dans " + quickModeMinutes + " minutes.");

            revealRoles(game);

            Bukkit.getScheduler().runTaskLater(
                    LoupGarouPlugin.getInstance(),
                    () -> enablePvp(game, startedAt),
                    minutesToTicks(quickModeMinutes)
            );

            Bukkit.getScheduler().runTaskLater(
                    LoupGarouPlugin.getInstance(),
                    () -> startVoting(game, startedAt),
                    minutesToTicks(quickModeMinutes)
            );

            return;

        }

        Bukkit.broadcastMessage("§aLa partie commence ! §7Les rôles seront révélés dans " + roleRevealMinutes
                + " minutes, le PVP activé dans " + pvpDelayMinutes + " minutes.");

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

            // En mode rapide, l'arc du kit de départ reçoit directement Puissance IV + Punch I au lieu
            // de donner un second arc simple + un livre séparé à combiner soi-même à l'enclume.
            if (quickMode) {
                applyCupidonKitBowEnchants(player);
            } else {
                giveItem(player, new ItemStack(Material.BOW));
                giveItem(player, createPowerBook());
            }

            giveItem(player, new ItemStack(Material.ARROW, 64));

        } else if (lgPlayer.getRole() instanceof ChasseurRole) {
            giveItem(player, createPowerBow());
            giveItem(player, new ItemStack(Material.ARROW, 64));
        } else if (lgPlayer.getRole() instanceof BienfaiteurRole) {
            giveItem(player, createProtectionBook());
            giveItem(player, createProtectionBook());
        } else if (lgPlayer.getRole().getTeam() == RoleTeam.NEUTRAL) {

            // En mode rapide, l'épée du kit de départ est déjà enchantée à Tranchant IV pour les rôles
            // solitaires : donner ce livre en plus serait redondant, puisqu'il n'y a rien d'autre à enchanter.
            if (!quickMode) {
                giveItem(player, createSharpnessBook());
            }

        }

        if (lgPlayer.getRole() instanceof FeuFolletRole) {
            giveItem(player, createFeuFolletFeather());
        }

    }

    private static void giveItem(Player player, ItemStack item) {

        player.getInventory().addItem(item).values()
                .forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));

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
