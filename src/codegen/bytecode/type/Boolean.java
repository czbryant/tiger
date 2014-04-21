package codegen.bytecode.type;

import codegen.bytecode.Visitor;


public class Boolean extends T {

	
	public Boolean(){
		
	}
	

	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		v.visit(this);
	}

}
