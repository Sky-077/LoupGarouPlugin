package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.game.ColorMenuHolder;
import fr.dmall.loupgarou.scoreboard.NameColorManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ColorSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "color";
    }

    @Override
    public String getDescription() {
        return "Ouvre un menu pour colorer le pseudo (au-dessus de la tête + liste Tab) d'un ou plusieurs joueurs, visible uniquement par vous.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player observer = (Player) sender;

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg color <joueur1> <joueur2> ...");
            return true;
        }

        List<UUID> targets = new ArrayList<>();

        for (int i = 1; i < args.length; i++) {

            Player target = Bukkit.getPlayerExact(args[i]);

            if (target == null) {
                sender.sendMessage("§cJoueur introuvable : " + args[i]);
                return true;
            }

            if (!targets.contains(target.getUniqueId())) {
                targets.add(target.getUniqueId());
            }

        }

        openColorMenu(observer, targets);

        return true;
    }

    private void openColorMenu(Player observer, List<UUID> targets) {

        ColorMenuHolder holder = new ColorMenuHolder(targets);
        Inventory inventory = Bukkit.createInventory(holder, 18, "§6Choisir une couleur");
        holder.setInventory(inventory);

        int slot = 0;

        for (Map.Entry<Material, ChatColor> entry : NameColorManager.WOOL_COLORS.entrySet()) {

            ItemStack item = new ItemStack(entry.getKey());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(entry.getValue() + "" + ChatColor.BOLD + entry.getValue().name());
            item.setItemMeta(meta);

            inventory.setItem(slot, item);
            slot++;

        }

        observer.openInventory(inventory);

    }

}
