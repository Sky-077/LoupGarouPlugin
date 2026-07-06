package fr.dmall.loupgarou.game;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Têtes personnalisées (minecraft-heads.com) pour le pool de rôles du menu /lg menu, ajoutées au fur
// et à mesure rôle par rôle. Un rôle sans entrée ici retombe sur l'icône générique par camp.
public class RoleHeadTextures {

    private static final Map<String, String> TEXTURES = new HashMap<>();

    static {
        // Loup-Garou : "Werewolf of Fever Swamp" (minecraft-heads.com, ID 25202)
        TEXTURES.put("loup-garou", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE1YzZmNWZhZTNhN2NmZWZhODQ2ZTBjNjFjOWE2NGY2ODVhMjNkMzEzOWU2ZDg4NTU0NGVmOTYwM2EwOGM1YyJ9fX0=");
    }

    private RoleHeadTextures() {
    }

    public static ItemStack createIcon(String roleKey, Material fallback) {

        String texture = TEXTURES.get(roleKey.toLowerCase());

        if (texture == null) {
            return new ItemStack(fallback);
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", texture));
        meta.setPlayerProfile(profile);

        head.setItemMeta(meta);

        return head;

    }

}
