package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.*;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.*;

public final class LevelGenerator {

    private static final Random rng = RandomGenerator.rng;

    // Classe utilitaire : pas d’instance
    private LevelGenerator() {
    }

    /**
     * Génère un niveau linéaire :
     * Spawn -> length aires procédurales -> Boss
     */
    public static ICMazeArea[] generateLine(ICMaze game, int length) {

        // Spawn + length salles + Boss
        ICMazeArea[] areas = new ICMazeArea[length + 2];

        // Référentiel fictif [x][y]
        DiscreteCoordinates current = new DiscreteCoordinates(0, 0);
        Set<DiscreteCoordinates> occupied = new HashSet<>();
        occupied.add(current);

        // 1) Spawn
        Spawn spawn = new Spawn();
        areas[0] = spawn;

        ICMazeArea previous = spawn;

        // 2) Aires intermédiaires
        for (int i = 0; i < length; i++) {

            // Progression (énoncé)
            double progress = (double) (i + 1) / length;

            // Choix direction libre (N, E, S)
            ICMazeArea.AreaPortals dir = chooseFreeDirection(current, occupied);

            DiscreteCoordinates next = move(current, dir);
            occupied.add(next);
            current = next;

            // Création de l’aire selon la progression
            ICMazeArea newArea = createAreaForProgress(i, progress);

            if (newArea instanceof AireLabyrinthique al) {
                // On entre dans newArea par l'opposé de la direction prise depuis previous
                al.setPortalEnter(getOpposite(dir));
            }

            if (previous instanceof AireLabyrinthique al) {
                // On sort de previous par la direction dir
                al.setPortalExit(dir);
            }

            // Connexion bidirectionnelle (TA manière) + Centralisation des clés
            connectAreas(previous, dir, newArea);

            areas[i + 1] = newArea;
            previous = newArea;
        }

        // 3) BossArea à la fin
        BossArea boss = new BossArea();

        ICMazeArea.AreaPortals bossDir = chooseFreeDirection(current, occupied);

        if (previous instanceof AireLabyrinthique al) {
            al.setPortalExit(bossDir);
        }

        connectAreas(previous, bossDir, boss);

        areas[length + 1] = boss;

        return areas;
    }

    // ======================
    // MÉTHODES UTILITAIRES
    // ======================

    /**
     * Choisit une direction libre parmi N, E, S
     * (jamais vers l’arrière)
     */
    private static ICMazeArea.AreaPortals chooseFreeDirection(
            DiscreteCoordinates current,
            Set<DiscreteCoordinates> occupied) {

        List<ICMazeArea.AreaPortals> dirs = new ArrayList<>(List.of(
                ICMazeArea.AreaPortals.N,
                ICMazeArea.AreaPortals.E,
                ICMazeArea.AreaPortals.S));

        Collections.shuffle(dirs, rng);

        for (ICMazeArea.AreaPortals d : dirs) {
            DiscreteCoordinates next = move(current, d);
            if (!occupied.contains(next)) {
                return d;
            }
        }

        // Cas extrême : on force Est
        return ICMazeArea.AreaPortals.E;
    }

    /**
     * Déplace une position selon une direction
     */
    private static DiscreteCoordinates move(
            DiscreteCoordinates c,
            ICMazeArea.AreaPortals dir) {

        return switch (dir) {
            case N -> new DiscreteCoordinates(c.x, c.y + 1);
            case S -> new DiscreteCoordinates(c.x, c.y - 1);
            case E -> new DiscreteCoordinates(c.x + 1, c.y);
            case W -> new DiscreteCoordinates(c.x - 1, c.y);
        };
    }

    /**
     * Crée Small / Medium / Large selon la progression
     * (règle EPFL)
     */
    private static ICMazeArea createAreaForProgress(
            int index,
            double progress) {

        int keyId = Integer.MAX_VALUE - index;
        double r = rng.nextDouble();

        if (r < progress * progress) {
            return new LargeArea(keyId);
        }
        if (r < progress) {
            return new MediumArea(keyId);
        }
        return new SmallArea(keyId);
    }

    /**
     * Connecte deux aires via leurs portails
     * en utilisant TES setters
     */
    private static void connectAreas(ICMazeArea from, ICMazeArea.AreaPortals dir, ICMazeArea to) {

        switch (dir) {
            case E -> {
                if (from instanceof AireLabyrinthique) {
                    AireLabyrinthique al = (AireLabyrinthique) from;
                    from.setEastKeyId(al.getKeyId());
                    from.setEastState(Portal.State.LOCKED);
                } else {
                    // Spawn
                    from.setEastKeyId(Integer.MAX_VALUE);
                    from.setEastState(Portal.State.LOCKED);
                }
                from.setEastDestination(to.getTitle(), to.getSize());
                to.setWestDestination(from.getTitle(), from.getSize());
                if (to instanceof BossArea) {
                    to.setWestKeyId(999);
                    to.setWestState(Portal.State.LOCKED);
                } else {
                    to.setWestState(Portal.State.OPEN);
                }
            }
            case W -> {
                if (from instanceof AireLabyrinthique) {
                    AireLabyrinthique al = (AireLabyrinthique) from;
                    from.setWestKeyId(al.getKeyId());
                    from.setWestState(Portal.State.LOCKED);
                } else {
                    from.setWestState(Portal.State.OPEN);
                }
                from.setWestDestination(to.getTitle(), to.getSize());
                to.setEastDestination(from.getTitle(), from.getSize());
                if (to instanceof BossArea) {
                    to.setEastKeyId(999);
                    to.setEastState(Portal.State.LOCKED);
                } else {
                    to.setEastState(Portal.State.OPEN);
                }
            }
            case N -> {
                if (from instanceof AireLabyrinthique) {
                    AireLabyrinthique al = (AireLabyrinthique) from;
                    from.setNorthKeyId(al.getKeyId());
                    from.setNorthState(Portal.State.LOCKED);
                } else {
                    // Spawn
                    from.setNorthKeyId(Integer.MAX_VALUE);
                    from.setNorthState(Portal.State.LOCKED);
                }
                from.setNorthDestination(to.getTitle(), to.getSize());
                to.setSouthDestination(from.getTitle(), from.getSize());
                if (to instanceof BossArea) {
                    to.setSouthKeyId(999);
                    to.setSouthState(Portal.State.LOCKED);
                } else {
                    to.setSouthState(Portal.State.OPEN);
                }
            }
            case S -> {
                if (from instanceof AireLabyrinthique) {
                    AireLabyrinthique al = (AireLabyrinthique) from;
                    from.setSouthKeyId(al.getKeyId());
                    from.setSouthState(Portal.State.LOCKED);
                } else {
                    // Spawn
                    from.setSouthKeyId(Integer.MAX_VALUE);
                    from.setSouthState(Portal.State.LOCKED);
                }
                from.setSouthDestination(to.getTitle(), to.getSize());
                to.setNorthDestination(from.getTitle(), from.getSize());
                if (to instanceof BossArea) {
                    to.setNorthKeyId(999);
                    to.setNorthState(Portal.State.LOCKED);
                } else {
                    to.setNorthState(Portal.State.OPEN);
                }
            }
        }
    }

    private static ICMazeArea.AreaPortals getOpposite(ICMazeArea.AreaPortals dir) {
        return switch (dir) {
            case N -> ICMazeArea.AreaPortals.S;
            case S -> ICMazeArea.AreaPortals.N;
            case E -> ICMazeArea.AreaPortals.W;
            case W -> ICMazeArea.AreaPortals.E;
        };
    }

}
