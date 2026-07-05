package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.game.ColorMenuHolder;
import fr.dmall.loupgarou.scoreboard.NameColorManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ColorMenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getInventory().getHolder() instanceof ColorMenuHolder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player observer = (Player) event.getWhoClicked();
        ColorMenuHolder holder = (ColorMenuHolder) event.getInventory().getHolder();

        ItemStack clicked = event.getCurrentItem();

        if (clicked == null) {
            return;
        }

        ChatColor color = NameColorManager.WOOL_COLORS.get(clicked.getType());

        if (color == null) {
            return;
        }

        int applied = 0;

        for (UUID targetUuid : holder.getTargets()) {

            Player target = Bukkit.getPlayer(targetUuid);

            if (target == null) {
                continue;
            }

            NameColorManager.setColor(observer, target, color);
            applied++;

        }

        observer.sendMessage("§aCouleur appliquée à " + applied + " joueur(s) (visible uniquement par vous).");
        observer.closeInventory();

    }

}
