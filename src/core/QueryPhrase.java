package core;

import java.io.Serializable;

public class QueryPhrase implements Serializable {
	//recommended query attributes
	public String queryStatement;
	public double score;
	public int queryNumWords;
}
