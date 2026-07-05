package fr.dmall.loupgarou.game;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;
import java.util.UUID;

public class ColorMenuHolder implements InventoryHolder {

    private final List<UUID> targets;
    private Inventory inventory;

    public ColorMenuHolder(List<UUID> targets) {
        this.targets = targets;
    }

    public List<UUID> getTargets() {
        return targets;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

}
