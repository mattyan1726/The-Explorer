package byow;

import byow.Core.Engine;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;


public class Player {
    private static final int UP = 1, DOWN = 2, LEFT = 3, RIGHT = 4;
    private static final boolean VERTICAL = true, HORIZONTAL = false;
    static final int W = Engine.HEIGHT;
    static final int L = Engine.WIDTH;
    private static final int SUR = 10;

    public static TETile[][] getPlayerWld(long sight, TETile[][] wld, Point pos) {
        TETile[][] world = new TETile[L][W];
        for (int x = 0; x < L; x += 1) {
            for (int y = 0; y < W; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        List<Point> pts = getPoints(sight, pos);
        for (Point pt : pts) {
            int x = pt.getX();
            int y = pt.getY();
            if ((x < L) && (x >= 0) && (y < W) && (y >= 0)) {
                world[x][y] = wld[x][y];
            }
        }
        return world;
    }

    private static List<Point> getPoints(long sight, Point pos) {
        List<Point> pts = findSurroundingPoints(pos, SUR);
        int posX = pos.getX();
        int posY = pos.getY();
        if (sight == LEFT) {
            pts.addAll(makePoints(VERTICAL, new Point(posX - SUR - 1, posY - SUR), 2 * SUR + 1));
            pts.addAll(makePoints(VERTICAL, new Point(posX - SUR - 2, posY - SUR), 2 * SUR + 1));
        } else if (sight == RIGHT) {
            pts.addAll(makePoints(VERTICAL, new Point(posX + SUR + 1, posY - SUR), 2 * SUR + 1));
            pts.addAll(makePoints(VERTICAL, new Point(posX + SUR + 2, posY - SUR), 2 * SUR + 1));
        } else if (sight == UP) {
            pts.addAll(makePoints(HORIZONTAL, new Point(posX - SUR, posY + SUR + 1), 2 * SUR + 1));
            pts.addAll(makePoints(HORIZONTAL, new Point(posX - SUR, posY + SUR + 2), 2 * SUR + 1));
        } else {
            pts.addAll(makePoints(HORIZONTAL, new Point(posX - SUR, posY - SUR - 1), 2 * SUR + 1));
            pts.addAll(makePoints(HORIZONTAL, new Point(posX - SUR, posY - SUR - 2), 2 * SUR + 1));
        }
        return pts;
    }

    private static List<Point> findSurroundingPoints(Point pos, int surround) {
        List<Point> pts = new ArrayList<>();
        int posX = pos.getX();
        int posY = pos.getY();
        for (int i = -surround; i < surround + 1; i++) {
            for (int j = -surround; j < surround + 1; j++) {
                pts.add(new Point(posX + i, posY + j));
            }
        }
        return pts;
    }

    private static List<Point> makePoints(boolean orientation, Point start, int num) {
        // collect a number of points from the start point according to orientation
        List<Point> pts = new ArrayList<>();
        int startX = start.getX();
        int startY = start.getY();
        if (orientation == VERTICAL) {
            for (int i = 0; i < num; i++) {
                Point newPt = new Point(startX, startY + i);
                pts.add(newPt);
            }
        } else {
            for (int i = 0; i < num; i++) {
                Point newPt = new Point(startX + i, startY);
                pts.add(newPt);
            }
        }
        return pts;
    }
}
