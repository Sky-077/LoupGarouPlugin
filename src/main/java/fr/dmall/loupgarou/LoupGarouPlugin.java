package fr.dmall.loupgarou;

import fr.dmall.loupgarou.command.LGCommand;
import fr.dmall.loupgarou.game.CharmManager;
import fr.dmall.loupgarou.game.CorruptionManager;
import fr.dmall.loupgarou.game.CraintifManager;
import fr.dmall.loupgarou.game.CycleManager;
import fr.dmall.loupgarou.game.DeathManager;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.HostManager;
import fr.dmall.loupgarou.game.LobbySpawnManager;
import fr.dmall.loupgarou.game.LoveManager;
import fr.dmall.loupgarou.game.StealthVisionManager;
import fr.dmall.loupgarou.game.VoteManager;
import fr.dmall.loupgarou.game.WorldManager;
import fr.dmall.loupgarou.item.FluteItem;
import fr.dmall.loupgarou.listener.AgonyListener;
import fr.dmall.loupgarou.listener.AncienResistanceListener;
import fr.dmall.loupgarou.listener.AutoSmeltListener;
import fr.dmall.loupgarou.listener.ChasseurStrengthListener;
import fr.dmall.loupgarou.listener.ColorMenuListener;
import fr.dmall.loupgarou.listener.DiamondCounterListener;
import fr.dmall.loupgarou.listener.EnchantLimitListener;
import fr.dmall.loupgarou.listener.FeuFolletListener;
import fr.dmall.loupgarou.listener.FishingListener;
import fr.dmall.loupgarou.listener.InvisibilityListener;
import fr.dmall.loupgarou.listener.JoueurDeFluteListener;
import fr.dmall.loupgarou.listener.LavaBucketListener;
import fr.dmall.loupgarou.listener.LethalDamageListener;
import fr.dmall.loupgarou.listener.PlayerConnectionListener;
import fr.dmall.loupgarou.listener.PlayerDeathListener;
import fr.dmall.loupgarou.listener.PortalBlockListener;
import fr.dmall.loupgarou.listener.PvpListener;
import fr.dmall.loupgarou.listener.SalvateurProtectionListener;
import fr.dmall.loupgarou.listener.SettingsMenuListener;
import fr.dmall.loupgarou.listener.VilainPetitLoupListener;
import fr.dmall.loupgarou.listener.VoteListener;
import fr.dmall.loupgarou.manager.ManagerRegistry;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.RoleManager;
import fr.dmall.loupgarou.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
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
        managerRegistry.register(new CorruptionManager());
        managerRegistry.register(new LoveManager());
        managerRegistry.register(new LobbySpawnManager());
        managerRegistry.register(new HostManager());
        managerRegistry.register(new VoteManager());
        managerRegistry.register(new StealthVisionManager());
        managerRegistry.register(new CharmManager());
        managerRegistry.register(new CraintifManager());

        managerRegistry.enableAll();

        getCommand("lg").setExecutor(new LGCommand());

        getServer().getPluginManager().registerEvents(
                new PlayerConnectionListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new InvisibilityListener(),
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

        getServer().getPluginManager().registerEvents(
                new VoteListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new ChasseurStrengthListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new SalvateurProtectionListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new AncienResistanceListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new FeuFolletListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new JoueurDeFluteListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new VilainPetitLoupListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new ColorMenuListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new EnchantLimitListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new FishingListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new LavaBucketListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new SettingsMenuListener(),
                this
        );

        registerFluteRecipe();

        getLogger().info("Plugin LoupGarou activé !");
    }

    @Override
    public void onDisable() {

        managerRegistry.disableAll();

    }

    private void registerFluteRecipe() {

        NamespacedKey key = new NamespacedKey(this, "flute");
        ShapedRecipe recipe = new ShapedRecipe(key, FluteItem.create());

        recipe.shape("GGG", "GSG", "GGG");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);

    }

    public static LoupGarouPlugin getInstance() {
        return instance;
    }

    public ManagerRegistry getManagerRegistry() {
        return managerRegistry;
    }

}