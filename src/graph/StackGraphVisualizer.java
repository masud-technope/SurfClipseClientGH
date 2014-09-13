package graph;
import org.eclipse.ui.PlatformUI;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import ca.usask.ca.srlab.surfclipse.client.views.StackTraceView;

public class StackGraphVisualizer{
	
	DirectedGraph<String, DefaultEdge> sgraph;
	public StackGraphVisualizer(DirectedGraph<String, DefaultEdge> sgraph)
	{
		//initialization
		this.sgraph=sgraph;
	}
	
	public void visualize()
	{
		//visualizing the graph
		/*JFrame frame=new JFrame();
		StackGraph applet=new StackGraph(sgraph, 700, 600);
		frame.getContentPane().add(applet);
		frame.setVisible(true);
		frame.setSize(700,600);
		applet.init();
		applet.start();*/
		try{
		String viewID="ca.usask.ca.srlab.surfclipse.client.views.StackTraceView";
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewID);
		StackTraceView stackTraceView= (StackTraceView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(viewID);
		//StackGraph myApplet=new StackGraph(this.sgraph, 700, 600);
		stackTraceView.sgraphApplet.stackgraph=this.sgraph;
		stackTraceView.sgraphApplet.maxWidth=700;
		stackTraceView.sgraphApplet.maxHeight=600;
		stackTraceView.sgraphApplet.init();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewID);
		//show this view
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}
