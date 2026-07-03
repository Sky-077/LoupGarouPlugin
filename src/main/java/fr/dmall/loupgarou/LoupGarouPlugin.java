package fr.dmall.loupgarou;

import fr.dmall.loupgarou.command.LGCommand;
import fr.dmall.loupgarou.game.CycleManager;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.listener.PetiteFilleListener;
import fr.dmall.loupgarou.listener.PlayerConnectionListener;
import fr.dmall.loupgarou.listener.PlayerDeathListener;
import fr.dmall.loupgarou.manager.ManagerRegistry;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.RoleManager;
import fr.dmall.loupgarou.scoreboard.ScoreboardManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class LoupGarouPlugin extends JavaPlugin {

    private static LoupGarouPlugin instance;

    private ManagerRegistry managerRegistry;

    @Override
    public void onEnable() {

        instance = this;

        managerRegistry = new ManagerRegistry();

        managerRegistry.register(new GameManager());
        managerRegistry.register(new PlayerManager());
        managerRegistry.register(new RoleManager());
        managerRegistry.register(new CycleManager());
        managerRegistry.register(new ScoreboardManager());

        managerRegistry.enableAll();

        getCommand("lg").setExecutor(new LGCommand());

        getServer().getPluginManager().registerEvents(
                new PlayerConnectionListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new PetiteFilleListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new PlayerDeathListener(),
                this
        );

        getLogger().info("Plugin LoupGarou activé !");
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