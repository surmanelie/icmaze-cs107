# Conception du Projet

Ce fichier détaille les extensions et choix de conception spécifiques implémentés dans notre version de **ICMaze**.

## 1. Score et Économie (Coins)

Nous avons enrichi le système de score en liant l'économie du jeu à la performance au combat.

-   **Récompense de Fin** : Lorsque toutes les aires sont résolues (Signal actif dans `Spawn`), un certain nombre de pièces apparaît dans la zone de départ.
-   **Formule** : Le nombre de pièces dépend directement du nombre d'ennemis tués (`monsterKillCount`) :
    -   **3 pièces** pour 5 éliminations ou plus.
    -   **2 pièces** pour 3 ou 4 éliminations.
    -   **1 pièce** pour moins de 3 éliminations.

    *Concept* : Ce système encourage le joueur à ne pas simplement fuir vers la sortie, mais à "nettoyer" les salles pour être récompensé à la toute fin.

## 2. Bonus Temporaires (SpeedBalls)

Nous avons introduit des objets consommables, les **SpeedBalls**, qui modifient temporairement les statistiques du joueur pour ajouter une dimension stratégique aux déplacements.

-   **Blue SpeedBall** : Augmente la vitesse de déplacement (`FAST_MOVE_DURATION`). Utile pour traverser rapidement des zones dangereuses.
-   **Red SpeedBall** : Réduit la vitesse (`SLOW_MOVE_DURATION`). C'est un objet "piège" ou de défi, qui rend l'esquive des projectiles plus difficile.
-   **Gestionnaire** : Ces effets sont gérés par des `Cooldown` dans `ICMazePlayer`, assurant que l'état normal est restauré après 10 secondes.

## 3. Menu de Pause et Gestion de Partie

Pour améliorer l'expérience utilisateur (UX), nous avons implémenté un système de contrôle de flux de jeu robuste.

-   **PauseMenu** :
    -   Accessible via la touche `P`.
    -   Gèle la boucle de jeu (`update` retourne sans rien faire).
    -   Affiche un overlay graphique (`PauseMenu.java`) qui rappelle les commandes essentielles.
-   **Reset (R)** :
    -   Permet de relancer la partie à tout moment sans fermer l'application.
    -   Réinitialise proprement l'état du joueur, l'inventaire et recharge le niveau depuis le début.

## 4. Le Boss Final et son Assistant (Lieutenant)

Le défi final a été complexifié par l'ajout d'une hiérarchie d'ennemis.

**L'Assistant (FinalLieutenant, anciennement DarkLord)** :
Contrairement aux ennemis standards, cet ennemi est conçu pour soutenir le Boss principal.
-   **Comportement** : Il alterne entre une phase de patrouille aléatoire (`IDLE`) et une phase d'attaque. S'il détecte le joueur, il s'arrête et lance un projectile de feu (`FinalFireProjectile`).
-   **Visuel** : Il utilise des sprites spécifiques (`darkLord`) et change d'apparence lorsqu'il incante son sort.
-   **Vulnérabilité** : bien qu'il ait moins de vie (5 HP) que le Boss, son petit gabarit et sa mobilité le rendent difficile à toucher avec la pioche. Il ne possède pas de barre de vie visible pour garder l'interface épurée lors du combat de boss final surchargé.

Cette conception crée une dynamique où le joueur doit gérer **deux types de menaces simultanées** : les téléportations massives du Boss et le harcèlement constant des Lieutenants.
