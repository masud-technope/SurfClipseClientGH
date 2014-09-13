package ca.usask.ca.srlab.surfclipse.client.handlers;

import java.util.ArrayList;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import core.Result;

public class ViewContentProvider implements IStructuredContentProvider {
	
	String searchQuery;
	String stacktrace;
	String sourcecode_context;
	Result[] displayableResults;
	public int total_results_returned;
	
	public ViewContentProvider(String searchQuery, String stacktrace, String codeContext)
	{
		//assigning members
		this.searchQuery=searchQuery;
		this.stacktrace=stacktrace;
		this.sourcecode_context=codeContext;
	}
	
	public ViewContentProvider(Result[] results)
	{
		this.displayableResults=results;
	}
	
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	public void dispose() {
	}
	public Object[] getElements(Object parent) {
		//populating the results
		return this.displayableResults;
	}
}
