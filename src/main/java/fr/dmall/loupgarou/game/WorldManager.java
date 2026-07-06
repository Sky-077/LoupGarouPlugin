package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class WorldManager implements Manager {

    private static final String WORLD_NAME = "lg_uhc";
    private static final double DEFAULT_BORDER_SIZE = 1000.0;
    public static final double MIN_BORDER_SIZE = 100.0;

    private static final int BIOME_SAMPLE_Y = 64;
    private static final int SEARCH_RINGS = 4; // plafonné (au lieu de 8) pour rester proche de la zone déjà pré-générée par Minecraft à la création du monde, et éviter un blocage du serveur en génération synchrone de chunks lointains jamais touchés
    private static final int POINTS_PER_RING = 6;
    private static final int RING_STEP = 150; // réduit (au lieu de 400) pour la même raison que SEARCH_RINGS

    private static final int VOTE_HOUSE_RADIUS = 150;
    private static final int VOTE_HOUSE_HEIGHT = 4;

    private static final Set<Biome> FORBIDDEN_BIOMES = Set.of(
            Biome.OCEAN, Biome.DEEP_OCEAN, Biome.WARM_OCEAN, Biome.LUKEWARM_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN, Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN,
            Biome.FROZEN_OCEAN, Biome.DEEP_FROZEN_OCEAN,
            Biome.MUSHROOM_FIELDS,
            Biome.ICE_SPIKES, Biome.SNOWY_PLAINS, Biome.SNOWY_TAIGA, Biome.SNOWY_SLOPES,
            Biome.FROZEN_PEAKS, Biome.JAGGED_PEAKS, Biome.GROVE, Biome.FROZEN_RIVER, Biome.SNOWY_BEACH
    );

    private double borderSize = DEFAULT_BORDER_SIZE;
    private World gameWorld;
    private int centerX;
    private int centerZ;
    private CompletableFuture<Void> pregenerationFuture = CompletableFuture.completedFuture(null);

    private final List<Location> voteJukeboxes = new ArrayList<>();
    private final List<int[]> voteHouseBounds = new ArrayList<>();

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    public double getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(double borderSize) {
        this.borderSize = borderSize;
    }

    public World getGameWorld() {
        return gameWorld;
    }

    public void clearGameWorld() {
        gameWorld = null;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterZ() {
        return centerZ;
    }

    public CompletableFuture<Void> getPregenerationFuture() {
        return pregenerationFuture;
    }

    public World prepareGameWorld() {

        LobbySpawnManager lobbySpawnManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(LobbySpawnManager.class);

        if (gameWorld != null) {

            for (Player player : gameWorld.getPlayers()) {
                player.teleport(lobbySpawnManager.getSpawn());
            }

            if (!Bukkit.unloadWorld(gameWorld, false)) {
                Bukkit.getLogger().warning("[LoupGarouPlugin] Le monde " + gameWorld.getName()
                        + " n'a pas pu être déchargé (chunks encore en cours de génération ?) — il restera en mémoire.");
            }

        }

        // Nom unique à chaque partie : évite de dépendre de la suppression de l'ancien dossier
        // (sur Windows, les fichiers de région restent parfois verrouillés juste après unloadWorld).
        String newWorldName = WORLD_NAME + "_" + System.currentTimeMillis();

        cleanupOldWorldFolders(newWorldName);

        WorldCreator creator = new WorldCreator(newWorldName);
        creator.seed(new Random().nextLong());
        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);

        World world = creator.createWorld();

        int[] center = findGoodRegionCenter(world);
        centerX = center[0];
        centerZ = center[1];

        WorldBorder border = world.getWorldBorder();
        border.setCenter(centerX, centerZ);
        border.setSize(borderSize);

        world.setFullTime(0L);
        world.setSpawnFlags(false, true);

        gameWorld = world;

        // buildVoteHouses() n'est plus appelé ici : elle est déclenchée par GameStarter après un délai,
        // pour laisser la pré-génération ci-dessous avancer en arrière-plan avant de forcer du chargement
        // de chunks bloquant (sinon, sur un monde tout juste créé, ça peut geler le serveur au point de
        // faire timeout et déconnecter les joueurs).
        pregenerateScatterArea(world);

        return world;

    }

    private void pregenerateScatterArea(World world) {

        int centerChunkX = centerX >> 4;
        int centerChunkZ = centerZ >> 4;

        // Même rayon que findScatterLocation (90% de la bordure) : c'est là que les joueurs atterrissent réellement.
        int radius = (int) ((borderSize / 2.0) * 0.9) / 16;

        List<CompletableFuture<Chunk>> futures = new ArrayList<>();

        for (int dx = -radius; dx <= radius; dx++) {

            for (int dz = -radius; dz <= radius; dz++) {
                futures.add(world.getChunkAtAsync(centerChunkX + dx, centerChunkZ + dz, true));
            }

        }

        long startedAt = System.currentTimeMillis();
        int chunkCount = futures.size();

        pregenerationFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() ->
                Bukkit.getLogger().info("[LoupGarouPlugin] Pré-génération de " + chunkCount + " chunks terminée en "
                        + (System.currentTimeMillis() - startedAt) + "ms.")
        );

    }

    public boolean isVoteJukebox(Location location) {

        return voteJukeboxes.stream().anyMatch(loc ->
                loc.getWorld().equals(location.getWorld())
                        && loc.getBlockX() == location.getBlockX()
                        && loc.getBlockY() == location.getBlockY()
                        && loc.getBlockZ() == location.getBlockZ());

    }

    public boolean isInsideVoteHouse(Location location) {

        for (int[] bounds : voteHouseBounds) {

            if (location.getBlockX() >= bounds[0] && location.getBlockX() <= bounds[1]
                    && location.getBlockY() >= bounds[2] && location.getBlockY() <= bounds[3]
                    && location.getBlockZ() >= bounds[4] && location.getBlockZ() <= bounds[5]) {
                return true;
            }

        }

        return false;

    }

    public void buildVoteHouses(World world) {

        voteJukeboxes.clear();
        voteHouseBounds.clear();

        int[][] offsets = {
                { VOTE_HOUSE_RADIUS, 0 },
                { -VOTE_HOUSE_RADIUS, 0 },
                { 0, VOTE_HOUSE_RADIUS },
                { 0, -VOTE_HOUSE_RADIUS },
        };

        for (int[] offset : offsets) {
            buildVoteHouse(world, centerX + offset[0], centerZ + offset[1]);
        }

    }

    private void buildVoteHouse(World world, int houseX, int houseZ) {

        int baseY = world.getHighestBlockYAt(houseX, houseZ) + 1;

        for (int dx = -2; dx <= 2; dx++) {

            for (int dz = -2; dz <= 2; dz++) {

                int x = houseX + dx;
                int z = houseZ + dz;

                world.getBlockAt(x, baseY - 1, z).setType(Material.OAK_PLANKS);
                world.getBlockAt(x, baseY + VOTE_HOUSE_HEIGHT, z).setType(Material.OAK_PLANKS);

                boolean edge = (dx == -2 || dx == 2 || dz == -2 || dz == 2);

                for (int dy = 0; dy < VOTE_HOUSE_HEIGHT; dy++) {
                    world.getBlockAt(x, baseY + dy, z).setType(edge ? Material.OAK_LOG : Material.AIR);
                }

            }

        }

        world.getBlockAt(houseX, baseY, houseZ + 2).setType(Material.AIR);
        world.getBlockAt(houseX, baseY + 1, houseZ + 2).setType(Material.AIR);

        world.getBlockAt(houseX - 1, baseY, houseZ).setType(Material.ANVIL);
        world.getBlockAt(houseX + 1, baseY, houseZ).setType(Material.JUKEBOX);

        voteJukeboxes.add(new Location(world, houseX + 1, baseY, houseZ));

        voteHouseBounds.add(new int[] {
                houseX - 2, houseX + 2,
                baseY - 1, baseY + VOTE_HOUSE_HEIGHT,
                houseZ - 2, houseZ + 2
        });

    }

    private static final int SCATTER_ATTEMPTS = 10;

    public Location findScatterLocation(World world) {

        double radius = (borderSize / 2.0) * 0.9;

        Location fallback = null;

        for (int attempt = 0; attempt < SCATTER_ATTEMPTS; attempt++) {

            int blockX = centerX + (int) ((Math.random() * 2 - 1) * radius);
            int blockZ = centerZ + (int) ((Math.random() * 2 - 1) * radius);

            world.getChunkAt(blockX >> 4, blockZ >> 4);

            int blockY = world.getHighestBlockYAt(blockX, blockZ, HeightMap.MOTION_BLOCKING_NO_LEAVES);
            Material ground = world.getBlockAt(blockX, blockY, blockZ).getType();

            Location location = new Location(world, blockX + 0.5, blockY + 1, blockZ + 0.5);

            if (ground != Material.WATER && ground != Material.LAVA) {
                return location;
            }

            if (fallback == null) {
                fallback = location;
            }

        }

        return fallback;

    }

    private int[] findGoodRegionCenter(World world) {

        if (isRegionClear(world, 0, 0)) {
            return new int[] { 0, 0 };
        }

        for (int ring = 1; ring <= SEARCH_RINGS; ring++) {

            int distance = ring * RING_STEP;

            for (int i = 0; i < POINTS_PER_RING; i++) {

                double angle = (2 * Math.PI / POINTS_PER_RING) * i;
                int x = (int) Math.round(Math.cos(angle) * distance);
                int z = (int) Math.round(Math.sin(angle) * distance);

                if (isRegionClear(world, x, z)) {
                    return new int[] { x, z };
                }

            }

        }

        return new int[] { 0, 0 };

    }

    private boolean isRegionClear(World world, int centerX, int centerZ) {

        int radius = (int) (borderSize / 2.0);

        int[][] offsets = {
                { 0, 0 },
                { radius, 0 }, { -radius, 0 },
                { 0, radius }, { 0, -radius },
        };

        for (int[] offset : offsets) {

            Biome biome = world.getBiome(centerX + offset[0], BIOME_SAMPLE_Y, centerZ + offset[1]);

            if (FORBIDDEN_BIOMES.contains(biome)) {
                return false;
            }

        }

        return true;

    }

    private void cleanupOldWorldFolders(String excludeName) {

        File container = Bukkit.getWorldContainer();
        File[] candidates = container.listFiles((dir, name) -> name.startsWith(WORLD_NAME + "_") && !name.equals(excludeName));

        if (candidates == null) {
            return;
        }

        for (File folder : candidates) {
            deleteWorldFolder(folder);
        }

    }

    private void deleteWorldFolder(File folder) {

        if (!folder.exists()) {
            return;
        }

        try (Stream<Path> walk = Files.walk(folder.toPath())) {

            walk.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ignored) {
                        }
                    });

        } catch (IOException ignored) {
        }

    }

}
