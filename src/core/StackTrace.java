package core;

import java.util.ArrayList;

public class StackTrace {
	//fields of StackTrace class.
	public String exception_name=new String();
	public String error_message=new String();
	public String complete_exception_message=new String();
	public ArrayList<StackTraceElem> TraceElems=new ArrayList<>();
	public String stackTraceTokens=new String();
	public String primaryContent=new String();
}
