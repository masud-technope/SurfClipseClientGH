package querysuggest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import core.StackTraceElem;

import utility.StackTraceUtils;

public class SCQueryMaker {

	/**
	 * @param args
	 */
	
	String oldQuery;
	String stackTrace;
	String cContext;
	String searchQuery;
	
	public SCQueryMaker(String oldQuery, String stackTrace, String cContext)
	{
		this.oldQuery=oldQuery;
		this.stackTrace=stackTrace;
		this.cContext=cContext;
		this.searchQuery=new String();
	}
	
	public ArrayList<String> getSearchQuerySuggestions()
	{
		//code for getting search query suggestions
		ArrayList<String> suggestions=new ArrayList<>();
		StackTraceUtils utils=new StackTraceUtils(this.stackTrace);
		//error or exception name
		String exceptionName=utils.extract_exception_name();
		String exeptionMessage=utils.get_error_without_exception_name();
		
	    //String exceptionName=utils.extract_exception_name();
	    //System.out.println("Exception:" +exceptionName);
	    HashSet<String> mytokens=new HashSet<>();
	    HashSet<String> tokens1=collect_strace_references();
	    HashSet<String> tokens2=collect_tokens_from_context_code();
	    mytokens.addAll(tokens1);
	    mytokens.addAll(tokens2);
	    //creating the search query
	    suggestions.add(exceptionName.trim());
	    if(!exeptionMessage.isEmpty())
	    suggestions.add(exeptionMessage.trim());
	    suggestions.addAll(mytokens);
	    return suggestions;
	}
	
	protected HashSet<String> collect_strace_references()
	{
		//code for collecting stack trace references
		//token hash set will contain tokens
		StackTraceUtils utils=new StackTraceUtils(this.stackTrace);
	    HashSet<String> tokenhset=new HashSet<String>();
	    //now analyze the trace lines
	    //collect top
	    ArrayList<StackTraceElem> elems=utils.get_trace_elem_for_lines();
	    int traceLimit=elems.size()<5?elems.size():5;
	    for(int i=0;i<traceLimit;i++)
	    {
	    	StackTraceElem elem=elems.get(i);
	    	tokenhset.add(elem.className);
	    	tokenhset.add(elem.methodName);
	    }
	    return tokenhset;
	}
	
	
	protected HashSet<String> collect_tokens_from_context_code()
	{
		//code for collecting tokens from context code
		HashSet<String> ccontext_tokens=new HashSet<>();
		IslandParser parser=new IslandParser(this.cContext);
		ArrayList<String> method_call_tokens=parser.extract_method_calls();
		ArrayList<String> masterList=new ArrayList<>();
		masterList.addAll(method_call_tokens);
		HashMap<String, Integer> tokendict=new HashMap<>();
		for(String token:masterList)
		{
			if(!tokendict.containsKey(token))
			{
				tokendict.put(token, new Integer(1));
			}else
			{
				Integer val=tokendict.get(token);
				int count=val.intValue()+1;
				tokendict.put(token,new Integer(count));
			}
		}
		//now sort the hash map
		List<Entry<String,Integer>> list=new LinkedList<>(tokendict.entrySet());
		Collections.sort(list,new CustomComparator_counter());
		int limit=list.size()<5?list.size():5;
		for(int i=0;i<limit;i++)
		{
			Entry<String, Integer> entry=list.get(i);
			//System.out.println(entry.getKey()+" "+entry.getValue());
			String key=entry.getKey();
			ccontext_tokens.add(key);
		}
		return ccontext_tokens;
	}
	
	
	public class CustomComparator_counter implements Comparator<Entry<String,Integer>> {
	    @Override
	    public int compare(Entry<String, Integer> e1, Entry<String,Integer> e2) {
	    	if(e1.getValue()>e2.getValue())return -1;
	    	else if(e1.getValue()<e2.getValue())return 1;
	    	else return 0;
	    }
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
