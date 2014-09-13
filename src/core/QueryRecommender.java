package core;

import java.util.ArrayList;

public class QueryRecommender {
	
	String stackTrace;
	String codeContext;
	public QueryRecommender()
	{
		//default constructor
	}
	
	protected String normalizeExceptionName(String excepName)
	{
		//normalize the exception name
		String[] tokens=excepName.split("\\.");
		return tokens[tokens.length-1].toLowerCase();
	}
	
	public ArrayList<String> recommendQueries()
	{
		//recommending queries
		ContextProvider contextProvider=new ContextProvider();
		String stack=contextProvider.extract_stacktrace_from_console();
		
		String ccode=contextProvider.extract_source_code_context(stack);
		StackTraceManager stManager=new StackTraceManager(stack, ccode);
		stManager.collectTokenScores();
		ArrayList<QueryPhrase> phrases=stManager.calculateFinalScores();
		String exceptionName=stManager.exceptionName;
		exceptionName=normalizeExceptionName(exceptionName);
		System.out.println("Target exception:"+exceptionName);
		String errorMessage=stManager.errorMessage;
		ArrayList<String> queries=new ArrayList<>();
		int count=0;
		for(QueryPhrase phrase:phrases){
			String query=exceptionName+" "+phrase.queryStatement;
			queries.add(query);
			count++;
			if(count==5)break;
		}
		//showing the queries
		System.out.println(queries);
		
		return queries;
	}
	
	

}
