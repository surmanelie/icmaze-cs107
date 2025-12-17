package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.*;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.*;

public final class LevelGenerator {

    private static final Random rng = RandomGenerator.rng;

    // Utility class: no instance
    private LevelGenerator() {
    }

    /**
     * Generate a linear level:
     * Spawn -> length procedural areas -> Boss
     */
    public static ICMazeArea[] generateLine(ICMaze game, int length) {


        ICMazeArea[] areas = new ICMazeArea[length + 2];


        DiscreteCoordinates current = new DiscreteCoordinates(0, 0);
        Set<DiscreteCoordinates> occupied = new HashSet<>();
        occupied.add(current);

        // 1) Spawn
        Spawn spawn = new Spawn();
        areas[0] = spawn;

        if (length == 0) {
            BossArea boss = new BossArea();
            ICMazeArea.AreaPortals dir = chooseFreeDirection(current, occupied);
            connectSpawnToBoss(spawn, dir, boss);
            areas[1] = boss;
            return areas;
        }

        // Premiere area connecte
        int index = 0;
        double progress = (double) (index + 1) / length;
        ICMazeArea.AreaPortals dir = chooseFreeDirection(current, occupied);
        current = move(current, dir);
        occupied.add(current);

        LabyrinthArea firstArea = createAreaForProgress(index, progress);
        firstArea.setPortalEnter(getOpposite(dir));


        connectSpawnToLabyrinth(spawn, dir, firstArea);
        areas[1] = firstArea;

        LabyrinthArea previous = firstArea;

        // 2) Aires inetremediaires
        for (int i = 1; i < length; i++) {
            progress = (double) (i + 1) / length;
            dir = chooseFreeDirection(current, occupied);
            current = move(current, dir);
            occupied.add(current);

            LabyrinthArea newArea = createAreaForProgress(i, progress);
            newArea.setPortalEnter(getOpposite(dir));

            previous.setPortalExit(dir);

            connectLabyrinthToLabyrinth(previous, dir, newArea);

            areas[i + 1] = newArea;
            previous = newArea;
        }


        BossArea boss = new BossArea();
        ICMazeArea.AreaPortals bossDir = chooseFreeDirection(current, occupied);

        previous.setPortalExit(bossDir);

        connectLabyrinthToBoss(previous, bossDir, boss);

        areas[length + 1] = boss;


        for (ICMazeArea area : areas) {
            if (area != boss) {
                area.setValidationSignal(boss);
            }
        }

        return areas;
    }



    /**
     * Chooses a free direction among N, E, S
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

        return ICMazeArea.AreaPortals.E;
    }

    /**
     * Moves position by direction
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
     * Creates Small / Medium / Large based on progress
     * Now strictly returns LabyrinthArea
     */
    private static LabyrinthArea createAreaForProgress(
            int index,
            double progress) {

        int keyId = Integer.MAX_VALUE - index;
        double r = rng.nextDouble();

        int difficulty = switch ((int) (progress * 4)) {
            case 0 -> Difficulty.EASY;
            case 1 -> Difficulty.MEDIUM;
            case 2 -> Difficulty.HARD;
            default -> Difficulty.HARDEST;
        };

        if (r < progress * progress) {
            return new LargeArea(keyId, difficulty);
        }
        if (r < progress) {
            return new MediumArea(keyId, difficulty);
        }
        return new SmallArea(keyId, difficulty);
    }


    private static void connectSpawnToLabyrinth(Spawn from, ICMazeArea.AreaPortals dir, LabyrinthArea to) {
        setOutgoingLocked(from, dir, Integer.MAX_VALUE); // Spawn uses MAX_VALUE key

        setDestinations(from, dir, to);


        setIncomingOpen(to, dir);
    }

    private static void connectLabyrinthToLabyrinth(LabyrinthArea from, ICMazeArea.AreaPortals dir,
            LabyrinthArea to) {
        setOutgoingLocked(from, dir, from.getKeyId());

        setDestinations(from, dir, to);

        setIncomingOpen(to, dir);
    }

    private static void connectLabyrinthToBoss(LabyrinthArea from, ICMazeArea.AreaPortals dir, BossArea to) {
        setOutgoingLocked(from, dir, from.getKeyId());

        setDestinations(from, dir, to);


        setIncomingLocked(to, dir, 999);
    }


    private static void connectSpawnToBoss(Spawn from, ICMazeArea.AreaPortals dir, BossArea to) {
        setOutgoingLocked(from, dir, Integer.MAX_VALUE);

        setDestinations(from, dir, to);

        setIncomingLocked(to, dir, 999);
    }



    private static void setDestinations(ICMazeArea from, ICMazeArea.AreaPortals dir, ICMazeArea to) {
        switch (dir) {
            case E -> {
                from.setEastDestination(to.getTitle(), to.getSize());
                to.setWestDestination(from.getTitle(), from.getSize());
            }
            case W -> {
                from.setWestDestination(to.getTitle(), to.getSize());
                to.setEastDestination(from.getTitle(), from.getSize());
            }
            case N -> {
                from.setNorthDestination(to.getTitle(), to.getSize());
                to.setSouthDestination(from.getTitle(), from.getSize());
            }
            case S -> {
                from.setSouthDestination(to.getTitle(), to.getSize());
                to.setNorthDestination(from.getTitle(), from.getSize());
            }
        }
    }

    private static void setOutgoingLocked(ICMazeArea from, ICMazeArea.AreaPortals dir, int keyId) {
        switch (dir) {
            case E -> {
                from.setEastKeyId(keyId);
                from.setEastState(Portal.State.LOCKED);
            }
            case W -> {
                from.setWestKeyId(keyId);
                from.setWestState(Portal.State.LOCKED);
            }
            case N -> {
                from.setNorthKeyId(keyId);
                from.setNorthState(Portal.State.LOCKED);
            }
            case S -> {
                from.setSouthKeyId(keyId);
                from.setSouthState(Portal.State.LOCKED);
            }
        }
    }

    private static void setIncomingOpen(ICMazeArea to, ICMazeArea.AreaPortals fromDir) {

        switch (fromDir) {
            case E -> to.setWestState(Portal.State.OPEN);
            case W -> to.setEastState(Portal.State.OPEN);
            case N -> to.setSouthState(Portal.State.OPEN);
            case S -> to.setNorthState(Portal.State.OPEN);
        }
    }

    private static void setIncomingLocked(ICMazeArea to, ICMazeArea.AreaPortals fromDir, int keyId) {
        switch (fromDir) {
            case E -> {
                to.setWestKeyId(keyId);
                to.setWestState(Portal.State.LOCKED);
            }
            case W -> {
                to.setEastKeyId(keyId);
                to.setEastState(Portal.State.LOCKED);
            }
            case N -> {
                to.setSouthKeyId(keyId);
                to.setSouthState(Portal.State.LOCKED);
            }
            case S -> {
                to.setNorthKeyId(keyId);
                to.setNorthState(Portal.State.LOCKED);
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
