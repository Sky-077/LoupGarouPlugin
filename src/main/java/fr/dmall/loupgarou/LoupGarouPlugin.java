package fr.dmall.loupgarou;

import fr.dmall.loupgarou.command.LGCommand;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.manager.ManagerRegistry;
import org.bukkit.plugin.java.JavaPlugin;

public final class LoupGarouPlugin extends JavaPlugin {

    private static LoupGarouPlugin instance;

    private ManagerRegistry managerRegistry;

    @Override
    public void onEnable() {
        instance = this;

        managerRegistry = new ManagerRegistry();

        managerRegistry.register(new GameManager());

        managerRegistry.enableAll();

        getCommand("lg").setExecutor(new LGCommand());

        getLogger().info("Plugin activé !");
    }

    @Override
    public void onDisable() {
        managerRegistry.disableAll();
    }

    public static LoupGarouPlugin getInstance() {
        return instance;
    }

    public ManagerRegistry getManagerRegistry() {
        return managerRegistry;
    }
}