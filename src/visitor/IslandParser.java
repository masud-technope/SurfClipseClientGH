package visitor;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.TokenMgrError;
import japa.parser.ast.CompilationUnit;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class IslandParser {

	/**
	 * @param args
	 */
	String codeSnippet;
	ArrayList<String> methodcalls;
	ArrayList<String> importtokens;
	ArrayList<String> stopwords;
	
	public IslandParser(String codeSnippet) {

		this.methodcalls = new ArrayList<>();
		this.importtokens=new ArrayList<>();
		this.stopwords=new ArrayList<>();
		
		if (!codeSnippet.contains("class ")) {
			// wrap it up
			this.codeSnippet = "class Sample { " + codeSnippet + "}";
		} else
			this.codeSnippet = codeSnippet;
		
		this.create_stop_word_list();
	}

	
	
	
	public ArrayList<String> collect_method_calls()
	{
		//code for collecting method calls
		CompilationUnit cu = null;
		try {
			// System.out.println(this.codeSnippet);
			cu = JavaParser.parse(new ByteArrayInputStream(this.codeSnippet
					.getBytes()));
			MethodCallVisitor visitor = new MethodCallVisitor();
			if (cu != null) {
				cu.accept(visitor, null);
				this.methodcalls = visitor.MethodCallAll;
			}
		} catch (TokenMgrError err) {
		} catch (ParseException exc) {
		} catch (Exception exc) {
		}
		
		if(cu==null) //code parsing was not successful
		{
			this.methodcalls=extract_method_calls(this.codeSnippet);
		}
		
		System.out.println("Method calls:" + this.methodcalls.size());
		return this.methodcalls;
	}
	
	public ArrayList<String> collect_import_stmt_tokens()
	{
		//code for collecting import statement tokens
				CompilationUnit cu = null;
				try {
					// System.out.println(this.codeSnippet);
					cu = JavaParser.parse(new ByteArrayInputStream(this.codeSnippet
							.getBytes()));
					ImportStmtVisitor visitor = new ImportStmtVisitor();
					if (cu != null) {
						cu.accept(visitor, null);
						this.importtokens = visitor.import_st_tokens;
					}
				} catch (TokenMgrError err) {
				} catch (ParseException exc) {
				} catch (Exception exc) {
				}
				
				if(cu==null) //code parsing was not successful
				{
					this.importtokens=extract_import_tokens(this.codeSnippet);
				}
				
				System.out.println("Import tokens:" + this.importtokens.size());
				return this.importtokens;
	}
	
	
	
	protected ArrayList<String> extract_method_calls(String code)
	{
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
	
	protected ArrayList<String> extract_import_tokens(String code)
	{
		//code for extracting import tokens
		ArrayList<String> mytokens=new ArrayList<>();
		String[] lines=code.split("\n");
		for(String line:lines)
		{
			if(line.startsWith("import"))
			{
				String[] tokens=line.split("\\.");
				for(String token:tokens)
				{
					if(stopwords.contains(token))continue;
					mytokens.add(token);
				}
			}
		}
		return mytokens;
	}
	
	protected void create_stop_word_list()
	{
		stopwords.add("org");
		stopwords.add("java");
		stopwords.add("javax");
		stopwords.add("sun");
		stopwords.add("*");
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
			iparser.collect_method_calls();
			iparser.show_method_calls();
			
		}catch(Exception exc){
			exc.printStackTrace();
		}
		
		
	}

}
