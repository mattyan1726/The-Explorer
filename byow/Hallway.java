package byow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Hallway implements Block {

    private int length; //positive: up and right
    private boolean orientation; //true is vertical
    private Point start;
    private Point end;
    private Room prevRoom;

    public Hallway(int length, boolean o, Point start, Room r) {
        this.length = length;
        this.orientation = o;
        this.start = start;
        this.prevRoom = r;

        int x = start.getX();
        int y = start.getY();
        if (orientation) {
            if (length < 0) {
                end = new Point(x, y + length + 1);
            } else {
                end = new Point(x, y + length - 1);
            }
        } else {
            if (length < 0) {
                end = new Point(x + length + 1, y);
            } else {
                end = new Point(x + length - 1, y);
            }
        }
    }

    public List<Point> getEndsOf(Point pt) {
        List<Point> ends = getAdjacent(pt);
        ends.add(pt);
        return ends;
    }

    public Point getEnd() {
        return end;
    }

    public Point getStart() {
        return start;
    }

    public boolean getO() {
        return orientation;
    }

    public int getL() {
        return length;
    }

    public Room create(Random rd) {
        int l = rd.nextInt(6) + 3;
        int w = rd.nextInt(6) + 3;
        Point bottomLeft;
        if (orientation) {
            int fromLeft = rd.nextInt(l - 2) + 1;
            int llX = end.getX() - fromLeft;
            int llY;
            if (length > 0) {
                llY = end.getY();
            } else {
                llY = end.getY() - w + 1;
            }
            bottomLeft = new Point(llX, llY);
        } else {
            int fromBottom = rd.nextInt(w - 2) + 1;
            int llY = end.getY() - fromBottom;
            int llX;
            if (length > 0) {
                llX = end.getX();
            } else {
                llX = end.getX() - l + 1;
            }
            bottomLeft = new Point(llX, llY);
        }
        Room newRoom = new Room(l, w, bottomLeft, this);
        return newRoom;
    }

    @Override
    public List<Point> getModifiedAllPoints() {
        //no start points
        List<Point> l = getAllPoints();
        List<Point> starts = getEndsOf(start);
        l.removeAll(starts);
        l.add(getOneFurtherFromEnd());
        return l;
    }

    public Point getOneFurtherFromEnd() {
        Point pt;
        if (orientation) {
            if (length > 0) {
                pt = new Point(end.getX(), end.getY() + 1);
            } else {
                pt = new Point(end.getX(), end.getY() - 1);
            }
        } else {
            if (length > 0) {
                pt = new Point(end.getX() + 1, end.getY());
            } else {
                pt = new Point(end.getX() - 1, end.getY());
            }
        }
        return pt;
    }

    @Override
    public List<Point> getAllPoints() {
        List<Point> pts = getSides();
        pts.addAll(getSpace());
        return pts;
    }

    @Override
    public Room getPrevRoom() {
        return prevRoom;
    }

    public List<Point> getAdjacent(Point pt) {
        // get the two adjacent points of the given point
        Point adj1, adj2;
        if (orientation) {
            int x1 = pt.getX() - 1;
            int x2 = pt.getX() + 1;
            adj1 = new Point(x1, pt.getY());
            adj2 = new Point(x2, pt.getY());
        } else {
            int y1 = pt.getY() - 1;
            int y2 = pt.getY() + 1;
            adj1 = new Point(pt.getX(), y1); //lower side start
            adj2 = new Point(pt.getX(), y2); //upper side start
        }
        List<Point> adj = new ArrayList<>();
        adj.add(adj1);
        adj.add(adj2);
        return adj;
    }

    public List<Point> getSides() {
        List<Point> pts = new ArrayList<>();
        Point adj1, adj2;
        if (orientation) {
            int x1 = start.getX() - 1;
            int x2 = start.getX() + 1;
            adj1 = new Point(x1, start.getY());
            adj2 = new Point(x2, start.getY());
            Hallway left = new Hallway(length, orientation, adj1, null);
            Hallway right = new Hallway(length, orientation, adj2, null);
            pts.addAll(left.getSpace());
            pts.addAll(right.getSpace());
        } else {
            int y1 = start.getY() - 1;
            int y2 = start.getY() + 1;
            adj1 = new Point(start.getX(), y1); //lower side start
            adj2 = new Point(start.getX(), y2); //upper side start
            Hallway down = new Hallway(length, orientation, adj1, null);
            Hallway up = new Hallway(length, orientation, adj2, null);
            pts.addAll(down.getSpace());
            pts.addAll(up.getSpace());
        }
        return pts;
    }

    public List<Point> getSpace() {
        List<Point> pts = new ArrayList<>();
        if (orientation) {
            if (length < 0) {
                for (int i = 0; i > length; i--) {
                    int y = start.getY() + i;
                    Point pt = new Point(start.getX(), y);
                    pts.add(pt);
                }
            } else {
                for (int i = 0; i < length; i++) {
                    int y = start.getY() + i;
                    Point pt = new Point(start.getX(), y);
                    pts.add(pt);
                }
            }
        } else {
            if (length < 0) {
                for (int i = 0; i > length; i--) {
                    int x = start.getX() + i;
                    Point pt = new Point(x, start.getY());
                    pts.add(pt);
                }
            } else {
                for (int i = 0; i < length; i++) {
                    int x = start.getX() + i;
                    Point pt = new Point(x, start.getY());
                    pts.add(pt);
                }
            }
        }
        return pts;
    }

    public static void main(String[] args) {
        Hallway test = new Hallway(4, false, new Point(4, 3), null);
    }
}


