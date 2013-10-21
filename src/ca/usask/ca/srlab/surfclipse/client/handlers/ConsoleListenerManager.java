package ca.usask.ca.srlab.surfclipse.client.handlers;
import java.io.PrintStream;

import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.debug.ui.*;


public class ConsoleListenerManager {

	public ConsoleListenerManager()
	{
		//default constructor
	}
	
	
	public void add_listener_to_console2() {
		// code for adding listener to debug consoles
		MessageConsole myconsole = null;
		ConsolePlugin cplugin = ConsolePlugin.getDefault();
		IConsoleManager manager = cplugin.getConsoleManager();
		IConsole[] consoles = manager.getConsoles();
		if (consoles.length == 0) {
			myconsole = new MessageConsole("SurfClipse", null, false);
			manager.addConsoles(new IConsole[] { myconsole });
			//MessageConsoleStream errStream = myconsole.newMessageStream();
			//System.setErr(new PrintStream(errStream));
			// System.err.println("This is a test error message");
			myconsole.activate();
		} else if (consoles.length > 1 && contains_custom_console(consoles)) {
			MessageConsole mconsole = null;
			for (IConsole console : consoles) {
				if (console.getName().equals("SurfClipse")) {
					myconsole = (MessageConsole) console;
				}
				else if (console instanceof TextConsole) {
					mconsole = (MessageConsole) console;
				}
			}
			
			//now we have got two consoles. Now we can exchange their output
			//MessageConsoleStream mstream=tconsole.
			

			System.out.println(mconsole.getName());
			System.out.println(myconsole.getName());
		}
	}

	protected boolean contains_custom_console(IConsole[] consoles) {
		boolean existed = false;
		for (IConsole console : consoles) {
			if (console.getName().equals("SurfClipse")) {
				existed = true;
				break;
			}
		}
		return existed;
	}
	
	
	public void add_listener_to_console()
	{
		try {
			MessageConsole myconsole;
			ConsolePlugin cplugin = ConsolePlugin.getDefault();
			IConsoleManager manager = cplugin.getConsoleManager();
			IConsole[] consoles = manager.getConsoles();
			if(consoles.length==0)
			{
				myconsole=new MessageConsole("Surfclipse", null);
				manager.addConsoles(new IConsole[]{myconsole});
				consoles=manager.getConsoles();
				MessageConsoleStream out=new MessageConsoleStream(myconsole);
				//System.setErr(out);
				
				//out.println("This is a standard message");
			}
			System.out.println("IConsoles found:"+consoles.length);
			
			for (IConsole currconsole : consoles) {
				IConsole defaultConsole = currconsole;
				
				TextConsole tconsole = (TextConsole) defaultConsole;
				System.out.println("Current console:" + tconsole.getName());
				tconsole.addPatternMatchListener(new IPatternMatchListener() {
				TextConsole console;
					
					@Override
					public void matchFound(PatternMatchEvent event) {
						// TODO Auto-generated method stub
						try {
							System.out.println("Pattern matched with stack trace "+ event.toString());
							System.out.println("Starting search operation");
							SearchEventManager manager = new SearchEventManager();
							manager.fire_search_operation2();
						} catch (Exception exc) {
						}
					}
					
					@Override
					public void disconnect() {
						// TODO Auto-generated method stub
						System.out.println("Disconnected from:"
								+ console.getName());
						//console = null;
					}

					@Override
					public void connect(TextConsole console) {
						// TODO Auto-generated method stub
						this.console = console;
						System.out.println("Connected to console :"+console.getName());
					}

					@Override
					public String getPattern() {
						// TODO Auto-generated method stub
						System.out.println("Pattern provided.");
						//return "^.+Exception[^\\n]+\\n(\\t+\\Qat \\E.+\\s+)+$";
						return "^.+Exception.$";
					}

					@Override
					public String getLineQualifier() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public int getCompilerFlags() {
						// TODO Auto-generated method stub
						return 0;
					}
				});
			}
		} catch (Exception exc) {
			// System.err.println(exc.getMessage());
			// exc.printStackTrace();
			System.err.println("Failed to bind to an active Console.");
		}
	}
}
