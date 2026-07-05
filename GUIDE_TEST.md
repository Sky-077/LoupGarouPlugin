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
| `/lg role add <role> <nombre>` | Ajoute des rôles au pool (`villageois`, `loup-garou`, `pere-des-loups`, `petite-fille`, `voyante`, `sorciere`, `chasseur`, `cupidon`, `chasseur-de-primes`, `loup-blanc`, `ange`) |
| `/lg role remove <role>` | Retire un rôle du pool |
| `/lg role list` | Liste les rôles configurés |
| `/lg role clear` | Réinitialise la configuration |
| `/lg role available` | Liste tous les rôles utilisables avec la bonne orthographe |

### Pouvoirs de rôle (utilisables uniquement une fois les rôles révélés, 10 min après le vrai début)

| Commande | Rôle | Description |
|---|---|---|
| `/lg sonder <joueur>` | Voyante | Découvre le rôle d'un joueur, la nuit, 1x/nuit |
| `/lg infecter <joueur>` | Père des Loups | Accepte de transformer un joueur corrompu à 100% en Loup-Garou (offre cliquable reçue en jeu, 10s, quand un tel joueur meurt de la main d'un loup) |
| `/lg laissermourir <joueur>` | Père des Loups | Refuse l'infection et laisse mourir le joueur corrompu à 100% (même offre) |
| `/lg soigner <joueur>` | Sorcière | Potion de vie : reçue via une offre cliquable de 10s à la mort d'un joueur non infecté, 1x/partie |
| `/lg empoisonner <joueur>` | Sorcière | Potion de mort : retire 2 cœurs de vie maximum, définitivement, à un joueur, 1x/partie |
| `/lg tirer <joueur>` | Chasseur | Riposte pendant sa propre fenêtre de 15s de sursis, fait perdre 6 cœurs à un joueur autre que son tueur, 1x/partie |
| `/lg lier <joueur1> <joueur2>` | Cupidon | Lie deux joueurs par l'amour, 1x/partie |
| `/lg ange <dechu\|gardien>` | Ange | Choisit sa forme (cible aléatoire assignée), 1x/partie |
| `/lg regen` | Ange Gardien | Donne Régénération I (1 min) à son protégé sous 4 cœurs, 1x/partie |

### Commandes de debug (réservées aux OP)

| Commande | Description |
|---|---|
| `/lg forcestart` | Force le lancement de la partie sans minimum de joueurs inscrits |
| `/lg forcereveal` | Force la révélation immédiate des rôles sans attendre 10 min |
| `/lg forcepvp` | Active immédiatement le PVP sans attendre 30 min |
| `/lg lobbyspawn` | Définit le spawn du lobby (fin de partie) à la position actuelle de l'OP — persiste dans `config.yml` |
| `/lg forcevote` | Ouvre immédiatement le vote sans attendre le délai de 45 min |

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
- [🔴] Avant la révélation : `/lg me`, `/lg regle`, `/lg sonder`, `/lg soigner`, `/lg empoisonner`, `/lg tirer` renvoient tous "les rôles n'ont pas encore été révélés"
- [ ] Avant la révélation : la corruption des loups ne progresse pas (aucun gain même en restant collé à la victime)
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
- [ ] Un coup mortel annule les dégâts, rend le joueur invulnérable, et le tue réellement 15 secondes plus tard (+ jusqu'à 10s supplémentaires si une offre de soin ou de conversion est en cours, voir sections dédiées)
- [ ] Le message de mort (avec rôle révélé) et le kill crédité au bon joueur
- [🟢] Le respawn remet bien le joueur en mode spectateur
- [ ] `/lg tirer` (Chasseur) fonctionne bien pendant sa propre fenêtre de mort différée

### Potion de vie de la Sorcière (offre automatique, remplace l'ancien `/lg soigner` disponible à tout moment)
- [ ] Si une Sorcière vivante n'a pas encore utilisé sa potion de vie : à la fin de l'agonie (15s) de tout joueur qui n'est pas éligible à l'infection, la mort réelle est suspendue et la Sorcière reçoit un message cliquable "[Soigner]" pendant 10 secondes
- [ ] En cliquant (ou via `/lg soigner <joueur>`) dans les 10s : le joueur est sauvé (revit, équipement restauré), la Sorcière gagne +1 honneur, sa potion de vie est consommée
- [ ] Sans réaction de la Sorcière dans les 10s : la mort réelle a bien lieu normalement
- [ ] Si aucune Sorcière n'est vivante, ou si sa potion de vie est déjà utilisée : aucune offre n'est envoyée, la mort réelle a lieu immédiatement à la fin de l'agonie
- [ ] La Sorcière peut recevoir l'offre pour sa propre mort et se sauver elle-même (`/lg soigner <sonNom>`)
- [ ] `/lg soigner <joueur>` échoue ("n'attend pas de décision de soin") si utilisé en dehors de cette fenêtre de 10s

### Potion de mort de la Sorcière (retire 2 cœurs définitifs, ne tue plus instantanément)
- [ ] `/lg empoisonner <joueur>` retire bien 2 cœurs de vie maximum de façon permanente (barre de cœurs réduite, visible immédiatement) au lieu de tuer instantanément
- [ ] Si la vie actuelle du joueur dépasse son nouveau maximum, elle est bien ramenée à ce nouveau maximum (peut tuer le joueur si sa vie était déjà très basse)
- [ ] La réduction de vie maximum persiste jusqu'à la fin de la partie (pas de régénération naturelle du plafond)
- [ ] En fin de partie, le malus de cœurs est bien nettoyé (`PoisonManager.clearPoison`), le joueur retrouve 10 cœurs normaux au prochain lobby

### Corruption des loups et conversion (`/lg infecter` a changé de fonctionnement : il n'infecte plus la propre victime pendant l'agonie, mais accepte l'offre de conversion envoyée au Père des Loups)
- [ ] Un Loup-Garou qui reste à moins de 6 blocs d'un joueur (non-loup) le corrompt de 1% toutes les 5 secondes
- [ ] Un Père des Loups qui reste à moins de 6 blocs d'un joueur le corrompt bien plus vite : 1% par seconde
- [ ] Plusieurs loups proches de la même victime cumulent bien leurs contributions (progression plus rapide)
- [ ] La victime ne reçoit **aucun message/indicateur** de sa progression de corruption (mécanisme silencieux)
- [ ] La corruption ne redescend jamais, elle stagne juste si aucun loup n'est à proximité
- [ ] Le Loup Blanc ne participe **pas** à la corruption (solo, hors meute)
- [ ] Quand un joueur corrompu à 100% meurt de la main d'un Loup-Garou ou Père des Loups (pas un autre camp) : sa mort réelle est suspendue, et c'est le **Père des Loups** (pas la victime) qui reçoit un message cliquable dans le chat pendant 10 secondes ("Infecter" / "Laisser mourir")
- [ ] Si aucun Père des Loups n'est vivant au moment du kill : la victime meurt normalement, aucune offre n'est envoyée
- [ ] En cliquant "Infecter" (ou `/lg infecter <joueur>`) dans les 10s : la victime devient Loup-Garou, reprend sa vie/équipement, rejoint la liste des loups connus (et réciproquement), `/lg regle` reflète le nouveau rôle
- [ ] En cliquant "Laisser mourir" (ou `/lg laissermourir <joueur>`) : la mort réelle a lieu immédiatement, sans attendre la fin des 10s
- [ ] Sans réponse du Père des Loups dans les 10s : la mort réelle a bien lieu normalement (comportement par défaut)
- [ ] La victime elle-même ne reçoit aucun message pendant ce délai (silencieux jusqu'au résultat final)
- [ ] Une conversion réussie relance bien `VictoryChecker` (le camp d'origine peut perdre la partie si c'était son dernier membre)
- [ ] La corruption est bien réinitialisée en fin de partie (`/lg stop` ou victoire)

### Rôles (un par un, en conditions réelles avec plusieurs joueurs)
- [ ] **Villageois** : aucun comportement particulier
- [ ] **Loup-Garou** : Force I la nuit, retirée le jour ; `/lg regle` liste bien les autres Loups-Garous/Père des Loups de la partie (pas lui-même)
- [ ] **Père des Loups** : Force I comme un loup, corrompt les autres joueurs plus vite qu'un Loup-Garou classique (voir section Corruption) ; `/lg regle` liste bien les autres loups
- [ ] **Bonus de kill des loups** : tuer un joueur donne Speed I + Absorption I (2♥) pendant 1 minute au tueur — vérifier pour Loup-Garou, Père des Loups **et** Loup Blanc
- [🟢] **Petite Fille** : invisibilité 5 min en retirant toute l'armure la nuit, 1x/nuit, annulée en remettant une pièce d'armure
- [ ] **Voyante** : `/lg sonder` révèle bien rôle + équipe, seulement la nuit, 1x/nuit
- [ ] **Sorcière** : `/lg soigner` (offre automatique à 10s) et `/lg empoisonner` (2 cœurs définitifs), chacun 1x/partie
- [ ] **Chasseur** : reçoit un arc Puissance IV + 64 flèches à la révélation
  - [ ] Bonus de dégâts contre le camp des Loups-Garous : commence à Force 0.5 (+1.5 dégâts au corps-à-corps uniquement, pas à l'arc), augmente de 0.1 par Loup-Garou/Père des Loups tué, plafonné à Force I (+3 dégâts)
  - [ ] `/lg tirer <joueur>` pendant sa propre agonie fait perdre 6 cœurs à la cible (pas de mort instantanée garantie), 1x/partie
  - [ ] `/lg tirer` refuse de cibler son propre tueur ("vous ne pouvez pas tirer sur votre propre tueur")
  - [ ] Si un Loup-Garou ou un Père des Loups tue le Chasseur, il ne reçoit **pas** le bonus Speed I + Absorption I habituel (le Loup Blanc, lui, le reçoit normalement)
- [ ] **Cupidon** : reçoit un arc simple + livre Puissance III + Punch I + 64 flèches **à la révélation (10 min)**, pas au scattering ; `/lg lier <joueur1> <joueur2>` fonctionne, 1x/partie
- [ ] **Chasseur de Primes** : reçoit un livre Tranchant IV **à la révélation (10 min)**, pas au scattering
  - [ ] Aucun contrat avant l'activation du PVP (`/lg regle` dit "Aucun contrat actif")
  - [ ] Premier contrat reçu **à l'activation du PVP** (testable via `/lg forcepvp`, pas besoin d'attendre 30 min)
  - [ ] Tuer la cible du 1er contrat donne un arc Puissance IV + 64 flèches et marque le contrat "rempli"
  - [ ] Tuer la cible du 2e contrat donne des bottes en diamant Chute Amortie III
  - [ ] Le second contrat n'arrive **pas** tout de suite après le premier, mais au **lever du jour suivant** (nouvel épisode)
  - [ ] Si la cible du contrat en cours meurt d'une autre main (PVP, chute...), le contrat est annulé (message au Chasseur de Primes) et le contrat suivant arrive au lever du jour suivant, comme pour un succès
  - [ ] Une fois les 2 contrats résolus (remplis ou annulés), `/lg regle` affiche "Vous n'avez plus de contrat à accomplir"
- [ ] **Loup Blanc** : reçoit Force I la nuit comme un loup ; `/lg regle` liste bien les Loups-Garous/Père des Loups de la partie ; reçoit le livre Tranchant IV comme tout solo ; camp `Solitaire` dans le scoreboard (pas `Loups`) ; gagne seul même si tous les autres morts sont des loups
- [ ] **Ange** : reçoit le livre Tranchant IV comme tout solo ; à la révélation, `/lg regle` invite à choisir sa forme
  - [ ] `/lg ange dechu` : passe à 12 cœurs, une cible vivante aléatoire est assignée (rôle révélé dans le message et via `/lg regle`)
  - [ ] Tuer sa cible (crédité comme tueur) fait passer l'Ange Déchu à 15 cœurs (message dédié) ; gagne seul
  - [ ] `/lg ange gardien` : passe à 15 cœurs, un protégé vivant aléatoire est assigné (rôle révélé) ; l'Ange rejoint le camp effectif de son protégé (`teamOverride`, vérifier le scoreboard)
  - [ ] Si le protégé meurt : l'Ange Gardien passe à 12 cœurs + Faiblesse I permanente, redevient solitaire (`teamOverride` retiré), doit désormais gagner seul
  - [ ] `/lg regen` : utilisable seulement en forme Gardien, seulement si le protégé est sous 4 cœurs et vivant, 1x/partie — donne Régénération I 1 minute au protégé
  - [ ] Impossible de choisir une forme deux fois, ou avant la révélation des rôles
  - [ ] En fin de partie, le bonus/malus de cœurs et la Faiblesse sont bien nettoyés (`AngeManager.clearHearts`)

### Cupidon / camp Amoureux (nécessite au moins 3 comptes)
- [ ] Lien entre deux joueurs du **même camp** : si l'un meurt, l'autre meurt aussi de chagrin (message dédié), pas de changement de camp
- [ ] Lien entre deux joueurs de **camps opposés** (ex: Villageois + Loup-Garou) : les deux amoureux ET le Cupidon rejoignent le nouveau camp "Amoureux" (vérifier le "Groupe" dans le scoreboard des 3 joueurs)
- [ ] Le camp Amoureux gagne s'il est le dernier camp survivant (message de victoire dédié)
- [ ] Si les deux amoureux (camps opposés) meurent tous les deux, le Cupidon redevient un simple Villageois (vérifier `/lg me` et le scoreboard)
- [ ] Le lien amoureux ne survit pas d'une partie à l'autre (reset propre via `/lg stop` ou victoire)

### Rôles solo (camp NEUTRAL)
- [ ] Tout rôle solo (ex: Chasseur de Primes) reçoit à la révélation un livre Tranchant IV, pas de bow/livre Cupidon
- [ ] Un solitaire gagne **seul** : il doit être l'unique joueur restant (Village, Loups, Amoureux et tous les autres solitaires éliminés) — message de victoire nominatif
- [ ] Avec plusieurs joueurs solo en même temps : ils doivent s'éliminer entre eux aussi, la partie ne se termine pas tant qu'il en reste plus d'un

### Conditions de victoire
- [🟢] Village gagne quand tous les Loups (et solos) sont morts — message de victoire + partie `FINISHED`
- [ ] Loups gagnent quand tous les Villageois (et solos) sont morts — message de victoire + partie `FINISHED`
- [ ] Vérifier qu'une conversion par corruption qui fait basculer l'équilibre déclenche bien la victoire immédiatement
- [ ] Les Amoureux (camps opposés) gagnent quand ils sont les seuls survivants (voir section Cupidon)
- [ ] Les solitaires gagnent quand ils sont les seuls survivants (voir section Rôles solo)

### Autosmelting et diamants
- [🟢] Fer/or/cuivre bruts minés donnent directement le lingot
- [🟢] Un outil Silk Touch sur un minerai ne déclenche **pas** l'autosmelt (le bloc de minerai reste intact)
- [🟢] Les débris antiques donnent directement un éclat de netherite
- [🟢] Le compteur de diamants (`X/17` dans le scoreboard) s'incrémente correctement, y compris avec Fortune

### Système de vote (démarre 45 min après le vrai début, dure 3 épisodes)
- [ ] Les 4 maisons de vote apparaissent bien autour du centre de la bordure (à ~150 blocs), chacune avec une enclume et un jukebox
- [ ] Les maisons/jukebox/enclumes ne peuvent pas être détruits pendant la partie
- [ ] Clic droit sur un jukebox **avant** l'ouverture du vote : message "le vote n'est pas ouvert", pas de menu
- [ ] Clic droit sur un jukebox **pendant** le vote : ouvre un menu avec une tête par joueur vivant (sauf soi-même) + un item "Passer"
- [ ] Voter pour un joueur, repasser dans le jukebox et changer son vote : le dernier choix est bien celui pris en compte
- [ ] À la fin de chaque épisode de vote : honneur +1 pour ceux qui ont voté, inchangé pour ceux qui ont passé, -1 pour ceux qui n'ont rien fait (Villageois et Loups traités pareil)
- [ ] Le joueur le plus voté voit son vrai rôle apparaître dans le chat, mélangé avec 2 autres noms de rôles au hasard (sans indiquer lequel est le vrai)
- [ ] En cas d'égalité au nombre de votes, un des joueurs à égalité est choisi (pas de crash)
- [ ] Après 3 épisodes de vote, le vote se désactive (jukebox redevient inactif)
- [ ] Le vote et les votes en cours sont bien réinitialisés entre deux parties

### Honneur (au-delà du vote)
- [ ] Sorcière qui soigne quelqu'un (`/lg soigner`) gagne +1 honneur
- [ ] Un joueur qui tue un allié de son propre camp (Village tue Village, ou Loup tue Loup) perd -1 honneur
- [ ] À +3 d'honneur : Villageois gagne un cœur, Loup en perd un ; à -3 c'est l'inverse (vérifier via `/lg regle` ou en comptant les cœurs à l'écran)
- [ ] L'effet de cœur disparaît si l'honneur redescend sous le seuil (pas un bonus figé)
- [ ] L'honneur et l'effet de cœur sont bien réinitialisés entre deux parties

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
- Beaucoup de ces tests nécessitent plusieurs comptes Minecraft connectés simultanément (scattering, PVP, sonder, corruption des loups...) — les commandes de debug OP permettent de contourner certains délais mais pas le besoin d'avoir plusieurs joueurs pour les interactions à deux.
