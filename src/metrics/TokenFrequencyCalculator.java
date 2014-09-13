package metrics;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import visitor.MethodCallVisitor;
import core.QueryToken;

public class TokenFrequencyCalculator {
	String contextCode;
	HashMap<String, QueryToken> tokendb;
	public TokenFrequencyCalculator(String contextCode, HashMap<String, QueryToken> tokendb)
	{
		this.contextCode=contextCode;
		this.tokendb=tokendb;
	}
	
	public HashMap<String, QueryToken> calculateTokenFrequency()
	{
		//calculating token frequency
		try{
			CompilationUnit cu = JavaParser.parse(new ByteArrayInputStream(
					this.contextCode.getBytes()));
			if (cu != null) {
				MethodCallVisitor visitor = new MethodCallVisitor();
				visitor.visit(cu, null);
				// now count the frequency
				ArrayList<String> tokenList = new ArrayList<>();
				tokenList.addAll(visitor.MethodCallAll);
				tokenList.addAll(visitor.ObjectDecAll);
				double maxCount=0;
				for (String token : this.tokendb.keySet()) {
					QueryToken qtoken = this.tokendb.get(token);
					for (String ctoken : tokenList) {
						if (ctoken.equals(token)) {
							qtoken.occurenceScore++;
						}
					}
					if(qtoken.occurenceScore>maxCount){
						maxCount=qtoken.occurenceScore;
					}
					this.tokendb.put(token, qtoken);
				}
				//now normalize the count
				if(maxCount>0){
				for(String token:this.tokendb.keySet()){
					QueryToken qtoken = this.tokendb.get(token);
					qtoken.occurenceScore=qtoken.occurenceScore/maxCount;
					this.tokendb.put(token, qtoken);
				}}
			}
		}catch(Exception e){
			//handle the exception
		}
		return this.tokendb;
	}
	
	
}
