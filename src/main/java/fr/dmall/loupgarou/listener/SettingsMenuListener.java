package fr.dmall.loupgarou.listener;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.GameStarter;
import fr.dmall.loupgarou.game.HostManager;
import fr.dmall.loupgarou.game.SettingsMenuBuilder;
import fr.dmall.loupgarou.game.SettingsMenuHolder;
import fr.dmall.loupgarou.game.WorldManager;
import fr.dmall.loupgarou.role.RoleFactory;
import fr.dmall.loupgarou.role.RoleManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class SettingsMenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getInventory().getHolder() instanceof SettingsMenuHolder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        SettingsMenuHolder holder = (SettingsMenuHolder) event.getInventory().getHolder();

        HostManager hostManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(HostManager.class);

        if (!hostManager.isActiveHost(player)) {
            player.closeInventory();
            player.sendMessage("§cSeul l'hôte de la partie peut modifier ces paramètres.");
            return;
        }

        int slot = event.getRawSlot();

        switch (holder.getPage()) {

            case MAIN:
                handleMainClick(player, slot);
                break;

            case BORDURE:
                handleBordureClick(player, event, slot);
                break;

            case MIN_PLAYERS:
                handleMinPlayersClick(player, event, slot);
                break;

            case DELAIS:
                handleDelaisClick(player, event, slot);
                break;

            case ROLES:
                handleRolesClick(player, event, holder, slot);
                break;

        }

    }

    private void handleMainClick(Player player, int slot) {

        switch (slot) {

            case 10:
                SettingsMenuBuilder.openBordure(player);
                break;

            case 12:
                SettingsMenuBuilder.openMinPlayers(player);
                break;

            case 14:
                SettingsMenuBuilder.openDelais(player);
                break;

            case 16:
                SettingsMenuBuilder.openRoles(player, 0);
                break;

            case 22:
                GameStarter.setQuickMode(!GameStarter.isQuickMode());
                SettingsMenuBuilder.openMain(player);
                break;

            default:
                break;

        }

    }

    private void handleBordureClick(Player player, InventoryClickEvent event, int slot) {

        if (slot == 22) {
            SettingsMenuBuilder.openMain(player);
            return;
        }

        if (slot != 13) {
            return;
        }

        int delta = resolveDelta(event, 50, 500);

        if (delta == 0) {
            return;
        }

        WorldManager worldManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(WorldManager.class);

        double newSize = Math.max(WorldManager.MIN_BORDER_SIZE, worldManager.getBorderSize() + delta);
        worldManager.setBorderSize(newSize);

        SettingsMenuBuilder.openBordure(player);

    }

    private void handleMinPlayersClick(Player player, InventoryClickEvent event, int slot) {

        if (slot == 22) {
            SettingsMenuBuilder.openMain(player);
            return;
        }

        if (slot != 13) {
            return;
        }

        int delta = resolveDelta(event, 1, 5);

        if (delta == 0) {
            return;
        }

        GameStarter.setMinPlayers(GameStarter.getMinPlayers() + delta);

        SettingsMenuBuilder.openMinPlayers(player);

    }

    private void handleDelaisClick(Player player, InventoryClickEvent event, int slot) {

        if (slot == 22) {
            SettingsMenuBuilder.openMain(player);
            return;
        }

        int delta = resolveDelta(event, 1, 10);

        if (delta == 0) {
            return;
        }

        switch (slot) {

            case 10:
                GameStarter.setInvincibilityMinutes(GameStarter.getInvincibilityMinutes() + delta);
                break;

            case 12:
                GameStarter.setRoleRevealMinutes(GameStarter.getRoleRevealMinutes() + delta);
                break;

            case 14:
                GameStarter.setPvpDelayMinutes(GameStarter.getPvpDelayMinutes() + delta);
                break;

            case 16:
                GameStarter.setVoteStartMinutes(GameStarter.getVoteStartMinutes() + delta);
                break;

            default:
                return;

        }

        SettingsMenuBuilder.openDelais(player);

    }

    private void handleRolesClick(Player player, InventoryClickEvent event, SettingsMenuHolder holder, int slot) {

        if (slot == 49) {
            SettingsMenuBuilder.openMain(player);
            return;
        }

        if (slot == 45) {
            SettingsMenuBuilder.openRoles(player, holder.getRolePageIndex() - 1);
            return;
        }

        if (slot == 53) {
            SettingsMenuBuilder.openRoles(player, holder.getRolePageIndex() + 1);
            return;
        }

        if (slot < 0 || slot >= SettingsMenuBuilder.ROLES_PER_PAGE) {
            return;
        }

        List<String> names = new ArrayList<>(RoleFactory.getRegisteredNames());
        int index = holder.getRolePageIndex() * SettingsMenuBuilder.ROLES_PER_PAGE + slot;

        if (index >= names.size()) {
            return;
        }

        int delta = resolveDelta(event, 1, 5);

        if (delta == 0) {
            return;
        }

        RoleManager roleManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(RoleManager.class);

        roleManager.adjustGameRole(names.get(index), delta);

        SettingsMenuBuilder.openRoles(player, holder.getRolePageIndex());

    }

    private int resolveDelta(InventoryClickEvent event, int step, int shiftStep) {

        int magnitude = event.isShiftClick() ? shiftStep : step;

        if (event.isLeftClick()) {
            return magnitude;
        }

        if (event.isRightClick()) {
            return -magnitude;
        }

        return 0;

    }

}
