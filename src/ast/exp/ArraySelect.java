package ast.exp;

public class ArraySelect extends T
{
  public T array;
  public T index;
  public int line;
  
  public ArraySelect(T array, T index)
  {
    this.array = array;
    this.index = index;
  }

  public ArraySelect(T array, T index,int line)
  {
    this.array = array;
    this.index = index;
    this.line = line;
  }
  
  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
