package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.village.SalvateurRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ProtegerSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "proteger";
    }

    @Override
    public String getDescription() {
        return "Protège un joueur jusqu'à la fin de l'épisode (Salvateur, une fois par épisode).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player salvateur = (Player) sender;

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgSalvateur = playerManager.get(salvateur);

        if (lgSalvateur == null || !lgSalvateur.isAlive()) {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande.");
            return true;
        }

        Role role = lgSalvateur.getRole();

        if (!(role instanceof SalvateurRole)) {
            sender.sendMessage("§cVous n'êtes pas le Salvateur.");
            return true;
        }

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (!game.isRevealed()) {
            sender.sendMessage("§cLes rôles n'ont pas encore été révélés.");
            return true;
        }

        SalvateurRole salvateurRole = (SalvateurRole) role;

        if (!salvateurRole.isProtectAvailable()) {
            sender.sendMessage("§cVous avez déjà utilisé votre protection cet épisode.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg proteger <joueur>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        LGPlayer lgTarget = playerManager.get(target);

        if (lgTarget == null || !lgTarget.isAlive()) {
            sender.sendMessage("§cCe joueur ne fait pas partie de la partie en cours.");
            return true;
        }

        salvateurRole.consumeProtect();
        salvateurRole.setProtectedUuid(target.getUniqueId());

        target.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 0, false, false));

        salvateur.sendMessage("§bVous protégez " + target.getName() + " jusqu'à la fin de l'épisode.");

        return true;
    }

}
