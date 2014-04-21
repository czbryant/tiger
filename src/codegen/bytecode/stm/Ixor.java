package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Ixor extends T {

	public Ixor(){
		
	}
	
	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		v.visit(this);
	}

}
