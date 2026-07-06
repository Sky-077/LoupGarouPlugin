package fr.dmall.loupgarou.game;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class SettingsMenuHolder implements InventoryHolder {

    public enum Page {
        MAIN,
        BORDURE,
        MIN_PLAYERS,
        DELAIS,
        ROLES
    }

    private final Page page;
    private final int rolePageIndex;
    private Inventory inventory;

    public SettingsMenuHolder(Page page, int rolePageIndex) {
        this.page = page;
        this.rolePageIndex = rolePageIndex;
    }

    public Page getPage() {
        return page;
    }

    public int getRolePageIndex() {
        return rolePageIndex;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

}
