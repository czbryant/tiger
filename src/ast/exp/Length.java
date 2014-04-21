package ast.exp;

public class Length extends T
{
  public T array;
  public int line;
  
  public Length(T array)
  {
    this.array = array;
  }

  public Length(T array,int line)
  {
    this.array = array;
    this.line  = line;;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
