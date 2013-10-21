package ca.usask.ca.srlab.surfclipse.client.handlers;
import java.util.ArrayList;

import org.eclipse.core.commands.*;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.jface.text.*;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.framelist.UpAction;
import org.eclipse.ui.internal.views.log.LogEntry;

import ca.usask.ca.srlab.surfclipse.client.views.SurfClipseClientView;
import core.*;


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
