package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Arraylength extends T {

	
	public Arraylength(){
		
	}
	
	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		v.visit(this);
	}

}
