package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Astore extends T
{
  public int index;

  //pop a ref
  public Astore(int index)
  {
    this.index = index;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
