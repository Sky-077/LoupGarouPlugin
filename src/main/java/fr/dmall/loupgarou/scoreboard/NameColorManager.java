package fr.dmall.loupgarou.scoreboard;

import fr.dmall.loupgarou.LoupGarouPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.LinkedHashMap;
import java.util.Map;

public class NameColorManager {

    public static final Map<Material, ChatColor> WOOL_COLORS = new LinkedHashMap<>();

    static {
        WOOL_COLORS.put(Material.WHITE_WOOL, ChatColor.WHITE);
        WOOL_COLORS.put(Material.LIGHT_GRAY_WOOL, ChatColor.GRAY);
        WOOL_COLORS.put(Material.GRAY_WOOL, ChatColor.DARK_GRAY);
        WOOL_COLORS.put(Material.BLACK_WOOL, ChatColor.BLACK);
        WOOL_COLORS.put(Material.BROWN_WOOL, ChatColor.DARK_RED);
        WOOL_COLORS.put(Material.RED_WOOL, ChatColor.RED);
        WOOL_COLORS.put(Material.ORANGE_WOOL, ChatColor.GOLD);
        WOOL_COLORS.put(Material.YELLOW_WOOL, ChatColor.YELLOW);
        WOOL_COLORS.put(Material.LIME_WOOL, ChatColor.GREEN);
        WOOL_COLORS.put(Material.GREEN_WOOL, ChatColor.DARK_GREEN);
        WOOL_COLORS.put(Material.CYAN_WOOL, ChatColor.DARK_AQUA);
        WOOL_COLORS.put(Material.LIGHT_BLUE_WOOL, ChatColor.AQUA);
        WOOL_COLORS.put(Material.BLUE_WOOL, ChatColor.BLUE);
        WOOL_COLORS.put(Material.PURPLE_WOOL, ChatColor.DARK_PURPLE);
        WOOL_COLORS.put(Material.MAGENTA_WOOL, ChatColor.LIGHT_PURPLE);
        WOOL_COLORS.put(Material.PINK_WOOL, ChatColor.DARK_BLUE);
    }

    private NameColorManager() {
    }

    public static void setColor(Player observer, Player target, ChatColor color) {

        ScoreboardManager scoreboardManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(ScoreboardManager.class);

        Scoreboard scoreboard = scoreboardManager.getOrCreateScoreboard(observer);

        String teamName = "lg_color_" + color.name().toLowerCase();
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.setColor(color);
        }

        Team currentTeam = scoreboard.getEntryTeam(target.getName());

        if (currentTeam != null && !currentTeam.equals(team)) {
            currentTeam.removeEntry(target.getName());
        }

        team.addEntry(target.getName());

    }

}
