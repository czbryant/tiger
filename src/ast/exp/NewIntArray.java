package ast.exp;

public class NewIntArray extends T
{
  public T exp;
  public int line;
  
  public NewIntArray(T exp)
  {
    this.exp = exp;
  }

  public NewIntArray(T exp,int line)
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
