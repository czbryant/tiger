package codegen.bytecode;

import java.util.Hashtable;
import java.util.LinkedList;

import ast.exp.ParenExp;
import util.Label;

// Given a Java ast, translate it into Java bytecode.

public class TranslateVisitor implements ast.Visitor {
	private String classId;
	private int index;
	private Hashtable<String, Integer> indexTable;
	private codegen.bytecode.type.T type; // type after translation
	private codegen.bytecode.dec.T dec;
	private LinkedList<codegen.bytecode.stm.T> stms;
	private codegen.bytecode.method.T method;
	private codegen.bytecode.classs.T classs;
	private codegen.bytecode.mainClass.T mainClass;
	public codegen.bytecode.program.T program;

	public TranslateVisitor() {
		this.classId = null;
		this.indexTable = null;
		this.type = null;
		this.dec = null;
		this.stms = new java.util.LinkedList<codegen.bytecode.stm.T>();
		this.method = null;
		this.classs = null;
		this.mainClass = null;
		this.program = null;
	}

	private void emit(codegen.bytecode.stm.T s) {
		this.stms.add(s);
	}

	// /////////////////////////////////////////////////////
	// expressions
	@Override
	public void visit(ast.exp.Add e) {
		e.left.accept(this);
		e.right.accept(this);
		emit(new codegen.bytecode.stm.Iadd());
	}

	@Override
	public void visit(ast.exp.And e) {
		e.left.accept(this);
		e.right.accept(this);
		emit(new codegen.bytecode.stm.Iand());
	}

	@Override
	public void visit(ast.exp.ArraySelect e) {
		//  iaload(an array of int)
		e.array.accept(this);
		e.index.accept(this);
		emit(new codegen.bytecode.stm.Iaload());
	}

	@Override
	public void visit(ast.exp.Call e) {
		e.exp.accept(this);
		if (e.args != null) {
			for (ast.exp.T x : e.args) {
				x.accept(this);
			}
		}
		e.rt.accept(this);
		codegen.bytecode.type.T rt = this.type;
		java.util.LinkedList<codegen.bytecode.type.T> at = new java.util.LinkedList<codegen.bytecode.type.T>();
		
		for (ast.type.T t : e.at) {
			t.accept(this);
//			if(this.type.toString().equals("MyVisitor"))
//				this.type = new codegen.bytecode.type.Class("Visitor"); 
			at.add(this.type);
		}
		emit(new codegen.bytecode.stm.Invokevirtual(e.id, e.type, at, rt));
		return;
	}

	@Override
	public void visit(ast.exp.False e) {
		emit(new codegen.bytecode.stm.Ldc(0));
	}

	@Override
	public void visit(ast.exp.Id e) {
		if (!e.isField) {
			int index = this.indexTable.get(e.id);
			ast.type.T type = e.type;
			
			if(type !=null){
			if (type.getNum() > 0)// a reference
				emit(new codegen.bytecode.stm.Aload(index));
			else
				emit(new codegen.bytecode.stm.Iload(index));
			}// but what about this is a field?
		} else {//this is a field
				emit(new codegen.bytecode.stm.Aload(0));
				emit(new codegen.bytecode.stm.Getfield(classId,e.id,e.type.toString()));
		}
		return;
	}

	@Override
	public void visit(ast.exp.Length e) {
		e.array.accept(this);
		emit(new codegen.bytecode.stm.Arraylength());
	}

	@Override
	public void visit(ast.exp.Lt e) {
		Label tl = new Label(), fl = new Label(), el = new Label();
		e.left.accept(this);
		e.right.accept(this);
		emit(new codegen.bytecode.stm.Ificmplt(tl));
		emit(new codegen.bytecode.stm.Label(fl));
		emit(new codegen.bytecode.stm.Ldc(0));
		emit(new codegen.bytecode.stm.Goto(el));
		emit(new codegen.bytecode.stm.Label(tl));
		emit(new codegen.bytecode.stm.Ldc(1));
		emit(new codegen.bytecode.stm.Goto(el));
		emit(new codegen.bytecode.stm.Label(el));
		return;
	}

	@Override
	public void visit(ast.exp.NewIntArray e) {
		e.exp.accept(this);
		emit(new codegen.bytecode.stm.Newarray());
		return;
	}

	@Override
	public void visit(ast.exp.NewObject e) {
		emit(new codegen.bytecode.stm.New(e.id));
		return;
	}

	@Override
	public void visit(ast.exp.Not e) {
		e.exp.accept(this);
		
		Label tl = new Label(), fl = new Label(), el = new Label();
		emit(new codegen.bytecode.stm.Ifeq(tl));
		emit(new codegen.bytecode.stm.Label(fl));
		emit(new codegen.bytecode.stm.Ldc(0));
		emit(new codegen.bytecode.stm.Goto(el));
		emit(new codegen.bytecode.stm.Label(tl));
		emit(new codegen.bytecode.stm.Ldc(1));
		emit(new codegen.bytecode.stm.Goto(el));
		emit(new codegen.bytecode.stm.Label(el));
	}

	@Override
	public void visit(ast.exp.Num e) {
		emit(new codegen.bytecode.stm.Ldc(e.num));
		return;
	}

	@Override
	public void visit(ast.exp.Sub e) {
		e.left.accept(this);
		e.right.accept(this);
		emit(new codegen.bytecode.stm.Isub());
		return;
	}

	@Override
	public void visit(ast.exp.This e) {
		emit(new codegen.bytecode.stm.Aload(0));
		return;
	}

	@Override
	public void visit(ast.exp.Times e) {
		e.left.accept(this);
		e.right.accept(this);
		emit(new codegen.bytecode.stm.Imul());
		return;
	}

	@Override
	public void visit(ast.exp.True e) {
		emit(new codegen.bytecode.stm.Ldc(1));
		return;
	}

	// statements
	@Override
	public void visit(ast.stm.Assign s) {
	
		if(!s.isField){
		s.exp.accept(this);
		int index = this.indexTable.get(s.id);
		ast.type.T type = s.type;
		if (type.getNum() > 0)
			emit(new codegen.bytecode.stm.Astore(index));
		else
			emit(new codegen.bytecode.stm.Istore(index));
		}else{  //is a field
			
			emit(new codegen.bytecode.stm.Aload(0));
			s.exp.accept(this);
			emit(new codegen.bytecode.stm.Putfield(this.classId,s.id,s.type.toString()));
		}
		return;
	}

	// id[index] = exp
	public void visit(ast.stm.AssignArray s) {
	

		if(!s.isField){
		
		int index = this.indexTable.get(s.id);
		ast.type.T type = s.type;
		if (type.getNum() > 0)
			emit(new codegen.bytecode.stm.Astore(index));
		else
			emit(new codegen.bytecode.stm.Istore(index));
		
		s.index.accept(this);
		s.exp.accept(this);
		
		emit(new codegen.bytecode.stm.Iastore());
		}else{  //is a field
			emit(new codegen.bytecode.stm.Aload(0));
			emit(new codegen.bytecode.stm.Getfield(classId,s.id,s.type.toString()));
			
			s.index.accept(this);
			s.exp.accept(this);
			
			emit(new codegen.bytecode.stm.Iastore());
		}
		return;

	}

	@Override
	public void visit(ast.stm.Block s) {
		for (ast.stm.T stm : s.stms)
			stm.accept(this);
	}

	@Override
	public void visit(ast.stm.If s) {
		
		Label tl = new Label(), fl = new Label(), el = new Label();
		s.condition.accept(this);
		emit(new codegen.bytecode.stm.Ifeq(tl));
		emit(new codegen.bytecode.stm.Label(fl));
		s.thenn.accept(this);
		emit(new codegen.bytecode.stm.Goto(el));
		emit(new codegen.bytecode.stm.Label(tl));
		s.elsee.accept(this);
		emit(new codegen.bytecode.stm.Goto(el));
		emit(new codegen.bytecode.stm.Label(el));
		return;
	}

	@Override
	public void visit(ast.stm.Print s) {
		s.exp.accept(this);
		emit(new codegen.bytecode.stm.Print());
		return;
	}

	@Override
	public void visit(ast.stm.While s) {
		Label tl = new Label(), fl = new Label(), el = new Label();
		emit(new codegen.bytecode.stm.Label(tl));
		s.condition.accept(this);
		emit(new codegen.bytecode.stm.Ifeq(el));
		emit(new codegen.bytecode.stm.Label(fl));
		s.body.accept(this);
		emit(new codegen.bytecode.stm.Goto(tl));
		emit(new codegen.bytecode.stm.Label(el));
		return;
	}

	// type
	@Override
	public void visit(ast.type.Boolean t) {
		this.type = new codegen.bytecode.type.Boolean();
		return;
	}

	@Override
	public void visit(ast.type.Class t) {
		this.type = new codegen.bytecode.type.Class(t.id);
	}

	@Override
	public void visit(ast.type.Int t) {
		this.type = new codegen.bytecode.type.Int();
	}

	@Override
	public void visit(ast.type.IntArray t) {
		this.type = new codegen.bytecode.type.IntArray();
	}

	// dec
	@Override
	public void visit(ast.dec.Dec d) {
		d.type.accept(this);
		this.dec = new codegen.bytecode.dec.Dec(this.type, d.id);
		if (this.indexTable != null)
			this.indexTable.put(d.id, index++);
		return;
	}

	// method
	@Override
	public void visit(ast.method.Method m) {
		// record, in a hash table, each var's index
		// this index will be used in the load store operation
		this.index = 1;
		this.indexTable = new java.util.Hashtable<String, Integer>();

		m.retType.accept(this);
		codegen.bytecode.type.T newRetType = this.type;
		java.util.LinkedList<codegen.bytecode.dec.T> newFormals = new java.util.LinkedList<codegen.bytecode.dec.T>();
		for (ast.dec.T d : m.formals) {
			d.accept(this);
			newFormals.add(this.dec);
		}
		java.util.LinkedList<codegen.bytecode.dec.T> locals = new java.util.LinkedList<codegen.bytecode.dec.T>();
		for (ast.dec.T d : m.locals) {
			d.accept(this);
			locals.add(this.dec);
		}
		this.stms = new java.util.LinkedList<codegen.bytecode.stm.T>();
		for (ast.stm.T s : m.stms) {
			s.accept(this);
		}

		// return statement is specially treated
		m.retExp.accept(this);

		if (m.retType.getNum() > 0)
			emit(new codegen.bytecode.stm.Areturn());
		else
			emit(new codegen.bytecode.stm.Ireturn());

		this.method = new codegen.bytecode.method.Method(newRetType, m.id,
				this.classId, newFormals, locals, this.stms, 0, this.index);

		return;
	}

	// class
	@Override
	public void visit(ast.classs.Class c) {
		this.classId = c.id;
		java.util.LinkedList<codegen.bytecode.dec.T> newDecs = new java.util.LinkedList<codegen.bytecode.dec.T>();
		for (ast.dec.T dec : c.decs) {
			dec.accept(this);
			newDecs.add(this.dec);
		}
		java.util.LinkedList<codegen.bytecode.method.T> newMethods = new java.util.LinkedList<codegen.bytecode.method.T>();
		for (ast.method.T m : c.methods) {
			m.accept(this);
			newMethods.add(this.method);
		}
		this.classs = new codegen.bytecode.classs.Class(c.id, c.extendss,
				newDecs, newMethods);
		return;
	}

	// main class
	@Override
	public void visit(ast.mainClass.MainClass c) {
		for (ast.stm.T stm : c.stms)
			stm.accept(this);
		this.mainClass = new codegen.bytecode.mainClass.MainClass(c.id, c.arg,
				this.stms);
		this.stms = new java.util.LinkedList<codegen.bytecode.stm.T>();
		return;
	}

	// program
	@Override
	public void visit(ast.program.Program p) {
		// do translations
		p.mainClass.accept(this);

		java.util.LinkedList<codegen.bytecode.classs.T> newClasses = new java.util.LinkedList<codegen.bytecode.classs.T>();
		for (ast.classs.T classs : p.classes) {
			classs.accept(this);
			newClasses.add(this.classs);
		}
		this.program = new codegen.bytecode.program.Program(this.mainClass,
				newClasses);
		return;
	}

	@Override
	public void visit(ParenExp e) {
		// TODO Auto-generated method stub
		e.exp.accept(this);
		return;
	}
}
