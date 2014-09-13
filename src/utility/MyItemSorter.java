package utility;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import core.QueryToken;


public class MyItemSorter {

	public static HashMap<String, QueryToken> sortItemMap(
			HashMap<String, QueryToken> codeObjectMap) {
		// code for sorting the hash map
		List<Map.Entry<String, QueryToken>> list = new LinkedList<>(
				codeObjectMap.entrySet());
		Collections.sort(list, new CustomComparator_cobjInt());
		// adding sorted item in the list
		Map<String, QueryToken> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<String, QueryToken> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		// returning the sorted item
		return (HashMap<String, QueryToken>) sortedMap;
	}

	public static class CustomComparator_cobjInt implements
			Comparator<Map.Entry<String, QueryToken>> {
		@Override
		public int compare(Map.Entry<String, QueryToken> e1,
				Map.Entry<String, QueryToken> e2) {
			QueryToken t1=e1.getValue();
			Double v1=new Double(t1.totalScore);
			QueryToken t2=e2.getValue();
			Double v2=new Double(t2.totalScore);
			return v2.compareTo(v1);
		}
	}
}
