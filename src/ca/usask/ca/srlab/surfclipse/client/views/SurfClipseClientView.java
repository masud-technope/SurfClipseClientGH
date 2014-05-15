package ca.usask.ca.srlab.surfclipse.client.views;


import history.HistoryLink;
import history.RecencyScoreManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mode.SurfClipseModeManager;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.part.*;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;

import querysuggest.SCQueryMaker;


import ca.usask.ca.srlab.surfclipse.client.Activator;
import ca.usask.ca.srlab.surfclipse.client.ActiveConsoleChecker;
import ca.usask.ca.srlab.surfclipse.client.handlers.SearchEventManager;
import core.Result;



/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class SurfClipseClientView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String Client_View_ID = "ca.usask.ca.srlab.surfclipse.client.views.SurfClipseClientView";

	public TableViewer viewer;
	//private Action action1;
	//private Action action2;
	//private Action doubleClickAction;
	//array containing Tab URL
	
	Display display=null;
	Shell shell=null;
	static ArrayList<String> suggestions=new ArrayList<>();
	public Label timerLabel;
	public Text input=null;
	ContentProposalAdapter adapter = null;
	static FocusListener flistener=null;

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	class ViewContentProvider implements IStructuredContentProvider {
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		
		}
		public void dispose() {
		
		}
		public Object[] getElements(Object parent) {
			return new String[]{};
		}
	}


	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			//Object myobj=getText(obj);
			Result myresult=(Result)obj;
			switch(index)
			{
			case 0:
				if(myresult.title!=null)return myresult.title;
				return "";
			case 1:
				if(myresult.resultURL!=null)return myresult.resultURL;
				return "";
			case 2:
				if(myresult.totalScore_content_context_popularity>=0)return myresult.totalScore_content_context_popularity+"";
				return "";
			case 3:
				double content_relevance=myresult.content_score;
				return content_relevance+""; 
			case 4:
				double context_relevance=myresult.context_score;
				return context_relevance+"";
			case 5:
				double popularity=myresult.popularity_score;
				return popularity+"";
			case 6:
				double confidence=myresult.search_result_confidence;
				return confidence+"";
			default:
				return "";
			}
		}
		public Image getColumnImage(Object obj, int index) {
			
			if(index>0)return null;
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			
			Image img=null;
			try
			{
				Result result=(Result)obj;
				if(result.resultURL.contains("stackoverflow"))
				{
					img=ImageDescriptor.createFromFile(ViewLabelProvider.class, "stackoverflow.png").createImage();
				}else
				img=ImageDescriptor.createFromFile(ViewLabelProvider.class, "answer.png").createImage();
			
			}catch(Exception exc){
				//exc.printStackTrace();
				System.err.println(exc.getMessage());
			}
			return img;
		}		
	}
	
	
	class MyTableSorter extends ViewerSorter {
		//table sorter class
		private static final int ASCENDING = 0;
		private static final int DESCENDING = 1;
		private int column;
		private int direction;
		public void doSort(int column) {
			    if (column == this.column) {
			      direction = 1 - direction;
			    } else {
			      this.column = column;
			      direction = ASCENDING;
			    }
			  }
		
		
		public int compare(double d1, double d2)
		{
			int resp=0;
			if(d1>d2)resp=1;
			else if(d1<d2)resp=-1;
			return resp;
		}
		
		
		public int compare(Viewer viewer, Object e1, Object e2) {
			int rc = 0;
			Result result1 = (Result) e1;
			Result result2 = (Result) e2;
			switch (column) {
			case 2:
				rc = compare(result1.totalScore_content_context_popularity,
						result2.totalScore_content_context_popularity);
				break;
			case 3:
				rc = compare(result1.content_score, result2.content_score);
				break;
			case 4:
				rc = compare(result1.context_score, result2.context_score);
				break;
			case 5:
				rc = compare(result1.popularity_score, result2.popularity_score);
				break;
			case 6:
				rc = compare(result1.search_result_confidence,
						result2.search_result_confidence);
				break;
			}

			if (direction == DESCENDING)
				rc = -rc;
			return rc;
		}
	}
	
	protected Image get_search_image()
	{
		return ImageDescriptor.createFromFile(ViewLabelProvider.class, "searchbt16.gif").createImage();
	}
	
	protected ArrayList<String> formattingKeywordQuery(ArrayList<String> rawSuggestions)
	{
		//code for formatting keyword query
		ArrayList<String> temp=new ArrayList<>();
		String query=new String();
		for(String item:rawSuggestions){
			query+=item.trim()+" ";
			temp.add(query);
		}
		return temp;
	}

	
	protected void add_related_exception_message(Composite parent,HashMap<String,ArrayList<Integer>> pointers)
	{
		// code for showing related exception message
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 10;
		gridLayout.verticalSpacing = 5;
		gridLayout.horizontalSpacing =5;
		composite.setLayout(gridLayout);

		GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		composite.setLayoutData(gridData);

		//gridData = new GridData(SWT.DEFAULT, SWT.FILL, false, false);
		GridData gdata2=new GridData();
		gdata2.heightHint=25;
		gdata2.widthHint=600;
		gdata2.horizontalAlignment=SWT.BEGINNING;
		gdata2.verticalAlignment=SWT.CENTER;
		gdata2.grabExcessHorizontalSpace=false;
		
		
		Label keywordlabel=new Label(composite, SWT.NONE);
		//final Image image=ImageDescriptor.createFromFile(SurfClipseClientView.class, "sclogo4.png").createImage();
		//keywordlabel.setImage(image);
		keywordlabel.setText("Keywords:");
		keywordlabel.setFont(new Font(composite.getDisplay(), "Arial",11, SWT.BOLD));
		
		
		
		input = new Text(composite, SWT.SINGLE | SWT.BORDER );
		input.setEditable(true);
		input.setToolTipText("Enter your search keywords (e.g., java.lang.ClassNotFoundException JDBC Driver)");
		Font myfont = new Font(composite.getDisplay(), "Arial", 11, SWT.NORMAL);
		input.setFont(myfont);
		input.setLayoutData(gdata2);
		
	    flistener=new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				//do nothing
				//System.out.println("Focus lost");
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				//System.out.println("Focus gained");
				if(suggestions.size()==0) //suggestions.size()==0
				{
					try
					{
					SearchEventManager manager=new SearchEventManager();
					String strace=manager.extract_stacktrace_from_console();
					String ccontext=manager.extract_source_code_context(strace);
					SCQueryMaker maker=new SCQueryMaker("", strace, ccontext);
					ArrayList<String> rawSuggestions=maker.getSearchQuerySuggestions();
					suggestions=formattingKeywordQuery(rawSuggestions);
					//current exception name
					String currentExceptionName=suggestions.get(0);
					//now assign the auto completion feature
					try {
						//collecting context information
						String[] proposals = suggestions.toArray(new String[suggestions.size()]);
						if(proposals.length>0){
						//ContentProposalAdapter adapter = null;
						SimpleContentProposalProvider scp = new SimpleContentProposalProvider(
								proposals);
						
						//setting filtering
						scp.setFiltering(true);
						
						//String keyPress = "Ctrl+Space";
						//KeyStroke ks = KeyStroke.getInstance(keyPress);
						//extracting the input
						//Text input=(Text)e.getSource();
						String autoactive="abcdefghijklmnopqrstuvwxyz0123456789";
						adapter = new ContentProposalAdapter(input,
								new TextContentAdapter(), scp, null, autoactive.toCharArray()); //keystroke is ignored
						adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

						//setting exception name
						//input.setText(currentExceptionName);
						} 
					} catch (Exception exc) {
						exc.printStackTrace();
					}
					}catch(Exception exc){}
				}
			}
			
			
		};
		if(!input.isListening(SWT.FOCUSED)){
		input.addFocusListener(flistener);
		}
		
		//final Button confirm; //=new Button(composite,SWT.CHECK);
		//confirm.setText("Associate context");
		//confirm.setLayoutData(gdata2);
		//Label blank=new Label(composite,SWT.NONE);
		
		GridData gdata3=new GridData();
		gdata3.heightHint=30;
		gdata3.widthHint=90;
		gdata3.horizontalAlignment=SWT.BEGINNING;
		//gdata2.grabExcessHorizontalSpace=true;
		
		Button searchButton=new Button(composite, SWT.PUSH);
		searchButton.setText("Search");
		searchButton.setToolTipText("Search with SurfClipse");
		searchButton.setFont(new Font(composite.getDisplay(), "Arial",10, SWT.BOLD));
		searchButton.setImage(get_search_image());
		//System.out.println("Search Icon:"+Display.getDefault().getSystemImage(SWT.ICON_SEARCH));
		searchButton.setLayoutData(gdata3);
		
		/*timerLabel=new Label(composite, SWT.NONE);
		timerLabel.setFont(new Font(composite.getDisplay(), "Arial",11, SWT.BOLD));
		final Color myColor = new Color(composite.getDisplay(), 00, 102, 255);
		timerLabel.setForeground(myColor);
		timerLabel.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e)
            {
                myColor.dispose();
            }
        });
		timerLabel.setText("Time:");*/
		
		//final Label progressLabel=new Label(composite.getShell(), SWT.BORDER);
		//Image image=ImageDescriptor.createFromFile(SurfClipseClientView.class, "progress.gif").createImage();
		//progressLabel.setImage(image);
		
		
		final Composite composite2 = new Composite(parent, SWT.NONE);
		GridLayout gridLayout2 = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 5;
		gridLayout.horizontalSpacing =5;
		composite2.setLayout(gridLayout2);

		GridData gridData2 = new GridData(SWT.CENTER, SWT.FILL, true, false);
		composite2.setLayoutData(gridData2);
		
		//Label blank=new Label(composite,SWT.NONE);
		Label info=new Label(composite2, SWT.NONE);
		info.setText("Enter your search keywords (e.g., java.lang.ClassNotFoundException JDBC Driver)");
		final Button confirm=new Button(composite2,SWT.CHECK);
		confirm.setText("Associate context");
		final Button clearButton=new Button(composite2,SWT.CHECK);
		clearButton.setText("Reset search");

		//adding listener to clear button
		clearButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				suggestions.clear();
				input.setText("");
				viewer.setContentProvider(new ViewContentProvider());
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		
		//searchButton.setLayoutData(gridData);
		searchButton.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						//showing progress
						//Image image=ImageDescriptor.createFromFile(SurfClipseClientView.class, "progress.gif").createImage();
						//ImageData imageData=new ImageData(getClass().getResourceAsStream("progress.gif"));
						//Image image=new Image(composite.getDisplay(), imageData);
						//System.out.println(image);
						//progressLabel.setImage(image);
						//progressLabel.pack();
						
						// TODO Auto-generated method stub
						String searchQuery=input.getText();
						if(!searchQuery.isEmpty()){
						boolean associate_context=false;
						//making the search
						SearchEventManager manager=new SearchEventManager();
						if(confirm.getSelection())associate_context=true;
						manager.fire_keyword_search(searchQuery,associate_context);
						//showing progress bar
						
						//clearing the suggestions
						suggestions.clear();
						
						//removes the listener
						//adapter=null;
						//input.removeFocusListener(flistener);
						}else{
							showMessageBox("Please enter your query for search");
						}
						
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
					}
				});
		
		// adding key listener to input
		input.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.CR:
					try {
						//System.out.println("Search initiated..");
						// initiating the search
						// TODO Auto-generated method stub
						String searchQuery = input.getText();
						if(!searchQuery.isEmpty()){
						//boolean associate_context = false;
						// making the search
						/*SearchEventManager manager = new SearchEventManager();
						if (confirm.getSelection())
							associate_context = true;
						manager.fire_keyword_search(searchQuery,
								associate_context);
						*/// showing progress bar
						// clearing the suggestions
						//suggestions.clear();
						}else{
							//showMessageBox("Please enter your query for search");
						}
					} catch (Exception exc) {
					}
					break;
				case SWT.DEL:
					// clearing the search input box
					input.setText("");
					break;
				case SWT.ESC:
					//canceling the search input
					//input.setText("");
					break;
				}
			}
		});
		
	}
	
	
	
	protected void add_result_table(Composite parent)
	{
		//code for adding the table viewer
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		//viewer.setSorter(new MyTableSorter());
		final Table table=viewer.getTable();
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		/*this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			final ToolTip tip=new ToolTip(table.getShell(), SWT.BALLOON);
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// TODO Auto-generated method stub
				System.out.println("Single clicked");
				IStructuredSelection selection=(IStructuredSelection) event.getSelection();
				Result selectedResult=(Result)selection.getFirstElement();
				if(selectedResult!=null){
				tip.setText(selectedResult.title);
				String description=selectedResult.description;
				DecimalFormat df=new DecimalFormat("##.00");
				String content=df.format(selectedResult.content_score*100);
				String context=df.format(selectedResult.context_score*100);
				String popularity=df.format(selectedResult.popularity_score*100);
				description+="\nContent relevance: "+content+"%";
				description+="\nContext relevance: "+context+"%";
				description+="\nRelative Popularity: "+popularity+"%";
				tip.setMessage(description+"\n\n"+selectedResult.resultURL);
				tip.getDisplay().syncExec(new Runnable() {
                    public void run() {
                        tip.setVisible(true);
                    }
                });  
				}
			}
		});*/
		
		//Tool tip for the table
		final ToolTip tip=new ToolTip(table.getShell(), SWT.BALLOON);
		//tip.setText("Result table");
		//tip.setMessage("This is the result table");
		tip.setAutoHide(true);
		table.addListener(SWT.MouseHover, new Listener() {
            public void handleEvent(Event event) {
            	try
            	{
            	TableItem item=table.getItem(new Point(event.x, event.y));
            	tip.setText(item.getText(0));
            	String description="";
				DecimalFormat df=new DecimalFormat("##.00");
				String content=df.format(Double.parseDouble(item.getText(3)) *100);
				String context=df.format(Double.parseDouble(item.getText(4))*100);
				String popularity=df.format(Double.parseDouble(item.getText(5))*100);
				String confidence=df.format(Double.parseDouble(item.getText(6))*100);
				description+="Content relevance: "+content+"%";
				description+="\nContext relevance: "+context+"%";
				description+="\nRelative popularity: "+popularity+"%";
				description+="\nResult confidence: "+confidence+"%";
				tip.setMessage(description+"\n\n"+item.getText(1));
            	
                tip.getDisplay().timerExec(50, new Runnable() {
                    public void run() {
                        tip.setVisible(true);
                    }
                }); }catch(Exception exc){}            
            }
        });
		table.addListener(SWT.MouseExit, new Listener() {
            public void handleEvent(Event event) {
                tip.getDisplay().timerExec(50, new Runnable() {
                    public void run() {
                        tip.setVisible(false);
                    }
                });
            }
        });
		
		String[] columnNames={"Title","URL","Score","Content Relevance","Context Relevance","Popularity","Confidence"};
		int[] colWidth={400,500,200,200,200,200, 200};
		int[] colAlignment={SWT.LEFT,SWT.LEFT, SWT.LEFT,SWT.LEFT,SWT.LEFT,SWT.LEFT, SWT.LEFT};
		for ( int i = 0; i < columnNames.length; i++) {
			
			//stored for sorting
			final int columnNum=i;
			
			TableColumn col = new TableColumn(table, colAlignment[i]);
			col.setText(columnNames[i]);
			col.setWidth(colWidth[i]);
			//col.setMoveable(true);
			col.addSelectionListener(new SelectionAdapter() {
			      public void widgetSelected(SelectionEvent event) {
			        ((MyTableSorter) viewer.getSorter()).doSort(columnNum);
			        viewer.refresh();
			      }
			    });
			// col.setImage(getDefaultImage());
		}
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new MyTableSorter());
		//viewer.setSorter(new TableColumnSorter());
		viewer.setInput(getViewSite());
		viewer.addDoubleClickListener(new IDoubleClickListener(){

			@Override
			public void doubleClick(DoubleClickEvent event) {
				// TODO Auto-generated method stub
				
				System.out.println("Double clicked");
				
				IStructuredSelection selection=(IStructuredSelection)event.getSelection();
				if(selection.isEmpty())return;
				@SuppressWarnings("unchecked")
				List<Object> list=selection.toList();
				Object obj1=list.get(0);
				//System.out.println("Clicked on:"+((Result)obj1).title);
				Result selected=(Result)obj1;
				System.out.println("Selected URL:"+selected.resultURL);
				try
				{
					//SurfClipseBrowser surfBrowser=new SurfClipseBrowser();
					//surfBrowser.show_the_result_link(selected.title, selected.resultURL);
					String viewID="ca.usask.ca.srlab.surfclipse.client.views.SurfClipseBrowser";
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewID);
					
					IWorkbenchPage page =(IWorkbenchPage)PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage();
					IViewPart vpart=page.findView(viewID);
					SurfClipseBrowser my_browser_view=(SurfClipseBrowser)vpart;
					Browser mybrowser=my_browser_view.webbrowser;
					mybrowser.setUrl(selected.resultURL);
					//showing page title
					//Label pageLabel=my_browser_view.pageLabel;
					//pageLabel.setText(selected.title);
					//adding currently displayed url
					add_currently_displayed_url(selected.resultURL);
					
				}catch(Exception exc){
					//System.err.println(exc.getMessage());
					exc.printStackTrace();
				}
			}
		});
		
	}
	
	public void createPartControl(final Composite parent) {

		GridLayout glayout=new GridLayout();
		glayout.marginWidth=15;
		glayout.marginHeight=10;
		parent.setLayout(glayout);
		
		GridData gdata=new GridData(SWT.FILL, SWT.FILL, true, true);
		parent.setLayoutData(gdata);
		
		add_related_exception_message(parent, null);
		add_result_table(parent);
		
		//adding to the Help system
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "ca.usask.ca.srlab.surfclipse.client.viewer");
	}

	protected void add_currently_displayed_url(String currentURL)
	{
		//code for adding the current URL
		try
		{
		RecencyScoreManager recenyScoreManager=Activator.recenyScoreManager;
		ArrayList<HistoryLink> RecentFiles=RecencyScoreManager.RecentFiles;
		//removing the existing entries in top 20
		//update with new visiting time
		ArrayList<HistoryLink> tempList=new ArrayList<>();
		tempList.addAll(RecencyScoreManager.RecentFiles);
				
		
		for(HistoryLink hlink:tempList)
		{
			if(hlink.linkURL.equals(currentURL))
			{
				RecentFiles.remove(hlink);
			}
		}
		
		long last_visit=System.currentTimeMillis()/1000;
		
		//adding new history links
		HistoryLink mylink=new HistoryLink();
		mylink.linkURL=currentURL;
		mylink.last_visit_time=last_visit;
		
		RecentFiles.add(0,mylink);
		RecencyScoreManager.RecentFiles=RecentFiles;
		
		}catch(Exception exc)
		{
			exc.printStackTrace();
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
	
	
	/**
	 * The constructor.
	 */
	
	public SurfClipseClientView() {
	}

	public void setFocus() {
		if(SurfClipseModeManager.current_mode==1)
		{
		new Thread(new ActiveConsoleChecker()).start();
		}
		viewer.getControl().setFocus();
		//setup_console_listener();
		//System.out.println("I am focused..");
		
	}	
}