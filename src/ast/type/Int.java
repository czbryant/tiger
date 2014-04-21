package ast.type;

import ast.Visitor;

public class Int extends T
{
  public int line;
  public Int()
  {
  }
  public Int(int line)
  {
	  this.line = line;
  }

  @Override
  public String toString()
  {
    return "@int";
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }

  @Override
  public int getNum()
  {
    return 0;
  }
}
