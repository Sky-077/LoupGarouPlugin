package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.manager.Manager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HostManager implements Manager {

    public static final String PERMANENT_HOST = "Skytag07";

    private final List<String> eligible = new ArrayList<>();
    private UUID activeHost;

    @Override
    public void enable() {
        load();
    }

    @Override
    public void disable() {
    }

    private void load() {

        FileConfiguration config = LoupGarouPlugin.getInstance().getConfig();

        eligible.clear();
        eligible.addAll(config.getStringList("hosts.eligible"));

    }

    private void save() {

        FileConfiguration config = LoupGarouPlugin.getInstance().getConfig();
        config.set("hosts.eligible", eligible);
        LoupGarouPlugin.getInstance().saveConfig();

    }

    public boolean isEligible(String name) {

        if (name.equalsIgnoreCase(PERMANENT_HOST)) {
            return true;
        }

        return eligible.stream().anyMatch(entry -> entry.equalsIgnoreCase(name));

    }

    public boolean addEligible(String name) {

        if (isEligible(name)) {
            return false;
        }

        eligible.add(name);
        save();

        return true;

    }

    public boolean removeEligible(String name) {

        if (name.equalsIgnoreCase(PERMANENT_HOST)) {
            return false;
        }

        boolean removed = eligible.removeIf(entry -> entry.equalsIgnoreCase(name));

        if (removed) {
            save();
        }

        return removed;

    }

    public List<String> listEligible() {

        List<String> names = new ArrayList<>();
        names.add(PERMANENT_HOST);
        names.addAll(eligible);

        return names;

    }

    public boolean hasActiveHost() {
        return activeHost != null;
    }

    public UUID getActiveHost() {
        return activeHost;
    }

    public boolean isActiveHost(Player player) {
        return activeHost != null && activeHost.equals(player.getUniqueId());
    }

    public String getActiveHostName() {

        if (activeHost == null) {
            return null;
        }

        Player player = Bukkit.getPlayer(activeHost);

        return (player != null) ? player.getName() : Bukkit.getOfflinePlayer(activeHost).getName();

    }

    public boolean claim(Player player) {

        if (!isEligible(player.getName())) {
            return false;
        }

        if (activeHost != null && !activeHost.equals(player.getUniqueId())) {
            return false;
        }

        activeHost = player.getUniqueId();
        Bukkit.broadcastMessage("§6" + player.getName() + " est désormais l'hôte de la partie.");

        return true;

    }

    public boolean release(Player player) {

        if (!isActiveHost(player)) {
            return false;
        }

        activeHost = null;
        Bukkit.broadcastMessage("§6" + player.getName() + " a quitté le rôle d'hôte.");

        return true;

    }

    public void onQuit(Player player) {

        if (isActiveHost(player)) {
            activeHost = null;
            Bukkit.broadcastMessage("§6L'hôte (" + player.getName() + ") s'est déconnecté, le poste est libéré.");
        }

    }

    public void promptOnJoin(Player player) {

        if (hasActiveHost() || !isEligible(player.getName())) {
            return;
        }

        TextComponent message = new TextComponent("§eVoulez-vous devenir l'hôte de cette partie ? ");
        TextComponent accept = new TextComponent("§a[Oui]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lg host claim"));
        message.addExtra(accept);
        message.addExtra(new TextComponent(" §7(ou plus tard via /lg host claim)"));

        player.spigot().sendMessage(message);

    }

}
