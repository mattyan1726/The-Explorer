package byow;

import byow.TileEngine.TETile;

public class Point {

    private int x;
    private int y;
    private TETile tile;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object b) {
        if (b == this) {
            return true;
        }
        if (!(b instanceof Point)) {
            return false;
        }
        Point pt = (Point) b;
        return (pt.getX() == x) && (pt.getY() == y);
    }

    @Override
    public int hashCode() {
        return x;
    }

    public int distanceTo(Point pt) {
        int xDis = Math.abs(x - pt.getX());
        int yDis = Math.abs(y - pt.getY());
        return xDis + yDis;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TETile getT() {
        return tile;
    }

    public void setT(TETile t) {
        this.tile = t;
    }
}
