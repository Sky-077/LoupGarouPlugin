package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.game.VoteInventoryHolder;
import fr.dmall.loupgarou.game.VoteManager;
import fr.dmall.loupgarou.game.WorldManager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VoteListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block == null || block.getType() != Material.JUKEBOX) {
            return;
        }

        WorldManager worldManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(WorldManager.class);

        if (!worldManager.isVoteJukebox(block.getLocation())) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();

        VoteManager voteManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(VoteManager.class);

        if (!voteManager.isActive()) {
            player.sendMessage("§cLe vote n'est pas ouvert actuellement.");
            return;
        }

        openVoteGui(player);

    }

    private void openVoteGui(Player voter) {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        List<LGPlayer> candidates = game.getPlayers().stream()
                .filter(LGPlayer::isAlive)
                .filter(lgPlayer -> !lgPlayer.getUuid().equals(voter.getUniqueId()))
                .collect(Collectors.toList());

        int size = ((candidates.size() + 1 + 8) / 9) * 9;
        size = Math.max(9, Math.min(54, size));

        VoteInventoryHolder holder = new VoteInventoryHolder();
        Inventory inventory = Bukkit.createInventory(holder, size, "§6Vote du village");
        holder.setInventory(inventory);

        int slot = 0;

        for (LGPlayer lgTarget : candidates) {

            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(lgTarget.getUuid());

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(offlineTarget);
            meta.setDisplayName("§e" + offlineTarget.getName());
            head.setItemMeta(meta);

            inventory.setItem(slot, head);
            holder.putTarget(slot, lgTarget.getUuid());

            slot++;

        }

        ItemStack passItem = new ItemStack(Material.PAPER);
        ItemMeta passMeta = passItem.getItemMeta();
        passMeta.setDisplayName("§7Passer");
        passItem.setItemMeta(passMeta);

        inventory.setItem(size - 1, passItem);
        holder.putTarget(size - 1, VoteInventoryHolder.PASS);

        voter.openInventory(inventory);

    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getInventory().getHolder() instanceof VoteInventoryHolder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player voter = (Player) event.getWhoClicked();
        VoteInventoryHolder holder = (VoteInventoryHolder) event.getInventory().getHolder();

        UUID target = holder.getTarget(event.getRawSlot());

        if (target == null) {
            return;
        }

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        if (playerManager.get(voter) == null) {
            return;
        }

        VoteManager voteManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(VoteManager.class);

        if (target.equals(VoteInventoryHolder.PASS)) {
            voteManager.castPass(voter);
            voter.sendMessage("§7Vous avez choisi de passer.");
        } else {
            Player targetPlayer = Bukkit.getPlayer(target);
            voteManager.castVote(voter, target);
            voter.sendMessage("§aVote enregistré" + (targetPlayer != null ? " pour " + targetPlayer.getName() : "") + " !");
        }

        voter.closeInventory();

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            return;
        }

        WorldManager worldManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(WorldManager.class);

        if (worldManager.isInsideVoteHouse(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }

    }

}
