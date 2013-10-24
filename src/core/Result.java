package core;

import java.io.Serializable;
import java.util.ArrayList;


//code for Search Results

public class Result implements Serializable {
	//attributes of a result entry
	public String title;
	public String description;
	public String resultURL;
	
	
	//rank
	public long alexaRank=-1;
	public long competeRank=-1;
	public double averageRank=-1;
	
	//scores
	
	//content related
	public double title_title_MatchScore=0;
	public double title_codestack_MathScore=0;
	public double title_content_MatchScore=0;
	
	//context related
	public double stackTraceContentMatchScore=0;
	public double stackTraceStructuralMatchScore=0;
	public double sourceContextMatchScore=0;
	public double stackTraceMatchScore=0;
	public double recentHistoryScore=0;
	
	//popularity
	public double AlexaCompeteRankScore=0;
	public double SOVoteScore=0;
	public double SOViewCountScore=0;

	//cumulative score
	public double content_score=0;
	public double context_score=0;
	public double popularity_score=0;
	
	//search confidence
	public double search_result_confidence=0;
	
	
	//total scores: different version
	public double totalScore_content_context=0;
	public double totalScore_content_popularity=0;
	public double totalScore_context_popularity=0;
	public double totalScore_content_context_popularity=0;
	
	
	//result content
	public String resultContent=new String();
	public ArrayList<String> codeStacksContent=new ArrayList<String>();
	public ArrayList<StackTrace> StacksProcessed=new ArrayList<>();
	public String textContent=new String();
	public String representativeText=new String();
	public double max_matching_score=0;
	
	
	
}
