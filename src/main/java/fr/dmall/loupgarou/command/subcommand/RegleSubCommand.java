package fr.dmall.loupgarou.command.subcommand;

import org.bukkit.command.CommandSender;

public class RegleSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "regle";
    }

    @Override
    public String getDescription() {
        return "Explique les règles générales du jeu.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        sender.sendMessage("§6========== Règles du jeu ==========");
        sender.sendMessage("§fLoup-Garou UHC : une partie de survie en PVP libre où chacun reçoit un rôle secret. Le Village doit démasquer et éliminer les Loups avant qu'ils n'éliminent tout le monde, pendant que certains rôles solitaires jouent leur propre partie, seuls contre tous.");
        sender.sendMessage("§6Déroulé d'une partie :");
        sender.sendMessage("§e1. Dispersion §f- chaque joueur inscrit est téléporté à un endroit différent, avec de quoi manger.");
        sender.sendMessage("§e2. Invincibilité §f- personne ne peut être blessé le temps que tout le monde s'installe.");
        sender.sendMessage("§e3. Phase de farm §f- les rôles ne sont pas encore actifs, c'est le moment de se préparer (sautée en mode rapide).");
        sender.sendMessage("§e4. Révélation des rôles §f- chacun découvre son rôle et ses pouvoirs s'activent (revoir avec /lg me).");
        sender.sendMessage("§e5. Activation du PVP §f- les combats comptent vraiment. Avant ça, un coup normalement mortel ne fait que soigner.");
        sender.sendMessage("§e6. Vote §f- chaque jour, Village et Loups vivants désignent quelqu'un dans une maison de vote ; son rôle est révélé publiquement, sans le tuer ni le bannir.");
        sender.sendMessage("§e7. Fin de partie §f- dès qu'un camp remplit sa condition de victoire.");
        sender.sendMessage("§6Si vous recevez un coup normalement mortel :");
        sender.sendMessage("§fVous ne mourez pas instantanément : vous devenez spectateur pendant 15 secondes. Une Sorcière peut vous sauver, ou le Père des Loups peut vous transformer en Loup-Garou si vous étiez corrompu à 100%. Sans intervention, la mort devient définitive à la fin du délai.");
        sender.sendMessage("§6Camps et victoire :");
        sender.sendMessage("§eVillage §f- gagne si tous les Loups, solitaires et Amoureux sont éliminés.");
        sender.sendMessage("§eLoups §f- gagnent si tout le Village, les solitaires et les Amoureux sont éliminés.");
        sender.sendMessage("§eSolitaire §f- gagne s'il est l'unique survivant parmi les solitaires, tous les autres camps éliminés.");
        sender.sendMessage("§eAmoureux §f- gagnent si les deux joueurs liés par Cupidon sont les seuls survivants.");

        return true;
    }

}
