package fr.dmall.loupgarou.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class LavaBucketListener implements Listener {

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {

        if (event.getBucket() != Material.LAVA_BUCKET) {
            return;
        }

        event.setCancelled(true);
        event.getPlayer().sendMessage("§cLes seaux de lave sont désactivés.");

    }

}
