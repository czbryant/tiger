package cfg.optimizations;

import java.util.Iterator;

public class Cse implements cfg.Visitor
{
  public cfg.program.T program;
  private cfg.stm.T stm;
  private cfg.method.T method;
  private cfg.block.T block;
  private cfg.mainMethod.T mainMethod;
  public java.util.HashMap<cfg.stm.T, java.util.HashSet<cfg.stm.T>> stmIn;
  
  public Cse()
  {
	  this.method = null;
      this.stm = null;
      this.mainMethod = null;
      this.program = null;
      this.program = null;
  } 

  // /////////////////////////////////////////////////////
  // operand
  @Override
  public void visit(cfg.operand.Int operand)
  {
  }

  @Override
  public void visit(cfg.operand.Var operand)
  {
  }
  @Override
  public void visit(cfg.operand.IntArray o) {
  	
  }
  // statements
  @Override
  public void visit(cfg.stm.Add s)
  {
	  if (s.left instanceof cfg.operand.Int
			  && s.right instanceof cfg.operand.Int){
		  cfg.operand.Int left = (cfg.operand.Int)(s.left);
		  cfg.operand.Int right = (cfg.operand.Int)(s.right);
	      this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Int(left.i + right.i),false);
		  return;
	  }
	  java.util.HashSet<cfg.stm.T> in= this.stmIn.get(s);
	  for (Iterator it = in.iterator();  it.hasNext();){
		  cfg.stm.T member = (cfg.stm.T) it.next();
		  if (member == s){
			  continue;
		  }
	      if (member instanceof cfg.stm.Add){
	    	  cfg.stm.Add tmp = (cfg.stm.Add) member;
	    	  boolean A = (s.left instanceof cfg.operand.Var) && (tmp.left instanceof cfg.operand.Var);
	    	  boolean B = (s.left instanceof cfg.operand.Int) && (tmp.left instanceof cfg.operand.Int);
	    	  boolean C = (s.right instanceof cfg.operand.Var) && (tmp.right instanceof cfg.operand.Var);
	    	  boolean D = (s.right instanceof cfg.operand.Int) && (tmp.right instanceof cfg.operand.Int);
	    	  if (A && C){
	    		  cfg.operand.Var a_left = (cfg.operand.Var)s.left;
	    		  cfg.operand.Var b_left = (cfg.operand.Var)tmp.left;
	    		  if (a_left.isField || b_left.isField) 
	    			  continue;
	    		  if (!a_left.id.equals(b_left.id))
	    			  continue;
	    		  cfg.operand.Var a_right = (cfg.operand.Var)s.right;
	    		  cfg.operand.Var b_right = (cfg.operand.Var)tmp.right;
	    		  if (a_right.isField || b_right.isField) 
	    			  continue;
	    		  if (!a_right.id.equals(b_right.id))
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	    	  if (A && D){
	    		  cfg.operand.Var a_left = (cfg.operand.Var)s.left;
	    		  cfg.operand.Var b_left = (cfg.operand.Var)tmp.left;
	    		  if (a_left.isField || b_left.isField) 
	    			  continue;
	    		  if (!a_left.id.equals(b_left.id))
	    			  continue;
	    		  cfg.operand.Int a_right = (cfg.operand.Int)s.right;
	    		  cfg.operand.Int b_right = (cfg.operand.Int)tmp.right;
	    		  if (a_right.i != b_right.i)
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	    	  if (B && C){
	    		  cfg.operand.Int a_left = (cfg.operand.Int)s.left;
	    		  cfg.operand.Int b_left = (cfg.operand.Int)tmp.left;
	    		  if (a_left.i != b_left.i)
	    			  continue;
	    		  cfg.operand.Var a_right = (cfg.operand.Var)s.right;
	    		  cfg.operand.Var b_right = (cfg.operand.Var)tmp.right;
	    		  if (a_right.isField || b_right.isField) 
	    			  continue;
	    		  if (!a_right.id.equals(b_right.id))
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	          if (B && D){
	        	  return;
	          }
	      }
	  }
	  this.stm = s; 
  }

  @Override
  public void visit(cfg.stm.InvokeVirtual s)
  {
	  this.stm = s;
  }

  @Override
  public void visit(cfg.stm.Lt s)
  {
	  if (s.left instanceof cfg.operand.Int
			  && s.right instanceof cfg.operand.Int){
		  cfg.operand.Int left = (cfg.operand.Int)(s.left);
		  cfg.operand.Int right = (cfg.operand.Int)(s.right);
		  if (left.i < right.i)
			  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Int(1),false);
		  else
			  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Int(0),false);
		  return;
	  }
	  java.util.HashSet<cfg.stm.T> in= this.stmIn.get(s);
	  for (Iterator it = in.iterator();  it.hasNext();){
		  cfg.stm.T member = (cfg.stm.T) it.next();
		  if (member == s){
			  continue;
		  }
	      if (member instanceof cfg.stm.Lt){
	    	  cfg.stm.Lt tmp = (cfg.stm.Lt) member;
	    	  boolean A = (s.left instanceof cfg.operand.Var) && (tmp.left instanceof cfg.operand.Var);
	    	  boolean B = (s.left instanceof cfg.operand.Int) && (tmp.left instanceof cfg.operand.Int);
	    	  boolean C = (s.right instanceof cfg.operand.Var) && (tmp.right instanceof cfg.operand.Var);
	    	  boolean D = (s.right instanceof cfg.operand.Int) && (tmp.right instanceof cfg.operand.Int);
	    	  if (A && C){
	    		  cfg.operand.Var a_left = (cfg.operand.Var)s.left;
	    		  cfg.operand.Var b_left = (cfg.operand.Var)tmp.left;
	    		  if (a_left.isField || b_left.isField) 
	    			  continue;
	    		  if (!a_left.id.equals(b_left.id))
	    			  continue;
	    		  cfg.operand.Var a_right = (cfg.operand.Var)s.right;
	    		  cfg.operand.Var b_right = (cfg.operand.Var)tmp.right;
	    		  if (a_right.isField || b_right.isField) 
	    			  continue;
	    		  if (!a_right.id.equals(b_right.id))
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	    	  if (A && D){
	    		  cfg.operand.Var a_left = (cfg.operand.Var)s.left;
	    		  cfg.operand.Var b_left = (cfg.operand.Var)tmp.left;
	    		  if (a_left.isField || b_left.isField) 
	    			  continue;
	    		  if (!a_left.id.equals(b_left.id))
	    			  continue;
	    		  cfg.operand.Int a_right = (cfg.operand.Int)s.right;
	    		  cfg.operand.Int b_right = (cfg.operand.Int)tmp.right;
	    		  if (a_right.i != b_right.i)
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	    	  if (B && C){
	    		  cfg.operand.Int a_left = (cfg.operand.Int)s.left;
	    		  cfg.operand.Int b_left = (cfg.operand.Int)tmp.left;
	    		  if (a_left.i != b_left.i)
	    			  continue;
	    		  cfg.operand.Var a_right = (cfg.operand.Var)s.right;
	    		  cfg.operand.Var b_right = (cfg.operand.Var)tmp.right;
	    		  if (a_right.isField || b_right.isField) 
	    			  continue;
	    		  if (!a_right.id.equals(b_right.id))
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	          if (B && D){
	        	  return;
	          }
	      }
	  }
	  this.stm = s; 
  }

  @Override
  public void visit(cfg.stm.Move s)
  {
	  this.stm = s;
  }
  
  @Override
  public void visit(cfg.stm.MoveArray s) {
	  this.stm = s;
  }

  @Override
  public void visit(cfg.stm.NewObject s)
  {
	  this.stm = s;
  }
  @Override
  public void visit(cfg.stm.NewIntArray s) {
	  this.stm = s;
  }
  @Override
  public void visit(cfg.stm.Print s)
  {
	  this.stm = s;
  }

  @Override
  public void visit(cfg.stm.Sub s)
  {
	  if (s.left instanceof cfg.operand.Int
			  && s.right instanceof cfg.operand.Int){
		  cfg.operand.Int left = (cfg.operand.Int)(s.left);
		  cfg.operand.Int right = (cfg.operand.Int)(s.right);
	      this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Int(left.i - right.i),false);
		  return;
	  }
	  java.util.HashSet<cfg.stm.T> in= this.stmIn.get(s);
	  for (Iterator it = in.iterator();  it.hasNext();){
		  cfg.stm.T member = (cfg.stm.T) it.next();
		  if (member == s){
			  continue;
		  }
	      if (member instanceof cfg.stm.Sub){
	    	  cfg.stm.Sub tmp = (cfg.stm.Sub) member;
	    	  boolean A = (s.left instanceof cfg.operand.Var) && (tmp.left instanceof cfg.operand.Var);
	    	  boolean B = (s.left instanceof cfg.operand.Int) && (tmp.left instanceof cfg.operand.Int);
	    	  boolean C = (s.right instanceof cfg.operand.Var) && (tmp.right instanceof cfg.operand.Var);
	    	  boolean D = (s.right instanceof cfg.operand.Int) && (tmp.right instanceof cfg.operand.Int);
	    	  if (A && C){
	    		  cfg.operand.Var a_left = (cfg.operand.Var)s.left;
	    		  cfg.operand.Var b_left = (cfg.operand.Var)tmp.left;
	    		  if (a_left.isField || b_left.isField) 
	    			  continue;
	    		  if (!a_left.id.equals(b_left.id))
	    			  continue;
	    		  cfg.operand.Var a_right = (cfg.operand.Var)s.right;
	    		  cfg.operand.Var b_right = (cfg.operand.Var)tmp.right;
	    		  if (a_right.isField || b_right.isField) 
	    			  continue;
	    		  if (!a_right.id.equals(b_right.id))
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	    	  if (A && D){
	    		  cfg.operand.Var a_left = (cfg.operand.Var)s.left;
	    		  cfg.operand.Var b_left = (cfg.operand.Var)tmp.left;
	    		  if (a_left.isField || b_left.isField) 
	    			  continue;
	    		  if (!a_left.id.equals(b_left.id))
	    			  continue;
	    		  cfg.operand.Int a_right = (cfg.operand.Int)s.right;
	    		  cfg.operand.Int b_right = (cfg.operand.Int)tmp.right;
	    		  if (a_right.i != b_right.i)
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	    	  if (B && C){
	    		  cfg.operand.Int a_left = (cfg.operand.Int)s.left;
	    		  cfg.operand.Int b_left = (cfg.operand.Int)tmp.left;
	    		  if (a_left.i != b_left.i)
	    			  continue;
	    		  cfg.operand.Var a_right = (cfg.operand.Var)s.right;
	    		  cfg.operand.Var b_right = (cfg.operand.Var)tmp.right;
	    		  if (a_right.isField || b_right.isField) 
	    			  continue;
	    		  if (!a_right.id.equals(b_right.id))
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	          if (B && D){
	        	  return;
	          }
	      }
	  }
	  this.stm = s; 
  }
  
  @Override
  public void visit(cfg.stm.Not s) {
	  if (s.exp instanceof cfg.operand.Int){
		  cfg.operand.Int exp = (cfg.operand.Int) s.exp;
		  if (0 == exp.i)
			  this.stm = new cfg.stm.Move(s.dst,new cfg.type.Int(),new cfg.operand.Int(1),false);
		  else
			  this.stm = new cfg.stm.Move(s.dst,new cfg.type.Int(),new cfg.operand.Int(0),false);
		  return;
	  }
	  java.util.HashSet<cfg.stm.T> in= this.stmIn.get(s);
	  for (Iterator it = in.iterator();  it.hasNext();){
		  cfg.stm.T member = (cfg.stm.T) it.next();
		  if (member == s){
			  continue;
		  }
		  if (member instanceof cfg.stm.Not){
			  cfg.stm.Not tmp = (cfg.stm.Not) member;
			  boolean A = (s.exp instanceof cfg.operand.Var) && 
					  (tmp.exp instanceof cfg.operand.Var);
			  if (A){
				  cfg.operand.Var a_exp = (cfg.operand.Var)s.exp;
	    		  cfg.operand.Var b_exp = (cfg.operand.Var)tmp.exp;
	    		  if (a_exp.isField || b_exp.isField) 
	    			  continue;
	    		  if (!a_exp.id.equals(b_exp.id))
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,new cfg.type.Int(),
	    				  new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
			  }
		  }
	  }
	  this.stm = s;
  }

  @Override
  public void visit(cfg.stm.Times s)
  {
	  if (s.left instanceof cfg.operand.Int
			  && s.right instanceof cfg.operand.Int){
		  cfg.operand.Int left = (cfg.operand.Int)(s.left);
		  cfg.operand.Int right = (cfg.operand.Int)(s.right);
	      this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Int(left.i * right.i),false);
		  return;
	  }
	  java.util.HashSet<cfg.stm.T> in= this.stmIn.get(s);
	  for (Iterator it = in.iterator();  it.hasNext();){
		  cfg.stm.T member = (cfg.stm.T) it.next();
		  if (member == s){
			  continue;
		  }
	      if (member instanceof cfg.stm.Times){
	    	  cfg.stm.Times tmp = (cfg.stm.Times) member;
	    	  boolean A = (s.left instanceof cfg.operand.Var) && (tmp.left instanceof cfg.operand.Var);
	    	  boolean B = (s.left instanceof cfg.operand.Int) && (tmp.left instanceof cfg.operand.Int);
	    	  boolean C = (s.right instanceof cfg.operand.Var) && (tmp.right instanceof cfg.operand.Var);
	    	  boolean D = (s.right instanceof cfg.operand.Int) && (tmp.right instanceof cfg.operand.Int);
	    	  if (A && C){
	    		  cfg.operand.Var a_left = (cfg.operand.Var)s.left;
	    		  cfg.operand.Var b_left = (cfg.operand.Var)tmp.left;
	    		  if (a_left.isField || b_left.isField) 
	    			  continue;
	    		  if (!a_left.id.equals(b_left.id))
	    			  continue;
	    		  cfg.operand.Var a_right = (cfg.operand.Var)s.right;
	    		  cfg.operand.Var b_right = (cfg.operand.Var)tmp.right;
	    		  if (a_right.isField || b_right.isField) 
	    			  continue;
	    		  if (!a_right.id.equals(b_right.id))
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	    	  if (A && D){
	    		  cfg.operand.Var a_left = (cfg.operand.Var)s.left;
	    		  cfg.operand.Var b_left = (cfg.operand.Var)tmp.left;
	    		  if (a_left.isField || b_left.isField) 
	    			  continue;
	    		  if (!a_left.id.equals(b_left.id))
	    			  continue;
	    		  cfg.operand.Int a_right = (cfg.operand.Int)s.right;
	    		  cfg.operand.Int b_right = (cfg.operand.Int)tmp.right;
	    		  if (a_right.i != b_right.i)
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	    	  if (B && C){
	    		  cfg.operand.Int a_left = (cfg.operand.Int)s.left;
	    		  cfg.operand.Int b_left = (cfg.operand.Int)tmp.left;
	    		  if (a_left.i != b_left.i)
	    			  continue;
	    		  cfg.operand.Var a_right = (cfg.operand.Var)s.right;
	    		  cfg.operand.Var b_right = (cfg.operand.Var)tmp.right;
	    		  if (a_right.isField || b_right.isField) 
	    			  continue;
	    		  if (!a_right.id.equals(b_right.id))
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	          if (B && D){
	        	  return;
	          }
	      }
	  }
	  this.stm = s; 
  }
  
  @Override
  public void visit(cfg.stm.And s) {
	  if (s.left instanceof cfg.operand.Int
			  && s.right instanceof cfg.operand.Int){
		  cfg.operand.Int left = (cfg.operand.Int)(s.left);
		  cfg.operand.Int right = (cfg.operand.Int)(s.right);
		  if (0 != left.i && 0 != right.i)
			  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Int(1),false);
		  else
			  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Int(0),false);
		  return;
	  }
	  java.util.HashSet<cfg.stm.T> in= this.stmIn.get(s);
	  for (Iterator it = in.iterator();  it.hasNext();){
		  cfg.stm.T member = (cfg.stm.T) it.next();
		  if (member == s){
			  continue;
		  }
	      if (member instanceof cfg.stm.And){
	    	  cfg.stm.And tmp = (cfg.stm.And) member;
	    	  boolean A = (s.left instanceof cfg.operand.Var) && (tmp.left instanceof cfg.operand.Var);
	    	  boolean B = (s.left instanceof cfg.operand.Int) && (tmp.left instanceof cfg.operand.Int);
	    	  boolean C = (s.right instanceof cfg.operand.Var) && (tmp.right instanceof cfg.operand.Var);
	    	  boolean D = (s.right instanceof cfg.operand.Int) && (tmp.right instanceof cfg.operand.Int);
	    	  if (A && C){
	    		  cfg.operand.Var a_left = (cfg.operand.Var)s.left;
	    		  cfg.operand.Var b_left = (cfg.operand.Var)tmp.left;
	    		  if (a_left.isField || b_left.isField) 
	    			  continue;
	    		  if (!a_left.id.equals(b_left.id))
	    			  continue;
	    		  cfg.operand.Var a_right = (cfg.operand.Var)s.right;
	    		  cfg.operand.Var b_right = (cfg.operand.Var)tmp.right;
	    		  if (a_right.isField || b_right.isField) 
	    			  continue;
	    		  if (!a_right.id.equals(b_right.id))
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	    	  if (A && D){
	    		  cfg.operand.Var a_left = (cfg.operand.Var)s.left;
	    		  cfg.operand.Var b_left = (cfg.operand.Var)tmp.left;
	    		  if (a_left.isField || b_left.isField) 
	    			  continue;
	    		  if (!a_left.id.equals(b_left.id))
	    			  continue;
	    		  cfg.operand.Int a_right = (cfg.operand.Int)s.right;
	    		  cfg.operand.Int b_right = (cfg.operand.Int)tmp.right;
	    		  if (a_right.i != b_right.i)
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	    	  if (B && C){
	    		  cfg.operand.Int a_left = (cfg.operand.Int)s.left;
	    		  cfg.operand.Int b_left = (cfg.operand.Int)tmp.left;
	    		  if (a_left.i != b_left.i)
	    			  continue;
	    		  cfg.operand.Var a_right = (cfg.operand.Var)s.right;
	    		  cfg.operand.Var b_right = (cfg.operand.Var)tmp.right;
	    		  if (a_right.isField || b_right.isField) 
	    			  continue;
	    		  if (!a_right.id.equals(b_right.id))
	    			  continue;
	    		  this.stm = new cfg.stm.Move(s.dst,s.ty,new cfg.operand.Var(tmp.dst,false),false);
	    		  return;
	    	  }else
	          if (B && D){
	        	  return;
	          }
	      }
	  }
	  this.stm = s;  
  }

  // transfer
  @Override
  public void visit(cfg.transfer.If s)
  {
  }

  @Override
  public void visit(cfg.transfer.Goto s)
  {
  }

  @Override
  public void visit(cfg.transfer.Return s)
  {
  }

  // type
  @Override
  public void visit(cfg.type.Class t)
  {
  }

  @Override
  public void visit(cfg.type.Int t)
  {
  }

  @Override
  public void visit(cfg.type.IntArray t)
  {
  }

  // dec
  @Override
  public void visit(cfg.dec.Dec d)
  {
  }

  // block
  @Override
  public void visit(cfg.block.Block b)
  {
	  java.util.LinkedList<cfg.stm.T> new_stms = 
			  new  java.util.LinkedList<>();	  
	 for (cfg.stm.T s :b.stms){
		 s.accept(this);
		 if (null != this.stm)
			 new_stms.add(this.stm);
	 }
	 this.block = new cfg.block.Block(b.label, new_stms, b.transfer);
  }

  // method
  @Override
  public void visit(cfg.method.Method m)
  {
	  java.util.LinkedList<cfg.block.T> new_blocks = 
			  new  java.util.LinkedList<>();
	  for (cfg.block.T block : m.blocks) {
		  block.accept(this);
		  new_blocks.add(this.block);
	  }
	  this.method = new cfg.method.Method(m.retType, m.id, m.classId, 
			  m.formals, m.locals,m.blocks, m.entry, m.exit, m.retValue);
  }

  @Override
  public void visit(cfg.mainMethod.MainMethod m)
  {
	  java.util.LinkedList<cfg.block.T> new_blocks = 
			  new  java.util.LinkedList<>();
	  for (cfg.block.T block : m.blocks) {
		  block.accept(this);
		  new_blocks.add(this.block);
	  }
	  this.mainMethod = new cfg.mainMethod.MainMethod(m.locals, new_blocks);
  }

  // vtables
  @Override
  public void visit(cfg.vtable.Vtable v)
  {
  }

  // class
  @Override
  public void visit(cfg.classs.Class c)
  {
  }

  // program
  @Override
  public void visit(cfg.program.Program p)
  {
	  p.mainMethod.accept(this);
	  java.util.LinkedList<cfg.method.T> new_method = 
			  new  java.util.LinkedList<>();
	  for (cfg.method.T mth : p.methods) {
	     mth.accept(this);
	     new_method.add(this.method);
	  }
      this.program = new cfg.program.Program(p.classes, p.vtables, 
    		new_method, this.mainMethod);
  }
}
