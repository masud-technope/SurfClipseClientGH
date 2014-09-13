package query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import core.QueryPhrase;
import core.QueryToken;

public class QueryManager {
	
	ArrayList<String> tokens;
	HashMap<String, QueryToken> tokenMap;
	int queryLength;
	
	public QueryManager(ArrayList<String> tokens, HashMap<String, QueryToken> tokenMap, int queryLength)
	{
		//initialization
		this.tokens=tokens;
		this.tokenMap=tokenMap;
		this.queryLength=queryLength;
		//this.refineTokenList();
	}
	
	protected static Set<Set<String>> combinations(List<String> group, int k) {
		// getting all the combinations
		Set<Set<String>> allcombs = new HashSet<>();
		if (k == 0) {
			allcombs.add(new HashSet<String>());
			return allcombs;
		}
		if (k > group.size()) {
			return allcombs;
		}
		List<String> groupWithoutX = new ArrayList<>(group);
		String X = groupWithoutX.remove(group.size() - 1);
		Set<Set<String>> comboWithoutX = combinations(groupWithoutX, k);
		Set<Set<String>> comboWithX = combinations(groupWithoutX, k - 1);
		for (Set<String> combo : comboWithX) {
			combo.add(X);
		}
		allcombs.addAll(comboWithoutX);
		allcombs.addAll(comboWithX);
		return allcombs;
	}
	

	protected boolean repeatTokens(Set<String> query)
	{
		boolean repeats=false;
		for(int i=0;i<query.size();i++){
			for(int j=i+1;j<query.size();j++){
				String x=(String) query.toArray()[i];
				String y=(String) query.toArray()[j];
				if(x.contains(y) || y.contains(x)){
					repeats=true;
					break;
				}
			}
		}
		return repeats;
	}
	
	
	public ArrayList<QueryPhrase> getQueryRecommendations()
	{
		//collecting recommended queries
		ArrayList<QueryPhrase> phraseList=new ArrayList<>();
		Set<Set<String>> allcombinations=combinations(this.tokens, this.queryLength);
		for(Set<String> query:allcombinations){
			double score=0;
			String statement=new String();
			
			if(repeatTokens(query))continue; //if there is a repeated token, discard that query
			
			for(String token:query){
				if(tokenMap.containsKey(token)){
					QueryToken qtoken=tokenMap.get(token);
					score+=qtoken.totalScore;
				}else{
					score+=0;
				}
				statement+=token+" ";
			}
			QueryPhrase phrase=new QueryPhrase();
			phrase.queryStatement=statement;
			phrase.score=score;
			phrase.queryNumWords=this.queryLength;
			phraseList.add(phrase);
		}
		//now sort the list
		Collections.sort(phraseList, new Comparator<QueryPhrase>() {
			@Override
			public int compare(QueryPhrase o1, QueryPhrase o2) {
				// TODO Auto-generated method stub
				Double value1=o1.score;
				Double value2=o2.score;
				return value2.compareTo(value1);
			}
		});
		
		//now normalize the queries
		for(QueryPhrase qp:phraseList){
			qp.queryStatement=normalizeQueryPhrase(qp.queryStatement);
		}
		//now return the sorted the list
		return phraseList;
	}
	
	
	protected String normalizeQueryPhrase(String queryString) {
		// normalize the query phrase
		String[] tokens = queryString.split("\\s+");
		String discard = "$#<>()0123456789";
		String newStatement = new String();
		for (String token : tokens) {
			String temp = token.toLowerCase();
			for (int i = 0; i < discard.length(); i++) {
				temp = temp.replace(discard.charAt(i), ' ');
			}
			newStatement += " " + temp;
		}
		return newStatement.trim();
	}
	
	public static void main(String[] args){
		ArrayList<String> items=new ArrayList<>();
		items.add("Socket");
		items.add("OutputStream");
		items.add("writeObject");
		items.add("run");
		items.add("write");
		Set<Set<String>> allcombs=combinations(items, 3);
		for(Set<String> comb:allcombs){
			System.out.println(comb);
		}
		
		
	}
}
