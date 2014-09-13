package core;

import graph.StackGraphManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import query.QueryManager;
import metrics.DOICalculator;
import metrics.TokenFrequencyCalculator;
import utility.ContentLoader;
import utility.ExceptionIndexLoader;
import utility.MyItemSorter;
import core.StackTrace;
import core.StackTraceElem;
import utility.StackTraceUtils;

public class StackTraceManager {
	
	String stackTrace;
	String contextCode;
	public String exceptionName;
	public String errorMessage;
	HashMap<String, QueryToken> tokendb;
	ArrayList<StackTraceElem> elems;
	int queryLength;
	int exceptionID;
	ArrayList<String> stopList;
	int index;
	
	@Deprecated
	public StackTraceManager(String stackTrace)
	{
		//initialize the objects
		this.stackTrace=stackTrace;
		this.tokendb=new HashMap<>();
		this.elems=new ArrayList<>();
		this.collectExceptionDetails();
		this.populateStopList();
	}
	
	
	public StackTraceManager(String stackTrace, String contextCode){
		//this.exceptionID=exceptionID;
		this.stackTrace=stackTrace;
		this.contextCode=contextCode;
		this.tokendb=new HashMap<>();
		this.elems=new ArrayList<>();
		this.collectExceptionDetails();
		this.queryLength= 3;//queryLength;
		this.stopList=new ArrayList<>();
		this.populateStopList();
		//this.index=index;
	}
	
	@Deprecated
	public StackTraceManager(int exceptionID, String stackTrace, String codeContext, int queryLength, int index)
	{
		//initialize the objects
		this.exceptionID=exceptionID;
		this.stackTrace=stackTrace;
		this.contextCode=codeContext;
		this.tokendb=new HashMap<>();
		this.elems=new ArrayList<>();
		this.collectExceptionDetails();
		this.queryLength=queryLength;
		this.stopList=new ArrayList<>();
		this.populateStopList();
		this.index=index;
	}
	
	protected void createTokenDatabase(ArrayList<StackTraceElem> traces)
	{
		//develop token database
		for(StackTraceElem elem:traces){
			String className=elem.className;
			if(!tokendb.containsKey(className)){
				QueryToken qtoken=new QueryToken();
				qtoken.token=className;
				tokendb.put(className, qtoken);
			}
			String methodName=elem.methodName;
			if(!tokendb.containsKey(methodName)){
				QueryToken qtoken=new QueryToken();
				qtoken.token=methodName;
				tokendb.put(methodName, qtoken);
			}
		}
	}
	
	protected void collectExceptionDetails()
	{
		//analyze the stack trace
		StackTraceUtils utils=new StackTraceUtils(this.stackTrace);
		this.exceptionName=utils.extract_exception_name();
		this.errorMessage=utils.get_error_without_exception_name();
		StackTrace trace=utils.analyze_stack_trace();
		elems=trace.TraceElems;
		this.createTokenDatabase(elems);
	}
	
	protected void collectTokenScores()
	{
		DOICalculator doicalc=new DOICalculator(elems, tokendb);
		tokendb=doicalc.calculateDOI();
		StackGraphManager gmanager=new StackGraphManager(elems, tokendb);
		tokendb=gmanager.calculateTokenRankScore();
		if(!this.contextCode.isEmpty()){
		TokenFrequencyCalculator freqCalc=new TokenFrequencyCalculator(contextCode, tokendb);
		tokendb=freqCalc.calculateTokenFrequency();
		}
	}
	
	protected ArrayList<QueryPhrase> calculateFinalScores()
	{
		//calculating total scores
		for(String key:this.tokendb.keySet()){
			QueryToken qtoken=this.tokendb.get(key);
			//score metrics
			double doiScore=qtoken.doi;
			double tokenRank=qtoken.tokenRankScore;
			double frequencyScore=qtoken.occurenceScore;
			double totalScore=doiScore*MetricWeights.DOIWeight+tokenRank*MetricWeights.TRWeight+
					frequencyScore*MetricWeights.TFWeight;
			qtoken.totalScore=totalScore;
			//updating the value
			this.tokendb.put(key, qtoken);
		}
		
		//sort the items
		HashMap<String, QueryToken> sortedMap=MyItemSorter.sortItemMap(this.tokendb);
		
		//insignificant tokens
		//now process with top 5 tokens
		ArrayList<String> tokens=new ArrayList<>();
		int count=0;
		for(String key:sortedMap.keySet()){
			if(this.stopList.contains(key))continue;
			tokens.add(key);
			count++;
			if(count==5)break;
		}
		
		//int queryLength=3;
		QueryManager qmanager=new QueryManager(tokens, this.tokendb,this.queryLength);
		ArrayList<QueryPhrase> recommended=qmanager.getQueryRecommendations();
		
		//showSortedItems(sortedMap);
		//showRecommendations(recommended);
		//saveQueryList(recommended);
		//saveRankedQuery(recommended, index);
		return recommended;
	}
	
	protected void showRecommendations(ArrayList<QueryPhrase> recommended)
	{
		//showing the recommended queries
		int count=0;
		System.out.println("Exception ID:"+exceptionID);
		for(QueryPhrase phrase:recommended){
			System.out.println(exceptionName+" "+errorMessage+" " +phrase.queryStatement+" "+phrase.score);
			count++;
			if(count==5)break;
		}
	}
	
	protected static void clearQueryResults()
	{
		String queryDir="";//StaticData.QCDataset+"/proposedrank/query";
		String resultDir="";//StaticData.QCDataset+"/proposedrank/results";
		new File(queryDir).delete();
		new File(resultDir).delete();
		new File(queryDir).mkdir();
		new File(resultDir).mkdir();
	}
	
	protected void saveRankedQuery(ArrayList<QueryPhrase> recommended, int index)
	{
		String queryFile="";//StaticData.QCDataset+"/proposedrank/query/"+exceptionID+".txt";
		try {
			String query=exceptionName;
			if(!errorMessage.isEmpty()){
				query+=" "+errorMessage;
			}
			if(index<recommended.size()){
				FileWriter fwriter=new FileWriter(new File(queryFile));
				QueryPhrase phrase=recommended.get(index);
				fwriter.write(query+" "+phrase.queryStatement);
				fwriter.close();
				System.out.println(query+" "+phrase.queryStatement);
				System.out.println("Exception ID:"+exceptionID+", index:"+index+" query saved.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	protected void saveQueryList(ArrayList<QueryPhrase> recommended)
	{
		//saving the recommended queries
		String fileName="";//StaticData.QCDataset+"/proposed/query/"+exceptionID+".txt";
		try {
			FileWriter fwriter=new FileWriter(new File(fileName));
			int count=0;
			for(QueryPhrase phrase:recommended){
				String query=exceptionName;
				if(!errorMessage.isEmpty()){
					query+=" "+errorMessage;
				}
				fwriter.write(query+" "+phrase.queryStatement+"\n");
				count++;
				if(count==5)break;
			}
			fwriter.close();
			System.out.println("Query saved:"+exceptionID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void showSortedItems(HashMap<String, QueryToken> sorted)
	{
		System.out.println("Exception:"+exceptionName);
		//showing sorted tokens
		for(String key:sorted.keySet()){
			QueryToken qtoken=sorted.get(key);
			System.out.println(exceptionName+" "+ key+" "+qtoken.doi+" "+qtoken.tokenRankScore+" "+qtoken.occurenceScore);
		}
	}

	protected void populateStopList()
	{
		//refine the token list
		try{
			String content=ContentLoader.loadFileContent("./data/stop.txt");
			String lines[]=content.split("\n");
			for(String token:lines){
				this.stopList.add(token.trim());
			}
		}catch(Exception e){
			//handle the exception
		}
	}
	
	
	public static void main(String[] args) {
		//ArrayList<Integer> indices = ExceptionIndexLoader
		//		.loadExceptionIndices();
		//int[] list={2,4,11,19,84};
		int index=4;
		clearQueryResults();
		int[] list={4,11,19,34,36,38,39,46,85,93};
		for (int i = 0; i <list.length /*indices.size()*/; i++) {
			try {
				int exceptionID =list[i];// indices.get(i).intValue();
				String fileName =""// StaticData.QCDataset + "/strace/"
						+ exceptionID + ".txt";
				String stacktrace = ContentLoader.loadFileContent(fileName);
				String codeFile ="";// StaticData.QCDataset + "/ccontext/"+ exceptionID + ".java";
				String codeContext = ContentLoader.loadFileContent(codeFile);
				StackTraceManager manager = new StackTraceManager(exceptionID,
						stacktrace, codeContext, 3, index);
				manager.collectTokenScores();
				manager.calculateFinalScores();
				Thread.sleep(2000);
			} catch (Exception e) {

			}
		}
	}
}
