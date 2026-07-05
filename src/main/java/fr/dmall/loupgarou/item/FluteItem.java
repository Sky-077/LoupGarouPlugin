package fr.dmall.loupgarou.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FluteItem {

    private static final String DISPLAY_NAME = "§6Flûte";

    private FluteItem() {
    }

    public static ItemStack create() {

        ItemStack flute = new ItemStack(Material.GOAT_HORN);
        ItemMeta meta = flute.getItemMeta();
        meta.setDisplayName(DISPLAY_NAME);
        flute.setItemMeta(meta);

        return flute;

    }

    public static boolean isFlute(ItemStack item) {

        return item != null
                && item.getType() == Material.GOAT_HORN
                && item.hasItemMeta()
                && DISPLAY_NAME.equals(item.getItemMeta().getDisplayName());

    }

}
