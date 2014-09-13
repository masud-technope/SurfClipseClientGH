package utility;
import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import core.StaticData;

public class RegexMatcher {

	
	static String stacktrace_regex="(^\\d+\\) .+)|(^.+Exception(:)?(.+)*)|(^\\s*at .+)|(^\\s+... \\d+ more)|(^\\s*Caused by:.+)"; 
	static String exception_name_regex="^.+Exception";
	
	public static boolean matches_stacktrace(String raw_stack_trace_content)
	{
		boolean matched=false;
		try
		{
		Pattern pattern=Pattern.compile(stacktrace_regex);
		Matcher matcher=pattern.matcher(raw_stack_trace_content);
		if(matcher.find())
		{
			//System.out.println("Pattern found: "+matcher.start()+" "+matcher.end());
			//System.out.println(raw_stack_trace_content.substring(matcher.start(), matcher.end()));
			matched=true;
		}}catch(Exception exc){
			matched=false;
		}
		return matched;
	}
	
	public static boolean matches_exception_name(String token)
	{
		boolean matched=false;
		try
		{
		Pattern pattern=Pattern.compile(exception_name_regex);
		Matcher matcher=pattern.matcher(token);
		if(matcher.find())
		{
			//System.out.println("Pattern found: "+matcher.start()+" "+matcher.end());
			//System.out.println(raw_stack_trace_content.substring(matcher.start(), matcher.end()));
			matched=true;
		}}catch(Exception exc){
			matched=false;
		}
		return matched;
	}
	
	
	/*public static void main(String args[])
	{
		try
		{
			File file=new File(StaticData.QCDataset+"/strace");
			if(file.isDirectory())
			{
				File[] files=file.listFiles();
				for(File f:files)
				{
					Scanner scanner=new Scanner(f);
					String content=new String();
					while(scanner.hasNext())
					{
						String line=scanner.nextLine().trim();
						content+=line+"\n";
					}
					scanner.close();
					boolean resp=RegexMatcher.matches_stacktrace(content);
					if(resp)
					{
						System.out.println(f.getName()+" OK ");
					}else 
					{
						System.out.println(f.getName()+" Failed ");
					}
					//RegexMatcher.matches_exception_name("Thread java.lang.ArithmeticException : / by zero");
				}
			}
		}catch(Exception exc){	
		}
	}*/
	
}
