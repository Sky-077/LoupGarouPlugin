package fr.dmall.loupgarou.role.solo;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeuFolletRole extends Role {

    private static final long FOLIE_COOLDOWN_MILLIS = 10L * 60L * 1000L; // 10 minutes
    private static final long FOLIE_DURATION_MILLIS = 60L * 1000L; // 1 minute
    private static final long FEATHER_COOLDOWN_MILLIS = 10L * 60L * 1000L; // 10 minutes
    private static final double NEARBY_RADIUS = 50.0;
    private static final double NEARBY_RADIUS_SQUARED = NEARBY_RADIUS * NEARBY_RADIUS;

    private long lastFolieTime = 0L;
    private long folieActiveUntil = 0L;
    private long lastFeatherTime = 0L;

    public FeuFolletRole() {
        super("Feu Follet", RoleTeam.NEUTRAL);
    }

    @Override
    public void onNight(Player player) {

        List<Player> nearby = new ArrayList<>();

        for (Player other : player.getWorld().getPlayers()) {

            if (other.equals(player)) {
                continue;
            }

            if (other.getLocation().distanceSquared(player.getLocation()) <= NEARBY_RADIUS_SQUARED) {
                nearby.add(other);
            }

        }

        if (nearby.isEmpty()) {
            player.sendMessage("§5Aucun joueur à moins de 50 blocs cette nuit.");
            return;
        }

        Collections.shuffle(nearby);
        Player target = nearby.get(0);

        String roleName = "Inconnu";

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgTarget = playerManager.get(target);

        if (lgTarget != null && lgTarget.getRole() != null) {
            roleName = lgTarget.getRole().getName();
        }

        player.sendMessage("§5Vous sentez la présence de " + target.getName() + " : son rôle est §d" + roleName + "§5.");

    }

    public boolean isFolieAvailable() {
        return System.currentTimeMillis() - lastFolieTime >= FOLIE_COOLDOWN_MILLIS;
    }

    public long getFolieRemainingSeconds() {
        return Math.max(0L, (FOLIE_COOLDOWN_MILLIS - (System.currentTimeMillis() - lastFolieTime)) / 1000L);
    }

    public void activateFolie() {
        lastFolieTime = System.currentTimeMillis();
        folieActiveUntil = System.currentTimeMillis() + FOLIE_DURATION_MILLIS;
    }

    public boolean isFolieActive() {
        return System.currentTimeMillis() < folieActiveUntil;
    }

    public boolean isFeatherAvailable() {
        return System.currentTimeMillis() - lastFeatherTime >= FEATHER_COOLDOWN_MILLIS;
    }

    public long getFeatherRemainingSeconds() {
        return Math.max(0L, (FEATHER_COOLDOWN_MILLIS - (System.currentTimeMillis() - lastFeatherTime)) / 1000L);
    }

    public void consumeFeather() {
        lastFeatherTime = System.currentTimeMillis();
    }

    public boolean hasNoArmor(Player player) {

        PlayerInventory inventory = player.getInventory();

        return isEmpty(inventory.getHelmet())
                && isEmpty(inventory.getChestplate())
                && isEmpty(inventory.getLeggings())
                && isEmpty(inventory.getBoots());

    }

    private boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    public void checkInitialInvisibility(Player player) {

        if (hasNoArmor(player)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false));
        }

    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "Vous ne pouvez l'emporter que seul, sans allié.",
                "Se dépouiller entièrement de son armure, à n'importe quel moment, déclenche une invisibilité sans limite de durée (stoppée dès qu'une pièce d'armure est remise).",
                "Une fois invisible, des particules trahissent votre position aux yeux de la Petite Fille et du Loup-Garou Perfide — et réciproquement, vous percevez les leurs.",
                "/lg folie déclenche votre Folie Incendiaire (rechargement de 10 minutes, effet d'1 minute) : Speed I, et chacun de vos coups au corps-à-corps enflamme l'adversaire.",
                "Un clic droit avec votre Plume vous projette 50 blocs dans la direction regardée (rechargement de 10 minutes).",
                "À chaque tombée de la nuit, le rôle d'un joueur aléatoire situé à moins de 50 blocs vous est révélé (rien ne se passe si personne n'est à portée).",
        };
    }

}
