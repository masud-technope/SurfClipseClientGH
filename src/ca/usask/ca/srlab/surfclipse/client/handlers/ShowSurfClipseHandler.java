package ca.usask.ca.srlab.surfclipse.client.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

public class ShowSurfClipseHandler extends AbstractHandler{

	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// code for event handling on the menu item
		try {
			//code for showing SurfClipse View
			
			
			String SCviewID="ca.usask.ca.srlab.surfclipse.client.views.SurfClipseClientView";
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SCviewID);
			
			String SCBviewID="ca.usask.ca.srlab.surfclipse.client.views.SurfClipseBrowser";
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SCBviewID);
			
			String STviewID="ca.usask.ca.srlab.surfclipse.client.views.StackTraceView";
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(STviewID);
			
			System.out.println("Surfclipse windows shown successfully");
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return null;
	}
	
}
