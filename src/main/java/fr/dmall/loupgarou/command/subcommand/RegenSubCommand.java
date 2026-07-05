package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.AngeManager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.solo.AngeRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class RegenSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "regen";
    }

    @Override
    public String getDescription() {
        return "Donne Régénération I pendant 1 minute à votre protégé sous 4 cœurs (Ange Gardien, 1x/partie).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgPlayer = playerManager.get(player);

        if (lgPlayer == null || !lgPlayer.isAlive()) {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande.");
            return true;
        }

        Role role = lgPlayer.getRole();

        if (!(role instanceof AngeRole)) {
            sender.sendMessage("§cVous n'êtes pas l'Ange.");
            return true;
        }

        AngeRole ange = (AngeRole) role;

        if (ange.getForm() != AngeRole.Form.GARDIEN) {
            sender.sendMessage("§cVous n'êtes pas Ange Gardien.");
            return true;
        }

        if (ange.isProtegeDead()) {
            sender.sendMessage("§cVotre protégé est mort.");
            return true;
        }

        if (!ange.isRegenAvailable()) {
            sender.sendMessage("§cVous avez déjà utilisé Régénération cette partie.");
            return true;
        }

        UUID protegeUuid = ange.getLinkedPlayer();
        Player protege = (protegeUuid != null) ? Bukkit.getPlayer(protegeUuid) : null;

        if (protege == null) {
            sender.sendMessage("§cVotre protégé est introuvable.");
            return true;
        }

        if (!AngeManager.isBelowLowHealthThreshold(protege)) {
            sender.sendMessage("§cVotre protégé n'est pas sous 4 cœurs.");
            return true;
        }

        ange.consumeRegen();

        protege.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 60, 0, false, true));

        protege.sendMessage("§dVotre Ange Gardien vous a offert une Régénération !");
        player.sendMessage("§dVous avez donné Régénération à " + protege.getName() + " !");

        return true;
    }

}
