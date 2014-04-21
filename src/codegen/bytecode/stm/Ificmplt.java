package codegen.bytecode.stm;

import util.Label;
import codegen.bytecode.Visitor;

public class Ificmplt extends T
{
  public Label l;

  //if value1 is less than value2,
  public Ificmplt(Label l)
  {
    this.l = l;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
