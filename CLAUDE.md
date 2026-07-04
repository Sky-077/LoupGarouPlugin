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

- **Pattern Manager** : chaque système implémente `Manager` (`enable()`/`disable()`), enregistré dans `ManagerRegistry` au démarrage (`LoupGarouPlugin.onEnable()`). Managers actuels : `GameManager`, `WorldManager`, `PlayerManager`, `RoleManager`, `CycleManager`, `ScoreboardManager`, `DeathManager`, `LoveManager`, `LobbySpawnManager`.
- **Commandes** : `/lg <sous-commande>` via `LGCommand`, qui route vers des classes `SubCommand` (interface `getName()`/`getDescription()`/`execute()`). Les commandes de debug étendent `DebugSubCommand` (abstraite, vérifie `sender.isOp()` avant d'exécuter).
- **Rôles** : classe abstraite `Role` (nom, `RoleTeam`, hooks `onGameStart`, `onDeath`, `onDay`, `onNight`, `getInstructions()` pour le texte d'explication envoyé au joueur, `sendInstructions()`). `RoleFactory` = registre statique nom→constructeur. `RoleManager` gère le pool de rôles configuré (`/lg role add/remove/list/clear`) et les distribue aléatoirement (`assignRoles`), en complétant avec des Villageois.
- **Joueurs** : `LGPlayer` (UUID, `alive`, `kills`, `diamonds`, `joined`, `role`, `teamOverride`) géré par `PlayerManager`. `getEffectiveTeam()` retourne `teamOverride` si présent, sinon `role.getTeam()` — permet aux Amoureux de camps opposés de changer de camp sans toucher à la classe `Role`.
- **Partie** : `Game` (état `GameState` : `WAITING, SCATTERING, INVINCIBILITY, DAY, NIGHT, MEETUP` + compteurs `startTimeMillis`, `episode`, `pvpEnabled`, `revealed`). `GameManager` détient une seule instance de `Game`, jamais recréée (juste réinitialisée via `resetForNewMatch()`/`markStarted()`/`GameEnder`).
- **Cycle jour/nuit** : `CycleManager`, basé sur `world.getTime()` du monde de partie (`WorldManager.getGameWorld()`, avec repli sur `Bukkit.getWorlds().get(0)` si pas de partie). Les hooks `onDay`/`onNight` des rôles ne s'exécutent que si `game.isRevealed()`.
- **Monde dédié** (`WorldManager`) : à chaque `/lg start`, le monde `lg_uhc` est supprimé et régénéré avec une seed aléatoire (`generateStructures(false)`, mobs hostiles/neutres désactivés via `setSpawnFlags(false, true)`, heure forcée à 0 via `setFullTime(0)` pour toujours démarrer de jour). Une recherche en spirale (`findGoodRegionCenter`) évite de centrer la bordure sur un biome interdit (océan, île champignon, glace/neige). Bordure configurable via `/lg bordure <taille>`. La suppression du dossier monde avant régénération (`deleteWorldFolderWithRetry`) retente jusqu'à 10 fois (100ms d'écart) car sur Windows les fichiers de région restent parfois verrouillés juste après `unloadWorld`, ce qui sinon laissait réapparaître des chunks/constructions de la partie précédente.

## Déroulé complet d'une partie (`GameStarter`)

1. Joueurs s'inscrivent avec `/lg join` (`/lg leave` pour annuler).
2. Host lance `/lg start` (minimum 3 inscrits, sauf `/lg forcestart` en debug qui bypass ce minimum).
3. Rôles assignés, monde généré, joueurs inscrits téléportés (scattering, inventaire vidé + `GameMode.SURVIVAL` forcé avant le tp) + stack de 64 steaks cuits chacun ; les connectés non-inscrits sont téléportés en spectateur dans le même monde.
4. **Invincibilité** 30 secondes.
5. Partie "vraiment" commencée (`markStarted()`, `DAY`/`NIGHT` selon l'heure du monde).
6. **10 minutes après le vrai début** : révélation des rôles (`revealRoles`) — message d'explication + activation immédiate du pouvoir jour/nuit en cours. **Avant la révélation, tous les pouvoirs de rôle sont bloqués** (`/lg me`, `/lg regle`, `/lg sonder`, `/lg infecter`, `/lg soigner`, `/lg empoisonner`, `/lg tirer`, `/lg lier`, invisibilité Petite Fille, buffs jour/nuit). **Le camp (`Groupe`) n'apparaît dans le scoreboard qu'une fois `game.isRevealed()` vrai** (avant, affiche `-`). `/lg forcereveal` (OP) force la révélation immédiate.
7. **30 minutes après le vrai début** : le PVP s'active (`enablePvp`). Avant ça, tout dégât normalement mortel soigne le joueur à la place ("Vous avez survécu !") au lieu de le tuer, quelle que soit la cause. `/lg forcepvp` (OP) force l'activation immédiate.
8. Fin de partie (victoire d'un camp, plus aucun survivant, ou `/lg stop`) → `GameEnder.end()` : diffuse le message, remet tous les joueurs en survie au spawn du lobby (`LobbySpawnManager.getSpawn()`), reset les stats (`LGPlayer.resetStats()`) et le `LoveManager`, repasse l'état à `WAITING`.

Toutes les tâches différées (révélation, PVP, invincibilité) se protègent contre un `/lg stop` ou un redémarrage entre-temps via un contrôle `game.getStartTimeMillis() == startedAt` et/ou `game.getState()`.

## Système de mort en deux temps (`DeathManager` + `LethalDamageListener` + `PlayerDeathListener` + `AgonyListener`)

1. Un coup normalement mortel est annulé (`EntityDamageEvent` cancel), le joueur devient invulnérable, reçoit Cécité + Lenteur + Invisibilité (particules visibles pour Cécité/Lenteur, pas pour l'invisibilité) et un titre "Vous agonisez...". Son armure et son objet en main sont retirés (stockés en mémoire dans `DeathManager`, remis en place à la fin de l'agonie) pour éviter l'effet vanilla d'armure/objet qui flotte visiblement malgré l'invisibilité. **Note** : en vue 3ᵉ personne, le joueur agonisant voit toujours son propre corps semi-transparent avec son ancien équipement — c'est une exception vanilla qui ne s'applique qu'à l'auto-visualisation, pas à ce que voient les autres joueurs/spectateurs (donc pas un bug si constaté en solo).
2. `AgonyListener` immobilise complètement le joueur (annule `PlayerMoveEvent`, translation bloquée, rotation caméra autorisée) et annule tout dégât qu'il tenterait d'infliger (`EntityDamageByEntityEvent` si l'attaquant est en train d'agoniser).
3. 1 minute plus tard (`finalizeDeath`) : équipement restauré, et si le PVP n'est pas encore activé, le joueur est soigné à la place ; sinon mort réelle (`setHealth(0)` → `PlayerDeathEvent` → `PlayerDeathListener` marque mort, crédite le kill, révèle le rôle dans le message, force le respawn dans le monde de partie en cours (`gameWorld.getSpawnLocation()`, pas le monde lobby) en spectateur, déclenche `LoveManager.handleDeath()` puis `VictoryChecker.check()`).
4. `DeathManager.killInstantly()` : mort immédiate créditée sans le délai d'1 minute (utilisé par la potion de mort de la Sorcière et le tir du Chasseur — ces pouvoirs restent des morts définitives même avant l'activation du PVP, contrairement aux morts "normales").
5. `DeathManager.revive()` : annule une mort programmée et restaure l'équipement (utilisé par la potion de vie de la Sorcière et l'infection du Père des Loups).

## Rôles implémentés (8)

| Rôle | Team | Pouvoir |
|---|---|---|
| Villageois | VILLAGE | Aucun |
| Loup-Garou | LOUP | Force I la nuit |
| Père des Loups | LOUP | Force I + `/lg infecter <joueur>` (1x/partie) transforme sa victime en Loup-Garou au lieu de la tuer |
| Petite Fille | VILLAGE | Invisibilité 5 min en retirant toute son armure la nuit (1x/nuit) |
| Voyante | VILLAGE | `/lg sonder <joueur>` révèle rôle+équipe, jour ou nuit (1x/cycle) |
| Sorcière | VILLAGE | `/lg soigner <joueur>` (revive, 1x/partie) + `/lg empoisonner <joueur>` (kill instantané, 1x/partie) — peut se soigner elle-même |
| Chasseur | VILLAGE | `/lg tirer <joueur>` pendant sa propre agonie (kill instantané, 1x/partie) |
| Cupidon | VILLAGE (dynamique) | Spawn avec arc Puissance V + 64 flèches. `/lg lier <joueur1> <joueur2>` (1x/partie) : si l'un meurt, l'autre meurt de chagrin. Si camps opposés, les deux amoureux + Cupidon forment le camp **AMOUREUX** (gagne en étant le dernier camp survivant) ; si les deux amoureux meurent, Cupidon redevient Villageois |

Camps (`RoleTeam`) : `VILLAGE`, `LOUP`, `NEUTRAL` (solo, prévu mais aucun rôle ne l'utilise encore), `AMOUREUX`.

## Spawn du lobby (`LobbySpawnManager`)

Le spawn où les joueurs sont renvoyés en fin de partie (victoire ou `/lg stop`) n'est plus deviné à partir du monde par défaut (`world.getSpawnLocation()` s'est révélé peu fiable chez l'utilisateur — Y en dur à la bedrock, ou coordonnées codées en dur jamais bonnes malgré plusieurs tentatives via F3). À la place : `/lg lobbyspawn` (OP) sauvegarde la position exacte du joueur qui l'exécute (monde + XYZ + yaw/pitch) dans `config.yml`, persistant entre redémarrages du serveur. `GameEnder` et `WorldManager.prepareGameWorld()` (pour les joueurs restés dans l'ancien `lg_uhc`) utilisent tous les deux `LobbySpawnManager.getSpawn()`. **Tant que `/lg lobbyspawn` n'a jamais été exécuté**, ça retombe sur `Bukkit.getWorlds().get(0).getSpawnLocation()` (l'ancien comportement, potentiellement peu fiable) — **penser à demander à l'utilisateur de lancer `/lg lobbyspawn` une fois depuis un endroit sûr avant de considérer ce bug définitivement clos**.

## Conditions de victoire (`VictoryChecker`)

Basées sur `LGPlayer.getEffectiveTeam()` (pas `role.getTeam()` directement, pour prendre en compte les Amoureux) :
- **Village** gagne si Loups + Solos + Amoureux = 0 et Village > 0.
- **Loups** gagnent si Village + Solos + Amoureux = 0 et Loups > 0.
- **Amoureux** gagnent s'ils sont > 0 et que Loups + Village + Solos = 0.

## Commandes

Voir [GUIDE_TEST.md](GUIDE_TEST.md) pour la liste complète à jour des commandes et la checklist de ce qui a été testé ou non en jeu réel.

## État des tests solo (session du 2026-07-04) — à reprendre

L'utilisateur a testé seul (via `/lg forcestart` avec 1 joueur) et remonté une série de bugs, la plupart corrigés dans cette session :

**Corrigés et validés (ou en attente de confirmation) :**
- Le scoreboard affichait le camp (`Groupe`) avant la révélation des rôles → corrigé (gate sur `game.isRevealed()`).
- `VictoryChecker` ne gérait pas le cas où plus aucun joueur n'est vivant dans aucun camp (ex: solo qui meurt) → la partie restait bloquée indéfiniment → ajouté un 4ᵉ cas "Plus aucun survivant, partie terminée sans vainqueur".
- Au scattering, l'inventaire n'était pas vidé et le mode de jeu pas forcé en survie → corrigé dans `GameStarter`.
- Pendant l'agonie : ajout invisibilité + armure/main cachées + immobilisation totale + impossibilité d'infliger des dégâts (voir section "Système de mort en deux temps").
- Le respawn après une mort en partie utilisait le spawn par défaut du monde (potentiellement le lit/spawn du monde lobby, cassé chez l'utilisateur) au lieu de rester dans le monde de partie → corrigé (`event.setRespawnLocation(gameWorld.getSpawnLocation())` dans `PlayerDeathListener`).
- Le monde de partie démarrait parfois de nuit selon le temps réel écoulé pendant les tests → l'heure est maintenant forcée à 0 (jour) à la génération (`world.setFullTime(0)`), décision confirmée par l'utilisateur.
- Suppression de l'ancien monde `lg_uhc` qui échouait silencieusement sur Windows (fichiers de région verrouillés juste après `unloadWorld`), laissant réapparaître d'anciennes constructions/chunks → ajout d'un retry (`deleteWorldFolderWithRetry`, 10 tentatives, 100ms d'écart, log d'avertissement si échec persistant).

**Spawn au lobby en fin de partie — changement d'approche (2026-07-04) :**
- Deviner des coordonnées (spawn brut du monde, recherche de terrain, coordonnées codées en dur via F3) s'est révélé infructueux après 4 tentatives successives (voir historique dans l'ancien commit si besoin).
- Remplacé par une commande `/lg lobbyspawn` (voir section "Spawn du lobby" plus haut) : l'utilisateur définit lui-même la position exacte en se plaçant dessus, plus de devinette.
- **À vérifier à la prochaine session** : demander à l'utilisateur d'exécuter `/lg lobbyspawn` une fois depuis un endroit sûr, puis de refaire un test de fin de partie pour confirmer que ça atterrit au bon endroit.

## Idées / étapes pas encore commencées

- Autres rôles potentiels du LG UHC de TheGuill (Ancien/Doyen, Idiot du Village, Bouc émissaire, Salvateur, etc.) — proposés puis non retenus pour l'instant, seul Cupidon a été demandé jusqu'ici.
- Rôle solo/NEUTRAL concret (aucun n'existe encore, seule l'infrastructure de comptage existe dans `VictoryChecker`).
- Le résumé initial du projet (avant l'intervention de Claude) est dans `resume-projet-loupgarou.md` côté utilisateur (hors repo) — contient l'historique ChatGPT → Claude jusqu'à l'étape 10.
