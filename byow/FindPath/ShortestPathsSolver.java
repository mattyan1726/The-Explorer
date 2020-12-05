package byow.FindPath;

import java.util.List;

/**
 * Interface for shortest path solvers.
 * @source Josh Hug.
 */
public interface ShortestPathsSolver<Vertex> {
    SolverOutcome outcome();
    List<Vertex> solution();
    double solutionWeight();
    int numStatesExplored();
    double explorationTime();
}
