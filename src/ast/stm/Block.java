package ast.stm;

public class Block extends T
{
  public java.util.LinkedList<T> stms;
  public boolean isBlock = false;

  public Block(java.util.LinkedList<T> stms)
  {
    this.stms = stms;
  }
  
  public Block(java.util.LinkedList<T> stms,Boolean isBlock)
  {
    this.stms = stms;
    this.isBlock = isBlock;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
  }
}
