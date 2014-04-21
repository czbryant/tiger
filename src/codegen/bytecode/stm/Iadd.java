package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Iadd extends T {
	
	//plus two int
	public Iadd(){
		
	}

	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		v.visit(this);
	}

}
