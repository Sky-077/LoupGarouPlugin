package fr.dmall.loupgarou.role.loup;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class WolfRole extends Role {

    private final List<UUID> knownWolves = new ArrayList<>();

    protected WolfRole(String name) {
        super(name, RoleTeam.LOUP);
    }

    public void setKnownWolves(List<UUID> wolves) {
        knownWolves.clear();
        knownWolves.addAll(wolves);
    }

    public List<UUID> getKnownWolves() {
        return knownWolves;
    }

    public void addKnownWolf(UUID uuid) {
        knownWolves.add(uuid);
    }

    @Override
    public void onNight(Player player) {

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.STRENGTH,
                PotionEffect.INFINITE_DURATION,
                0,
                false,
                false
        ));

    }

    @Override
    public void onDay(Player player) {
        player.removePotionEffect(PotionEffectType.STRENGTH);
    }

    protected List<String> getWolfPackLines() {

        List<String> lines = new ArrayList<>();

        if (knownWolves.isEmpty()) {
            lines.add("§fAucun autre Loup dans cette partie : vous êtes seul de votre camp.");
            return lines;
        }

        lines.add("§eMembres de la meute :");

        for (UUID wolf : knownWolves) {

            String name = Bukkit.getOfflinePlayer(wolf).getName();

            if (name == null) {
                name = "Inconnu";
            }

            lines.add(" - " + name);

        }

        return lines;

    }

}
