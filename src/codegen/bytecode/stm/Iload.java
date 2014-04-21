package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Iload extends T
{
  public int index;

  
  //load an int value from local variable 0
  public Iload(int index)
  {
    this.index = index;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
