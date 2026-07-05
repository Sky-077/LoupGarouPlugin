# LoupGarouPlugin — Contexte pour Claude

## Contexte général

- Plugin Minecraft **Paper** qui recrée le fonctionnement du plugin **LG UHC** de TheGuill / uhcworld.fr (Loup-Garou + UHC en PVP libre).
- Repo GitHub : https://github.com/Sky-077/LoupGarouPlugin.git (branche `main`)
- Package racine : `fr.dmall.loupgarou`
- Java 21, Gradle Kotlin DSL, `io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT` (`compileOnly`).
- Le développeur teste souvent **seul** (pas toujours 3 comptes Minecraft dispo) → des commandes de debug existent pour contourner les contraintes (voir plus bas).

## Règles de travail à respecter absolument

1. **Toujours proposer un commit + push après chaque étape terminée et compilée**, et attendre la confirmation de l'utilisateur avant de commit/push (ne jamais commit sans qu'il ait dit "oui"/"vas-y").
2. **Compiler (`./gradlew compileJava -q`) après chaque modification** avant de proposer le commit, pour être sûr que ça build.
3. Progression **incrémentale, étape par étape** : une fonctionnalité à la fois.
4. Quand une demande est ambiguë ou implique un choix de design avec de vraies conséquences (ex: regen du monde à chaque partie vs monde persistant, comportement d'un pouvoir), **demander avant de coder** plutôt que de deviner.
5. Le monde de jeu (`lg_uhc`) est **supprimé et régénéré à chaque `/lg start`** — ne jamais construire dedans entre deux tests, prévenir l'utilisateur si une action pourrait perdre des données.
6. Pas de commentaires inutiles dans le code, pas de sur-ingénierie, pas d'abstraction prématurée — code direct et lisible, cohérent avec le style existant (voir Architecture ci-dessous).
7. Après chaque lot de changements, il faut relancer `./gradlew build` et redéployer le `.jar` avant de tester en jeu (rappeler ce point si pertinent).

## Architecture générale

- **Pattern Manager** : chaque système implémente `Manager` (`enable()`/`disable()`), enregistré dans `ManagerRegistry` au démarrage (`LoupGarouPlugin.onEnable()`). Managers actuels : `GameManager`, `WorldManager`, `PlayerManager`, `RoleManager`, `CycleManager`, `ScoreboardManager`, `DeathManager`, `CorruptionManager`, `LoveManager`, `LobbySpawnManager`, `VoteManager`.
- **Classes utilitaires statiques** (pas de cycle `enable()`/`disable()`, pas dans `ManagerRegistry`) : `GameStarter`/`GameEnder` (déroulé de partie), `VictoryChecker` (conditions de victoire), `BountyManager` (contrats du Chasseur de Primes), `HonorManager`/`AngeManager`/`PoisonManager`/`LoupBlancManager` (effets de cœurs via `AttributeModifier` sur `Attribute.MAX_HEALTH`, tous nettoyés dans `GameEnder.end()`).
- **Commandes** : `/lg <sous-commande>` via `LGCommand`, qui route vers des classes `SubCommand` (interface `getName()`/`getDescription()`/`execute()`). Les commandes de debug étendent `DebugSubCommand` (abstraite, vérifie `sender.isOp()` avant d'exécuter).
- **Rôles** : classe abstraite `Role` (nom, `RoleTeam`, hooks `onGameStart`, `onDeath`, `onDay`, `onNight`, `getInstructions()` pour le texte d'explication envoyé au joueur, `sendInstructions()`). `RoleFactory` = registre statique nom→constructeur. `RoleManager` gère le pool de rôles configuré (`/lg role add/remove/list/clear`, noms exacts via `/lg role available`) et les distribue aléatoirement (`assignRoles`), en complétant avec des Villageois. `WolfRole` (classe abstraite) factorise la Force I jour/nuit + la liste `knownWolves` partagées par `LoupGarouRole` et `PereDesLoupsRole` ; `LoupBlancRole` duplique cette logique indépendamment (camp `NEUTRAL`, ne peut pas hériter de `WolfRole`).
- **Joueurs** : `LGPlayer` (UUID, `alive`, `kills`, `diamonds`, `honor`, `joined`, `role`, `teamOverride`) géré par `PlayerManager`. `getEffectiveTeam()` retourne `teamOverride` si présent, sinon `role.getTeam()` — permet aux Amoureux (et à l'Ange Gardien) de camps différents de changer de camp sans toucher à la classe `Role`.
- **Partie** : `Game` (état `GameState` : `WAITING, SCATTERING, INVINCIBILITY, DAY, NIGHT, MEETUP` — `MEETUP` déclaré mais non utilisé actuellement — + compteurs `startTimeMillis`, `episode`, `pvpEnabled`, `revealed`). `GameManager` détient une seule instance de `Game`, jamais recréée (juste réinitialisée via `resetForNewMatch()`/`markStarted()`/`GameEnder`).
- **Cycle jour/nuit** : `CycleManager`, basé sur `world.getTime()` du monde de partie (`WorldManager.getGameWorld()`, avec repli sur `Bukkit.getWorlds().get(0)` si pas de partie). Les hooks `onDay`/`onNight` des rôles ne s'exécutent que si `game.isRevealed()`. Chaque passage à `DAY` incrémente `game.episode` et déclenche `VoteManager.onEpisodeChange()` + `BountyManager.onEpisodeChange()`.
- **Monde dédié** (`WorldManager`) : à chaque `/lg start`, un **nouveau monde avec un nom unique** (`lg_uhc_<timestamp>`) est créé avec une seed aléatoire (`generateStructures(false)`, mobs hostiles/neutres désactivés via `setSpawnFlags(false, true)`, heure forcée à 0 via `setFullTime(0)` pour toujours démarrer de jour) ; l'ancien monde de partie est déchargé (`unloadWorld`) et son dossier nettoyé au mieux (`cleanupOldWorldFolders`, best-effort, sans bloquer). **Ce nom unique par partie remplace l'ancienne approche (réutiliser/supprimer le même dossier `lg_uhc`)** : sur Windows, les fichiers de région restaient parfois verrouillés juste après `unloadWorld`, ce qui laissait réapparaître des chunks/constructions (et même des maisons de vote dupliquées) de la partie précédente — impossible désormais puisque chaque monde est garanti vierge. Bordure par défaut 1000 blocs (`MIN_BORDER_SIZE` = 100), configurable via `/lg bordure <taille>`. Une recherche en spirale (`findGoodRegionCenter`) évite de centrer la bordure sur un biome interdit (océan, île champignon, glace/neige) — ne garantit pas l'absence totale d'eau ailleurs dans la bordure (terrain varié normal). 4 maisons de vote (voir plus bas) sont construites autour du centre à chaque génération. **Pré-génération asynchrone** (`pregenerateScatterArea`) : juste après la création du monde, tous les chunks du disque où `findScatterLocation` peut piocher (90% du rayon de la bordure, donc plafonnée à la bordure réelle) sont demandés en arrière-plan via `getChunkAtAsync` pour limiter le lag pendant l'exploration ; le temps total est loggé en console (`Bukkit.getLogger().info`) une fois terminé — sert de référence pour choisir la taille de bordure (le nombre de chunks croît au carré du rayon, donc diviser la bordure par 2 divise le temps par ~4). `findScatterLocation` force le chargement du chunk, évite les feuilles d'arbres (`HeightMap.MOTION_BLOCKING_NO_LEAVES`) et retente jusqu'à 10 fois s'il tombe dans l'eau/la lave.
- **Systèmes annexes** (chacun un `Listener` dédié, sans configuration) : `AutoSmeltListener` (minerais bruts auto-fondus au minage), `DiamondCounterListener` (compte les diamants minés par joueur, plafonné à 17, alimente le scoreboard), `PortalBlockListener` (Nether/End désactivés pendant toute partie), `PvpListener` (annule les dégâts PVP tant que `game.isPvpEnabled()` est faux).
- **Scoreboard** (`ScoreboardManager`) : un seul `Scoreboard`/`Objective` est créé **une fois par joueur** (mis en cache dans une `Map<UUID, Scoreboard>`, nettoyée à la déconnexion) et simplement mis à jour chaque seconde, au lieu d'appeler `Bukkit.getScoreboardManager().getNewScoreboard()` à chaque tick — cet appel enregistre un scoreboard à vie côté serveur sans jamais le libérer (piège classique de l'API Bukkit), ce qui causait une fuite mémoire notable sur les parties longues.

## Déroulé complet d'une partie (`GameStarter`)

1. Joueurs s'inscrivent avec `/lg join` (`/lg leave` pour annuler).
2. Host lance `/lg start` (minimum 3 inscrits, sauf `/lg forcestart` en debug qui bypass ce minimum).
3. Rôles assignés, monde généré, joueurs inscrits téléportés (scattering, inventaire vidé + `GameMode.SURVIVAL` forcé + vie/faim/feu réinitialisés avant le tp) + stack de 64 steaks cuits chacun ; les connectés non-inscrits sont téléportés en spectateur dans le même monde. Un message annonce les coordonnées X/Z du centre de la zone (maisons de vote).
4. **Invincibilité 3 minutes** (le temps de laisser la pré-génération asynchrone du monde avancer avant que ce soit réellement dangereux — allongée depuis les 30 secondes d'origine).
5. Partie "vraiment" commencée (`markStarted()`, `DAY`/`NIGHT` selon l'heure du monde).
6. **10 minutes après le vrai début** : révélation des rôles (`revealRoles`) — message d'explication + objets de rôle (arcs/livres, voir table des rôles) + activation immédiate du pouvoir jour/nuit en cours + cœurs bonus (Loup Blanc). **Avant la révélation, tous les pouvoirs de rôle sont bloqués** (`/lg me`, `/lg regle`, `/lg sonder`, `/lg infecter`, `/lg laissermourir`, `/lg soigner`, `/lg empoisonner`, `/lg tirer`, `/lg lier`, `/lg ange`, `/lg regen`, invisibilité Petite Fille, bonus de dégâts du Chasseur, buffs jour/nuit). **Le camp (`Groupe`) n'apparaît dans le scoreboard qu'une fois `game.isRevealed()` vrai** (avant, affiche `-`). `/lg forcereveal` (OP) force la révélation immédiate.
7. **30 minutes après le vrai début** : le PVP s'active (`enablePvp` + `BountyManager.onPvpEnabled` qui distribue le premier contrat du/des Chasseur(s) de Primes). Avant ça, tout dégât normalement mortel soigne le joueur à la place ("Vous avez survécu !") au lieu de le tuer, quelle que soit la cause. `/lg forcepvp` (OP) force l'activation immédiate.
8. **45 minutes après le vrai début** : le vote s'ouvre (`VoteManager.startVoting`, dure 3 épisodes). `/lg forcevote` (OP) force l'ouverture immédiate.
9. Fin de partie (victoire d'un camp, plus aucun survivant, ou `/lg stop`) → `GameEnder.end()` : passe l'état à `WAITING` **avant** de traiter les joueurs (pour que `PlayerDeathListener.onRespawn()` sache renvoyer au lobby), force le respawn immédiat de tout joueur mort (`player.spigot().respawn()`, plus besoin de cliquer "Réapparaître"), nettoie tous les modificateurs de cœurs (`HonorManager`, `AngeManager`, `PoisonManager`, `LoupBlancManager`), **retire tous les effets de potion liés aux rôles** (Force, Vitesse, Absorption, Faiblesse, Régénération, Cécité, Lenteur, Invisibilité — sinon un effet encore actif à la fin de la partie, ex: Force du Loup-Garou en pleine nuit, persistait dans la partie suivante), remet tous les joueurs en survie au spawn du lobby (`LobbySpawnManager.getSpawn()`), reset les stats (`LGPlayer.resetStats()`), le `LoveManager`, le `VoteManager`, la `CorruptionManager`, force **`DeathManager.resetAll()`** (annule toute agonie/offre en cours et restaure les joueurs concernés — sinon un joueur encore marqué "en train de mourir" à la fin de la partie restait bloqué indéfiniment : plus de dégâts reçus/infligés, déplacement figé, jusqu'à une reconnexion) et libère la référence au monde de partie (`WorldManager.clearGameWorld()`, sinon le scoreboard affichait encore l'ancienne bordure), diffuse le message, repasse l'état à `WAITING`.

Toutes les tâches différées (révélation, PVP, vote, invincibilité) se protègent contre un `/lg stop` ou un redémarrage entre-temps via un contrôle `game.getStartTimeMillis() == startedAt` et/ou `game.getState()`.

## Système de mort en deux temps (`DeathManager` + `LethalDamageListener` + `PlayerDeathListener` + `AgonyListener`)

1. Un coup normalement mortel est annulé (`EntityDamageEvent` cancel), le joueur devient invulnérable, reçoit Cécité + Lenteur + Invisibilité (particules visibles pour Cécité/Lenteur, pas pour l'invisibilité) et un titre "Vous agonisez...". Son armure et son objet en main sont retirés (stockés en mémoire dans `DeathManager`, remis en place à la fin de l'agonie) pour éviter l'effet vanilla d'armure/objet qui flotte visiblement malgré l'invisibilité. **Note** : en vue 3ᵉ personne, le joueur agonisant voit toujours son propre corps semi-transparent avec son ancien équipement — c'est une exception vanilla qui ne s'applique qu'à l'auto-visualisation, pas à ce que voient les autres joueurs/spectateurs (donc pas un bug si constaté en solo).
2. `AgonyListener` immobilise complètement le joueur (annule `PlayerMoveEvent`, translation bloquée, rotation caméra autorisée) et annule tout dégât qu'il tenterait d'infliger (`EntityDamageByEntityEvent` si l'attaquant est en train d'agoniser).
3. **15 secondes plus tard** (`finalizeDeath`) : si le PVP n'est pas encore activé, le joueur est soigné à la place ("Vous avez survécu !"). Sinon, avant de tuer réellement, deux offres peuvent intercepter la mort (mutuellement exclusives, voir sections dédiées) :
   - Si la victime est corrompue à 100% par les loups et que son tueur est un Loup-Garou/Père des Loups → offre d'infection envoyée au Père des Loups vivant (10s).
   - Sinon, si une Sorcière vivante a encore sa potion de vie → offre de soin qui lui est envoyée (10s).
   - Sans offre applicable (ou offre refusée/expirée) : mort réelle (`setHealth(0)` → `PlayerDeathEvent` → `PlayerDeathListener` marque mort, crédite le kill, révèle le rôle dans le message, force le respawn dans le monde de partie en cours (`gameWorld.getSpawnLocation()`, pas le monde lobby) en spectateur, déclenche `LoveManager.handleDeath()` puis `VictoryChecker.check()`).
4. `DeathManager.killInstantly()` : mort immédiate créditée sans passer par l'agonie (utilisé par le chagrin d'amour des Amoureux liés).
5. `DeathManager.applyDamage(player, source, amount)` : réduit la vie **actuelle** d'un montant fixe (peut tuer si la vie restante tombe à 0, alors traité comme une mort réelle classique) — utilisé par `/lg tirer` du Chasseur (6 cœurs / 12 PV).
6. `DeathManager.revive()` : annule une mort programmée (ou une offre en cours) et restaure l'équipement — utilisé par la potion de vie de la Sorcière et l'acceptation d'infection du Père des Loups.

### Corruption des loups et infection passive du Père des Loups (`CorruptionManager`, remplace l'ancien `/lg infecter` à usage unique)

- Chaque seconde (`CorruptionManager`, uniquement si `game.isRevealed()` et partie en `DAY`/`NIGHT`), tout joueur vivant qui n'est **pas** du camp `LOUP` accumule de la corruption (0-100%, jamais décroissante) en fonction des loups vivants à moins de 6 blocs : **Père des Loups** = +1%/seconde, **Loup-Garou** classique = +1%/5 secondes, cumulable si plusieurs loups sont proches simultanément. Le **Loup Blanc** ne participe pas (solo, hors meute). La victime ne reçoit **aucun** message/indicateur de sa progression (mécanisme silencieux).
- Quand un joueur corrompu à 100% subirait une mort réelle **et** que son tueur (`DeathManager.getPendingKiller`) est un `WolfRole` (Loup-Garou ou Père des Loups, pas Loup Blanc), sa mort est suspendue : si un Père des Loups est vivant, il reçoit un message cliquable dans le chat ("[Infecter]" / "[Laisser mourir]", `net.md_5.bungee.api.chat.TextComponent`/`ClickEvent`) pendant 10 secondes. `/lg infecter <joueur>` transforme la victime en `LoupGarouRole` (rejoint la liste `knownWolves` de la meute et réciproquement, corruption réinitialisée pour ce joueur) ; `/lg laissermourir <joueur>` ou l'absence de réponse en 10s finalise la mort réelle. Si aucun Père des Loups n'est vivant, la victime meurt normalement sans offre.

## Rôles implémentés (11)

| Rôle | Team | Pouvoir |
|---|---|---|
| Villageois | VILLAGE | Aucun |
| Loup-Garou | LOUP | Force I la nuit. Corrompt les joueurs proches (1%/5s, voir Corruption). Reçoit Speed I + Absorption I (2♥, 1 min) après un kill — **sauf** en tuant le Chasseur. Bonus de dégâts du Chasseur contre lui (voir Chasseur) |
| Père des Loups | LOUP | Comme Loup-Garou, mais corrompt 5x plus vite (1%/s) et reçoit l'offre d'infection quand une victime corrompue à 100% meurt d'un loup (voir Corruption) |
| Petite Fille | VILLAGE | Invisibilité 5 min en retirant toute son armure la nuit (1x/nuit) |
| Voyante | VILLAGE | `/lg sonder <joueur>` révèle rôle+équipe, jour ou nuit (1x/cycle) |
| Sorcière | VILLAGE | Potion de vie : reçoit une offre cliquable (10s) à la fin de l'agonie de tout joueur non éligible à l'infection, `/lg soigner <joueur>` la sauve (1x/partie, +1 honneur, peut se sauver elle-même) ; Potion de mort : `/lg empoisonner <joueur>` retire 2 cœurs de vie maximum **définitivement** (1x/partie, ne tue plus instantanément) |
| Chasseur | VILLAGE | Reçoit un arc Puissance IV + flèches. Bonus de dégâts au corps-à-corps contre le camp Loups-Garous : Force 0.5 (+1.5 dégâts), +0.1 par Loup tué, plafonné à Force I (+3 dégâts) — n'affecte pas les tirs à l'arc. `/lg tirer <joueur>` pendant sa propre agonie (15s) fait perdre 6 cœurs à une cible autre que son propre tueur (1x/partie). Si tué par un Loup-Garou/Père des Loups, celui-ci ne reçoit pas son bonus Speed/Absorption habituel |
| Cupidon | VILLAGE (dynamique) | Spawn avec arc + livre Puissance III + Punch I + 64 flèches. `/lg lier <joueur1> <joueur2>` (1x/partie) : si l'un meurt (n'importe quelle cause), l'autre meurt de chagrin (mort instantanée) — garantit qu'ils ne peuvent gagner qu'ensemble. **Le camp AMOUREUX se forme systématiquement**, peu importe que les deux liés soient du même camp ou de camps opposés (avant, seuls les camps opposés formaient ce camp) ; le Cupidon rejoint aussi ce camp dès le lien, mais n'a pas besoin de survivre pour que les deux amoureux gagnent ; si les deux amoureux meurent tous les deux, le Cupidon redevient Villageois |
| Chasseur de Primes | NEUTRAL (solo) | `BountyManager` lui attribue un contrat secret (cible aléatoire vivante) à l'activation du PVP, puis un second à l'épisode suivant la résolution du premier (succès ou annulation si la cible meurt d'une autre main). Éliminer sa cible en cours donne du matériel (arc Puissance IV puis bottes Chute Amortie III) et marque le contrat rempli. Gagne seul, doit éliminer tout le monde |
| Loup Blanc | NEUTRAL (solo) | Force I la nuit comme un loup, connaît l'identité des Loups-Garous/Père des Loups (mais doit tous les éliminer, eux compris, pour gagner). Obtient 15 cœurs de vie (`LoupBlancManager`) à la révélation des rôles. Gagne seul |
| Ange | NEUTRAL (solo) | Choisit sa forme via `/lg ange <dechu\|gardien>` une fois les rôles révélés (cible/protégé aléatoire assigné). **Déchu** : 12♥, doit gagner seul, passe à 15♥ en tuant sa cible (kill crédité). **Gardien** : 15♥, rejoint le camp de son protégé (`teamOverride`, gagne avec lui), passe à 12♥ + Faiblesse I permanente si le protégé meurt (redevient solitaire) ; `/lg regen` (1x/partie) donne Régénération I 1 min au protégé s'il passe sous 4♥ |

Tout rôle `NEUTRAL` (sauf Cupidon, qui a son propre arc/livre) reçoit automatiquement un livre Tranchant IV à la révélation (`GameStarter.giveRoleItems`).

Camps (`RoleTeam`) : `VILLAGE`, `LOUP`, `NEUTRAL` (rôles solo : Chasseur de Primes, Loup Blanc, Ange — chacun gagne seul, sauf exception ci-dessous), `AMOUREUX`. Un solo ne gagne que s'il est l'**unique** survivant (`VictoryChecker`, `soloAlive == 1`), même s'il ne reste que des solos entre eux à la fin.

## Spawn du lobby (`LobbySpawnManager`)

Le spawn où les joueurs sont renvoyés en fin de partie (victoire ou `/lg stop`) n'est plus deviné à partir du monde par défaut (`world.getSpawnLocation()` s'est révélé peu fiable chez l'utilisateur — Y en dur à la bedrock, ou coordonnées codées en dur jamais bonnes malgré plusieurs tentatives via F3). À la place : `/lg lobbyspawn` (OP) sauvegarde la position exacte du joueur qui l'exécute (monde + XYZ + yaw/pitch) dans `config.yml`, persistant entre redémarrages du serveur. `GameEnder` et `WorldManager.prepareGameWorld()` (pour les joueurs restés dans l'ancien `lg_uhc`) utilisent tous les deux `LobbySpawnManager.getSpawn()`. **Tant que `/lg lobbyspawn` n'a jamais été exécuté**, ça retombe sur `Bukkit.getWorlds().get(0).getSpawnLocation()` (l'ancien comportement, potentiellement peu fiable). Le respawn forcé en fin de partie (`GameEnder`) et `PlayerDeathListener.onRespawn()` (qui priorise `game.getState() == WAITING` avant de checker `lgPlayer.isAlive()`) éliminent le besoin de cliquer manuellement "Réapparaître".

## Système d'honneur (`HonorManager`)

- Jauge **individuelle** par joueur (`LGPlayer.honor`, -3 à +3, clampée dans `setHonor()`).
- `HonorManager.gainHonor(lgPlayer, player)` / `loseHonor(lgPlayer, player)` : font varier la jauge d'1 point et recalculent l'effet de cœur.
- Effet aux extrêmes, basé sur `getEffectiveTeam()` : à **+3**, un Villageois gagne un cœur (+2 PV max) et un Loup en perd un (-2 PV max) ; à **-3** c'est l'inverse (Villageois puni, Loup récompensé). Implémenté via un `AttributeModifier` nommé (`NamespacedKey "honor_hearts"`) sur `Attribute.MAX_HEALTH`, recalculé à chaque changement (donc réversible si l'honneur repasse sous le seuil). Aucun effet pour les Amoureux/solos.
- **Déclencheurs actuellement branchés** : +1 honneur en votant (`VoteManager.resolveRound`), -1 en ne votant pas sans passer explicitement (`/lg` ne rien faire pendant le vote), +1 pour la Sorcière qui soigne quelqu'un (`SoignerSubCommand`), -1 pour un kill sur un joueur du **même camp effectif** (trahison/tir ami, `PlayerDeathListener`).
- `GameEnder.end()` nettoie le modificateur (`HonorManager.clearModifier()`) et `LGPlayer.resetStats()` remet l'honneur à 0 en fin de partie.
- `/lg honneur <joueur> [valeur]` (debug, OP) : sans valeur affiche l'honneur actuel, avec une valeur (-3 à 3) le règle directement via `HonorManager.setHonor()` et recalcule immédiatement l'effet de cœur.

## Système de vote (`VoteManager` + maisons de vote `WorldManager` + `VoteListener`)

- 4 maisons (enclume + jukebox) sont construites automatiquement autour du centre de la bordure à chaque génération de monde (`WorldManager.buildVoteHouses`, rayon 150 blocs, intérieur explicitement vidé à la construction pour ne pas piéger de feuillage/arbre déjà présent). Casser **ou poser** un bloc à l'intérieur d'une maison de vote est bloqué (`VoteListener.onBlockBreak`/`onBlockPlace`) dès le `SCATTERING` et jusqu'à la fin de la partie (pas seulement en `DAY`/`NIGHT`, sinon les maisons étaient destructibles pendant le scattering/l'invincibilité).
- Le vote s'ouvre 45 minutes après le vrai début (`VoteManager.startVoting`, ou `/lg forcevote` en debug) et dure 3 épisodes (`ROUND_COUNT`). Interagir avec un jukebox de vote pendant que le vote est actif ouvre une GUI (`VoteInventoryHolder`) listant les joueurs vivants (têtes de joueur) + un item "Passer".
- À chaque passage au jour (`CycleManager` → `VoteManager.onEpisodeChange`), le round est résolu (`resolveRound`) : les votes des Village/Loups vivants sont comptés, honneur ajusté (voir section Honneur), et le joueur le plus désigné est annoncé publiquement avec son vrai rôle mélangé parmi 2 leurres (`ALL_ROLE_NAMES`, pas de mort ni bannissement — juste de l'information). Après 3 rounds, le vote se referme automatiquement.

## Chasseur de Primes et contrats (`BountyManager`, voir aussi table des rôles)

- `BountyManager.onPvpEnabled` distribue un premier contrat (cible aléatoire vivante, hors lui-même) à chaque Chasseur de Primes vivant dès l'activation du PVP. `onEpisodeChange` distribue le second contrat à l'épisode suivant la résolution du premier (`ChasseurDePrimesRole.isSecondContractDue`).
- Si la cible du contrat en cours meurt d'une autre main (PVP, chute...), `PlayerDeathListener.cancelContractsOnOthers` annule le contrat (message au Chasseur de Primes) ; le suivant arrive au prochain épisode comme pour un succès.

## Conditions de victoire (`VictoryChecker`)

Basées sur `LGPlayer.getEffectiveTeam()` (pas `role.getTeam()` directement, pour prendre en compte les Amoureux/Ange Gardien), vérifiées après chaque mort :
- **Village** gagne si Loups + Solos + Amoureux = 0 et Village > 0.
- **Loups** gagnent si Village + Solos + Amoureux = 0 et Loups > 0.
- **Amoureux** gagnent s'ils sont > 0 et que Loups + Village + Solos = 0.
- **Un solitaire** gagne s'il est **seul** solo restant (`soloAlive == 1`) et que tous les autres camps sont à 0.
- Si plus aucun camp n'a de survivant (cas rare, ex: dernier solo qui meurt), la partie se termine sans vainqueur plutôt que de rester bloquée.

## Commandes

Voir [GUIDE_TEST.md](GUIDE_TEST.md) pour la liste complète à jour des commandes et la checklist de ce qui a été testé ou non en jeu réel.

## Idées / étapes pas encore commencées

- Autres rôles potentiels du LG UHC de TheGuill (Ancien/Doyen, Idiot du Village, Bouc émissaire, Salvateur, etc.) — proposés puis non retenus pour l'instant.
- Le résumé initial du projet (avant l'intervention de Claude) est dans `resume-projet-loupgarou.md` côté utilisateur (hors repo) — contient l'historique ChatGPT → Claude jusqu'à l'étape 10.
