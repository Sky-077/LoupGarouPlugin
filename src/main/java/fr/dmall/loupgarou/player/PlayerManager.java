package fr.dmall.loupgarou.player;

import fr.dmall.loupgarou.manager.Manager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager implements Manager {

    private final Map<UUID, LGPlayer> players = new HashMap<>();

    @Override
    public void enable() {

        // Création des LGPlayer des joueurs déjà connectés
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            players.put(player.getUniqueId(), new LGPlayer(player.getUniqueId()));
        }
    }

    @Override
    public void disable() {
        players.clear();
    }

    public LGPlayer get(Player player) {
        return players.get(player.getUniqueId());
    }

    public void add(Player player) {
        players.put(player.getUniqueId(), new LGPlayer(player.getUniqueId()));
    }

    public void remove(Player player) {
        players.remove(player.getUniqueId());
    }
}