package utility;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackTraceUtils {

	/**
	 * @param args
	 */
	ArrayList<String> stack_packages;
	ArrayList<String> stack_methods;
	ArrayList<String> stack_fileNames;
	String stacktrace;
	String exceptionName;
	String errorMessage;
	
	
	public StackTraceUtils(String stacktrace)
	{
		this.stacktrace=stacktrace;
		this.stack_packages=new ArrayList<String>();
		this.stack_methods=new ArrayList<String>();
		this.stack_fileNames=new ArrayList<String>();
	}
	
	public String get_error_message()
	{
		//code for getting error message from stack trace
		String[] lines=this.stacktrace.split("\n");
		String temp=lines[0];
		if(RegexMatcher.matches_exception_name(temp))
			this.errorMessage=temp;
		return this.errorMessage;
	}
	
	public String extract_exception_name()
	{
		//code for extracting exception name from error message
		String errorMessage=get_error_message();
		String exceptionName=new String();
		try
		{
			String exceptionNameRegex="^.+Exception";
			Pattern pattern=Pattern.compile(exceptionNameRegex);
			Matcher matcher=pattern.matcher(errorMessage);
			if(matcher.find())
			{
				int start=matcher.start();
				int end=matcher.end();
				String tempStr=errorMessage.substring(start,end);
				String[] parts=tempStr.split("\\s+");
				exceptionName=parts[parts.length-1];
			}
		}catch(Exception exc){
		}
		return exceptionName;
	}
	
	public static ArrayList<String> extract_reference_packages(String stacktrace)
	{
		//code for extracting packages
		ArrayList<String> packs=new ArrayList<String>();
		return null;
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String error="Primefaces exception INFO: java.lang.ArithmeticException: / by zero java.lang.ArithmeticException: / by zero";
		//String exceptionName=extract_exception_name(error);
		//if(!exceptionName.isEmpty())System.out.println(" "+exceptionName);
		
	}

}
