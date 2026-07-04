package fr.dmall.loupgarou.game;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoteInventoryHolder implements InventoryHolder {

    public static final UUID PASS = new UUID(0, 0);

    private final Map<Integer, UUID> slotTargets = new HashMap<>();
    private Inventory inventory;

    public void putTarget(int slot, UUID target) {
        slotTargets.put(slot, target);
    }

    public UUID getTarget(int slot) {
        return slotTargets.get(slot);
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

}
