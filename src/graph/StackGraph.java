package graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.JApplet;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.ListenableDirectedGraph;

public class StackGraph extends JApplet{
	
	DirectedGraph<String, DefaultEdge> stackgraph;
	int maxWidth;
	int maxHeight;
	public StackGraph(DirectedGraph<String, DefaultEdge> stackgraph, int maxWidth, int maxHeight)
	{
		//initialization
		this.stackgraph=stackgraph;
		this.maxWidth=maxWidth;
		this.maxHeight=maxHeight;
	}
	
	public StackGraph()
	{
		//default constructor
	}
	
	
	private static final Color     DEFAULT_BG_COLOR = Color.decode( "#ffffff" );
    private static final Dimension DEFAULT_SIZE = new Dimension( 700, 600 );
    private JGraphModelAdapter m_jgAdapter;
    
    public void init(  ) {
        
    	if(stackgraph.vertexSet().isEmpty())return;
    	
    	// create a JGraphT graph
        ListenableGraph g = new ListenableDirectedGraph( DefaultEdge.class );
        // create a visualization using JGraph, via an adapter
        m_jgAdapter = new JGraphModelAdapter( g );
        JGraph jgraph = new JGraph( m_jgAdapter );

        adjustDisplaySettings( jgraph );
        getContentPane(  ).add( jgraph );
        resize( DEFAULT_SIZE );

        //adding vertices
        for(String vertex:stackgraph.vertexSet()){
        	g.addVertex(vertex);
        }
        //adding edges
        for(DefaultEdge edge:stackgraph.edgeSet()){
        	//g.edgeSet().addAll(stackgraph.edgeSet());
        	String v1=stackgraph.getEdgeSource(edge);
        	String v2=stackgraph.getEdgeTarget(edge);
        	g.addEdge(v1, v2);
        }
        
        //positioning the vertices of the graph
        int xpos=100;
        int ypos=100;
        for(String vertex:stackgraph.vertexSet()){
        	Random random=new Random();
        	while(true){
        		int temp=random.nextInt(maxWidth);
        		if(temp>xpos && temp<maxWidth-100){
        			xpos=temp;
        			break;
        		}
        	}
        	while(true){
        		int temp=random.nextInt(maxHeight);
        		if(temp>ypos && temp<maxHeight-100){
        			ypos=temp;
        			break;
        		}
        	}
        	//now position the vertex
        	positionVertexAt(vertex, xpos, ypos);
        	xpos=100;
        	ypos=100;
        	
        }
        

        // position vertices nicely within JGraph component
       /* positionVertexAt( "v1", 130, 40 );
        positionVertexAt( "v2", 60, 200 );
        positionVertexAt( "v3", 310, 230 );
        positionVertexAt( "v4", 380, 70 );*/

        // that's all there is to it!...
    }
    
    private void adjustDisplaySettings( JGraph jg ) {
        jg.setPreferredSize( DEFAULT_SIZE );

        Color  c        = DEFAULT_BG_COLOR;
        String colorStr = null;

        try {
            colorStr = getParameter( "bgcolor" );
        }
         catch( Exception e ) {}

        if( colorStr != null ) {
            c = Color.decode( colorStr );
        }

        jg.setBackground( c );
    }


    private void positionVertexAt( Object vertex, int x, int y ) {
        DefaultGraphCell cell = m_jgAdapter.getVertexCell( vertex );
        Map              attr = cell.getAttributes(  );
        Rectangle2D        b    = (Rectangle2D) GraphConstants.getBounds( attr );
        //GraphConstants.setBounds( attr, new Rectangle2D( x, y, b.getWidth(), b.getHeight() ) );
        Rectangle rect=new Rectangle(x, y, (int)b.getWidth(), (int)b.getHeight());
        GraphConstants.setBounds( attr, rect );
        Map cellAttr = new HashMap(  );
        cellAttr.put( cell, attr );
        m_jgAdapter.edit( cellAttr, null, null, null );
    }
	
	
}
