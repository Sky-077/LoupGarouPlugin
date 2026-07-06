package fr.dmall.loupgarou.player;

import fr.dmall.loupgarou.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager implements Manager {

    private final Map<UUID, LGPlayer> players = new HashMap<>();

    @Override
    public void enable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            add(player);
        }
    }

    @Override
    public void disable() {
        players.clear();
    }

    public void add(Player player) {
        players.computeIfAbsent(player.getUniqueId(), LGPlayer::new);
    }

    public LGPlayer get(Player player) {
        return players.get(player.getUniqueId());
    }

    public Collection<LGPlayer> getPlayers() {
        return players.values();
    }
}