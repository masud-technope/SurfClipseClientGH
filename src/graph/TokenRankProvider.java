package graph;

import java.util.HashMap;
import java.util.Set;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import core.MetricWeights;
import core.QueryToken;

public class TokenRankProvider {
	public DirectedGraph<String, DefaultEdge> graph;
	HashMap<String, QueryToken> tokendb;
	HashMap<String, Double> oldScoreMap;
	HashMap<String, Double> newScoreMap;

	public TokenRankProvider(DirectedGraph<String, DefaultEdge> graph,
			HashMap<String, QueryToken> tokendb) {
		// initialization of different objects
		this.graph = graph;
		this.tokendb = tokendb;
		this.oldScoreMap = new HashMap<>();
		this.newScoreMap = new HashMap<>();
	}

	boolean checkSignificantDiff(double oldV, double newV) {
		double diff = 0;
		if (newV > oldV)
			diff = newV - oldV;
		else
			diff = oldV - newV;
		return diff > MetricWeights.SIGNIFICANCE_THRESHOLD ? true : false;
	}

	protected HashMap<String, QueryToken> calculateTokenRank() {
		// calculating token rank score
		double d = 0.85;
		double N = graph.vertexSet().size();
		// initially putting 1 to all
		for (String vertex : graph.vertexSet()) {
			oldScoreMap.put(vertex, 1.00);
			newScoreMap.put(vertex, 1.00);
		}
		boolean enoughIteration = false;
		int itercount = 0;

		while (!enoughIteration) {
			int insignificant = 0;
			for (String vertex : graph.vertexSet()) {
				Set<DefaultEdge> incomings = graph.incomingEdgesOf(vertex);
				// now calculate the PR score
				double trank = (1 - d);
				double comingScore = 0;
				for (DefaultEdge edge : incomings) {
					String source1 = graph.getEdgeSource(edge);
					int outdegree = graph.outDegreeOf(source1);
					double score = oldScoreMap.get(source1);
					if (outdegree == 0)
						comingScore += score;
					else
						comingScore += (score / outdegree);
				}
				comingScore = comingScore * d;
				trank += comingScore;
				boolean significant = checkSignificantDiff(
						oldScoreMap.get(vertex).doubleValue(), trank);
				if (significant) {
					newScoreMap.put(vertex, trank);
				} else {
					insignificant++;
				}
			}
			// coping values to new Hash Map
			for (String key : newScoreMap.keySet()) {
				oldScoreMap.put(key, newScoreMap.get(key));
			}
			itercount++;
			if (insignificant == graph.vertexSet().size())
				enoughIteration = true;
			if (itercount == 100)
				enoughIteration = true;
		}
		System.out.println("Iter count:" + itercount);
		// saving token ranks
		recordNormalizeScores();
		//showTokenRanks();
		return this.tokendb;
	}

	protected void recordNormalizeScores() {
		// record normalized scores
		double maxRank = 0;
		for (String key : newScoreMap.keySet()) {
			double score = newScoreMap.get(key).doubleValue();
			if (score > maxRank) {
				maxRank = score;
			}
		}
		for (String key : newScoreMap.keySet()) {
			double score = newScoreMap.get(key).doubleValue();
			score = score / maxRank;
			// this.newScoreMap.put(key, score);
			QueryToken qtoken = tokendb.get(key);
			qtoken.tokenRankScore = score;
			tokendb.put(key, qtoken);
		}
	}

	protected void showTokenRanks() {
		// showing token ranks
		for (String key : this.tokendb.keySet()) {
			System.out.println(key + " " + tokendb.get(key).tokenRankScore);
		}
	}

}
