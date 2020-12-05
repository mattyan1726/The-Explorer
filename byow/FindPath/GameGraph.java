package byow.FindPath;

import byow.Point;
import byow.TileEngine.TETile;

import java.util.ArrayList;
import java.util.List;

import static byow.Monster.accessible;

public class GameGraph implements AStarGraph<Point> {

    private TETile[][] wld;

    public GameGraph(TETile[][] wld) {
        this.wld = wld;
    }

    @Override
    public List<WeightedEdge<Point>> neighbors(Point v) {
        ArrayList<WeightedEdge<Point>> neighbors = new ArrayList<>();
        int x = v.getX();
        int y = v.getY();
        if (accessible(wld[x - 1][y])) {
            Point neighbor = new Point(x - 1, y);
            neighbors.add(new WeightedEdge<>(v, neighbor, 1));
        }
        if (accessible(wld[x + 1][y])) {
            Point neighbor = new Point(x + 1, y);
            neighbors.add(new WeightedEdge<>(v, neighbor, 1));
        }
        if (accessible(wld[x][y - 1])) {
            Point neighbor = new Point(x, y - 1);
            neighbors.add(new WeightedEdge<>(v, neighbor, 1));
        }
        if (accessible(wld[x][y + 1])) {
            Point neighbor = new Point(x, y + 1);
            neighbors.add(new WeightedEdge<>(v, neighbor, 1));
        }
        return neighbors;
    }

    @Override
    public double estimatedDistanceToGoal(Point s, Point goal) {
        return s.distanceTo(goal);
    }
}
