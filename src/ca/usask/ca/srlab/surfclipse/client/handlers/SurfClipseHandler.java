package ca.usask.ca.srlab.surfclipse.client.handlers;
import org.eclipse.core.commands.*;


public class SurfClipseHandler extends AbstractHandler{
	String searchQuery;
	String stacktrace;
	String sourcecode_context;
	String current_exception_message;
	static int typeofSelection=0; //0: from console, 1: from Error log or other view
	int total_results_returned=0;
	
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// code for event handling on the menu item
		try {
			long started=System.currentTimeMillis();
			SearchEventManager searchManager=new SearchEventManager();
			searchManager.fire_search_operation2();
			long ended=System.currentTimeMillis();
			System.out.println("Time elapsed:"+(ended-started)/1000);
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
}
