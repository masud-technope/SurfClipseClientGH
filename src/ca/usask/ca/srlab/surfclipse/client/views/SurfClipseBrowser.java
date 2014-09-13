package ca.usask.ca.srlab.surfclipse.client.views;



import org.eclipse.swt.widgets.MenuItem;
import java.util.HashMap;

import org.eclipse.swt.graphics.Color;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ca.usask.ca.srlab.surfclipse.client.handlers.BookmarkManager;



public class SurfClipseBrowser extends ViewPart {
	
	public static final String ID = "ca.usask.ca.srlab.surfclipse.client.views.SurfClipseBrowser";
	public Browser webbrowser;
	String title;
	String url;
	String runningDocumentTitle;
	Button prevButton;
	Button nextButton;
	Label pageLabel;
	
	public SurfClipseBrowser()
	{
		//browser constructor
	}
	protected void create_navigator_buttons(Composite parent)
	{
		// code for creating buttons
		 Composite composite = new Composite(parent, SWT.NONE);
	        GridLayout gridLayout = new GridLayout(5, false);
	        gridLayout.marginWidth = 0;
	        gridLayout.marginHeight = 0;
	        gridLayout.verticalSpacing = 10;
	        gridLayout.horizontalSpacing = 0;
	        composite.setLayout(gridLayout);

	        GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
	        composite.setLayoutData(gridData);

	        gridData = new GridData(SWT.DEFAULT, SWT.FILL, false, false);
		
		// adding button
		prevButton = new Button(composite, SWT.PUSH);
		prevButton.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(org.eclipse.ui.ISharedImages.IMG_TOOL_BACK));
		prevButton.setToolTipText("Previous Page");
		prevButton.setLayoutData(gridData);
		
		prevButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				//go to previous page
				webbrowser.back();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				//go to next page
				
			}
		});
		nextButton = new Button(composite, SWT.PUSH);
		nextButton.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD));
		nextButton.setToolTipText("Next Page");
		nextButton.setLayoutData(gridData);
		nextButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				webbrowser.forward();	
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		//back to results
		Button backtoresult=new Button(composite, SWT.PUSH);
		backtoresult.setToolTipText("Back to Search Results");
		backtoresult.setImage(ImageDescriptor.createFromFile(SurfClipseBrowser.class, "btores.png").createImage());
		backtoresult.setLayoutData(gridData);
		backtoresult.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				try{
				String viewID="ca.usask.ca.srlab.surfclipse.client.views.SurfClipseClientView";
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewID);
				}catch(Exception exc){}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		 //book mark button
        Button bookmarkButton=new Button(composite, SWT.PUSH);
        bookmarkButton.setToolTipText("Bookmark this Page");
        bookmarkButton.setImage(ImageDescriptor.createFromFile(SurfClipseBrowser.class, "bookmark.png").createImage());
        bookmarkButton.setLayoutData(gridData);
        
        bookmarkButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				//adding current URL as the book mark
				
				String title=runningDocumentTitle;
				String url=webbrowser.getUrl();
				BookmarkManager.addBookMark(title, url);
				showMessageBox("Bookmarked successfully!");
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
        
        //browse from book marks
        final Button showBookmarkButton=new Button(composite, SWT.PUSH);
        showBookmarkButton.setToolTipText("Browse from Bookmarks");
        showBookmarkButton.setImage(ImageDescriptor.createFromFile(SurfClipseBrowser.class, "bmcoll.png").createImage());
        showBookmarkButton.setLayoutData(gridData);
        showBookmarkButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				Button myButton=(Button)e.getSource();
				if(myButton==showBookmarkButton){
					showAllBookMarks(myButton);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub	
			}
		});
        
	}
	
	public void showAllBookMarks(Button bm)
	{
		//code for adding book mark context menu
		try{
			final HashMap<String, String> bookmarks=BookmarkManager.loadBookMarks();
			System.out.println("Bookmarks loaded:"+bookmarks.keySet().size());
			final Menu menu=new Menu(bm);
			for(String title:bookmarks.keySet()){
				MenuItem item=new MenuItem(menu, SWT.NONE);
				item.setText(title);
				
				item.addListener(SWT.Selection, new Listener() {
					@Override
					public void handleEvent(Event event) {
						// TODO Auto-generated method stub
						MenuItem s_item=(MenuItem)event.widget;
						System.out.println(s_item.getText());
						String pageURL=bookmarks.get(s_item.getText());
						webbrowser.setUrl(pageURL);
					}
				});
			}
			//adding listener to menu
			bm.setMenu(menu);
			menu.setVisible(true);
			/*Event event=new Event();
			event.type=SWT.Show;
			event.button=1;
			menu.notifyListeners(SWT.Show,event);
			*/
		}catch(Exception exc){
		}
	}

	protected Image get_search_image()
	{
		return ImageDescriptor.createFromFile(SurfClipseBrowser.class, "gof.png").createImage();
	}
	
	protected void add_page_search_panel(Composite parent)
	{
		//code for adding page search panel
		 Composite composite = new Composite(parent, SWT.NONE);
	        GridLayout gridLayout = new GridLayout(3, false);
	        gridLayout.marginWidth = 0;
	        gridLayout.marginHeight = 0;
	        gridLayout.verticalSpacing = 10;
	        gridLayout.horizontalSpacing = 5;
	        composite.setLayout(gridLayout);

	        GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
	        composite.setLayoutData(gridData);

	        //gridData = new GridData(SWT.DEFAULT, SWT.FILL, true, false);
	        GridData gdata2=new GridData();
			gdata2.heightHint=25;
			gdata2.widthHint=600;
			gdata2.horizontalAlignment=SWT.BEGINNING;
			gdata2.grabExcessHorizontalSpace=false;
		
		
		Label address=new Label(composite, SWT.NONE);
		address.setText("Address:");
		address.setFont(new Font(composite.getDisplay(), "Arial",11, SWT.BOLD));
		
		final Text input=new Text(composite, SWT.FILL |SWT.BORDER);
		input.setToolTipText("Enter a page URL");
		input.setLayoutData(gdata2);
		input.setFont(new Font(composite.getDisplay(), "Arial",11, SWT.NORMAL));
		
		
		GridData gdata3=new GridData();
		gdata3.heightHint=30;
		gdata3.widthHint=70;
		gdata3.horizontalAlignment=SWT.BEGINNING;
		//gdata2.grabExcessHorizontalSpace=true;
		
		
		Button searchButton=new Button(composite,SWT.PUSH);
		searchButton.setText("Go");
		searchButton.setFont(new Font(composite.getDisplay(), "Arial",10, SWT.BOLD));
		searchButton.setLayoutData(gdata3);
		searchButton.setImage(get_search_image());
		
		searchButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				String targetUrl=input.getText();
				if(!targetUrl.isEmpty()){
				webbrowser.setUrl(targetUrl);}
				else{
					showMessageBox("Please enter your search query or URL");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		 //adding key listener to address box
		input.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.CR:
					try {
						String targetUrl=input.getText();
						if(!targetUrl.isEmpty())
						webbrowser.setUrl(targetUrl);
						else{
							showMessageBox("Please enter your search query or URL");
						}
					}catch(Exception exc){}
					break;
				case SWT.DEL:
					// clearing the search input box
					input.setText("");
					break;
				case SWT.ESC:
					//canceling the search input
					input.setText("");
					break;
					}
			}});
	}
	
	
	protected void add_swt_browser(Composite parent)
	{
		//code for adding SWT browser
		try
		{
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		//gridData.verticalIndent=5;
		webbrowser=new Browser(parent, SWT.NONE);
		webbrowser.setLayoutData(gridData);
		webbrowser.addTitleListener(new TitleListener() {
		      public void changed(TitleEvent event) {
		        System.out.println("TitleEvent: " + event.title);
		        //shell.setText(event.title);
		        //storing current browser title
		        runningDocumentTitle=event.title;
		        System.out.println("Current running page title:"+runningDocumentTitle);
		      }
		    });
		
		webbrowser.addProgressListener(new ProgressListener() {
			@Override
				public void completed(ProgressEvent event) {
					// TODO Auto-generated method stub
				    //no need to implement right now
				}
			@Override
			public void changed(ProgressEvent event) {
				// TODO Auto-generated method stub
				//no need to implement right now
			}
		});
		webbrowser.setUrl("http://www.google.ca");
		
		}catch(Exception exc)
		{
			exc.printStackTrace();
		}
		
	}
	
	
	protected void add_browsed_page_info(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.verticalSpacing = 10;
        gridLayout.horizontalSpacing = 5;
        composite.setLayout(gridLayout);

        GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
        composite.setLayoutData(gridData);

        //gridData = new GridData(SWT.DEFAULT, SWT.FILL, true, false);
        GridData gdata2=new GridData();
		gdata2.heightHint=25;
		gdata2.widthHint=800;
		gdata2.horizontalAlignment=SWT.CENTER;
		gdata2.grabExcessHorizontalSpace=true;
		
        pageLabel=new Label(parent, SWT.NONE);
        pageLabel.setFont(new Font(parent.getDisplay(),"Arial",14,SWT.BOLD));
        pageLabel.setLayoutData(gdata2);
        final Color myColor = new Color(parent.getDisplay(), 00, 102, 255);
        pageLabel.setForeground(myColor);
        pageLabel.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e)
            {
                myColor.dispose();
            }
        });
        //pageLabel.setText("This is the page title");
	}
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		//code for creating view control
		
		//adding a layout
		
		GridLayout glayout=new GridLayout();
		glayout.marginWidth=5;
		glayout.marginHeight=5;
		parent.setLayout(glayout);
		
		GridData gdata=new GridData(SWT.FILL, SWT.FILL, true, true);
		parent.setLayoutData(gdata);
		
		//adding items
		
		create_navigator_buttons(parent);
		add_page_search_panel(parent);
		//add_browsed_page_info(parent);
		add_swt_browser(parent);
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
	
	public void show_the_result_link(String title, String url)
	{
		//code for showing the result link
		this.title=title;
		this.url=url;
		webbrowser.setUrl(this.url);
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		webbrowser.setFocus();
	}

}
