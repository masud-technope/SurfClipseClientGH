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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;
import querysuggest.SCQueryMaker;
import ca.usask.ca.srlab.surfclipse.client.Activator;
import ca.usask.ca.srlab.surfclipse.client.ActiveConsoleChecker;
import ca.usask.ca.srlab.surfclipse.client.handlers.SearchEventManager;
import core.QueryRecommender;
import core.Result;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class SurfClipseClientView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String Client_View_ID = "ca.usask.ca.srlab.surfclipse.client.views.SurfClipseClientView";

	public TableViewer viewer;
	// private Action action1;
	// private Action action2;
	// private Action doubleClickAction;
	// array containing Tab URL

	Display display = null;
	Shell shell = null;
	static ArrayList<String> suggestions = new ArrayList<>();
	public Label timerLabel;
	public Text input = null;
	ContentProposalAdapter adapter = null;
	static FocusListener flistener = null;
	final int TEXT_MARGIN = 3;
	final int MIN_HEIGHT=60;
	
	final Display currDisplay = Display.getCurrent();
	final TextLayout textLayout = new TextLayout(currDisplay);
	Font font1 = new Font(currDisplay, "Arial", 12, SWT.BOLD);
	Font font2 = new Font(currDisplay, "Arial", 10, SWT.NORMAL);
	Font font3 = new Font(currDisplay, "Arial", 10, SWT.NORMAL);
	Color blue = currDisplay.getSystemColor(SWT.COLOR_BLUE);
	Color green = currDisplay.getSystemColor(SWT.COLOR_DARK_GREEN);
	Color gray = currDisplay.getSystemColor(SWT.COLOR_DARK_GRAY);
	TextStyle style1 = new TextStyle(font1, blue, null);
	TextStyle style2 = new TextStyle(font2, green, null);
	TextStyle style3 = new TextStyle(font3, gray, null);

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	class ViewContentProvider implements IStructuredContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return new String[] {};
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			// Object myobj=getText(obj);
			Result myresult = (Result) obj;
			switch (index) {
			case 0:
				if (myresult.title != null)
					return myresult.title.trim()+"\n"+myresult.description.trim()+"\n"+myresult.resultURL;
				return "";
			case 1:
					return String.format("%.2f", myresult.totalScore_content_context_popularity * 100);
			case 2:
				return String.format("%.2f", myresult.content_score*100);
			case 3:
				return String.format("%.2f", myresult.context_score*100);
			case 4:
				return String.format("%.2f", myresult.search_result_confidence*100);
			default:
				return "";
			}
		}

		public Image getColumnImage(Object obj, int index) {

			if (index > 0)
				return null;
			return getImage(obj);
		}

		public Image getImage(Object obj) {

			Image img = null;
			try {
				Result result = (Result) obj;
				if (result.resultURL.contains("stackoverflow")) {
					img = ImageDescriptor.createFromFile(
							ViewLabelProvider.class, "stackoverflow.png")
							.createImage();
				} else
					img = ImageDescriptor.createFromFile(
							ViewLabelProvider.class, "answer.png")
							.createImage();

			} catch (Exception exc) {
				// exc.printStackTrace();
				System.err.println(exc.getMessage());
			}
			return img;
		}
	}

	class MyTableSorter extends ViewerSorter {
		// table sorter class
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

		public int compare(double d1, double d2) {
			int resp = 0;
			if (d1 > d2)
				resp = 1;
			else if (d1 < d2)
				resp = -1;
			return resp;
		}

		public int compare(Viewer viewer, Object e1, Object e2) {
			int rc = 0;
			Result result1 = (Result) e1;
			Result result2 = (Result) e2;
			switch (column) {
			case 1:
				rc = compare(result1.totalScore_content_context_popularity,
						result2.totalScore_content_context_popularity);
				break;
			case 2:
				rc = compare(result1.content_score, result2.content_score);
				break;
			case 3:
				rc = compare(result1.context_score, result2.context_score);
				break;
			case 4:
				rc = compare(result1.popularity_score, result2.popularity_score);
				break;
			case 5:
				rc = compare(result1.search_result_confidence,
						result2.search_result_confidence);
				break;
			}

			if (direction == DESCENDING)
				rc = -rc;
			return rc;
		}
	}

	protected Image get_search_image() {
		return ImageDescriptor.createFromFile(ViewLabelProvider.class,
				"searchbt16.gif").createImage();
	}

	protected ArrayList<String> formattingKeywordQuery(
			ArrayList<String> rawSuggestions) {
		// code for formatting keyword query
		ArrayList<String> temp = new ArrayList<>();
		String query = new String();
		for (String item : rawSuggestions) {
			query += item.trim() + " ";
			temp.add(query);
		}
		return temp;
	}

	protected Image getRefreshImage() {
		return ImageDescriptor.createFromFile(SurfClipseClientView.class,
				"refresh.png").createImage();
	}

	protected void add_related_exception_message(Composite parent,
			HashMap<String, ArrayList<Integer>> pointers) {
		// code for showing related exception message
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(5, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 10;
		gridLayout.verticalSpacing = 5;
		gridLayout.horizontalSpacing = 5;
		composite.setLayout(gridLayout);

		GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		composite.setLayoutData(gridData);

		// gridData = new GridData(SWT.DEFAULT, SWT.FILL, false, false);
		GridData gdata2 = new GridData();
		gdata2.heightHint = 25;
		gdata2.widthHint = 600;
		gdata2.horizontalAlignment = SWT.BEGINNING;
		gdata2.verticalAlignment = SWT.CENTER;
		gdata2.grabExcessHorizontalSpace = false;

		Label keywordlabel = new Label(composite, SWT.NONE);
		// final Image
		// image=ImageDescriptor.createFromFile(SurfClipseClientView.class,
		// "sclogo4.png").createImage();
		// keywordlabel.setImage(image);
		keywordlabel.setText("Keywords:");
		keywordlabel.setFont(new Font(composite.getDisplay(), "Arial", 11,
				SWT.BOLD));

		input = new Text(composite, SWT.SINGLE | SWT.BORDER);
		input.setEditable(true);
		input.setToolTipText("Enter your search keywords (e.g., ClassNotFoundException JDBC Driver)");
		Font myfont = new Font(composite.getDisplay(), "Arial", 11, SWT.NORMAL);
		input.setFont(myfont);
		input.setLayoutData(gdata2);

		flistener = new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				// now assign the auto completion feature
				try {
					// collecting context information
					String[] proposals = suggestions
							.toArray(new String[suggestions.size()]);
					if (proposals.length > 0) {
						// ContentProposalAdapter adapter = null;
						SimpleContentProposalProvider scp = new SimpleContentProposalProvider(
								proposals);
						// setting filtering
						scp.setFiltering(true);
						String autoactive = "abcdefghijklmnopqrstuvwxyz0123456789";
						adapter = new ContentProposalAdapter(input,
								new TextContentAdapter(), scp, null,
								autoactive.toCharArray()); // keystroke is
															// ignored
						adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

						// setting exception name
						//input.setText(currentExceptionName);
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		};

		if (!input.isListening(SWT.FOCUSED)) {
			input.addFocusListener(flistener);
		}

		GridData gdata4 = new GridData();
		gdata4.heightHint = 30;
		gdata4.widthHint = 120;
		gdata4.horizontalAlignment = SWT.BEGINNING;
		Button queryButton = new Button(composite, SWT.PUSH);
		queryButton.setText("Get Queries");
		queryButton
				.setToolTipText("Get Query Suggestions for Current Exception");
		queryButton.setImage(getRefreshImage());
		queryButton.setFont(new Font(composite.getDisplay(), "Arial", 10,
				SWT.BOLD));
		queryButton.setLayoutData(gdata4);
		queryButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				// collecting queries
				QueryRecommender recommender = new QueryRecommender();
				suggestions=recommender.recommendQueries();
				String exceptionName=suggestions.get(0).split("\\s+")[0];
				//System.out.println(suggestions);
				//setting the exception name
				input.setText(exceptionName);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				// default handler
			}
		});

		GridData gdata3 = new GridData();
		gdata3.heightHint = 30;
		gdata3.widthHint = 90;
		gdata3.horizontalAlignment = SWT.BEGINNING;
		// gdata2.grabExcessHorizontalSpace=true;

		Button searchButton = new Button(composite, SWT.PUSH);
		searchButton.setText("Search");
		searchButton.setToolTipText("Search with SurfClipse");
		searchButton.setFont(new Font(composite.getDisplay(), "Arial", 10,
				SWT.BOLD));
		searchButton.setImage(get_search_image());
		// System.out.println("Search Icon:"+Display.getDefault().getSystemImage(SWT.ICON_SEARCH));
		searchButton.setLayoutData(gdata3);
		
		final Composite composite2 = new Composite(parent, SWT.NONE);
		GridLayout gridLayout2 = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 5;
		gridLayout.horizontalSpacing = 5;
		composite2.setLayout(gridLayout2);

		GridData gridData2 = new GridData(SWT.CENTER, SWT.FILL, true, false);
		composite2.setLayoutData(gridData2);

		// Label blank=new Label(composite,SWT.NONE);
		Label info = new Label(composite2, SWT.NONE);
		info.setText("Enter your search keywords (e.g., ClassNotFoundException JDBC Driver)");
		final Button confirm = new Button(composite2, SWT.CHECK);
		confirm.setText("Associate context");
		final Button clearButton = new Button(composite2, SWT.CHECK);
		clearButton.setText("Reset search");

		// adding listener to clear button
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

		// searchButton.setLayoutData(gridData);
		searchButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				// TODO Auto-generated method stub
				String searchQuery = input.getText();
				if (!searchQuery.isEmpty()) {
					boolean associate_context = false;
					// making the search
					SearchEventManager manager = new SearchEventManager();
					if (confirm.getSelection())
						associate_context = true;
					manager.fire_keyword_search(searchQuery, associate_context);
					// showing progress bar
					// clearing the suggestions
					//suggestions.clear();
				} else {
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
						// System.out.println("Search initiated..");
						// initiating the search
						// TODO Auto-generated method stub
						String searchQuery = input.getText();
						if (!searchQuery.isEmpty()) {
							// boolean associate_context = false;
							// making the search
							/*
							 * SearchEventManager manager = new
							 * SearchEventManager(); if (confirm.getSelection())
							 * associate_context = true;
							 * manager.fire_keyword_search(searchQuery,
							 * associate_context);
							 */// showing progress bar
								// clearing the suggestions
								// suggestions.clear();
						} else {
							// showMessageBox("Please enter your query for search");
						}
					} catch (Exception exc) {
					}
					break;
				case SWT.DEL:
					// clearing the search input box
					input.setText("");
					break;
				case SWT.ESC:
					// canceling the search input
					// input.setText("");
					break;
				}
			}
		});
	}

	protected void add_result_table(Composite parent) {
		// code for adding the table viewer
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		// viewer.setSorter(new MyTableSorter());
		final Table table = viewer.getTable();
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Tool tip for the table
		//final ToolTip tip = new ToolTip(table.getShell(), SWT.BALLOON);
		// tip.setText("Result table");
		// tip.setMessage("This is the result table");
		//tip.setAutoHide(true);
		/*table.addListener(SWT.MouseHover, new Listener() {
			public void handleEvent(Event event) {
				try {
					TableItem item = table.getItem(new Point(event.x, event.y));
					tip.setText(item.getText(0));
					String description = "";
					DecimalFormat df = new DecimalFormat("##.00");
					String content = df.format(Double.parseDouble(item
							.getText(2)) * 100);
					String context = df.format(Double.parseDouble(item
							.getText(3)) * 100);
					String popularity = df.format(Double.parseDouble(item
							.getText(4)) * 100);
					String confidence = df.format(Double.parseDouble(item
							.getText(5)) * 100);
					description += "Content relevance: " + content + "%";
					description += "\nContext relevance: " + context + "%";
					description += "\nRelative popularity: " + popularity + "%";
					description += "\nResult confidence: " + confidence + "%";
					tip.setMessage(description + "\n\n" + item.getText(1));

					tip.getDisplay().timerExec(50, new Runnable() {
						public void run() {
							tip.setVisible(true);
						}
					});
				} catch (Exception exc) {
				}
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
		}); */
		String[] columnNames = { "Search Result", "Score", "Content Relevance",
				"Context Relevance", "Confidence" };
		int[] colWidth = { 700, 100, 120, 120, 120 };
		int[] colAlignment = { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT,
				SWT.LEFT, SWT.LEFT};
		for (int i = 0; i < columnNames.length; i++) {
			// stored for sorting
			final int columnNum = i;

			TableColumn col = new TableColumn(table, colAlignment[i]);
			col.setText(columnNames[i]);
			col.setWidth(colWidth[i]);
			// col.setMoveable(true);
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
		// viewer.setSorter(new TableColumnSorter());
		viewer.setInput(getViewSite());
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				// TODO Auto-generated method stub

				System.out.println("Double clicked");

				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				if (selection.isEmpty())
					return;
				@SuppressWarnings("unchecked")
				List<Object> list = selection.toList();
				Object obj1 = list.get(0);
				// System.out.println("Clicked on:"+((Result)obj1).title);
				Result selected = (Result) obj1;
				System.out.println("Selected URL:" + selected.resultURL);
				try {
					// SurfClipseBrowser surfBrowser=new SurfClipseBrowser();
					// surfBrowser.show_the_result_link(selected.title,
					// selected.resultURL);
					String viewID = "ca.usask.ca.srlab.surfclipse.client.views.SurfClipseBrowser";
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(viewID);
					IWorkbenchPage page = (IWorkbenchPage) PlatformUI
							.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage();
					IViewPart vpart = page.findView(viewID);
					SurfClipseBrowser my_browser_view = (SurfClipseBrowser) vpart;
					Browser mybrowser = my_browser_view.webbrowser;
					mybrowser.setUrl(selected.resultURL);
					// showing page title
					// Label pageLabel=my_browser_view.pageLabel;
					// pageLabel.setText(selected.title);
					// adding currently displayed url
					add_currently_displayed_url(selected.resultURL);

				} catch (Exception exc) {
					// System.err.println(exc.getMessage());
					exc.printStackTrace();
				}
			}
		});
		
		//formatting the table
		setItemHeight(table);
		setPaintItem(table);
	}

	protected void setItemHeight(Table table)
	{
		table.addListener(SWT.MeasureItem, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				TableItem item = (TableItem) event.item;
				String text = item.getText(event.index);
				Point size = event.gc.textExtent(text);
				event.width = size.x + 2 * TEXT_MARGIN;
				int min = MIN_HEIGHT;
				event.height =MIN_HEIGHT;// Math.max(min, size.y + TEXT_MARGIN);
			}
		});
		table.addListener(SWT.EraseItem, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				event.detail &= ~SWT.FOREGROUND;
			}
		});
	}
	
	protected void setPaintItem(Table table) {
		table.addListener(SWT.PaintItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				if (event.index == 0) {
					TableItem item = (TableItem) event.item;
					// item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
					// redraw text layout
					String resultText = item.getText(0).trim();
					int firstNL = resultText.indexOf('\n');
					int lastNL = resultText.lastIndexOf('\n');
					textLayout.setText(resultText);
					textLayout.setStyle(style1, 0, firstNL - 1);
					textLayout.setStyle(style2, firstNL + 1, lastNL - 1);
					textLayout.setStyle(style3, lastNL, resultText.length());
					textLayout.draw(event.gc, event.x, event.y);
				}
				else if (event.index > 0) {
					GC gc = event.gc;
					int index = event.index;
					
					TableItem item = (TableItem) event.item;
					int percent = (int) Double.parseDouble(item.getText(index));
					Color foreground = gc.getForeground();
					Color background = gc.getBackground();
					gc.setForeground(new Color(null, 11, 59, 23));
					Color myforeground =null; 
					if(index==1){
						myforeground=new Color(null, 11, 97, 11);
					}
					 if(index==2){ myforeground=new Color(null, 0,64,255); }
					 if(index==3){ myforeground=new Color(null, 17,122,141); }
					 if(index==4){ myforeground=new Color(null,171,104,104); }
					 
					gc.setForeground(myforeground);
					gc.setBackground(new Color(null, 255, 255, 255));
					int col2Width = 100;
					int width = (col2Width - 1) * percent / 100;
					int height = 25;
					// gc.fillRectangle(event.x, event.y + 10, width,
					// height);
					gc.fillGradientRectangle(event.x, event.y + 15, width,
							height, false);
					Rectangle rect2 = new Rectangle(event.x, event.y + 15,
							width - 1, height - 1);
					gc.drawRectangle(rect2);
					gc.setForeground(new Color(null, 255, 255, 255));
					String text = percent + "%";
					Point size = event.gc.textExtent(text);
					int offset = Math.max(0, (height - size.y) / 2);
					gc.drawText(text, event.x + 2, event.y + 15 + offset, true);
					gc.setForeground(background);
					gc.setBackground(foreground);
				}			
			}
		});
	}
	
	public void createPartControl(final Composite parent) {

		GridLayout glayout = new GridLayout();
		glayout.marginWidth = 15;
		glayout.marginHeight = 10;
		parent.setLayout(glayout);

		GridData gdata = new GridData(SWT.FILL, SWT.FILL, true, true);
		parent.setLayoutData(gdata);

		add_related_exception_message(parent, null);
		add_result_table(parent);

		// adding to the Help system
		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.setHelp(viewer.getControl(),
						"ca.usask.ca.srlab.surfclipse.client.viewer");
	}

	protected void add_currently_displayed_url(String currentURL) {
		// code for adding the current URL
		try {
			RecencyScoreManager recenyScoreManager = Activator.recenyScoreManager;
			ArrayList<HistoryLink> RecentFiles = RecencyScoreManager.RecentFiles;
			// removing the existing entries in top 20
			// update with new visiting time
			ArrayList<HistoryLink> tempList = new ArrayList<>();
			tempList.addAll(RecencyScoreManager.RecentFiles);

			for (HistoryLink hlink : tempList) {
				if (hlink.linkURL.equals(currentURL)) {
					RecentFiles.remove(hlink);
				}
			}

			long last_visit = System.currentTimeMillis() / 1000;

			// adding new history links
			HistoryLink mylink = new HistoryLink();
			mylink.linkURL = currentURL;
			mylink.last_visit_time = last_visit;

			RecentFiles.add(0, mylink);
			RecencyScoreManager.RecentFiles = RecentFiles;

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	protected void showMessageBox(String message) {
		// code for showing message box
		try {
			Shell shell = Display.getDefault().getShells()[0];
			MessageDialog.openInformation(shell, "Information", message);
		} catch (Exception exc) {
		}
	}
	
	public SurfClipseClientView() {
	}

	public void setFocus() {
		if (SurfClipseModeManager.current_mode == 1) {
			new Thread(new ActiveConsoleChecker()).start();
		}
		viewer.getControl().setFocus();
		// setup_console_listener();
		// System.out.println("I am focused..");
	}
}