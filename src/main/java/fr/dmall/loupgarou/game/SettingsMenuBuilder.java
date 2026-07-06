package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleFactory;
import fr.dmall.loupgarou.role.RoleManager;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingsMenuBuilder {

    public static final int ROLES_PER_PAGE = 45;

    private SettingsMenuBuilder() {
    }

    public static void openMain(Player player) {

        SettingsMenuHolder holder = new SettingsMenuHolder(SettingsMenuHolder.Page.MAIN, 0);
        Inventory inventory = Bukkit.createInventory(holder, 27, "§6Paramètres de la partie");
        holder.setInventory(inventory);

        inventory.setItem(10, namedItem(Material.BARRIER, "§eBordure", "§7Taille de la bordure de monde"));
        inventory.setItem(12, namedItem(Material.PLAYER_HEAD, "§eMinimum de joueurs", "§7Nombre de joueurs requis pour /lg start"));
        inventory.setItem(14, namedItem(Material.CLOCK, "§eDélais de partie", "§7Invincibilité, révélation, PVP, vote"));
        inventory.setItem(16, namedItem(Material.BOOK, "§ePool de rôles", "§7Ajoute/retire des rôles à la partie"));

        boolean quickMode = GameStarter.isQuickMode();

        inventory.setItem(22, namedItem(quickMode ? Material.LIME_WOOL : Material.RED_WOOL,
                "§eMode rapide : " + (quickMode ? "§aactivé" : "§cdésactivé"),
                "§7Skip la partie farm : stuff de départ,",
                "§7révélation immédiate, PVP + vote au même délai",
                "§7(réglable via /lg delais rapide <minutes>)",
                "§7Clic pour basculer"));

        player.openInventory(inventory);

    }

    public static void openBordure(Player player) {

        SettingsMenuHolder holder = new SettingsMenuHolder(SettingsMenuHolder.Page.BORDURE, 0);
        Inventory inventory = Bukkit.createInventory(holder, 27, "§6Bordure");
        holder.setInventory(inventory);

        WorldManager worldManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(WorldManager.class);

        long size = (long) worldManager.getBorderSize();

        inventory.setItem(13, namedItem(Material.BARRIER, "§e" + size + " blocs",
                "§7Clic gauche : +50 §7(+500 avec shift)",
                "§7Clic droit : -50 §7(-500 avec shift)"));

        inventory.setItem(22, backButton());

        player.openInventory(inventory);

    }

    public static void openMinPlayers(Player player) {

        SettingsMenuHolder holder = new SettingsMenuHolder(SettingsMenuHolder.Page.MIN_PLAYERS, 0);
        Inventory inventory = Bukkit.createInventory(holder, 27, "§6Minimum de joueurs");
        holder.setInventory(inventory);

        inventory.setItem(13, namedItem(Material.PLAYER_HEAD, "§e" + GameStarter.getMinPlayers() + " joueurs",
                "§7Clic gauche : +1 §7(+5 avec shift)",
                "§7Clic droit : -1 §7(-5 avec shift)"));

        inventory.setItem(22, backButton());

        player.openInventory(inventory);

    }

    public static void openDelais(Player player) {

        SettingsMenuHolder holder = new SettingsMenuHolder(SettingsMenuHolder.Page.DELAIS, 0);
        Inventory inventory = Bukkit.createInventory(holder, 27, "§6Délais de partie");
        holder.setInventory(inventory);

        inventory.setItem(10, namedItem(Material.SHIELD, "§eInvincibilité : " + GameStarter.getInvincibilityMinutes() + " min",
                "§7Clic gauche : +1 min §7(+10 avec shift)",
                "§7Clic droit : -1 min §7(-10 avec shift)"));

        inventory.setItem(12, namedItem(Material.ENDER_EYE, "§eRévélation : " + GameStarter.getRoleRevealMinutes() + " min",
                "§7Clic gauche : +1 min §7(+10 avec shift)",
                "§7Clic droit : -1 min §7(-10 avec shift)"));

        inventory.setItem(14, namedItem(Material.IRON_SWORD, "§ePVP : " + GameStarter.getPvpDelayMinutes() + " min",
                "§7Clic gauche : +1 min §7(+10 avec shift)",
                "§7Clic droit : -1 min §7(-10 avec shift)"));

        inventory.setItem(16, namedItem(Material.JUKEBOX, "§eVote : " + GameStarter.getVoteStartMinutes() + " min",
                "§7Clic gauche : +1 min §7(+10 avec shift)",
                "§7Clic droit : -1 min §7(-10 avec shift)"));

        inventory.setItem(22, backButton());

        player.openInventory(inventory);

    }

    public static void openRoles(Player player, int pageIndex) {

        RoleManager roleManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(RoleManager.class);

        List<String> names = new ArrayList<>(RoleFactory.getRegisteredNames());

        int totalPages = Math.max(1, (names.size() + ROLES_PER_PAGE - 1) / ROLES_PER_PAGE);
        int clampedPage = Math.max(0, Math.min(pageIndex, totalPages - 1));

        SettingsMenuHolder holder = new SettingsMenuHolder(SettingsMenuHolder.Page.ROLES, clampedPage);
        Inventory inventory = Bukkit.createInventory(holder, 54, "§6Pool de rôles (page " + (clampedPage + 1) + "/" + totalPages + ")");
        holder.setInventory(inventory);

        Map<String, Integer> gameRoles = roleManager.getGameRoles();

        int start = clampedPage * ROLES_PER_PAGE;
        int end = Math.min(start + ROLES_PER_PAGE, names.size());

        for (int i = start; i < end; i++) {

            String name = names.get(i);
            int count = gameRoles.getOrDefault(name, 0);
            Role role = RoleFactory.create(name);
            ItemStack icon = RoleHeadTextures.createIcon(name, getRoleIcon(role.getTeam()));

            inventory.setItem(i - start, namedItem(icon, "§e" + role.getName() + " §7(" + count + ")",
                    "§7Clic gauche : +1 §7(+5 avec shift)",
                    "§7Clic droit : -1 §7(-5 avec shift)"));

        }

        if (clampedPage > 0) {
            inventory.setItem(45, namedItem(Material.ARROW, "§ePage précédente"));
        }

        inventory.setItem(49, backButton());

        if (clampedPage < totalPages - 1) {
            inventory.setItem(53, namedItem(Material.ARROW, "§ePage suivante"));
        }

        player.openInventory(inventory);

    }

    private static Material getRoleIcon(RoleTeam team) {

        if (team == RoleTeam.LOUP) {
            return Material.WOLF_SPAWN_EGG;
        }

        if (team == RoleTeam.NEUTRAL) {
            return Material.NETHER_STAR;
        }

        return Material.IRON_INGOT;

    }

    private static ItemStack backButton() {
        return namedItem(Material.OAK_DOOR, "§eRetour");
    }

    private static ItemStack namedItem(Material material, String name, String... lore) {
        return namedItem(new ItemStack(material), name, lore);
    }

    private static ItemStack namedItem(ItemStack item, String name, String... lore) {

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        if (lore.length > 0) {
            meta.setLore(List.of(lore));
        }

        item.setItemMeta(meta);

        return item;

    }

}
