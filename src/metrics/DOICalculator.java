package metrics;
import java.util.ArrayList;
import java.util.HashMap;
import core.QueryToken;
import core.StackTraceElem;

public class DOICalculator {
	
	ArrayList<StackTraceElem> traceElems;
	public HashMap<String, QueryToken> tokendb;
	public DOICalculator(ArrayList<StackTraceElem> elems, HashMap<String, QueryToken> tokendb)
	{
		this.traceElems=elems;
		this.tokendb=tokendb;
	}
	
	public HashMap<String, QueryToken> calculateDOI() {
		// calculating DOI for each trace element
		int elemCount = this.traceElems.size();
		int index = 0;
		for (StackTraceElem elem : this.traceElems) {
			double doi = 1 - ((double)index/elemCount);
			elem.doi_value = doi;
			index++;
		}
		
		for (String key : tokendb.keySet()) {
			int count = 0;
			double doiSum = 0, avgDOI = 0;
			for (StackTraceElem elem : this.traceElems) {
				if (elem.className.equals(key) || elem.methodName.equals(key)) {
					doiSum += elem.doi_value;
					count++;
				}
			}
			avgDOI = doiSum / count;
			QueryToken qtoken = tokendb.get(key);
			qtoken.doi = avgDOI;
			this.tokendb.put(key, qtoken);
		}
		return this.tokendb;
	}
}
