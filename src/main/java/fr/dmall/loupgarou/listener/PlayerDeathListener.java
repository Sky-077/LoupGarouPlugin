package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.AngeManager;
import fr.dmall.loupgarou.game.DeathManager;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.game.HonorManager;
import fr.dmall.loupgarou.game.LobbySpawnManager;
import fr.dmall.loupgarou.game.LoveManager;
import fr.dmall.loupgarou.game.VictoryChecker;
import fr.dmall.loupgarou.game.WorldManager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.loup.WolfRole;
import fr.dmall.loupgarou.role.solo.ChasseurDePrimesRole;
import fr.dmall.loupgarou.role.solo.LoupBlancRole;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class PlayerDeathListener implements Listener {

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            return;
        }

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgPlayer = playerManager.get(player);

        if (lgPlayer == null || !game.getPlayers().contains(lgPlayer) || !lgPlayer.isAlive()) {
            return;
        }

        lgPlayer.setAlive(false);

        Role role = lgPlayer.getRole();

        if (role != null) {
            role.onDeath(player);
        }

        DeathManager deathManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(DeathManager.class);

        UUID killerUuid = deathManager.consumeKiller(player);

        Player killer = (killerUuid != null) ? Bukkit.getPlayer(killerUuid) : player.getKiller();

        if (killer != null && !killer.equals(player)) {

            LGPlayer killerLgPlayer = playerManager.get(killer);

            if (killerLgPlayer != null) {

                killerLgPlayer.addKill();

                if (killerLgPlayer.getEffectiveTeam() != null
                        && killerLgPlayer.getEffectiveTeam() == lgPlayer.getEffectiveTeam()) {
                    HonorManager.loseHonor(killerLgPlayer, killer);
                }

                if (killerLgPlayer.getRole() instanceof ChasseurDePrimesRole) {
                    fulfillContractIfNeeded((ChasseurDePrimesRole) killerLgPlayer.getRole(), killer, player, game.getEpisode());
                }

                if (killerLgPlayer.getRole() instanceof WolfRole || killerLgPlayer.getRole() instanceof LoupBlancRole) {
                    killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60, 0, false, true));
                    killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 60, 0, false, true));
                }

            }

        }

        cancelContractsOnOthers(game, lgPlayer, killer);
        AngeManager.onDeath(game, lgPlayer, killer);

        event.setDeathMessage(null);

        String roleName = (role != null) ? role.getName() : "Inconnu";

        if (killer != null) {
            Bukkit.broadcastMessage("§c☠ " + player.getName() + " est mort, tué par " + killer.getName() + " ! §7(Rôle : " + roleName + ")");
        } else {
            Bukkit.broadcastMessage("§c☠ " + player.getName() + " est mort ! §7(Rôle : " + roleName + ")");
        }

        LoveManager loveManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(LoveManager.class);

        loveManager.handleDeath(player);

        VictoryChecker.check();

    }

    private void fulfillContractIfNeeded(ChasseurDePrimesRole role, Player hunter, Player target, int episode) {

        if (!role.isContract(target.getUniqueId()) || role.isFulfilled(target.getUniqueId())) {
            return;
        }

        int contractNumber = role.getContractsIssued();

        role.fulfillContract(target.getUniqueId(), episode);

        if (contractNumber == 1) {
            hunter.getInventory().addItem(createPowerBow());
            hunter.getInventory().addItem(new ItemStack(Material.ARROW, 64));
        } else {
            hunter.getInventory().addItem(createFeatherFallingBoots());
        }

        hunter.sendMessage("§6Contrat rempli sur " + target.getName() + " ! Vous recevez du matériel.");

    }

    private void cancelContractsOnOthers(Game game, LGPlayer deceased, Player killer) {

        for (LGPlayer lgHunter : game.getPlayers()) {

            if (!(lgHunter.getRole() instanceof ChasseurDePrimesRole)) {
                continue;
            }

            if (killer != null && killer.getUniqueId().equals(lgHunter.getUuid())) {
                continue;
            }

            ChasseurDePrimesRole bounty = (ChasseurDePrimesRole) lgHunter.getRole();

            if (!bounty.isContract(deceased.getUuid())) {
                continue;
            }

            bounty.cancelContract(deceased.getUuid(), game.getEpisode());

            Player hunterPlayer = Bukkit.getPlayer(lgHunter.getUuid());

            if (hunterPlayer != null) {
                hunterPlayer.sendMessage("§7Votre cible a été éliminée par quelqu'un d'autre. Contrat annulé, en attente du suivant.");
            }

        }

    }

    private ItemStack createPowerBow() {

        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.addEnchant(Enchantment.POWER, 4, true);
        bow.setItemMeta(meta);

        return bow;

    }

    private ItemStack createFeatherFallingBoots() {

        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        ItemMeta meta = boots.getItemMeta();
        meta.addEnchant(Enchantment.FEATHER_FALLING, 3, true);
        boots.setItemMeta(meta);

        return boots;

    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {

        Player player = event.getPlayer();

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgPlayer = playerManager.get(player);

        if (lgPlayer == null) {
            return;
        }

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() == GameState.WAITING) {

            LobbySpawnManager lobbySpawnManager = LoupGarouPlugin.getInstance()
                    .getManagerRegistry()
                    .getManager(LobbySpawnManager.class);

            event.setRespawnLocation(lobbySpawnManager.getSpawn());

            Bukkit.getScheduler().runTask(
                    LoupGarouPlugin.getInstance(),
                    () -> player.setGameMode(GameMode.SURVIVAL)
            );

            return;

        }

        if (lgPlayer.isAlive()) {
            return;
        }

        WorldManager worldManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(WorldManager.class);

        World gameWorld = worldManager.getGameWorld();

        if (gameWorld != null) {
            event.setRespawnLocation(gameWorld.getSpawnLocation());
        }

        Bukkit.getScheduler().runTask(
                LoupGarouPlugin.getInstance(),
                () -> player.setGameMode(GameMode.SPECTATOR)
        );

    }

}