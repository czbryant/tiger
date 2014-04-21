package ast.exp;

public class False extends T
{
  public int line;
  public False()
  {
  }
  
  public False(int line)
  {
	  this.line = line;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
