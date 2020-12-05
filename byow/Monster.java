package byow;

import byow.Core.Engine;
import byow.FindPath.AStarSolver;
import byow.FindPath.GameGraph;
import byow.FindPath.ShortestPathsSolver;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.List;
import java.util.Random;

public class Monster {
    private Point pos;
    private static final int CHASETHRESHOLD = 18;

    public Monster(TETile[][] wld, Random rd, Point playerPos) {
        int x = rd.nextInt(Engine.WIDTH);
        int y = rd.nextInt(Engine.HEIGHT);
        Point pt = new Point(x, y);
        while (!wld[x][y].equals(Tileset.FLOOR) || pt.distanceTo(playerPos) < CHASETHRESHOLD) {
            x = rd.nextInt(Engine.WIDTH);
            y = rd.nextInt(Engine.HEIGHT);
            pt = new Point(x, y);
        }
        this.pos = pt;
    }

    public Monster(Point pos) {
        this.pos = pos;
    }

    public void move(TETile[][] wld, Random rd, Point playerPos) {
        int distanceToPlayer = pos.distanceTo(playerPos);
        if (distanceToPlayer < CHASETHRESHOLD) {
            List<Point> path = findPath(pos, playerPos, wld);
            if (path.size() > 1) {
                pos = path.get(1);
            } else {
                randomlyMove(wld, rd);
            }
        } else {
            randomlyMove(wld, rd);
        }
    }

    private void randomlyMove(TETile[][] wld, Random rd) {
        int dir = rd.nextInt(4) + 1;
        int xPos = pos.getX();
        int yPos = pos.getY();
        Point newPos;
        if (dir == Engine.LEFT) {
            newPos = new Point(xPos - 1, yPos);
        } else if (dir == Engine.RIGHT) {
            newPos = new Point(xPos + 1, yPos);
        } else if (dir == Engine.UP) {
            newPos = new Point(xPos, yPos + 1);
        } else {
            newPos = new Point(xPos, yPos - 1);
        }

        if (check(wld, newPos)) {
            pos = newPos;
        } else {
            randomlyMove(wld, rd);
        }
    }

    private boolean check(TETile[][] wld, Point pt) {
        // check if the monster can move to a point
        TETile tile = wld[pt.getX()][pt.getY()];
        return accessible(tile);
    }

    public static boolean accessible(TETile tile) {
        // return if the tile is accessible
        return tile != Tileset.LOCKED_DOOR && tile != Tileset.UNLOCKED_DOOR
                && tile != Tileset.WALL && tile != Engine.KEY && tile != Engine.PROP
                && tile != Engine.BOOTY;
    }

    public Point getPos() {
        return pos;
    }

    private List<Point> findPath(Point monsterPos, Point playerPos, TETile[][] wld) {
        GameGraph gg = new GameGraph(wld);
        ShortestPathsSolver<Point> solver = new AStarSolver<>(gg, monsterPos, playerPos, 10);
        return solver.solution();
    }
}
