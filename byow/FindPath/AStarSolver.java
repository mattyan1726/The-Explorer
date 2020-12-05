package byow.FindPath;

import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {

    private ArrayHeapMinPQ<Vertex> fringe;
    private HashMap<Vertex, Double> distTo;
    private HashMap<Vertex, WeightedEdge<Vertex>> edgeTo;
    private SolverOutcome outcome;
    private double solutionWeight;
    private List<Vertex> solution;
    private double timeSpent;
    private int numStatesExplored;

    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        numStatesExplored = 0;
        solutionWeight = 0;
        solution = new ArrayList<>();
        fringe = new ArrayHeapMinPQ<>();
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        distTo.put(start, 0.0);
        fringe.add(start, input.estimatedDistanceToGoal(start, end));
        Stopwatch sw = new Stopwatch();
        while (fringe.size() != 0) {
            if (sw.elapsedTime() > timeout) {
                outcome = SolverOutcome.TIMEOUT;
                return;
            }
            Vertex curr = fringe.removeSmallest();
            if (curr.equals(end)) {
                outcome = SolverOutcome.SOLVED;
                timeSpent = sw.elapsedTime();
                helper(start, end);
                return;
            }
            List<WeightedEdge<Vertex>> neighbors = input.neighbors(curr);
            relaxEdges(neighbors, input, end);
            numStatesExplored += 1;
        }
        outcome = SolverOutcome.UNSOLVABLE;
    }

    private void relaxEdges(List<WeightedEdge<Vertex>> edges,
                            AStarGraph<Vertex> input, Vertex end) {
        for (WeightedEdge<Vertex> edge : edges) {
            Vertex origin = edge.from();
            Vertex endpoint = edge.to();
            double weight = edge.weight();
            if (!distTo.containsKey(endpoint)) {
                distTo.put(endpoint, distTo.get(origin) + weight);
                edgeTo.put(endpoint, edge);
                fringe.add(endpoint, distTo.get(endpoint)
                        + input.estimatedDistanceToGoal(endpoint, end));
            } else {
                if (distTo.get(origin) + weight < distTo.get(endpoint)) {
                    distTo.put(endpoint, distTo.get(origin) + weight);
                    edgeTo.put(endpoint, edge);
                    if (fringe.contains(endpoint)) {
                        fringe.changePriority(endpoint, distTo.get(endpoint)
                                + input.estimatedDistanceToGoal(endpoint, end));
                    } else {
                        fringe.add(endpoint, distTo.get(endpoint)
                                + input.estimatedDistanceToGoal(endpoint, end));
                    }
                }
            }
        }
    }

    public SolverOutcome outcome() {
        return outcome;
    }

    public List<Vertex> solution() {
        return solution;
    }

    public double solutionWeight() {
        return solutionWeight;
    }

    public int numStatesExplored() {
        return numStatesExplored;
    }

    public double explorationTime() {
        return timeSpent;
    }

    private void helper(Vertex start, Vertex end) {
        List<Vertex> solutionHelper = new ArrayList<>();
        Vertex curr = end;
        solutionHelper.add(end);
        while (!curr.equals(start)) {
            WeightedEdge<Vertex> edge = edgeTo.get(curr);
            solutionHelper.add(edge.from());
            curr = edge.from();
            solutionWeight += edge.weight();
        }
        while (solutionHelper.size() != 0) {
            int lastIndex = solutionHelper.size() - 1;
            solution.add(solutionHelper.remove(lastIndex));
        }
    }
}
