package fr.dmall.loupgarou.role.village;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BienfaiteurRole extends Role {

    private static final int MAX_GIFTS = 3;
    private static final long COOLDOWN_MILLIS = 5L * 60L * 1000L; // 5 minutes
    private static final long REGEN_PERIOD_TICKS = 20L * 60L; // 1 minute

    private int giftsUsed = 0;
    private long lastGiftTime = 0L;
    private final List<UUID> giftedPlayers = new ArrayList<>();
    private BukkitTask regenTask;

    public BienfaiteurRole() {
        super("Bienfaiteur", RoleTeam.VILLAGE);
    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "Vous devez gagner avec le Village.",
                "Vous recevez 2 livres Protection II.",
                "Offrez 1 cœur permanent à 3 joueurs différents (jamais vous-même) avec /lg conferer <joueur> (1 fois toutes les 5 minutes).",
                "Le joueur visé reçoit le don discrètement, 3 minutes après votre commande.",
                "Une fois vos 3 dons utilisés, vous obtenez une régénération lente permanente (1 cœur par minute).",
        };
    }

    public boolean isGiftsExhausted() {
        return giftsUsed >= MAX_GIFTS;
    }

    public boolean isCooldownOver() {
        return System.currentTimeMillis() - lastGiftTime >= COOLDOWN_MILLIS;
    }

    public long getRemainingCooldownSeconds() {
        long remaining = COOLDOWN_MILLIS - (System.currentTimeMillis() - lastGiftTime);
        return Math.max(0L, remaining / 1000L);
    }

    public boolean hasAlreadyGifted(UUID uuid) {
        return giftedPlayers.contains(uuid);
    }

    public void consumeGift(UUID target) {
        giftsUsed++;
        lastGiftTime = System.currentTimeMillis();
        giftedPlayers.add(target);
    }

    public void startSlowRegen(Player player) {

        if (regenTask != null) {
            return;
        }

        regenTask = Bukkit.getScheduler().runTaskTimer(
                LoupGarouPlugin.getInstance(),
                () -> {

                    if (!player.isOnline()) {
                        return;
                    }

                    AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

                    if (maxHealth == null) {
                        return;
                    }

                    if (player.getHealth() < maxHealth.getValue()) {
                        player.setHealth(Math.min(maxHealth.getValue(), player.getHealth() + 2.0));
                    }

                },
                REGEN_PERIOD_TICKS,
                REGEN_PERIOD_TICKS
        );

    }

    public void cancelRegen() {

        if (regenTask != null) {
            regenTask.cancel();
            regenTask = null;
        }

    }

    @Override
    public void onDeath(Player player) {
        cancelRegen();
    }

}
