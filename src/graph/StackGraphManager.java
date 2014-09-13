package graph;

import java.util.ArrayList;
import java.util.HashMap;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import core.QueryToken;
import core.StackTraceElem;

public class StackGraphManager {
	
	ArrayList<StackTraceElem> traces;
	HashMap<String, QueryToken> tokendb;
	public StackGraphManager()
	{
		//default constructor
	}

	public StackGraphManager(ArrayList<StackTraceElem> traces,
			HashMap<String, QueryToken> tokendb) {
		// initialization
		this.traces = traces;
		this.tokendb = tokendb;
	}
	
	
	public DirectedGraph<String, DefaultEdge> createStackGraph()
	{
		//developing the traces graph
		DirectedGraph<String, DefaultEdge> graph=new DefaultDirectedGraph<>(DefaultEdge.class);
		String previousMethod=new String();
		for(StackTraceElem elem:this.traces){
			String className=elem.className;
			String methodName=elem.methodName;
			
			//adding vertices
			if(!graph.containsVertex(className))
			graph.addVertex(className);
			if(!graph.containsVertex(methodName))
			graph.addVertex(methodName);
			
			//adding edges
			if(!graph.containsEdge(methodName, className))
			graph.addEdge(className, methodName);
			
			if(previousMethod.isEmpty()){
				previousMethod=methodName;
			}else{
				if(!graph.containsEdge(previousMethod, methodName))
				graph.addEdge(methodName, previousMethod);
				previousMethod=methodName;
			}
		}
		//showGraph(graph);
		System.out.println("Visualizing the applet..");
		new StackGraphVisualizer(graph).visualize();
		
		return graph;
	}
	
	public HashMap<String, QueryToken> calculateTokenRankScore()
	{
		DirectedGraph< String, DefaultEdge> graph=createStackGraph();
		TokenRankProvider rankProvider=new TokenRankProvider(graph, tokendb);
		tokendb=rankProvider.calculateTokenRank();
		return tokendb;
	}
	
	protected void showGraph( DirectedGraph<String, DefaultEdge> g)
	{
		//showing the graph
		System.out.println(g.vertexSet());
		for(String key:g.vertexSet()){
			System.out.println(key+" "+ g.inDegreeOf(key)+" "+g.outDegreeOf(key));
		}
	}
	
	public static void main(String[] args){
	
	}
}
