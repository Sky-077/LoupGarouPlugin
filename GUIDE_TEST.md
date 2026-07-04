# Guide de test — LoupGarouPlugin

## Toutes les commandes

### Commandes générales

| Commande | Description |
|---|---|
| `/lg help` | Liste des commandes |
| `/lg info` | État de la partie + liste des rôles des joueurs |
| `/lg join` | S'inscrit pour la prochaine partie |
| `/lg leave` | Annule son inscription |
| `/lg start` | Lance la partie avec les joueurs inscrits (min. 3, sauf via `/lg forcestart`) |
| `/lg stop` | Arrête la partie, réinitialise les inscriptions |
| `/lg me` | Affiche son propre rôle (bloqué tant que les rôles ne sont pas révélés) |
| `/lg regle` | Réaffiche l'explication de son rôle (bloqué tant que non révélé) |
| `/lg bordure <taille>` | Configure la taille de la bordure pour la prochaine partie (sans argument : affiche la valeur actuelle) |

### Configuration des rôles avant une partie

| Commande | Description |
|---|---|
| `/lg role add <role> <nombre>` | Ajoute des rôles au pool (`villageois`, `loup-garou`, `pere-des-loups`, `petite-fille`, `voyante`, `sorciere`, `chasseur`, `cupidon`) |
| `/lg role remove <role>` | Retire un rôle du pool |
| `/lg role list` | Liste les rôles configurés |
| `/lg role clear` | Réinitialise la configuration |

### Pouvoirs de rôle (utilisables uniquement une fois les rôles révélés, 10 min après le vrai début)

| Commande | Rôle | Description |
|---|---|---|
| `/lg sonder <joueur>` | Voyante | Découvre le rôle d'un joueur, la nuit, 1x/nuit |
| `/lg infecter <joueur>` | Père des Loups | Transforme sa propre victime (en train de mourir) en Loup-Garou, 1x/partie |
| `/lg soigner <joueur>` | Sorcière | Potion de vie : sauve un joueur en train de mourir, 1x/partie |
| `/lg empoisonner <joueur>` | Sorcière | Potion de mort : tue instantanément un joueur, 1x/partie |
| `/lg tirer <joueur>` | Chasseur | Riposte une dernière fois pendant sa propre minute de sursis avant de mourir, 1x/partie |
| `/lg lier <joueur1> <joueur2>` | Cupidon | Lie deux joueurs par l'amour, 1x/partie |

### Commandes de debug (réservées aux OP)

| Commande | Description |
|---|---|
| `/lg forcestart` | Force le lancement de la partie sans minimum de joueurs inscrits |
| `/lg forcereveal` | Force la révélation immédiate des rôles sans attendre 10 min |
| `/lg forcepvp` | Active immédiatement le PVP sans attendre 30 min |
| `/lg lobbyspawn` | Définit le spawn du lobby (fin de partie) à la position actuelle de l'OP — persiste dans `config.yml` |

---

## Ce qui n'a jamais été testé en jeu (tout écrit et compilé, mais jamais lancé sur un vrai serveur)

### Cycle de partie de bout en bout
- [ ] `/lg join` par plusieurs comptes, puis `/lg start` par le host — vérifier que seuls les inscrits reçoivent un rôle et sont téléportés dans le monde de jeu
- [🟢] Les joueurs connectés mais **non inscrits** au moment du `/lg start` : doivent être téléportés dans le monde de partie en mode **spectateur** (pas dans l'ancien monde)
- [🟢] Minimum de 3 joueurs inscrits refusé correctement par `/lg start` (message d'erreur)
- [🟢] `/lg forcestart` (OP) : lance bien la partie avec 1 ou 2 joueurs inscrits seulement
- [🟢] Vérifier que les commandes de debug (`forcestart`, `forcereveal`, `forcepvp`) sont bien refusées à un joueur non-OP

### Génération du monde
- [🟢] Le monde `lg_uhc` est bien régénéré (ancien terrain supprimé) à chaque `/lg start`
- [] La bordure appliquée correspond bien à la valeur configurée via `/lg bordure`
- [🟢] La recherche de zone évite bien les biomes interdits (océan, île champignon, glace/neige) au centre de la bordure — vérifier visuellement en `/lg forcestart` + explorer
- [🟢] Aucune structure ne génère (village, temple, mineshaft, forteresse...)
- [🟢] Aucun mob hostile/neutre (creeper, zombie, squelette, araignée, enderman...) ne spawn naturellement ; les animaux passifs (vache, cochon, poule) spawnent bien
- [🟢] Impossible de voyager au Nether ou à l'End pendant la partie (message de blocage affiché)
- [🟢] Le temps de génération du monde au lancement (peut geler le serveur une ou deux secondes)

### Scattering et invincibilité
- [ ] Chaque joueur est téléporté à un endroit différent, sur un point de terrain sûr (pas dans le vide, pas dans l'eau/lave)
- [🟢] Invulnérabilité active pendant les 30 premières secondes, puis désactivée automatiquement
- [ ] `/lg stop` pendant la phase de scattering/invincibilité annule bien tout proprement (pas de bug de tâche différée qui se déclenche plus tard)
- [ ] Chaque joueur reçoit bien un stack de 64 steaks cuits au moment du scattering

### Révélation des rôles (10 min après le vrai début)
- [🔴] Avant la révélation : `/lg me`, `/lg regle`, `/lg sonder`, `/lg infecter`, `/lg soigner`, `/lg empoisonner`, `/lg tirer` renvoient tous "les rôles n'ont pas encore été révélés"
- [ ] Avant la révélation : le Loup-Garou n'a **pas** la Force la nuit, la Petite Fille ne peut **pas** activer l'invisibilité en retirant son armure
- [ ] À la révélation (10 min) : chaque joueur reçoit le message d'explication de son rôle, et le pouvoir jour/nuit s'active immédiatement si applicable (Force du Loup si c'est la nuit, etc.)
- [🟢] `/lg forcereveal` (OP) déclenche bien la révélation immédiatement et désactive le minuteur automatique (pas de double révélation à 10 min)

### PVP différé (30 min après le vrai début)
- [ ] Avant l'activation : un coup porté entre deux joueurs de la partie est bien annulé (aucun dégât, message affiché à l'attaquant)
- [ ] Avant l'activation : un coup PVP annulé ne déclenche **pas** le système de mort différée (pas de "mort" fantôme)
- [ ] À 30 min : le PVP s'active automatiquement pour tout le monde
- [🟢] `/lg forcepvp` (OP) active le PVP immédiatement
- [ ] Tant que le PVP n'est pas activé : un joueur qui subirait normalement une mort réelle (chute, lave, faim...) est soigné à pleine vie à la place ("Vous avez survécu !"), quelle que soit la cause
- [ ] Une fois le PVP activé : la mort redevient réelle dans les mêmes conditions

### Système de mort en deux temps
- [ ] Un coup mortel annule les dégâts, rend le joueur invulnérable, et le tue réellement 1 minute plus tard
- [ ] Le message de mort (avec rôle révélé) et le kill crédité au bon joueur
- [🟢] Le respawn remet bien le joueur en mode spectateur
- [🟠] `/lg soigner` (Sorcière) et `/lg infecter` (Père des Loups) fonctionnent bien pendant cette fenêtre d'une minute
- [ ] `/lg tirer` (Chasseur) fonctionne bien pendant sa propre fenêtre de mort différée

### Rôles (un par un, en conditions réelles avec plusieurs joueurs)
- [ ] **Villageois** : aucun comportement particulier
- [ ] **Loup-Garou** : Force I la nuit, retirée le jour
- [ ] **Père des Loups** : Force I comme un loup + `/lg infecter` transforme bien la victime en Loup-Garou (message privé, pas de broadcast)
- [🟢] **Petite Fille** : invisibilité 5 min en retirant toute l'armure la nuit, 1x/nuit, annulée en remettant une pièce d'armure
- [ ] **Voyante** : `/lg sonder` révèle bien rôle + équipe, seulement la nuit, 1x/nuit
- [🟢] **Sorcière** : `/lg soigner` et `/lg empoisonner`, chacun 1x/partie
- [ ] **Chasseur** : `/lg tirer` pendant sa propre agonie, 1x/partie
- [ ] **Cupidon** : spawn bien avec un arc Puissance V + 64 flèches ; `/lg lier <joueur1> <joueur2>` fonctionne, 1x/partie

### Cupidon / camp Amoureux (nécessite au moins 3 comptes)
- [ ] Lien entre deux joueurs du **même camp** : si l'un meurt, l'autre meurt aussi de chagrin (message dédié), pas de changement de camp
- [ ] Lien entre deux joueurs de **camps opposés** (ex: Villageois + Loup-Garou) : les deux amoureux ET le Cupidon rejoignent le nouveau camp "Amoureux" (vérifier le "Groupe" dans le scoreboard des 3 joueurs)
- [ ] Le camp Amoureux gagne s'il est le dernier camp survivant (message de victoire dédié)
- [ ] Si les deux amoureux (camps opposés) meurent tous les deux, le Cupidon redevient un simple Villageois (vérifier `/lg me` et le scoreboard)
- [ ] Le lien amoureux ne survit pas d'une partie à l'autre (reset propre via `/lg stop` ou victoire)

### Conditions de victoire
- [🟢] Village gagne quand tous les Loups (et solos) sont morts — message de victoire + partie `FINISHED`
- [ ] Loups gagnent quand tous les Villageois (et solos) sont morts — message de victoire + partie `FINISHED`
- [ ] Vérifier qu'une infection (Père des Loups) qui fait basculer l'équilibre déclenche bien la victoire immédiatement
- [ ] Les Amoureux (camps opposés) gagnent quand ils sont les seuls survivants (voir section Cupidon)

### Autosmelting et diamants
- [🟢] Fer/or/cuivre bruts minés donnent directement le lingot
- [🟢] Un outil Silk Touch sur un minerai ne déclenche **pas** l'autosmelt (le bloc de minerai reste intact)
- [🟢] Les débris antiques donnent directement un éclat de netherite
- [🟢] Le compteur de diamants (`X/17` dans le scoreboard) s'incrémente correctement, y compris avec Fortune

### Fin de partie / spawn du lobby
- [ ] `/lg lobbyspawn` sauvegarde bien la position exacte (monde + XYZ) de l'OP qui l'exécute
- [ ] Après une victoire ou un `/lg stop`, tous les joueurs atterrissent bien à cet endroit précis (pas dans un bloc, pas dans le vide)
- [ ] Le spawn configuré persiste après un redémarrage du serveur (relire `config.yml`)
- [ ] Tant que `/lg lobbyspawn` n'a jamais été utilisé, ça retombe sur l'ancien comportement (`Bukkit.getWorlds().get(0).getSpawnLocation()`)

### Scoreboard
- [🟢] Toutes les lignes s'affichent et se mettent à jour correctement : durée, cycle, épisode, groupe, joueurs vivants/total, bordure, kills, diamants
- [🟢] L'épisode s'incrémente à chaque lever du jour
- [ ] Le "Groupe" affiché est personnel à chaque joueur (Village/Loups/Solitaire) et vide/"-" avant la révélation

---

## Notes

- Le monde `lg_uhc` est supprimé et régénéré à chaque partie — ne rien construire dedans entre deux tests.
- Il faut relancer `./gradlew build` et redéployer le `.jar` avant de tester, sinon le serveur tourne avec une version obsolète du plugin.
- Beaucoup de ces tests nécessitent plusieurs comptes Minecraft connectés simultanément (scattering, PVP, sonder, infecter...) — les commandes de debug OP permettent de contourner certains délais mais pas le besoin d'avoir plusieurs joueurs pour les interactions à deux.
