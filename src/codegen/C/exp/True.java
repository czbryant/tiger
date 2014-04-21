package codegen.C.exp;

import codegen.C.Visitor;

public class True extends T {

	public True(){
		
	}
	
	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		 v.visit(this);
	}

}
