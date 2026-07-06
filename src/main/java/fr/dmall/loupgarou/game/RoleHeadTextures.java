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
    private static final Map<String, String> PLAYER_SKINS = new HashMap<>();

    static {

        // Loup-Garou : "Werewolf of Fever Swamp" (minecraft-heads.com, ID 25202)
        TEXTURES.put("loup-garou", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE1YzZmNWZhZTNhN2NmZWZhODQ2ZTBjNjFjOWE2NGY2ODVhMjNkMzEzOWU2ZDg4NTU0NGVmOTYwM2EwOGM1YyJ9fX0=");

        // Père des Loups : "Wolf" (minecraft-heads.com, ID 106234)
        TEXTURES.put("pere-des-loups", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZhM2M3NTIxM2JlNGFjNmUxMjA5YjEyZmI2MGZjY2E5YWY3ODJhY2I3MmZiZTgyZmUzNTc3MzNlM2M3OTcxMSJ9fX0=");

        // Grand Méchant Loup : "Wolf" (minecraft-heads.com, ID 109004)
        TEXTURES.put("grand-mechant-loup", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmE5MDIzZDQ2ZmQ2OWU4Mjk3MjMwNzU2N2Q0OGM4NjE1MmMzZGFmM2U1OWM5ZTZhM2QzZmFjYzVkNjBlNTUzMyJ9fX0=");

        // Loup-Garou Craintif : "Wolf" (minecraft-heads.com, ID 105260)
        TEXTURES.put("loup-garou-craintif", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzJkNDQ2ZTJiODljNmVmMGVkNmI0OWUwOWYyNjcwYzg4YTdjNTZiYTkyODY2YTI5YWRlOGRmOGFjZDhjMGI2In19fQ==");

        // Loup-Garou Perfide : "Sculk Wolf" (minecraft-heads.com, ID 89300)
        TEXTURES.put("loup-garou-perfide", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcwYzkyNTc1YjNmZjNmYjc0ZDE3ZTg1Y2NjZDZjOTg5OWFlNTU1NGJiNjJjYTFkOGIzZjM4ZDMzZjJhYjVjNCJ9fX0=");

        // Vilain Petit Loup : "Wolf" (minecraft-heads.com, ID 87900)
        TEXTURES.put("vilain-petit-loup", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzRkYmI5NmVkNmJmNTk3OTIxMGE0NzBmNmQxYjFkNGY5NjdjZGM3MDg3MzY4Nzk5NzE0ODFlY2Q1OGQzN2E5NCJ9fX0=");

        // Sorcière : "Witch" (minecraft-heads.com, ID 3864)
        TEXTURES.put("sorciere", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjBlMTNkMTg0NzRmYzk0ZWQ1NWFlYjcwNjk1NjZlNDY4N2Q3NzNkYWMxNmY0YzNmODcyMmZjOTViZjlmMmRmYSJ9fX0=");

        // Voyante : "Fortune Teller" (minecraft-heads.com, ID 53760)
        TEXTURES.put("voyante", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThmNWZmNWYxMzJjMjRkMTgxNTcyYWJmZDY0Y2ZiYzIwNGU3NDMxMjAwMTEwYzY5ZjFlNTFkYjhlYzEyY2RmZCJ9fX0=");

        // Chasseur : "Hunter Steve" (minecraft-heads.com, ID 120306)
        TEXTURES.put("chasseur", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWY3N2YxNTkwMzZjOTVlZGI2Yjg0Mzc1MWI1M2VkNWI2MGJlMTUwZTkwMTlkMzhjMGJkZWJhOGI3NDU2ODM4NiJ9fX0=");

        // Chasseur de Primes : "Bounty Hunter" (minecraft-heads.com, ID 90592)
        TEXTURES.put("chasseur-de-primes", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGYwN2MzMzFjODNhMDRjYTVlZWUyYWM3ODkyNzc0ZjIwOGQxYTI0NTE5OTU1MjdlNzIyZTg1YTk2MDY4YjBmYiJ9fX0=");

        // Loup Blanc : "Angry Pale Wolf" (minecraft-heads.com, ID 38704) — même tête que Grand Méchant Loup
        TEXTURES.put("loup-blanc", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQxYWE3ZTNiOTU2NGIzODQ2ZjFkZWExNGYxYjFjY2JmMzk5YmJiMjNiOTUyZGJkN2VlYzQxODAyYTI4OWM5NiJ9fX0=");

        // Ange : "Angel" (minecraft-heads.com, ID 108652)
        TEXTURES.put("ange", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmJjNThjODY0NGFlYzhjODJiNGMxMDc1MzJiZDM2NjhmZDE4YWRiMDAxOGZlNThhMmNkMmUzNDZkMTQ0YjBmOSJ9fX0=");

        // Salvateur : "Duck with Halo" (minecraft-heads.com, ID 65027)
        TEXTURES.put("salvateur", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFmM2UzZDY4OGViZWFmYTEwOWRiZDFhMjlhZDIwZWJjYzA5ZDFmY2Q1NzZlZGU5OWRkODVlYjc2Y2EzMmQ4ZCJ9fX0=");

        // Idiot du Village : "Village Idiot" (minecraft-heads.com, ID 27837)
        TEXTURES.put("idiot-du-village", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTNiZGMzZmU3MGFjMGYwNzI1YjgxYWQ5MTViNGQyODFhMWViM2JhYmIyOTBhYzdmMmFjMjUwZjMwMGFiODE4YyJ9fX0=");

        // Ancien : "Old Man" (minecraft-heads.com, ID 126518)
        TEXTURES.put("ancien", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjU2ZTUxZWFkYTlmNDdiMjk2ZDI2ZTU0YmIwNDE5MDU1ZjdiZjYxNjg1MTYzNDVlYjc1OTJjZjU2MmI4YzAxMyJ9fX0=");

        // Feu Follet : "Fire Chuchu" (minecraft-heads.com, ID 128161)
        TEXTURES.put("feu-follet", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ExYzY1NTEzNjg2MzI4MjJiODEzNjVmNWIzMzE4OTEyMWIwODRlNjZmMmJkYmU0MzRlZWJiNjY1ZDBmN2MxMCJ9fX0=");

        // Imitateur : "The Invisible Man" (minecraft-heads.com, ID 32265)
        TEXTURES.put("imitateur", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2IxZTAxN2I1ODQxYjk4NTc3YTJiOGVkOWJmMDIzZDNiZjE0OWQ3ZWY2Y2RkY2VmY2FkZjdiNGIyN2MzMWIzMSJ9fX0=");

        // Joueur de Flûte : "Witch Doctor" (minecraft-heads.com, ID 103060)
        TEXTURES.put("joueur-de-flute", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTIxNmEyOWRmZTMzY2RmMGY4YmJhYTUxZDczYTg0YjcwMWU2ODBkMzUwZTZiNTNmMzkxNGQ4Y2I4OWJlYWNiMiJ9fX0=");

        // Petite Fille : "Girl" (minecraft-heads.com, ID 108370)
        TEXTURES.put("petite-fille", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y5ZDc0MDk1M2U2MTE2Y2I5MzllYTc1NjMwMzhlODJkMTVlMzJiYzQ4ZmFlMjlmMWM5ODZlN2ZjZjM2YmZiMCJ9fX0=");

        // Cupidon : "Cupid" (mcheads.ru, ID 7533)
        TEXTURES.put("cupidon", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE4NWM5ZDdlYTM3NTUzM2RkNjBkZDQ3OGViYjE0OWExY2NkOTQ0YTRhM2ZjYTcxZDE5ZjlkNzg3YjQ2NDZmYyJ9fX0=");

        // Villageois : skin du joueur "DrTestificate_MD" (visage de villageois, référence connue de la communauté)
        PLAYER_SKINS.put("villageois", "DrTestificate_MD");

        // Bienfaiteur : skin du joueur "King"
        PLAYER_SKINS.put("bienfaiteur", "King");

    }

    private RoleHeadTextures() {
    }

    @SuppressWarnings("deprecation")
    public static ItemStack createIcon(String roleKey, Material fallback) {

        String key = roleKey.toLowerCase();
        String texture = TEXTURES.get(key);
        String playerName = PLAYER_SKINS.get(key);

        if (texture == null && playerName == null) {
            return new ItemStack(fallback);
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (texture != null) {

            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", texture));
            meta.setPlayerProfile(profile);

        } else {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
        }

        head.setItemMeta(meta);

        return head;

    }

}
