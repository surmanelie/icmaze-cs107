# ICMaze — CS-107 Mini-projet 2

Jeu de type *dungeon crawler* en Java, réalisé dans le cadre du cours de programmation orientée objet CS-107 à l'EPFL (mini-projet 2, 2025), en binôme.

Le joueur explore une série de labyrinthes générés, doit survivre à des ennemis, résoudre des énigmes à base de leviers et de clés, puis affronter un boss final avant de pouvoir revenir récupérer les richesses laissées derrière lui.

**Auteurs**
- Danny Levy
- Elie Menasche Reuben Surman

## Structure du projet

Le projet est un multi-module Maven :

- **`game-engine/`** — le moteur de jeu 2D fourni par le cours (gestion des zones, des acteurs, des collisions, de la boucle de jeu, du rendu). Ce module est la base commune fournie à tous les étudiants, pas notre travail.
- **`icmaze/`** — notre jeu, développé au-dessus du moteur : logique de gameplay, ennemis, objets, zones, interface.
- **`tutos/`** — tutoriels d'introduction au moteur, fournis par le cours.

## Le jeu

ICMaze est un jeu de labyrinthe où le joueur doit :
- Explorer des zones générées et collecter des ressources (pièces, cœurs, objets).
- Combattre des ennemis (LogMonster) à l'aide d'une pioche, qui sert aussi à casser des rochers bloquant le passage.
- Résoudre des énigmes à base de signaux logiques et de clés pour ouvrir des portails verrouillés.
- Affronter un boss final accompagné de lieutenants, avant de pouvoir revenir en arrière pour récupérer ce qui a été laissé de côté.

**Contrôles**
- Déplacement : flèches directionnelles
- Interagir : `E`
- Attaquer / utiliser un objet : `Espace`
- Avancer un dialogue : `Entrée`
- Réinitialiser la partie : `R`
- Pause : `P`

## Notions de POO mises en œuvre (côté `icmaze`)

- **Héritage et polymorphisme** : les entités du jeu (`ICMazeActor`, `Ennemy`, `PathFinderEnnemy`, `Boss`, `FinalLieutenant`...) étendent une hiérarchie commune fournie par le moteur, en spécialisant le comportement de chaque type d'acteur.
- **Pattern Visitor** : `ICMazeInteractionVisitor` gère les interactions entre les différents types d'acteurs (joueur, ennemis, objets, décor) sans multiplier les tests de type.
- **Encapsulation des comportements de zone** : `ICMazeBehavior` sépare la logique de comportement de chaque case du labyrinthe (murs, eau, portails...) du rendu et de la logique de jeu.
- **Génération procédurale** : `MazeGenerator` et `LevelGenerator` construisent les niveaux de façon aléatoire mais contrôlée (taille, difficulté).
- **Machine à états / gestion de cooldowns** : `Cooldown`, `Oscillator` pour les mécaniques temporisées (attaques, effets de SpeedBall, etc.).

## Compiler et lancer

Prérequis : Java 21, Maven.

```bash
mvn clean install
```

Le jeu se lance ensuite via la classe `Play` du module concerné (voir la configuration du module dans votre IDE, ou `mvn exec:java` selon le module).

## Remarque

Le module `game-engine` (et `tutos`) est le matériel de base fourni par le cours CS-107 ; seul le module `icmaze` constitue notre propre travail pour ce mini-projet.
