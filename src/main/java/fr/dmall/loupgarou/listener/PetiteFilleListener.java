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

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.NIGHT) {
            return;
        }

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        Player player = (Player) entity;

        LGPlayer lgPlayer = playerManager.get(player);

        if (lgPlayer == null || !(lgPlayer.getRole() instanceof PetiteFilleRole)) {
            return;
        }

        PetiteFilleRole role = (PetiteFilleRole) lgPlayer.getRole();

        if (role.hasNoArmor(player)) {
            role.tryActivateInvisibility(player);
        } else {
            role.removeInvisibility(player);
        }

    }

}