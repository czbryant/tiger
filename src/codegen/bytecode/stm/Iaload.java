package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Iaload extends T {

	
	public Iaload(){
		
	}
	
	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		v.visit(this);
	}

}
