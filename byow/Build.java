package byow;

import byow.Core.Engine;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.List;
import java.util.Random;
//import java.lang.Math;

public class Build {

    //private static long seed = 1234567;
    private int size = 0;
    private final double ratio = 0.4;
    static final int W = Engine.HEIGHT;
    static final int L = Engine.WIDTH;
    private TETile[][] world;
    private int limit = 60;


    public void putRoom(Room room) {
        List<Point> space = room.getSpace();
        List<Point> corners = room.getCorners();
        List<Point> sides = room.collectSides();
        sides.addAll(corners);
        for (Point pt : sides) {
            int x = pt.getX();
            int y = pt.getY();
            world[x][y] = Tileset.WALL;
        }
        for (Point pt : space) {
            int x = pt.getX();
            int y = pt.getY();
            world[x][y] = Tileset.FLOOR;
        }
        Hallway entry = room.getPrevH();
        if (entry != null) {
            Point end = entry.getEnd();
            world[end.getX()][end.getY()] = Tileset.FLOOR;
            size -= 3; // accommodate the overlap of the end of hallway
        }
        size += room.getLength() * room.getWidth();
    }

    public void putHallway(Hallway h) {
        // the end of the hallway is wall
        List<Point> space = h.getSpace();
        List<Point> sides = h.getSides();
        for (Point pt : sides) {
            int x = pt.getX();
            int y = pt.getY();
            world[x][y] = Tileset.WALL;
        }
        for (Point pt : space) {
            int x = pt.getX();
            int y = pt.getY();
            world[x][y] = Tileset.FLOOR;
        }
        putEnd(h);
        size += (Math.abs(h.getL()) - 1) * 3;
    }

    private boolean checkPoint(Point p) {
        // check if the given point is within the canvas and does not overlap with existing tile
        int x = p.getX();
        int y = p.getY();
        if ((L > x) && (x >= 0) && (W > y) && (y >= 0)) {
            if (world[x][y] != Tileset.NOTHING) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean check(Block b) {
        // check if the given block can be put
        List<Point> pts = b.getModifiedAllPoints();
        for (Point pt : pts) {
            if (!(checkPoint(pt))) {
                return false;
            }
        }
        return true;
    }

    public void putEnd(Hallway currH) {
        // replace the end of the hallway to be a wall, instead of a floor
        Point end = currH.getEnd();
        world[end.getX()][end.getY()] = Tileset.WALL;
    }

    public Hallway createValidH(Room currR, Random rd) {
        /* Create a hallway with current room, if success, return the hallway.
        If a valid hallway is not created in 100 tries, go back to the previous room
        and repeat the process. If previous room does not exist, return null.
         */
        Hallway currH = currR.create(rd);
        int count = 0;
        while (!(check(currH)) && count < limit) {
            currH = currR.create(rd);
            count++;
        }
        if (count == limit) {
            currR = currR.getPrevRoom();
            if (currR == null) {
                return null;
            } else {
                return createValidH(currR, rd);
            }
        }
        return currH;
    }

    public Block createValidB(Hallway currH, Random rd) {
        /* Firstly, try to create a room with current hallway. If success, return the room.
        If a valid room is not created in 100 tries, go back to the previous room.
        Create a valid hallway using previous room by calling createValidH and return it.
         */
        Room currR = currH.create(rd);
        int count = 0;
        while (!(check(currR)) && count < limit) {
            currR = currH.create(rd);
            count++;
        }
        if (count == limit) {
            currR = currR.getPrevRoom();
            if (currR == null) {
                return null;
            } else {
                return createValidH(currR, rd);
            }
        }
        return currR;
    }

    public void makeEmptyWorld() {
        world = new TETile[L][W];
        for (int x = 0; x < L; x += 1) {
            for (int y = 0; y < W; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    public Build(Random rd) {
        makeEmptyWorld();
        Room currR = Room.makeFirstRoom(rd);
        putRoom(currR);

        while ((double) size / (W * L) < ratio) {
            Hallway currH = createValidH(currR, rd);
            if (currH == null) {
                return;
            }
            putHallway(currH);

            Block b = createValidB(currH, rd);
            while (b instanceof Hallway) {
                if (b == null) {
                    return;
                } else {
                    currH = (Hallway) b;
                    putHallway(currH);
                    b = createValidB(currH, rd);
                }
            }
            currR = (Room) b;
            putRoom(currR);
        }
    }

    public TETile[][] getWorld() {
        return world;
    }

    public static void main(String[] args) {
        String str = args[0];
        Engine engine = new Engine();
        TETile[][] wld = engine.interactWithInputString(str);

        //Random rd = new Random(seed);
        //Build b = new Build(rd);
        TERenderer ter = new TERenderer();
        ter.initialize(L, W);
        ter.renderFrame(wld);
    }
}
