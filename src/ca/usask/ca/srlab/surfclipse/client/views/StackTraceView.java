package ca.usask.ca.srlab.surfclipse.client.views;

import java.awt.Frame;
import java.awt.Panel;

import graph.StackGraph;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class StackTraceView extends ViewPart {

	public static final String ID = "ca.usask.ca.srlab.surfclipse.client.views.StackTraceView";
	public StackGraph sgraphApplet=new StackGraph();
	
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		try {
			final Composite composite = new Composite(parent, SWT.EMBEDDED);
			final Frame frame = SWT_AWT.new_Frame(composite);
			Panel panel = new Panel();
			panel.add(sgraphApplet);
			frame.add(panel);
		} catch (Exception e) {
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

}
