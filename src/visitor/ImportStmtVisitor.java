package visitor;

import java.util.ArrayList;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class ImportStmtVisitor extends VoidVisitorAdapter {

	public ArrayList<String> import_st_tokens;
	ArrayList<String> stopwords;
	public ImportStmtVisitor()
	{
		import_st_tokens=new ArrayList<>();	
		stopwords=new ArrayList<>();
	}
	
	protected void create_stop_word_list()
	{
		stopwords.add("org");
		stopwords.add("java");
		stopwords.add("javax");
		stopwords.add("sun");
		stopwords.add("*");
	}
	
	@Override
	public void visit(ImportDeclaration expr, Object args)
	{
		String decl=expr.getName().toString();
		String parts[]=decl.split("\\.");
		for(String token:parts)
		{
			if(stopwords.contains(token))continue;
			this.import_st_tokens.add(token);
		}
	}
	
	
}
