package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class LobbySpawnManager implements Manager {

    private Location spawn;

    @Override
    public void enable() {
        load();
    }

    @Override
    public void disable() {
    }

    public void setSpawn(Location location) {

        this.spawn = location.clone();

        FileConfiguration config = LoupGarouPlugin.getInstance().getConfig();

        config.set("lobby.world", location.getWorld().getName());
        config.set("lobby.x", location.getX());
        config.set("lobby.y", location.getY());
        config.set("lobby.z", location.getZ());
        config.set("lobby.yaw", location.getYaw());
        config.set("lobby.pitch", location.getPitch());

        LoupGarouPlugin.getInstance().saveConfig();

    }

    public Location getSpawn() {

        if (spawn != null) {
            return spawn;
        }

        World fallback = Bukkit.getWorlds().get(0);

        return fallback.getSpawnLocation();

    }

    private void load() {

        FileConfiguration config = LoupGarouPlugin.getInstance().getConfig();

        String worldName = config.getString("lobby.world");

        if (worldName == null) {
            return;
        }

        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            return;
        }

        spawn = new Location(
                world,
                config.getDouble("lobby.x"),
                config.getDouble("lobby.y"),
                config.getDouble("lobby.z"),
                (float) config.getDouble("lobby.yaw"),
                (float) config.getDouble("lobby.pitch")
        );

    }

}
