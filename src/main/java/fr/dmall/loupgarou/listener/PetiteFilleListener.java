package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.village.PetiteFilleRole;
import io.papermc.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PetiteFilleListener implements Listener {

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event) {

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

        Player player = event.getPlayer();

        LGPlayer lgPlayer = playerManager.get(player);

        if (lgPlayer == null || !(lgPlayer.getRole() instanceof PetiteFilleRole)) {
            return;
        }

        PetiteFilleRole role = (PetiteFilleRole) lgPlayer.getRole();

        if (role.hasNoArmor(player)) {
            role.applyInvisibility(player);
        } else {
            role.removeInvisibility(player);
        }

    }

}