package ast.stm;

public class AssignArray extends T
{
  public String id;
  public ast.exp.T index;
  public ast.exp.T exp;
  public int line;
  public boolean isField;
  
  public ast.type.T type ; 

  public AssignArray(String id, ast.exp.T index, ast.exp.T exp,int line)
  {
    this.id = id;
    this.index = index;
    this.exp = exp;
    this.line = line;
  }

  public AssignArray(String id, ast.exp.T index, ast.exp.T exp,boolean isField,int line)
  {
    this.id = id;
    this.index = index;
    this.exp = exp;
    this.line = line;
    this.isField = isField;
  }
  
  public AssignArray(String id, ast.exp.T index, ast.exp.T exp)
  {
    this.id = id;
    this.index = index;
    this.exp = exp;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
  }
}
