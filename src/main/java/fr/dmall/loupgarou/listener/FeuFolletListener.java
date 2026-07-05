package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.solo.FeuFolletRole;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class FeuFolletListener implements Listener {

    private static final double TELEPORT_DISTANCE = 50.0;
    private static final int FIRE_TICKS = 100; // 5 secondes

    @EventHandler
    public void onEquipmentChange(EntityEquipmentChangedEvent event) {

        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        boolean armorSlotChanged = event.getEquipmentChanges().containsKey(EquipmentSlot.HEAD)
                || event.getEquipmentChanges().containsKey(EquipmentSlot.CHEST)
                || event.getEquipmentChanges().containsKey(EquipmentSlot.LEGS)
                || event.getEquipmentChanges().containsKey(EquipmentSlot.FEET);

        if (!armorSlotChanged) {
            return;
        }

        Player player = (Player) entity;

        LGPlayer lgPlayer = getLgPlayer(player);

        if (lgPlayer == null || !(lgPlayer.getRole() instanceof FeuFolletRole)) {
            return;
        }

        if (!isGameActiveAndRevealed()) {
            return;
        }

        FeuFolletRole role = (FeuFolletRole) lgPlayer.getRole();

        if (role.hasNoArmor(player)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false));
        } else {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent event) {

        Player attacker = resolveMeleeAttacker(event);

        if (attacker == null) {
            return;
        }

        LGPlayer lgAttacker = getLgPlayer(attacker);

        if (lgAttacker == null || !(lgAttacker.getRole() instanceof FeuFolletRole)) {
            return;
        }

        if (!((FeuFolletRole) lgAttacker.getRole()).isFolieActive()) {
            return;
        }

        if (event.getEntity() instanceof Player) {
            ((Player) event.getEntity()).setFireTicks(FIRE_TICKS);
        }

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.FEATHER || !item.hasItemMeta()
                || item.getItemMeta().getDisplayName() == null
                || !item.getItemMeta().getDisplayName().equals("§bPlume du Feu Follet")) {
            return;
        }

        LGPlayer lgPlayer = getLgPlayer(player);

        if (lgPlayer == null || !lgPlayer.isAlive() || !(lgPlayer.getRole() instanceof FeuFolletRole)) {
            return;
        }

        if (!isGameActiveAndRevealed()) {
            return;
        }

        event.setCancelled(true);

        FeuFolletRole role = (FeuFolletRole) lgPlayer.getRole();

        if (!role.isFeatherAvailable()) {
            player.sendMessage("§cVotre Plume n'est pas encore rechargée (" + role.getFeatherRemainingSeconds() + "s restantes).");
            return;
        }

        role.consumeFeather();
        teleportForward(player);

    }

    private void teleportForward(Player player) {

        Vector direction = player.getLocation().getDirection().normalize();

        RayTraceResult result = player.getWorld().rayTraceBlocks(
                player.getEyeLocation(),
                direction,
                TELEPORT_DISTANCE,
                FluidCollisionMode.NEVER,
                true
        );

        double distance = (result != null) ? Math.max(0.0, result.getHitPosition().distance(player.getEyeLocation().toVector()) - 1.0) : TELEPORT_DISTANCE;

        player.teleport(player.getLocation().add(direction.multiply(distance)));
        player.sendMessage("§5Vous vous êtes téléporté grâce à votre Plume !");

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

    private LGPlayer getLgPlayer(Player player) {

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        return playerManager.get(player);

    }

}
