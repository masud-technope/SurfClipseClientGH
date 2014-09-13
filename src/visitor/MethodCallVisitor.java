package visitor;

import java.util.ArrayList;
import java.util.List;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class MethodCallVisitor extends VoidVisitorAdapter {

	public ArrayList<String> MethodCallAll;
	public ArrayList<String> ObjectDecAll;
	
	public MethodCallVisitor()
	{
		MethodCallAll=new ArrayList<>();
		ObjectDecAll=new ArrayList<>();
	}
	
	@Override
	public void visit(MethodCallExpr expr, Object arg) {
		String methodName = expr.getName();
		MethodCallAll.add(methodName);
		super.visit(expr, arg);
	}
	
	@Override
	public void visit(VariableDeclarationExpr dec, Object arg){
	 List<VariableDeclarator> declrations=dec.getVars();
	 for(VariableDeclarator declration:declrations){
		 ObjectDecAll.add(declration.getId().getName());
	 }
	}
	
	public void visit(ImportDeclaration importDec, Object arg){
		String canonName=importDec.getName().toString();
		String[] parts=canonName.split("\\.");
		String smallName=parts[parts.length-1];
		if(!smallName.equals("*")){
			ObjectDecAll.add(smallName);
		}
	}
}
