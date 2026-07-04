package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.DeathManager;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.game.HonorManager;
import fr.dmall.loupgarou.game.LoveManager;
import fr.dmall.loupgarou.game.VictoryChecker;
import fr.dmall.loupgarou.game.WorldManager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.solo.ChasseurDePrimesRole;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

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
                    fulfillContractIfNeeded((ChasseurDePrimesRole) killerLgPlayer.getRole(), killer, player);
                }

            }

        }

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

    private void fulfillContractIfNeeded(ChasseurDePrimesRole role, Player hunter, Player target) {

        if (!role.isContract(target.getUniqueId()) || role.isFulfilled(target.getUniqueId())) {
            return;
        }

        role.fulfillContract(target.getUniqueId());

        hunter.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
        hunter.getInventory().addItem(new ItemStack(Material.DIAMOND, 8));

        hunter.sendMessage("§6Contrat rempli sur " + target.getName() + " ! Vous recevez du matériel.");

    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {

        Player player = event.getPlayer();

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgPlayer = playerManager.get(player);

        if (lgPlayer == null || lgPlayer.isAlive()) {
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