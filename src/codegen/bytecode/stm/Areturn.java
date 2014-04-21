package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Areturn extends T
{
	
	//return a reference from a method
  public Areturn()
  {
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
