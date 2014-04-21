package ast.exp;

public class Not extends T
{
  public T exp;
  public int line;

  public Not(T exp)
  {
    this.exp = exp;
  }
  
  public Not(T exp,int line)
  {
    this.exp = exp;
    this.line = line;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
