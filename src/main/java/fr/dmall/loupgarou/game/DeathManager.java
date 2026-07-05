package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.manager.Manager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.loup.PereDesLoupsRole;
import fr.dmall.loupgarou.role.loup.WolfRole;
import fr.dmall.loupgarou.role.village.IdiotDuVillageRole;
import fr.dmall.loupgarou.role.village.SorciereRole;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DeathManager implements Manager {

    private static final long DYING_DURATION_TICKS = 20L * 15L; // 15 secondes
    private static final long CONVERSION_OFFER_TICKS = 20L * 10L; // 10 secondes
    private static final long HEAL_OFFER_TICKS = 20L * 10L; // 10 secondes

    private final Map<UUID, BukkitTask> pendingTasks = new HashMap<>();
    private final Map<UUID, UUID> pendingKillers = new HashMap<>();
    private final Map<UUID, ItemStack[]> hiddenArmor = new HashMap<>();
    private final Map<UUID, ItemStack> hiddenMainHand = new HashMap<>();
    private final Set<UUID> conversionOffers = new HashSet<>();
    private final Set<UUID> healOffers = new HashSet<>();

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
        resetAll();
    }

    public void resetAll() {

        for (UUID uuid : pendingTasks.keySet()) {

            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                player.setInvulnerable(false);
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.removePotionEffect(PotionEffectType.SLOWNESS);
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                restoreEquipment(player);
            }

        }

        for (BukkitTask task : pendingTasks.values()) {
            task.cancel();
        }

        pendingTasks.clear();
        pendingKillers.clear();
        hiddenArmor.clear();
        hiddenMainHand.clear();
        conversionOffers.clear();
        healOffers.clear();

    }

    public boolean isDying(Player player) {
        return pendingTasks.containsKey(player.getUniqueId());
    }

    public void startDying(Player player, Player killer) {

        if (isDying(player)) {
            return;
        }

        UUID uuid = player.getUniqueId();

        if (killer != null) {
            pendingKillers.put(uuid, killer.getUniqueId());
        }

        player.setInvulnerable(true);

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) DYING_DURATION_TICKS, 0, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, (int) DYING_DURATION_TICKS, 3, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (int) DYING_DURATION_TICKS, 0, false, false));

        hiddenArmor.put(uuid, player.getInventory().getArmorContents());
        hiddenMainHand.put(uuid, player.getInventory().getItemInMainHand());
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setItemInMainHand(null);

        player.sendTitle("§4Vous agonisez...", "§7Vous allez mourir dans 15 secondes", 10, 60, 10);

        BukkitTask task = Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> finalizeDeath(player),
                DYING_DURATION_TICKS
        );

        pendingTasks.put(uuid, task);

    }

    public void revive(Player player) {

        UUID uuid = player.getUniqueId();

        BukkitTask task = pendingTasks.remove(uuid);

        if (task != null) {
            task.cancel();
        }

        pendingKillers.remove(uuid);
        conversionOffers.remove(uuid);
        healOffers.remove(uuid);

        player.setInvulnerable(false);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        restoreEquipment(player);
        player.sendTitle("§aVous avez été sauvé !", "", 5, 40, 10);

    }

    public UUID consumeKiller(Player player) {
        return pendingKillers.remove(player.getUniqueId());
    }

    public UUID getPendingKiller(Player player) {
        return pendingKillers.get(player.getUniqueId());
    }

    public void killInstantly(Player player, Player killer) {

        if (killer != null) {
            pendingKillers.put(player.getUniqueId(), killer.getUniqueId());
        }

        player.setHealth(0.0);

    }

    public void applyDamage(Player player, Player source, double amount) {

        if (source != null) {
            pendingKillers.put(player.getUniqueId(), source.getUniqueId());
        }

        player.setHealth(Math.max(0.0, player.getHealth() - amount));

    }

    private void finalizeDeath(Player player) {

        pendingTasks.remove(player.getUniqueId());

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (!game.isPvpEnabled()) {

            player.setInvulnerable(false);
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            restoreEquipment(player);

            pendingKillers.remove(player.getUniqueId());

            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
            player.setFireTicks(0);
            player.setRemainingAir(player.getMaximumAir());

            player.sendTitle("§aVous avez survécu !", "§7Le PVP n'est pas encore activé", 5, 40, 10);

            return;

        }

        if (shouldOfferConversion(player)) {
            offerConversion(player);
            return;
        }

        if (shouldAutoReviveIdiot(player)) {
            autoReviveIdiot(player);
            return;
        }

        if (findAliveSorciereWithHeal() != null) {
            offerWitchHeal(player);
            return;
        }

        finalizeRealDeath(player);

    }

    private boolean shouldAutoReviveIdiot(Player player) {

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgPlayer = playerManager.get(player);

        if (lgPlayer == null || !(lgPlayer.getRole() instanceof IdiotDuVillageRole)) {
            return false;
        }

        if (!((IdiotDuVillageRole) lgPlayer.getRole()).isReviveAvailable()) {
            return false;
        }

        UUID killerUuid = pendingKillers.get(player.getUniqueId());

        if (killerUuid == null) {
            return false;
        }

        Player killer = Bukkit.getPlayer(killerUuid);

        if (killer == null) {
            return false;
        }

        LGPlayer lgKiller = playerManager.get(killer);

        return lgKiller != null && !(lgKiller.getRole() instanceof WolfRole);

    }

    private void autoReviveIdiot(Player player) {

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgPlayer = playerManager.get(player);

        ((IdiotDuVillageRole) lgPlayer.getRole()).consumeRevive();

        revive(player);

        double eightHearts = 16.0;
        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.MAX_HEALTH);
        double maxHealth = (maxHealthAttribute != null) ? maxHealthAttribute.getValue() : eightHearts;

        player.setHealth(Math.min(eightHearts, maxHealth));

        player.sendTitle("§aVous survivez... par pure bêtise !", "", 5, 40, 10);
        Bukkit.broadcastMessage("§6" + player.getName() + " aurait dû mourir, mais il est bien trop bête pour ça ! "
                + "§7Il est en réalité l'Idiot du Village.");

    }

    private boolean shouldOfferConversion(Player player) {

        CorruptionManager corruptionManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(CorruptionManager.class);

        if (!corruptionManager.isFullyCorrupted(player.getUniqueId())) {
            return false;
        }

        UUID killerUuid = pendingKillers.get(player.getUniqueId());

        if (killerUuid == null) {
            return false;
        }

        Player killer = Bukkit.getPlayer(killerUuid);

        if (killer == null) {
            return false;
        }

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgKiller = playerManager.get(killer);

        if (lgKiller == null || !(lgKiller.getRole() instanceof WolfRole)) {
            return false;
        }

        return findAlivePereDesLoups() != null;

    }

    private LGPlayer findAlivePereDesLoups() {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (lgPlayer.isAlive() && lgPlayer.getRole() instanceof PereDesLoupsRole) {
                return lgPlayer;
            }

        }

        return null;

    }

    private void offerConversion(Player player) {

        UUID uuid = player.getUniqueId();

        conversionOffers.add(uuid);

        LGPlayer lgPereDesLoups = findAlivePereDesLoups();
        Player pereDesLoups = (lgPereDesLoups != null) ? Bukkit.getPlayer(lgPereDesLoups.getUuid()) : null;

        if (pereDesLoups != null) {

            TextComponent message = new TextComponent("§5" + player.getName() + " est corrompu à 100% ! ");
            TextComponent infect = new TextComponent("§a[Infecter]");
            infect.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lg infecter " + player.getName()));
            TextComponent decline = new TextComponent(" §c[Laisser mourir]");
            decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lg laissermourir " + player.getName()));
            message.addExtra(infect);
            message.addExtra(decline);

            pereDesLoups.spigot().sendMessage(message);
            pereDesLoups.sendMessage("§5Vous avez 10 secondes pour décider.");

        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> {
                    if (conversionOffers.remove(uuid)) {
                        finalizeRealDeath(player);
                    }
                },
                CONVERSION_OFFER_TICKS
        );

        pendingTasks.put(uuid, task);

    }

    private LGPlayer findAliveSorciereWithHeal() {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (lgPlayer.isAlive() && lgPlayer.getRole() instanceof SorciereRole
                    && ((SorciereRole) lgPlayer.getRole()).isHealAvailable()) {
                return lgPlayer;
            }

        }

        return null;

    }

    private void offerWitchHeal(Player player) {

        UUID uuid = player.getUniqueId();

        healOffers.add(uuid);

        LGPlayer lgSorciere = findAliveSorciereWithHeal();
        Player sorciere = (lgSorciere != null) ? Bukkit.getPlayer(lgSorciere.getUuid()) : null;

        if (sorciere != null) {

            TextComponent message = new TextComponent("§5" + player.getName() + " agonise ! ");
            TextComponent heal = new TextComponent("§a[Soigner]");
            heal.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lg soigner " + player.getName()));
            message.addExtra(heal);

            sorciere.spigot().sendMessage(message);
            sorciere.sendMessage("§5Vous avez 10 secondes pour utiliser votre potion de vie.");

        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> {
                    if (healOffers.remove(uuid)) {
                        finalizeRealDeath(player);
                    }
                },
                HEAL_OFFER_TICKS
        );

        pendingTasks.put(uuid, task);

    }

    public boolean hasHealOffer(Player player) {
        return healOffers.contains(player.getUniqueId());
    }

    public void consumeHealOffer(Player player) {
        healOffers.remove(player.getUniqueId());
    }

    private void finalizeRealDeath(Player player) {

        pendingTasks.remove(player.getUniqueId());

        player.setInvulnerable(false);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        restoreEquipment(player);

        player.setHealth(0.0);

    }

    public boolean hasConversionOffer(Player player) {
        return conversionOffers.contains(player.getUniqueId());
    }

    public void consumeConversionOffer(Player player) {
        conversionOffers.remove(player.getUniqueId());
    }

    public void declineConversion(Player player) {

        if (conversionOffers.remove(player.getUniqueId())) {
            finalizeRealDeath(player);
        }

    }

    private void restoreEquipment(Player player) {

        UUID uuid = player.getUniqueId();

        ItemStack[] armor = hiddenArmor.remove(uuid);
        ItemStack mainHand = hiddenMainHand.remove(uuid);

        if (armor != null) {
            player.getInventory().setArmorContents(armor);
        }

        if (mainHand != null) {
            player.getInventory().setItemInMainHand(mainHand);
        }

    }

}