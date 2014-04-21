package codegen.C;

public class ClassBinding
{
  public String extendss; // null for non-existing extends
  public boolean visited; // whether or not this class has been visited
  public java.util.LinkedList<Tuple> fields; // all fields
  public java.util.ArrayList<Ftuple> methods; // all methods

  public ClassBinding(String extendss)
  {
    this.extendss = extendss;
    this.visited = false;
    this.fields = new java.util.LinkedList<Tuple>();
    this.methods = new java.util.ArrayList<Ftuple>();
  }

  // put a single field
  public void put(String c, codegen.C.type.T type, String var)
  {
    this.fields.add(new Tuple(c, type, var));
  }

  public void put(Tuple t)
  {
    this.fields.add(t);
  }

  public void update(java.util.LinkedList<Tuple> fs)
  {
    this.fields = fs;
  }

  public void update(java.util.ArrayList<Ftuple> ms)
  {
    this.methods = ms;
  }

  public void putm(String c, codegen.C.type.T ret,
      java.util.LinkedList<codegen.C.dec.T> args,java.util.LinkedList<codegen.C.dec.T> locals, String mthd)
  {
    Ftuple t = new Ftuple(c, ret, args, locals,mthd);
    this.methods.add(t);
    return;
  }
  
  public boolean isField(String strId)
  {
	  boolean ret=false;
	  int size=this.fields.size();
	  for(int i=0;i<size;i++)
	  {
		  Tuple tp=fields.get(i);
		  if(tp.id.equals(strId))
		  {
			  ret=true;
			  break;
		  }
	  }
	  return ret;
  }
  public boolean isRefLocals(String strArg,String strMethod)
  {
	  boolean ret=false;
	  int size=this.methods.size();
	  for(int i=0;i<size;i++)
	  {
		  Ftuple tp=methods.get(i);
		  if(tp.id.equals(strMethod))
		  {
			  int localsSize=tp.locals.size();
			  for(int j=0;j<localsSize;j++)
			  {
				  codegen.C.dec.Dec dec=(codegen.C.dec.Dec)tp.locals.get(j);
				  if(dec.id.equals(strArg)
					&&((dec.type instanceof codegen.C.type.Class)
					||(dec.type instanceof codegen.C.type.IntArray)))
					return true;
			  }
		  }
	  }
	  return ret;
  }

  @Override
  public String toString()
  {
    System.out.print("extends: ");
    if (this.extendss != null)
      System.out.println(this.extendss);
    else
      System.out.println("<>");
    System.out.println("\nfields:\n  ");
    System.out.println(fields.toString());
    System.out.println("\nmethods:\n  ");
    System.out.println(methods.toString());

    return "";
  }

}
