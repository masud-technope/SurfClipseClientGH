package ca.usask.ca.srlab.surfclipse.client;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import ca.usask.ca.srlab.surfclipse.client.handlers.SearchEventManager;

public class ActiveConsoleChecker implements Runnable {

	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

		while (true) {
			ConsolePlugin cplugin = ConsolePlugin.getDefault();
			IConsoleManager manager = cplugin.getConsoleManager();
			IConsole[] consoles = manager.getConsoles();
			if (consoles.length > 0) {
				// get the new console
				IConsole newConsole = consoles[0];
				TextConsole tconsole = (TextConsole) newConsole;
				MyPatternListener listener=new MyPatternListener();
				tconsole.addPatternMatchListener(listener);
				System.out.println("Added listener to :" + tconsole.getName()+" at "+System.currentTimeMillis());
				break;
			}
		}
	}
	
	public class MyPatternListener implements IPatternMatchListener
	{
		TextConsole console;

		@Override
		public void matchFound(PatternMatchEvent event) {
			// TODO Auto-generated method stub
			try {
				System.out.println("Matched with the Regex at "+System.currentTimeMillis());
				int offset = event.getOffset();
				int length = event.getLength();
				String content = this.console.getDocument().get(
						offset, length);
				System.out.println(content);

				//making a pro active call
				long started=System.currentTimeMillis();
				SearchEventManager manager = new SearchEventManager(content);
				manager.fire_search_operation2();
				long ended=System.currentTimeMillis();
				System.out.println("Time elapsed:"+(ended-started)/1000);
				
			} catch (Exception exc) {
			}
		}
		@Override
		public void disconnect() {
			// TODO Auto-generated method stub
			// System.out.println("Disconnected from:"
			// + console.getName());
			System.out.println("Disconnected at "+System.currentTimeMillis());
			console = null;
		}

		@Override
		public void connect(TextConsole console) {
			// TODO Auto-generated method stub
			this.console = console;
			System.out.println("Connected at "+System.currentTimeMillis());
			// System.out.println("Connected to console :"+console.getName());
		}

		@Override
		public String getPattern() {
			// TODO Auto-generated method stub
			System.out.println("Pattern provided."+" at "+ System.currentTimeMillis());
			// return
			return "^.+Exception[^\\n]+\\n(\\t+\\Qat \\E.+\\s+)+$";
			//return "^(.*\n)*.*foo.*\n.*$";
		}

		@Override
		public String getLineQualifier() {
			// TODO Auto-generated method stub
			return "\\n|\\r";
		}

		@Override
		public int getCompilerFlags() {
			// TODO Auto-generated method stub
			return 0;
		}
	
	}
	

}
