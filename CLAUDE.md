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

- **Pattern Manager** : chaque système implémente `Manager` (`enable()`/`disable()`), enregistré dans `ManagerRegistry` au démarrage (`LoupGarouPlugin.onEnable()`). Managers actuels : `GameManager`, `WorldManager`, `PlayerManager`, `RoleManager`, `CycleManager`, `ScoreboardManager`, `DeathManager`, `LoveManager`.
- **Commandes** : `/lg <sous-commande>` via `LGCommand`, qui route vers des classes `SubCommand` (interface `getName()`/`getDescription()`/`execute()`). Les commandes de debug étendent `DebugSubCommand` (abstraite, vérifie `sender.isOp()` avant d'exécuter).
- **Rôles** : classe abstraite `Role` (nom, `RoleTeam`, hooks `onGameStart`, `onDeath`, `onDay`, `onNight`, `getInstructions()` pour le texte d'explication envoyé au joueur, `sendInstructions()`). `RoleFactory` = registre statique nom→constructeur. `RoleManager` gère le pool de rôles configuré (`/lg role add/remove/list/clear`) et les distribue aléatoirement (`assignRoles`), en complétant avec des Villageois.
- **Joueurs** : `LGPlayer` (UUID, `alive`, `kills`, `diamonds`, `joined`, `role`, `teamOverride`) géré par `PlayerManager`. `getEffectiveTeam()` retourne `teamOverride` si présent, sinon `role.getTeam()` — permet aux Amoureux de camps opposés de changer de camp sans toucher à la classe `Role`.
- **Partie** : `Game` (état `GameState` : `WAITING, SCATTERING, INVINCIBILITY, DAY, NIGHT, MEETUP` + compteurs `startTimeMillis`, `episode`, `pvpEnabled`, `revealed`). `GameManager` détient une seule instance de `Game`, jamais recréée (juste réinitialisée via `resetForNewMatch()`/`markStarted()`/`GameEnder`).
- **Cycle jour/nuit** : `CycleManager`, basé sur `world.getTime()` du monde de partie (`WorldManager.getGameWorld()`, avec repli sur `Bukkit.getWorlds().get(0)` si pas de partie). Les hooks `onDay`/`onNight` des rôles ne s'exécutent que si `game.isRevealed()`.
- **Monde dédié** (`WorldManager`) : à chaque `/lg start`, le monde `lg_uhc` est supprimé et régénéré avec une seed aléatoire (`generateStructures(false)`, mobs hostiles/neutres désactivés via `setSpawnFlags(false, true)`). Une recherche en spirale (`findGoodRegionCenter`) évite de centrer la bordure sur un biome interdit (océan, île champignon, glace/neige). Bordure configurable via `/lg bordure <taille>`.

## Déroulé complet d'une partie (`GameStarter`)

1. Joueurs s'inscrivent avec `/lg join` (`/lg leave` pour annuler).
2. Host lance `/lg start` (minimum 3 inscrits, sauf `/lg forcestart` en debug qui bypass ce minimum).
3. Rôles assignés, monde généré, joueurs inscrits téléportés (scattering) + stack de 64 steaks cuits chacun ; les connectés non-inscrits sont téléportés en spectateur dans le même monde.
4. **Invincibilité** 30 secondes.
5. Partie "vraiment" commencée (`markStarted()`, `DAY`/`NIGHT` selon l'heure du monde).
6. **10 minutes après le vrai début** : révélation des rôles (`revealRoles`) — message d'explication + activation immédiate du pouvoir jour/nuit en cours. **Avant la révélation, tous les pouvoirs de rôle sont bloqués** (`/lg me`, `/lg regle`, `/lg sonder`, `/lg infecter`, `/lg soigner`, `/lg empoisonner`, `/lg tirer`, `/lg lier`, invisibilité Petite Fille, buffs jour/nuit). `/lg forcereveal` (OP) force la révélation immédiate.
7. **30 minutes après le vrai début** : le PVP s'active (`enablePvp`). Avant ça, tout dégât normalement mortel soigne le joueur à la place ("Vous avez survécu !") au lieu de le tuer, quelle que soit la cause. `/lg forcepvp` (OP) force l'activation immédiate.
8. Fin de partie (victoire d'un camp ou `/lg stop`) → `GameEnder.end()` : diffuse le message, remet tous les joueurs en survie au spawn du monde précédent (`Bukkit.getWorlds().get(0)`), reset les stats (`LGPlayer.resetStats()`) et le `LoveManager`, repasse l'état à `WAITING`.

Toutes les tâches différées (révélation, PVP, invincibilité) se protègent contre un `/lg stop` ou un redémarrage entre-temps via un contrôle `game.getStartTimeMillis() == startedAt` et/ou `game.getState()`.

## Système de mort en deux temps (`DeathManager` + `LethalDamageListener` + `PlayerDeathListener`)

1. Un coup normalement mortel est annulé (`EntityDamageEvent` cancel), le joueur devient invulnérable, reçoit Cécité + Lenteur (particules visibles) et un titre "Vous agonisez...".
2. 1 minute plus tard (`finalizeDeath`) : si le PVP n'est pas encore activé, le joueur est soigné à la place ; sinon mort réelle (`setHealth(0)` → `PlayerDeathEvent` → `PlayerDeathListener` marque mort, crédite le kill, révèle le rôle dans le message, passe en spectateur au respawn, déclenche `LoveManager.handleDeath()` puis `VictoryChecker.check()`).
3. `DeathManager.killInstantly()` : mort immédiate créditée sans le délai d'1 minute (utilisé par la potion de mort de la Sorcière et le tir du Chasseur — ces pouvoirs restent des morts définitives même avant l'activation du PVP, contrairement aux morts "normales").
4. `DeathManager.revive()` : annule une mort programmée (utilisé par la potion de vie de la Sorcière et l'infection du Père des Loups).

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

## Conditions de victoire (`VictoryChecker`)

Basées sur `LGPlayer.getEffectiveTeam()` (pas `role.getTeam()` directement, pour prendre en compte les Amoureux) :
- **Village** gagne si Loups + Solos + Amoureux = 0 et Village > 0.
- **Loups** gagnent si Village + Solos + Amoureux = 0 et Loups > 0.
- **Amoureux** gagnent s'ils sont > 0 et que Loups + Village + Solos = 0.

## Commandes

Voir [GUIDE_TEST.md](GUIDE_TEST.md) pour la liste complète à jour des commandes et la checklist de ce qui a été testé ou non en jeu réel.

## Idées / étapes pas encore commencées

- Autres rôles potentiels du LG UHC de TheGuill (Ancien/Doyen, Idiot du Village, Bouc émissaire, Salvateur, etc.) — proposés puis non retenus pour l'instant, seul Cupidon a été demandé jusqu'ici.
- Rôle solo/NEUTRAL concret (aucun n'existe encore, seule l'infrastructure de comptage existe dans `VictoryChecker`).
- Le résumé initial du projet (avant l'intervention de Claude) est dans `resume-projet-loupgarou.md` côté utilisateur (hors repo) — contient l'historique ChatGPT → Claude jusqu'à l'étape 10.
