package querysuggest;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class IslandParser {

	/**
	 * @param args
	 */
	String codeSnippet;
	ArrayList<String> methodcalls;
	
	
	public IslandParser(String codeSnippet) {
		this.methodcalls = new ArrayList<>();
		this.codeSnippet=codeSnippet;
	}
	
	public ArrayList<String> extract_method_calls()
	{
		String code=this.codeSnippet;
		//code for parsing out the method calls from source
		ArrayList<String> mycalllist=new ArrayList<String>();
		String regex="[_a-zA-Z](\\w)*\\.[_a-zA-Z](\\w)*"; //regex for method call
		Pattern p=Pattern.compile(regex);
		Matcher m=p.matcher(code);
		while(m.find())
		{
			//System.out.println(code.substring(m.start(),m.end()));
			String methcall=code.substring(m.start(),m.end());
			String methodName=methcall.split("\\.")[1];
			mycalllist.add(methodName);
		}
		//returning method name
		return mycalllist;
	}
	
	protected void show_method_calls()
	{
		//code for showing the method calls
		for(String call:this.methodcalls)
		{
			System.out.println(call);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName="./testdata/file.txt";
		try
		{
			Scanner scanner=new Scanner(new File(fileName));
			String content=new String();
			while(scanner.hasNext())
			{
				String line=scanner.nextLine();
				content+=line+"\n";
			}
			System.out.println(content);
			
			IslandParser iparser=new IslandParser(content);
			//iparser.collect_method_calls();
			iparser.show_method_calls();
			
		}catch(Exception exc){
			exc.printStackTrace();
		}
		
		
	}

}
