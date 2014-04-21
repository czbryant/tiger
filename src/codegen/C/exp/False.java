package codegen.C.exp;

import codegen.C.Visitor;

public class False extends T {

	public False(){
		
	}
	
	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		v.visit(this);
	}

}
