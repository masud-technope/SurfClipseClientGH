package ca.usask.ca.srlab.surfclipse.client.views;

import java.io.PrintStream;

import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.TextConsoleViewer;
import org.eclipse.ui.part.ViewPart;

public class SurfClipseConsole extends ViewPart {

	public static final String ID = "ca.usask.ca.srlab.surfclipse.client.views.SurfClipseConsole";
	private MessageConsole console;
	private MessageConsoleStream stream;
	private MessageConsoleStream errStream;
	private MessageConsoleStream warStream;

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		console = new MessageConsole("Console", null, false);
		stream = console.newMessageStream();
		errStream = console.newMessageStream();
		errStream.setColor(new Color(null, 255, 0, 0));
		warStream = console.newMessageStream();
		warStream.setColor(new Color(null, 0, 0, 255));
		System.setOut(new PrintStream(stream, true));
		System.setErr(new PrintStream(errStream, true));
		final TextConsoleViewer consoleViewer = new TextConsoleViewer(parent,
				console);

		consoleViewer.addTextListener(new ITextListener() {

			@Override
			public void textChanged(TextEvent event) {
				// TODO Auto-generated method stub
				StyledText textWidget = consoleViewer.getTextWidget();
				if (textWidget != null) {
					int lineCount = textWidget.getLineCount();
					textWidget.setTopIndex(lineCount - 1);
				}
			}
		});
	}
	
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	public void addText(String text) {
		stream.println(text);
	}

	public void clearConsole() {
		console.clearConsole();
	}

	public MessageConsoleStream getErrStream() {
		return errStream;
	}

	public MessageConsoleStream getwarStream() {
		return warStream;
	}

}
