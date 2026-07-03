package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.village.PetiteFilleRole;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

public class PetiteFilleListener implements Listener {

    @EventHandler
    public void onEquipmentChange(EntityEquipmentChangedEvent event) {

        LoupGarouPlugin.getInstance().getLogger().info("[DEBUG] EntityEquipmentChangedEvent déclenché pour " + event.getEntity().getName());

        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            LoupGarouPlugin.getInstance().getLogger().info("[DEBUG] Stop : ce n'est pas un joueur.");
            return;
        }

        boolean armorSlotChanged = event.getEquipmentChanges().containsKey(EquipmentSlot.HEAD)
                || event.getEquipmentChanges().containsKey(EquipmentSlot.CHEST)
                || event.getEquipmentChanges().containsKey(EquipmentSlot.LEGS)
                || event.getEquipmentChanges().containsKey(EquipmentSlot.FEET);

        LoupGarouPlugin.getInstance().getLogger().info("[DEBUG] Slots modifiés : " + event.getEquipmentChanges().keySet());

        if (!armorSlotChanged) {
            LoupGarouPlugin.getInstance().getLogger().info("[DEBUG] Stop : aucun slot d'armure modifié.");
            return;
        }

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        LoupGarouPlugin.getInstance().getLogger().info("[DEBUG] État de la partie : " + game.getState());

        if (game.getState() != GameState.NIGHT) {
            LoupGarouPlugin.getInstance().getLogger().info("[DEBUG] Stop : la partie n'est pas en état NIGHT.");
            return;
        }

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        Player player = (Player) entity;

        LGPlayer lgPlayer = playerManager.get(player);

        if (lgPlayer == null) {
            LoupGarouPlugin.getInstance().getLogger().info("[DEBUG] Stop : LGPlayer introuvable.");
            return;
        }

        LoupGarouPlugin.getInstance().getLogger().info("[DEBUG] Rôle du joueur : " + (lgPlayer.getRole() == null ? "null" : lgPlayer.getRole().getName()));

        if (!(lgPlayer.getRole() instanceof PetiteFilleRole)) {
            LoupGarouPlugin.getInstance().getLogger().info("[DEBUG] Stop : le rôle n'est pas Petite Fille.");
            return;
        }

        PetiteFilleRole role = (PetiteFilleRole) lgPlayer.getRole();

        boolean noArmor = role.hasNoArmor(player);

        LoupGarouPlugin.getInstance().getLogger().info("[DEBUG] hasNoArmor = " + noArmor);

        if (noArmor) {
            role.applyInvisibility(player);
        } else {
            role.removeInvisibility(player);
        }

    }

}