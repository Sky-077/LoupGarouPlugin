package fr.dmall.loupgarou;

import org.bukkit.plugin.java.JavaPlugin;

public final class LoupGarouPlugin extends JavaPlugin {

    private static LoupGarouPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("Le plugin LoupGarou est activé !");
    }

    @Override
    public void onDisable() {

    }

    public static LoupGarouPlugin getInstance() {
        return instance;
    }
}