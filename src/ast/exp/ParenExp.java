package ast.exp;

import ast.Visitor;

public class ParenExp extends T {

	public ast.exp.T exp;
	public int line;
	
	public ParenExp(ast.exp.T exp){
		this.exp = exp;
	}
	
	public ParenExp(ast.exp.T exp,int line){
		this.exp = exp;
		this.line = line;
	}
	
	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		v.visit(this);
	}

}
