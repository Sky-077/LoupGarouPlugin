package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.CharmManager;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.item.FluteItem;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.solo.JoueurDeFluteRole;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

public class JoueurDeFluteListener implements Listener {

    private static final double GIVE_RANGE = 5.0;

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent event) {

        Player attacker = resolveMeleeAttacker(event);

        if (attacker == null || !(event.getEntity() instanceof Player)) {
            return;
        }

        LGPlayer lgAttacker = getLgPlayer(attacker);

        if (lgAttacker == null || !(lgAttacker.getRole() instanceof JoueurDeFluteRole)) {
            return;
        }

        if (!isGameActiveAndRevealed()) {
            return;
        }

        Player victim = (Player) event.getEntity();

        getCharmManager().addMeleeBonus(victim.getUniqueId());

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!FluteItem.isFlute(item)) {
            return;
        }

        LGPlayer lgPlayer = getLgPlayer(player);

        if (lgPlayer == null || !lgPlayer.isAlive() || !(lgPlayer.getRole() instanceof JoueurDeFluteRole)) {
            return;
        }

        if (!isGameActiveAndRevealed()) {
            return;
        }

        event.setCancelled(true);

        Player target = findTargetInSight(player);

        if (target == null) {
            player.sendMessage("§cAucun joueur visé à moins de " + (int) GIVE_RANGE + " blocs.");
            return;
        }

        CharmManager charmManager = getCharmManager();

        if (charmManager.hasBeenGivenFlute(target.getUniqueId())) {
            player.sendMessage("§cCe joueur a déjà reçu une Flûte.");
            return;
        }

        item.setAmount(item.getAmount() - 1);
        charmManager.giveFlute(target.getUniqueId());

        player.sendMessage("§dVous avez discrètement glissé une Flûte à " + target.getName() + ".");

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {

        if (!FluteItem.isFlute(event.getItemDrop().getItemStack())) {
            return;
        }

        LGPlayer lgPlayer = getLgPlayer(event.getPlayer());

        if (lgPlayer == null) {
            return;
        }

        if (getCharmManager().isFluteCarrier(lgPlayer.getUuid())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cImpossible de vous débarrasser de cette Flûte.");
        }

    }

    private Player findTargetInSight(Player player) {

        RayTraceResult result = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                GIVE_RANGE,
                0.3,
                entity -> entity instanceof Player && !entity.equals(player)
        );

        if (result == null || result.getHitEntity() == null) {
            return null;
        }

        return (Player) result.getHitEntity();

    }

    private Player resolveMeleeAttacker(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Projectile) {
            return null;
        }

        if (event.getDamager() instanceof Player) {
            return (Player) event.getDamager();
        }

        return null;

    }

    private boolean isGameActiveAndRevealed() {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        return (game.getState() == GameState.DAY || game.getState() == GameState.NIGHT) && game.isRevealed();

    }

    private CharmManager getCharmManager() {

        return LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(CharmManager.class);

    }

    private LGPlayer getLgPlayer(Player player) {

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        return playerManager.get(player);

    }

}
