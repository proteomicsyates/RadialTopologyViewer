package netQuant.display;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import netQuant.DemoTools;
import netQuant.graph.Graph;
import netQuant.graph.Node;

public class TestParallelDijkstra {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		DemoTools dt = new DemoTools();

		Graph g = dt.randomGraph(100, 50);

		double[][] m = new double[100][100];

		List<Node> nodes = new ArrayList<Node>();
		final Iterator<Node> nodesIterator = g.nodesIterator();
		while (nodesIterator.hasNext()) {
			nodes.add(nodesIterator.next());
		}
		ParallelDijkstra pd = new ParallelDijkstra(nodes, g.getEdges(), 0, m);
		pd.run();

		LinkedList<Node> path = pd.getPath(g.getNode(g.getEdges().get(6).getToName()));

		for (Node n : path) {
			System.out.println(n);
		}
	}

}
