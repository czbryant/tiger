package codegen.C.exp;

import codegen.C.Visitor;

public class ParanExp extends T {


	public codegen.C.exp.T exp;
	
	
	public ParanExp(T exp) {
		this.exp = exp;
	}

	
	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		v.visit(this);
	}

}
