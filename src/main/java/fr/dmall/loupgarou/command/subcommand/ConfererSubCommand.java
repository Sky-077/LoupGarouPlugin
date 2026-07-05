package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.BienfaiteurManager;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.village.BienfaiteurRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ConfererSubCommand implements SubCommand {

    private static final long GIFT_DELAY_TICKS = 20L * 60L * 3L; // 3 minutes

    @Override
    public String getName() {
        return "conferer";
    }

    @Override
    public String getDescription() {
        return "Offre 1 cœur permanent à un joueur, délivré 3 minutes plus tard (Bienfaiteur, 3 joueurs différents, 1x/5min).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player bienfaiteur = (Player) sender;

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgBienfaiteur = playerManager.get(bienfaiteur);

        if (lgBienfaiteur == null || !lgBienfaiteur.isAlive()) {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande.");
            return true;
        }

        Role role = lgBienfaiteur.getRole();

        if (!(role instanceof BienfaiteurRole)) {
            sender.sendMessage("§cVous n'êtes pas le Bienfaiteur.");
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

        BienfaiteurRole bienfaiteurRole = (BienfaiteurRole) role;

        if (bienfaiteurRole.isGiftsExhausted()) {
            sender.sendMessage("§cVous avez déjà offert vos 3 dons.");
            return true;
        }

        if (!bienfaiteurRole.isCooldownOver()) {
            sender.sendMessage("§cVous devez encore attendre " + bienfaiteurRole.getRemainingCooldownSeconds() + " secondes.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg conferer <joueur>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        if (target.equals(bienfaiteur)) {
            sender.sendMessage("§cVous ne pouvez pas vous cibler vous-même.");
            return true;
        }

        LGPlayer lgTarget = playerManager.get(target);

        if (lgTarget == null || !lgTarget.isAlive()) {
            sender.sendMessage("§cCe joueur ne fait pas partie de la partie en cours.");
            return true;
        }

        if (bienfaiteurRole.hasAlreadyGifted(target.getUniqueId())) {
            sender.sendMessage("§cVous avez déjà offert un don à ce joueur.");
            return true;
        }

        bienfaiteurRole.consumeGift(target.getUniqueId());

        bienfaiteur.sendMessage("§dVous avez discrètement offert un don à " + target.getName() + ". Il le recevra dans 3 minutes.");

        UUID targetUuid = target.getUniqueId();
        long startedAt = game.getStartTimeMillis();

        Bukkit.getScheduler().runTaskLater(
                LoupGarouPlugin.getInstance(),
                () -> deliverGift(targetUuid, startedAt),
                GIFT_DELAY_TICKS
        );

        if (bienfaiteurRole.isGiftsExhausted()) {
            bienfaiteurRole.startSlowRegen(bienfaiteur);
            bienfaiteur.sendMessage("§dVos dons sont épuisés. Vous bénéficiez désormais d'une régénération lente (1 cœur par minute).");
        }

        return true;
    }

    private void deliverGift(UUID targetUuid, long startedAt) {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getStartTimeMillis() != startedAt) {
            return;
        }

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            return;
        }

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        Player recipient = Bukkit.getPlayer(targetUuid);

        if (recipient == null) {
            return;
        }

        LGPlayer lgRecipient = playerManager.get(recipient);

        if (lgRecipient == null || !lgRecipient.isAlive() || !game.getPlayers().contains(lgRecipient)) {
            return;
        }

        BienfaiteurManager.grantHeart(recipient);

        recipient.sendMessage("§dVous recevez un don mystérieux : +1 cœur de vie permanent !");

    }

}
