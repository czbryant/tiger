package cfg.operand;

import cfg.Visitor;

public class Var extends T
{
  public String id;
  public boolean isField;

  public Var(String id,boolean isField) 
  {
    this.id = id;
    this.isField = isField;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
