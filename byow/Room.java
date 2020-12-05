package byow;

//import byow.TileEngine.TETile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room implements Block {

    private int length;
    private int width;
    private Point bottomLeft; // a wall
    private Hallway prevH;

    public Room(int length, int width, Point bl, Hallway h) {
        this.length = length;
        this.width = width;
        this.bottomLeft = bl;
        this.prevH = h;
    }

    public static Room makeFirstRoom(Random rd) {
        int length = rd.nextInt(7) + 4;
        int width = rd.nextInt(7) + 4;
        int x = rd.nextInt(Build.L / 2 - length);
        int y = rd.nextInt(Build.W - width);
        Point pt = new Point(x, y);
        Room first = new Room(length, width, pt, null);
        return first;
    }

    public List<Point> get(String side) {
        int initX = bottomLeft.getX();
        int initY = bottomLeft.getY();
        List<Point> pts = new ArrayList<>();
        if (side.equals("top")) {
            for (int i = 1; i < length - 1; i++) {
                Point pt = new Point(i + initX, initY + width - 1);
                pts.add(pt);
            }
        } else if (side.equals("bottom")) {
            for (int i = 1; i < length - 1; i++) {
                Point pt = new Point(i + initX, initY);
                pts.add(pt);
            }
        } else if (side.equals("left")) {
            for (int i = 1; i < width - 1; i++) {
                Point pt = new Point(initX, initY + i);
                pts.add(pt);
            }
        } else if (side.equals("right")) {
            for (int i = 1; i < width - 1; i++) {
                Point pt = new Point(initX + length - 1, initY + i);
                pts.add(pt);
            }
        } else {
            throw new IllegalArgumentException();
        }
        return pts;
    }

    public List<Point> getCorners() {
        List<Point> corners = new ArrayList<>();
        Point bottomRight = new Point(bottomLeft.getX() + length - 1, bottomLeft.getY());
        Point topLeft = new Point(bottomLeft.getX(), bottomLeft.getY() + width - 1);
        Point topRight = new Point(bottomLeft.getX() + length - 1, bottomLeft.getY() + width - 1);
        corners.add(bottomLeft);
        corners.add(bottomRight);
        corners.add(topLeft);
        corners.add(topRight);
        return corners;
    }

    public List<Point> getSpace() {
        List<Point> pts = new ArrayList<>();
        for (int j = 1; j <= width - 2; j++) {
            for (int i = 1; i <= length - 2; i++) {
                Point pt = new Point(bottomLeft.getX() + i, bottomLeft.getY() + j);
                pts.add(pt);
            }
        }
        return pts;
    }

    public List<Point> collectSides() {
        List<Point> pts = new ArrayList<>();
        pts.addAll(get("top"));
        pts.addAll(get("bottom"));
        pts.addAll(get("right"));
        pts.addAll(get("left"));
        return pts;
    }

    private List<Point> prevHallwayEntries() {
        List<Point> entries = new ArrayList<>();
        if (prevH == null) {
            return entries;
        } else {
            Point end = prevH.getEnd();
            return prevH.getEndsOf(end);
        }
    }

    @Override
    public List<Point> getModifiedAllPoints() {
        // filtered out previous hallway entries
        List<Point> l = getAllPoints();
        l.addAll(getFurtherSides());
        l.removeAll(prevHallwayEntries());
        return l;
    }

    @Override
    public List<Point> getAllPoints() {
        List<Point> pts = collectSides();
        pts.addAll(getSpace());
        pts.addAll(getCorners());
        return pts;
    }

    @Override
    public Room getPrevRoom() {
        if (prevH == null) {
            return null;
        }
        return prevH.getPrevRoom();
    }

    public Hallway create(Random rd) {
        //when created hallway conflicts with entry,
        // resolved by more tries to create Hallway.

        int s = rd.nextInt(4);
        List<Point> pts;
        int a; // determines the sign of length based on the side
        if (s == 0) {
            pts = get("left");
            a = -1;
        } else if (s == 1) {
            pts = get("right");
            a = 1;
        } else if (s == 2) {
            pts = get("top");
            a = 1;
        } else if (s == 3) {
            pts = get("bottom");
            a = -1;
        } else {
            throw new IllegalArgumentException("Side # out of 0 - 3");
        }
        int r = rd.nextInt(pts.size());
        Point pt = pts.get(r);
        boolean orientation;
        if (s == 0 || s == 1) {
            orientation = false;
        } else {
            orientation = true;
        }
        int len = rd.nextInt(4) + 2;
        Hallway h = new Hallway(len * a, orientation, pt, this);
        return h;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public Hallway getPrevH() {
        return prevH;
    }

    private List<Point> getFurtherSides() {
        List<Point> pts = new ArrayList<>();
        if (prevH.getO()) {
            if (prevH.getL() > 0) {
                pts.addAll(getOneFurther("top"));
                pts.addAll(getOneFurther("right"));
                pts.addAll(getOneFurther("left"));
            } else {
                pts.addAll(getOneFurther("bottom"));
                pts.addAll(getOneFurther("left"));
                pts.addAll(getOneFurther("right"));
            }
        } else {
            if (prevH.getL() > 0) {
                pts.addAll(getOneFurther("bottom"));
                pts.addAll(getOneFurther("right"));
                pts.addAll(getOneFurther("top"));
            } else {
                pts.addAll(getOneFurther("top"));
                pts.addAll(getOneFurther("left"));
                pts.addAll(getOneFurther("bottom"));
            }
        }
        return pts;
    }

    private List<Point> getOneFurther(String side) {
        int initX = bottomLeft.getX();
        int initY = bottomLeft.getY();
        List<Point> pts = new ArrayList<>();
        if (side.equals("top")) {
            for (int i = 0; i < length; i++) {
                Point pt = new Point(i + initX, initY + width);
                pts.add(pt);
            }
        } else if (side.equals("bottom")) {
            for (int i = 0; i < length; i++) {
                Point pt = new Point(i + initX, initY - 1);
                pts.add(pt);
            }
        } else if (side.equals("left")) {
            for (int i = 0; i < width; i++) {
                Point pt = new Point(initX - 1, initY + i);
                pts.add(pt);
            }
        } else if (side.equals("right")) {
            for (int i = 0; i < width; i++) {
                Point pt = new Point(initX + length, initY + i);
                pts.add(pt);
            }
        } else {
            throw new IllegalArgumentException();
        }
        return pts;
    }

    public static void main(String[] args) {
        Room test = new Room(4, 6, new Point(1, 1), null);
        List<Point> pts = test.collectSides();
        pts.add(test.bottomLeft);
        for (Point pt : pts) {
            System.out.print(pt.getX() + " ");
            System.out.println(pt.getY());
        }
    }
}
