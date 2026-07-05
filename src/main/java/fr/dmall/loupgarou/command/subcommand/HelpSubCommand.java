package fr.dmall.loupgarou.command.subcommand;

import org.bukkit.command.CommandSender;

public class HelpSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Affiche la liste des commandes.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        sender.sendMessage("§6========== LoupGarou ==========");
        sender.sendMessage("§e/lg help §7- Affiche cette aide");
        sender.sendMessage("§e/lg info §7- Informations de la partie");
        sender.sendMessage("§e/lg start §7- Lance la partie avec les joueurs inscrits (/lg join)");
        sender.sendMessage("§e/lg stop §7- Arrête la partie");
        sender.sendMessage("§e/lg role §7- Configure les rôles (add/remove/list/clear)");
        sender.sendMessage("§e/lg me §7- Affiche votre rôle actuel");
        sender.sendMessage("§e/lg sonder <joueur> §7- Sonde un joueur (Voyante, la nuit)");
        sender.sendMessage("§e/lg infecter <joueur> §7- Accepte de transformer un joueur corrompu à 100% en Loup-Garou (Père des Loups)");
        sender.sendMessage("§e/lg laissermourir <joueur> §7- Refuse et laisse mourir un joueur corrompu à 100% (Père des Loups)");
        sender.sendMessage("§e/lg regle §7- Réaffiche l'explication de votre rôle");
        sender.sendMessage("§e/lg soigner <joueur> §7- Potion de vie (Sorcière)");
        sender.sendMessage("§e/lg empoisonner <joueur> §7- Potion de mort (Sorcière)");
        sender.sendMessage("§e/lg tirer <joueur> §7- Riposte en mourant (Chasseur)");
        sender.sendMessage("§e/lg forcestart §7- [Debug] Force le lancement sans minimum de joueurs");
        sender.sendMessage("§e/lg bordure <taille> §7- Configure la bordure de la prochaine partie");
        sender.sendMessage("§e/lg forcepvp §7- [Debug] Active immédiatement le PVP");
        sender.sendMessage("§e/lg join §7- S'inscrit pour la prochaine partie");
        sender.sendMessage("§e/lg leave §7- Annule son inscription");
        sender.sendMessage("§e/lg forcereveal §7- [Debug] Force la révélation immédiate des rôles");
        sender.sendMessage("§e/lg lier <joueur1> <joueur2> §7- Lie deux joueurs par l'amour (Cupidon)");
        sender.sendMessage("§e/lg lobbyspawn §7- [OP] Définit le spawn du lobby à votre position actuelle");
        sender.sendMessage("§e/lg forcevote §7- [Debug] Ouvre immédiatement le vote");
        sender.sendMessage("§e/lg ange <dechu|gardien> §7- Choisit votre forme d'Ange");
        sender.sendMessage("§e/lg regen §7- Régénération pour votre protégé sous 4 cœurs (Ange Gardien)");

        return true;
    }
}