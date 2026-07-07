package fr.dmall.loupgarou.game;

import com.mojang.authlib.GameProfile;
import fr.dmall.loupgarou.manager.Manager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Faux joueurs (bots de test) : de vrais ServerPlayer NMS enregistrés auprès du PlayerList du serveur,
// sans connexion réseau réelle (Connection reliée à un canal Netty qui avale silencieusement les paquets
// sortants). Contrairement à un simple mob déguisé, ils passent les vérifications "instanceof Player"
// utilisées partout dans le code (corruption, charme, votes, scoreboard...), donc testent réellement le
// comportement des rôles qui dépendent de la proximité d'autres joueurs, sans avoir besoin de plusieurs
// comptes réels. Outil de debug uniquement : pas d'IA, ils restent immobiles à l'endroit du spawn.
public class FakePlayerManager implements Manager {

    private final Map<UUID, String> bots = new HashMap<>();

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
        removeAll();
    }

    public Player spawn(String name, Location location) {

        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        MinecraftServer server = craftServer.getServer();
        ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
        PlayerList playerList = craftServer.getHandle();

        GameProfile profile = new GameProfile(UUID.randomUUID(), name);

        ServerPlayer nmsPlayer = new ServerPlayer(server, level, profile, ClientInformation.createDefault());
        nmsPlayer.forceSetPositionRotation(
                location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch()
        );

        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        connection.channel = new EmbeddedChannel(new DiscardingOutboundHandler());

        CommonListenerCookie cookie = CommonListenerCookie.createInitial(profile, false);

        playerList.placeNewPlayer(connection, nmsPlayer, cookie);

        bots.put(profile.getId(), name);

        return nmsPlayer.getBukkitEntity();

    }

    public boolean isBot(UUID uuid) {
        return bots.containsKey(uuid);
    }

    public Map<UUID, String> getBots() {
        return Collections.unmodifiableMap(bots);
    }

    public boolean remove(UUID uuid) {

        if (!bots.containsKey(uuid)) {
            return false;
        }

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {

            CraftServer craftServer = (CraftServer) Bukkit.getServer();
            PlayerList playerList = craftServer.getHandle();

            playerList.remove(((CraftPlayer) player).getHandle());

        }

        bots.remove(uuid);

        return true;

    }

    public void removeAll() {
        new ArrayList<>(bots.keySet()).forEach(this::remove);
    }

    // N'avale que les écritures sortantes (paquets envoyés à ce faux client) : il n'y a personne de l'autre
    // côté pour les recevoir, mais on doit quand même compléter la promesse Netty pour ne jamais bloquer/lever
    // d'exception côté serveur quand celui-ci tente de synchroniser ce joueur (mouvement d'entités, tracking...).
    private static class DiscardingOutboundHandler extends ChannelOutboundHandlerAdapter {

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
            promise.trySuccess();
        }

    }

}
