package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Ireturn extends T
{
	
	//return int 
  public Ireturn()
  {
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
