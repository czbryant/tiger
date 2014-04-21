package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Getfield extends T {

	
	public String classid;
	public String id;
	public String type;
	
	public Getfield(String classid,String id,String type){
		this.classid  = classid;
		this.id = id;
		this.type = type;
	}
	
	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		v.visit(this);
	}

}
