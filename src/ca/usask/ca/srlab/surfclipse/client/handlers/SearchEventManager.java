package ca.usask.ca.srlab.surfclipse.client.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.views.log.LogEntry;
import org.eclipse.ui.texteditor.ITextEditor;

import core.Result;

import ca.usask.ca.srlab.surfclipse.client.views.SurfClipseClientView;

public class SearchEventManager {

	String searchQuery;
	String stacktrace;
	String sourcecode_context;
	Result[] collectedResults;
	static int typeofSelection=-1; //0: from console, 1: from Error log or other view
	int total_results_returned=0;
	
	static int mode_of_invocation=-1; // 0:interactive, 1: pro active 
	public static String currmatchedContent=new String();
	boolean continue_search=true;
	
	
	
	public SearchEventManager()
	{
		mode_of_invocation=0;
	}
	
	public SearchEventManager(String regex_matchedContent)
	{
		//for pro active triggering
		mode_of_invocation=1;
		
		System.out.println("Prev:"+currmatchedContent+", Current"+regex_matchedContent);
		if(regex_matchedContent.isEmpty())continue_search=false;
		if(regex_matchedContent.equals(currmatchedContent))
		{
			//do nothing
			System.out.println("Do nothing..");
			continue_search=false;
		}else
		{
			currmatchedContent=regex_matchedContent;
			//do pro active call
			System.out.println("Make the call");
			continue_search=true;
		}
	}
	
	public Result[] collect_search_results()
	{
		//System.out.println("Client accessing web service ....");
		MyClient client=new MyClient(searchQuery, stacktrace, sourcecode_context);
		ArrayList<Result> finalResults=client.collect_search_results();
		System.out.println("Results returned:"+finalResults.size());
		int total_result_size=finalResults.size();
		int result_size=total_result_size<30?total_result_size:30; //finalResults.size()
		Result[] myresults=new Result[result_size];
		total_results_returned=myresults.length;
		int counter=0;
		//showing top 20 results
		for(int i=0;i<result_size;i++)
		{
			Object obj=finalResults.get(i);
			Result result=(Result)obj;
			myresults[counter++]=result;
		}
		return myresults;
	}
	
	public void fire_search_operation2() {
		try {
			// code for firing search operation
			if (mode_of_invocation == 1) {  //pro active mode
				
				if (continue_search) {
					this.stacktrace = currmatchedContent;
					try {
						this.searchQuery = extract_error_message_from_stack(this.stacktrace);
						if (this.stacktrace == null)
							this.stacktrace = "";
					} catch (Exception exc) {
						this.stacktrace = "";
					}
					try {
						this.sourcecode_context = extract_source_code_context(this.stacktrace);
						if (this.sourcecode_context == null)
							this.sourcecode_context = "";
					} catch (Exception exc) {
						this.sourcecode_context = "";
					}
				}else return;
			} else if(mode_of_invocation==0) {   //interactive mode

				String selected_text = new String();
				selected_text = get_selected_message_from_IDE();
				if(selected_text.isEmpty() || selected_text==null)return;
				else this.searchQuery=selected_text;
				if (typeofSelection == 0) { //from console
					try {
						this.stacktrace = this.extract_stacktrace_from_console();
						if (this.stacktrace == null)
							this.stacktrace = "";
					} catch (Exception exc) {
						this.stacktrace = "";
					}
					try {
						this.sourcecode_context = this.extract_source_code_context(this.stacktrace);
						if (this.sourcecode_context == null)
							this.sourcecode_context = "";
					} catch (Exception exc) {
						this.sourcecode_context = "";
					}
				} else if (typeofSelection == 1) { //from Error log
					// selected text extracted
					// stack trace extracted
					// source code context not extracted
					try {
						this.sourcecode_context = this.extract_source_code_context(this.stacktrace);
						if (this.sourcecode_context == null)
							this.sourcecode_context = "";
					} catch (Exception exc) {
						this.sourcecode_context = "";
					}
				}
			}
			
			//extracted info
			System.out.println("Search query: "+searchQuery);
			System.out.println("Stack trace: "+this.stacktrace);
			System.out.println("Source code context: "+this.sourcecode_context);
			
			//at the least search query is non empty
			try
			{
			if(!this.searchQuery.isEmpty())
			{
				new Thread(){
					public void run()
					{
						collectedResults=collect_search_results();
						Display.getDefault().asyncExec(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try
								{
								final String viewID = "ca.usask.ca.srlab.surfclipse.client.views.SurfClipseClientView";
								PlatformUI.getWorkbench().getActiveWorkbenchWindow()
										.getActivePage().showView(viewID);
								
								update_surfclipse_view();
								}catch(Exception exc){
									exc.printStackTrace();
								}
							}
						});
					}
				}.start();
			}
			}catch(Exception exc){
				System.err.println(exc.getMessage());
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void fire_keyword_search(String keywords, boolean associate_context) {
		// variable assignment
		this.searchQuery = keywords;
		this.stacktrace = "";
		this.sourcecode_context = "";
		
		if(associate_context){
		//try to collect the context information
		try {
			this.stacktrace = this.extract_stacktrace_from_console();
			if (this.stacktrace == null)
				this.stacktrace = "";
		} catch (Exception exc) {
			this.stacktrace = "";
		}
		try {
			this.sourcecode_context = this.extract_source_code_context(this.stacktrace);
			if (this.sourcecode_context == null)
				this.sourcecode_context = "";
		} catch (Exception exc) {
			this.sourcecode_context = "";
		}}
		
		// performing the query
		try {
			if (!this.searchQuery.isEmpty()) {

				new Thread() {
					public void run() {
						collectedResults = collect_search_results();
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									final String viewID = "ca.usask.ca.srlab.surfclipse.client.views.SurfClipseClientView";
									PlatformUI.getWorkbench()
											.getActiveWorkbenchWindow()
											.getActivePage().showView(viewID);
									update_surfclipse_view();
								} catch (Exception exc) {
									exc.printStackTrace();
								}
							}
						});
					}
				}.start();
			}
		} catch (Exception exc) {
			System.err.println(exc.getMessage());
		}
	}

	protected String extract_error_message_from_stack(String extractedStack)
	{
		//code for extracting the error message from stack
		String[] stackElements = extractedStack.split("\n");
		String errorMessage=new String();
		errorMessage=stackElements[0];
		return errorMessage;
	}

	public String extract_stacktrace_from_console()
	{
		//code for extracting stack from console
		String extractedContent=new String();
		String stackPattern="^.+Exception(:)?[^\\n]+\\n(\\t+\\Qat \\E.+\\s+)+$";
		Pattern p=Pattern.compile(stackPattern);
		//console content
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole consoles[] = conMan.getConsoles();
		System.out.println("No of consoles:"+consoles.length);
		// get the default console opened
		TextConsole myConsole = (TextConsole) consoles[0];
		// storing the stack trace information.
		String consoleContent = myConsole.getDocument().get();
		//matching
		Matcher matcher=p.matcher(consoleContent);
		while(matcher.find())
		{
			extractedContent=consoleContent.substring(matcher.start(), matcher.end());
			break;
		}
		//returning
		return extractedContent;
	}
	
	public String extract_source_code_context(String extractedStack)
	{
		// code for extracting source code context
		String sourceContext = new String();
		String[] stackElements = extractedStack.split("\n");
		// current source code line
		String firstTraceLine = new String();
		for (String elem : stackElements) {
			if (elem.contains(".java:")) {
				firstTraceLine = elem;
				System.out.println("Trace of interest:"+elem);
				break;
			}
		}
		int open_brac = firstTraceLine.lastIndexOf('(');
		int close_brac = firstTraceLine.lastIndexOf(')');
		String fileNameLine = firstTraceLine.substring(open_brac + 1,
				close_brac);
		String parts[] = fileNameLine.split(":");
		String javaFileName = parts[0].trim();
		int lineNumber = Integer.parseInt(parts[1].trim());
		//System.out.println(javaFileName + " " + lineNumber);
		// now extract the context of the source line
		try
		{
		sourceContext= this.extract_the_context(javaFileName, lineNumber);
		}catch(Exception exc){}
		return sourceContext;
	}

	protected void open_target_project_file(final String fileName, final int lineNumber)
	{
		//code for opening target project file
		IWorkspace workspace=ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root=workspace.getRoot();
		try
		{
		root.accept(new IResourceVisitor() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean visit(IResource resource) throws CoreException {
				// TODO Auto-generated method stub
				if(resource.getType()==IResource.FILE)
				{
					if(resource.getName().endsWith(fileName))
					{
						//System.out.println(resource.getLocation().toOSString());
						IFile fileToBeOpened=(IFile)resource;
						IWorkbenchPage page=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						HashMap map=new HashMap();
						map.put(IMarker.LINE_NUMBER, new Integer(lineNumber));
						//map.put(IWorkbenchPage.EDITOR_ID_ATTR, "org.eclipse.ui.DefaultTextEditor");
						IMarker marker=fileToBeOpened.createMarker(IMarker.TEXT);
						marker.setAttributes(map);
						IDE.openEditor(page,fileToBeOpened);
						
						return false;
					}
				}
				return true;
			}
		});
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}
	
	protected String extract_the_context(String fileName, int lineNumber) {
		
		//opening the target file
		open_target_project_file(fileName, lineNumber);
		//collect contextual codes
		String context = new String();
		
		int block_depth = 3;
		// code for returning the context
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			ITextEditor editor = (ITextEditor) page.getActiveEditor();
			IDocument doc = editor.getDocumentProvider().getDocument(
					editor.getEditorInput());
			String fileContent = doc.get();
			String[] lines = fileContent.split("\n");
			int start_line = lineNumber > block_depth ? lineNumber
					- block_depth : 0;
			int end_line = lineNumber < (lines.length - block_depth) ? (lines.length - block_depth)
					: lines.length;
			for (int i = start_line; i < end_line; i++) {
				context += lines[i] + "\n";
			}
		} catch (Exception exc) {
			//System.err.println(exc.getMessage());
		}
		return context;
	}
	
	@SuppressWarnings("restriction")
	protected String get_selected_message_from_IDE()
	{
		String selected_text=new String();
		try
		{	
			IWorkbenchPage page =(IWorkbenchPage)PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage();
			ISelection selection=page.getSelection();
			//ISelection selection=(ISelection)page.getActiveEditor().getSite().getSelectionProvider().getSelection();
			//in case of text selection
			if(selection instanceof TextSelection){
				typeofSelection=0;
				selected_text=((TextSelection)selection).getText();
			}
			else if(selection instanceof IStructuredSelection)
			{
				typeofSelection=1; //selected from view
				//selected_text=((IStructuredSelection)selection).getFirstElement().toString();
				//System.out.println(((IStructuredSelection)selection).getFirstElement());
				TreeSelection itree=(TreeSelection)selection;
				Object[] myobj=itree.toArray();
				LogEntry entry=(LogEntry)myobj[0];
				//error message
				selected_text=entry.getMessage();
				//stack trace
				this.stacktrace=entry.getStack();
				//System.out.println(entry.getMessage());
			}
		}catch(Exception exc){
			//exc.printStackTrace();
		}
		//returning the text
		return selected_text;
	}
	
	
	protected void update_surfclipse_view()
	{
		//code for updating surfClipse view
		try
		{
			IWorkbenchPage page =(IWorkbenchPage)PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage();
			String viewID="ca.usask.ca.srlab.surfclipse.client.views.SurfClipseClientView";
			IViewPart vpart=page.findView(viewID);
			SurfClipseClientView myview=(SurfClipseClientView)vpart;
			//System.out.println(myview.viewer.toString());
			ViewContentProvider viewContentProvider=new ViewContentProvider(collectedResults);
			myview.viewer.setContentProvider(viewContentProvider);
			//myview.viewer.setSorter(new TableColumnSorter());
			//myview.viewer.setInput(this.getvi);
		}catch(Exception exc){
			//System.err.println(exc.getMessage());
			//System.err.println("Failed to update Eclipse view"+exc.getMessage());
			exc.printStackTrace();
			String message="Failed to collect search results. Please try again.";
			showMessageBox(message);
			
		}
	}
	
	protected void showMessageBox(String message)
	{
		//code for showing message box
		try
		{
		Shell shell=Display.getDefault().getShells()[0];
		MessageDialog.openInformation(shell, "Information", message);
		}catch(Exception exc){}
	}
}
