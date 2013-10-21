package ca.usask.ca.srlab.surfclipse.client.handlers;

import java.util.ArrayList;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import core.Result;

public class ViewContentProvider implements IStructuredContentProvider {
	
	String searchQuery;
	String stacktrace;
	String sourcecode_context;
	public int total_results_returned;
	
	public ViewContentProvider(String searchQuery, String stacktrace, String codeContext)
	{
		//assigning members
		this.searchQuery=searchQuery;
		this.stacktrace=stacktrace;
		this.sourcecode_context=codeContext;
	}
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	public void dispose() {
	}
	public Object[] getElements(Object parent) {
		
		//populating the results
		//SearchResultProvider provider=new SearchResultProvider(searchQuery, stacktrace, sourcecode_context);
		System.out.println("Client accessing web service ....");
		MyClient client=new MyClient(searchQuery, stacktrace, sourcecode_context);
		
		ArrayList<Result> finalResults=client.collect_search_results();
		System.out.println("Results returned:"+finalResults.size());
		int result_size=20; //finalResults.size()
		Result[] myresults=new Result[result_size];
		total_results_returned=myresults.length;
		int counter=0;
		
		//showing all results
		/*for(Object obj:finalResults)
		{
			Result result=(Result)obj;
			myresults[counter++]=result;
		}*/
		//showing top 20 results
		for(int i=0;i<20;i++)
		{
			Object obj=finalResults.get(i);
			Result result=(Result)obj;
			myresults[counter++]=result;
		}
		
		return myresults;
	}
}
