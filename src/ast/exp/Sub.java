package ast.exp;

public class Sub extends T
{
  public T left;
  public T right;
  public int line;

  public Sub(T left, T right)
  {
    this.left = left;
    this.right = right;
  }
  public Sub(T left, T right,int line)
  {
    this.left = left;
    this.right = right;
    this.line = line;
  }
  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
