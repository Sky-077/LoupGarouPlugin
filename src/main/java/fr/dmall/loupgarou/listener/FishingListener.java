package fr.dmall.loupgarou.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishingListener implements Listener {

    @EventHandler
    public void onFish(PlayerFishEvent event) {

        if (event.getState() != PlayerFishEvent.State.FISHING) {
            return;
        }

        event.setCancelled(true);
        event.getPlayer().sendMessage("§cLa pêche est désactivée.");

    }

}
