package byow;

import java.util.List;

public interface Block {
    List<Point> getAllPoints();
    Room getPrevRoom();
    List<Point> getModifiedAllPoints();
}
