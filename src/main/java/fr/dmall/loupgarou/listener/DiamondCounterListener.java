package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class DiamondCounterListener implements Listener {

    private static final int DIAMOND_LIMIT = 17;

    @EventHandler
    public void onBlockDrop(BlockDropItemEvent event) {

        Player player = event.getPlayer();

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

        if (lgPlayer == null || !game.getPlayers().contains(lgPlayer)) {
            return;
        }

        for (Item item : event.getItems()) {

            ItemStack stack = item.getItemStack();

            if (stack.getType() != Material.DIAMOND) {
                continue;
            }

            int remaining = DIAMOND_LIMIT - lgPlayer.getDiamonds();

            if (remaining <= 0) {
                item.remove();
                continue;
            }

            int amount = Math.min(stack.getAmount(), remaining);

            if (amount != stack.getAmount()) {
                stack.setAmount(amount);
                item.setItemStack(stack);
            }

            lgPlayer.addDiamonds(amount);

        }

    }

}
