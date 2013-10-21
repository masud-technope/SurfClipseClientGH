package ca.usask.ca.srlab.surfclipse.client.handlers;

import mode.SurfClipseModeManager;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.WorkbenchWindow;

public class EnableModeHandler extends AbstractHandler {

	int current_mode=SurfClipseModeManager.current_mode;
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// code for event handling on the menu item
		try {
			//code for enabling mode
			String command_name=event.getCommand().getName();
			if(command_name.equals("Proactive"))
			{
				SurfClipseModeManager.current_mode=1;
				current_mode=SurfClipseModeManager.current_mode;
				showMessageBox("SurfClipse set to Proactive mode. Your IDE will restart now to apply new settings ...");
				PlatformUI.getWorkbench().restart();
				
			}else if(command_name.equals("Interactive"))
			{
				SurfClipseModeManager.current_mode=0;
				current_mode=SurfClipseModeManager.current_mode;
				showMessageBox("SurfClipse set to Interactive mode. Your IDE will restart now to apply new settings ...");
				PlatformUI.getWorkbench().restart();
			}
			System.out.println("New mode:"+SurfClipseModeManager.current_mode);
			//disable_current_menu_item(event);
			//do_menu_service_manipulation(event);
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return null;
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
	
	
	protected void do_menu_service_manipulation(ExecutionEvent event)
	{
		//code for menu manipulation
		ISelection selection=HandlerUtil.getActiveMenuSelection(event);
		System.out.println(selection.toString());
	}
	
	
	@SuppressWarnings("restriction")
	protected void disable_current_menu_item(ExecutionEvent event)
	{
		//code for disabling current menu item
		try {
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			MenuManager manager = ((WorkbenchWindow) window).getMenuManager();
			
			Menu menu = manager.getMenu();
			String popupmenu = "ca.usask.ca.srlab.surfclipse.client.popupmenu";
			String mainmenu = "ca.usask.ca.srlab.surfclipse.client.mainmenu";
			String proactive = "ca.usask.ca.srlab.surfClipse.client.EnableProactiveMenu";
			String interactive = "ca.usask.ca.srlab.surfClipse.client.EnableInteractiveMenu";
			if (event.getCommand().getName().equals("Proactive")) {
				IContributionItem item = manager.find(mainmenu);
				MenuItem[] items=menu.getItems();
				for(MenuItem mitem:items)
				{
					System.out.println(mitem.getID());
				}
				// item.setVisible(false);
				manager.update();
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}
	
	
	
	
}
