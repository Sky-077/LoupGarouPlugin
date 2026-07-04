package fr.dmall.loupgarou;

import fr.dmall.loupgarou.command.LGCommand;
import fr.dmall.loupgarou.game.CycleManager;
import fr.dmall.loupgarou.game.DeathManager;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.LobbySpawnManager;
import fr.dmall.loupgarou.game.LoveManager;
import fr.dmall.loupgarou.game.WorldManager;
import fr.dmall.loupgarou.listener.AgonyListener;
import fr.dmall.loupgarou.listener.AutoSmeltListener;
import fr.dmall.loupgarou.listener.DiamondCounterListener;
import fr.dmall.loupgarou.listener.LethalDamageListener;
import fr.dmall.loupgarou.listener.PetiteFilleListener;
import fr.dmall.loupgarou.listener.PlayerConnectionListener;
import fr.dmall.loupgarou.listener.PlayerDeathListener;
import fr.dmall.loupgarou.listener.PortalBlockListener;
import fr.dmall.loupgarou.listener.PvpListener;
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
        managerRegistry.register(new WorldManager());
        managerRegistry.register(new PlayerManager());
        managerRegistry.register(new RoleManager());
        managerRegistry.register(new CycleManager());
        managerRegistry.register(new ScoreboardManager());
        managerRegistry.register(new DeathManager());
        managerRegistry.register(new LoveManager());
        managerRegistry.register(new LobbySpawnManager());

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
                new LethalDamageListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new PlayerDeathListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new AutoSmeltListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new DiamondCounterListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new PortalBlockListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new PvpListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new AgonyListener(),
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