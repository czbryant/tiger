package ast.exp;

public class NewObject extends T
{
  public String id;
  public int line;

  public NewObject(String id)
  {
    this.id = id;
  }
  

  public NewObject(String id,int line)
  {
    this.id = id;
    this.line = line;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
