package byow.FindPath;

import java.util.List;

/**
 * Represents a graph of vertices.
 * @source Josh Hug.
 */
public interface AStarGraph<Vertex> {
    List<WeightedEdge<Vertex>> neighbors(Vertex v);
    double estimatedDistanceToGoal(Vertex s, Vertex goal);
}
