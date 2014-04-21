package elaborator;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import ast.exp.ParenExp;

public class ElaboratorVisitor implements ast.Visitor {
	public ClassTable classTable; // symbol table for class
	public MethodTable methodTable; // symbol table for each method
	public String currentClass; // the class name being elaborated
	public ast.type.T type; // type of the expression being elaborated

	public Hashtable<String, Boolean> classhash = new Hashtable<String, Boolean>();
	public Hashtable<String, Boolean> methodhash = new Hashtable<String, Boolean>();
	public Hashtable<String, Boolean> mainhash = new Hashtable<String, Boolean>();

	public int line;
	public int tablesize;

	public ElaboratorVisitor() {
		this.classTable = new ClassTable();
		this.methodTable = new MethodTable();
		this.currentClass = null;
		this.type = null;
	}

	private void error(int num, String id) {
		if (num == 1) {
			System.out.println("symbol " + id + " at line " + line
					+ " cannot find in class table and method table");
		} else if (num == 2) {
			System.out.println("at line " + this.type.line);
			System.out.print(" Expected to get " + id);
			System.out.println(" But get " + this.type.toString());
			System.out.println("type mismatch");
		} 
		System.exit(1);
	}

	// /////////////////////////////////////////////////////
	// expressions
	@Override
	public void visit(ast.exp.Add e) {
		e.left.accept(this);
		ast.type.T leftty = this.type;
		e.right.accept(this);
		if (!this.type.toString().equals(leftty.toString())) {
			error(2,leftty.toString());
		}
		
		return;
	}

	@Override
	public void visit(ast.exp.And e) {
		e.left.accept(this);
		ast.type.T leftty = this.type;
		e.right.accept(this);
		
		if (!this.type.toString().equals(leftty.toString())) {
			error(2,leftty.toString());
		}
		this.type = new ast.type.Boolean();
		return;

	}

	@Override
	public void visit(ast.exp.ArraySelect e) {
		e.index.accept(this);
		e.array.accept(this);
		this.type = new ast.type.Int();
		
	}

	@Override
	public void visit(ast.exp.Call e) {
		ast.type.T leftty;
		ast.type.Class ty = null;

		e.exp.accept(this);
		leftty = this.type;
		if (leftty instanceof ast.type.Class) {
			ty = (ast.type.Class) leftty;
			e.type = ty.id;
		} else
			error(0, null);
		MethodType mty = this.classTable.getm(ty.id, e.id);
		java.util.LinkedList<ast.type.T> argsty = new java.util.LinkedList<ast.type.T>();
		if(e.args !=null){
		for (ast.exp.T a : e.args) {
			a.accept(this);
			argsty.addLast(this.type);
		}
		}
		if (mty.argsType.size() != argsty.size())
			error(0, null);
		for (int i = 0; i < argsty.size(); i++) {
			ast.dec.Dec dec = (ast.dec.Dec) mty.argsType.get(i);
			if (dec.type.toString().equals(argsty.get(i).toString()))
				;
			else {
			    ClassBinding cb = classTable.get(argsty.get(i).toString());
			
			    //handle the bug  Tree.accept(Visitor) while the real arg is MyVisitor
			    while(cb.extendss != null){
					if(dec.type.toString().equals(cb.extendss)){
						argsty.set(i, new ast.type.Class(cb.extendss));
						break;
					}
				}
			    if(!cb.extendss.equals(dec.type.toString()))
			    	error(0,null);
			}
		}
		this.type = mty.retType;
		e.at = argsty;
		e.rt = this.type;
		return;
	}

	@Override
	public void visit(ast.exp.Id e) {
		// first look up the id in method table
		this.line = e.line;
		ast.type.T type = this.methodTable.get(e.id);
		// if search failed, then s.id must be a class field.
		if (type == null) {
			type = this.classTable.get(this.currentClass, e.id);
			// mark this id as a field id, this fact will be
			// useful in later phase.
			e.isField = true;
			if (type == null)
				error(1, e.id);
			else
				classhash.put(e.id, true);
		} else
			methodhash.put(e.id, true);

		this.type = type;
		// record this type on this node for future use.
		e.type = type;
		return;
	}

	@Override
	public void visit(ast.exp.Length e) {
		e.array.accept(this);
		this.type = new ast.type.Int();
		return;
	}

	@Override
	public void visit(ast.exp.Lt e) {
		e.left.accept(this);
		ast.type.T ty = this.type;
		e.right.accept(this);
		if (!this.type.toString().equals(ty.toString()))
			error(2, ty.toString());
		this.type = new ast.type.Boolean();
		return;
	}

	@Override
	public void visit(ast.exp.NewIntArray e) {
		e.exp.accept(this);
	}

	@Override
	public void visit(ast.exp.NewObject e) {
		this.type = new ast.type.Class(e.id);
		return;
	}

	@Override
	public void visit(ast.exp.Not e) {
		e.exp.accept(this);
		if (!this.type.toString().equals("@boolean")) {
			error(2,"boolean");
		}
		this.type = new ast.type.Boolean();
	}

	@Override
	public void visit(ast.exp.Num e) {
		this.type = new ast.type.Int();
		return;
	}

	@Override
	public void visit(ast.exp.Sub e) {
		e.left.accept(this);
		ast.type.T leftty = this.type;
		e.right.accept(this);
		if (!this.type.toString().equals(leftty.toString())) {
			error(2,leftty.toString());
		}
		
		return;
	}

	@Override
	public void visit(ast.exp.This e) {
		this.type = new ast.type.Class(this.currentClass);
		return;
	}

	@Override
	public void visit(ast.exp.Times e) {
		e.left.accept(this);
		ast.type.T leftty = this.type;
		e.right.accept(this);
		if (!this.type.toString().equals(leftty.toString())) {
			error(2,leftty.toString());
		}
		this.type = new ast.type.Int();
		return;
	}

	@Override
	public void visit(ast.exp.True e) {
		this.type = new ast.type.Boolean();
		return;
	}

	@Override
	public void visit(ast.exp.False e) {
		this.type = new ast.type.Boolean();
		return;
	}

	// statements
	@Override
	public void visit(ast.stm.Assign s) {
		// first look up the id in method table
		this.line = s.line;
		ast.type.T type = this.methodTable.get(s.id);
		// if search failed, then s.id must
		if (type == null) {
			type = this.classTable.get(this.currentClass, s.id);
			s.isField = true;
			if (type == null)
				error(1, s.id);
			else
				classhash.put(s.id, true);
		} else
			methodhash.put(s.id, true);
		s.exp.accept(this);
		s.type = this.type;
		if (this.type.toString().equals("@int")
				&& type.toString().equals("@int[]"))
			return;
		/*if (!this.type.toString().equals(type.toString()))
			error(2, type.toString());*/
		// this.type.toString().equals(type.toString());
		return;
	}

	@Override
	public void visit(ast.stm.AssignArray s) {
		this.line = s.line;
		ast.type.T type = this.methodTable.get(s.id);
		// if search failed, then s.id must
		if (type == null) {
			type = this.classTable.get(this.currentClass, s.id);
			s.type = type;
			s.isField = true;
			if (type == null)
				error(1, s.id);
			else
				classhash.put(s.id, true);
			s.type = type;
		} else {
			methodhash.put(s.id, true);
			s.type = type;
		}
		s.index.accept(this);
		s.exp.accept(this);
		// to add
		if (type.toString().equals("@int[]")
				&& this.type.toString().equals("@int")) {
			return;
		}
		/*if (!this.type.toString().equals(type.toString())) {
			error(2, type.toString());
		}*/
		return;
	}

	@Override
	public void visit(ast.stm.Block s) {

		for (ast.stm.T stm : s.stms)
			stm.accept(this);
	}

	@Override
	public void visit(ast.stm.If s) {
		s.condition.accept(this);
		if (!this.type.toString().equals("@boolean")) {
			error(2, "boolean");
		}
		s.thenn.accept(this);
		s.elsee.accept(this);
		return;
	}

	@Override
	public void visit(ast.stm.Print s) {
		s.exp.accept(this);
		if (!this.type.toString().equals("@int")) {
			error(2, "int");
		}
		return;
	}

	@Override
	public void visit(ast.stm.While s) {

		s.condition.accept(this);
		if (!this.type.toString().equals("@boolean")) {
			error(2, "boolean");
		}
		s.body.accept(this);
		return;
	}

	// type
	@Override
	public void visit(ast.type.Boolean t) {
		this.type = new ast.type.Boolean();
		return;
	}

	@Override
	public void visit(ast.type.Class t) {
		this.line = t.line;
		ast.type.T type = classTable.get(this.currentClass, t.id);
		if (type == null)
			error(1, t.id);
		else
			classhash.put(t.id, true);
		this.type.toString().equals(type.toString());
		return;
	}

	@Override
	public void visit(ast.type.Int t) {
		this.type = new ast.type.Int();
		return;
	}

	@Override
	public void visit(ast.type.IntArray t) {
		this.type = new ast.type.IntArray();
		// this.type = new ast.type.Int();
		return;
	}

	// dec
	@Override
	public void visit(ast.dec.Dec d) {
		this.line = d.line;
		ast.type.T type = methodTable.get(d.id);
		if (type == null) {
			type = this.classTable.get(this.currentClass, d.id);
			if (type == null)
				error(1, d.id);
			else
				classhash.put(d.id, true);
		} else
			methodhash.put(d.id, true);

		this.type = type;

		// record this type on this node for future use.
		return;
	}

	// method
	@Override
	public void visit(ast.method.Method m) {

		this.methodTable.put(m.formals, m.locals);

		if (control.Control.elabMethodTable)
			this.methodTable.dump();

		for (ast.dec.T dec : m.formals) {
			if (dec != null) {
				dec.accept(this);
				methodhash.put(((ast.dec.Dec) dec).id, false);
			}
		}
		for (ast.dec.T dec : m.locals) {
			if (dec != null) {
				dec.accept(this);
				methodhash.put(((ast.dec.Dec) dec).id, false);
			}
		}

		for (ast.stm.T s : m.stms)
			s.accept(this);
		m.retExp.accept(this);

		// 输出警告
		Enumeration<String> keys = methodhash.keys();
		if (methodhash.size() != 0) {
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();

				// System.out.println("key:"+key);
				// System.out.println("value:"+methodhash.get(key).toString());
				if (!methodhash.get(key)) {
					ast.type.T t = methodTable.get(key);
					String name = t.getClass().getName();
					System.out.println();
					int l = 0;
					;
					if (name.equals("ast.type.Int")) {
						l = ((ast.type.Int) t).line - 1;

					} else if (name.equals("ast.type.Boolean")) {
						l = ((ast.type.Boolean) t).line - 1;

					} else if (name.equals("ast.type.Class")) {
						l = ((ast.type.Class) t).line - 1;

					} else if (name.equals("ast.type.IntArray")) {
						l = ((ast.type.IntArray) t).line - 1;

					} else if (name.equals("ast.type.StringType")) {
						l = ((ast.type.IntArray) t).line - 1;
					}
					System.out.println("-----------------Warning: variable "
							+ key + " declared at line " + l
							+ " never used----------------");
					System.out.println();
				}
			}
		}
		methodhash.clear();
		this.methodTable.clear();
	}

	// class
	@Override
	public void visit(ast.classs.Class c) {
		this.currentClass = c.id;

		for (ast.method.T m : c.methods) {
			m.accept(this);
		}
		return;
	}

	// main class
	@Override
	public void visit(ast.mainClass.MainClass c) {
		this.currentClass = c.id;
		// "main" has an argument "arg" of type "String[]", but
		// one has no chance to use it. So it's safe to skip it...

		for (ast.stm.T s : c.stms)
			s.accept(this);
		return;
	}

	// ////////////////////////////////////////////////////////
	// step 1: build class table
	// class table for Main class
	private void buildMainClass(ast.mainClass.MainClass main) {
		LinkedList<ast.dec.T> locals = main.locals;
		Hashtable<String, ast.type.T> fields = new Hashtable<String, ast.type.T>();
		// Hashtable<String, MethodType> methods = new Hashtable<String,
		// MethodType>();
		for (ast.dec.T dec : locals) {
			ast.dec.Dec d = (ast.dec.Dec) dec;
			fields.put(d.id, d.type);
		}

		this.classTable.put(main.id, new ClassBinding(null, fields, null));
		Enumeration<String> keys = fields.keys();

		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			mainhash.put(key, false);
		}
		
		// this.classTable.put(main.id,fields,null);
	}

	// class table for normal classes
	private void buildClass(ast.classs.Class c) {
		this.classTable.put(c.id, new ClassBinding(c.extendss));
		for (ast.dec.T dec : c.decs) {
			ast.dec.Dec d = (ast.dec.Dec) dec;
			if (d.type != null)
				this.classTable.put(c.id, d.id, d.type);
		}
		for (ast.method.T method : c.methods) {
			ast.method.Method m = (ast.method.Method) method;

		
			this.classTable.put(c.id, m.id,
					new MethodType(m.retType, m.formals));
		}

		// 普通类中加入fields
		ClassBinding cb = this.classTable.get(c.id);
		Enumeration<String> keys = cb.fields.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			classhash.put(key, false);
		}
	}

	// step 1: end
	// ///////////////////////////////////////////////////

	// program
	@Override
	public void visit(ast.program.Program p) {
		// ////////////////////////////////////////////////
		// step 1: build a symbol table for class (the class table)
		// a class table is a mapping from class names to class bindings
		// classTable: className -> ClassBinding{extends, fields, methods}
		buildMainClass((ast.mainClass.MainClass) p.mainClass);
		for (ast.classs.T c : p.classes) {
			buildClass((ast.classs.Class) c);
		}

		// we can double check that the class table is OK!
		if (control.Control.elabClassTable) {
			this.classTable.dump();
		}


		// ////////////////////////////////////////////////
		// step 2: elaborate each class in turn, under the class table
		// built above.
		p.mainClass.accept(this);

		for (ast.classs.T c : p.classes) {
			c.accept(this);
		}
		Enumeration<String> keyss = classhash.keys();
		if (classhash.size() != 0) {
			while (keyss.hasMoreElements()) {
				String key = keyss.nextElement();
				if (!classhash.get(key)) {
					for (ast.classs.T c : p.classes) {
						ast.type.T t = classTable.get(
								((ast.classs.Class) c).id, key);
						String name = "";
						if (t != null) {
							name = t.getClass().getName();
							System.out.println();
							int l = 0;
							if (name.equals("ast.type.Int")) {
								l = ((ast.type.Int) t).line;
							} else if (name.equals("ast.type.Boolean")) {
								l = ((ast.type.Boolean) t).line;

							} else if (name.equals("ast.type.Class")) {
								l = ((ast.type.Class) t).line;

							} else if (name.equals("ast.type.IntArray")) {
								l = ((ast.type.IntArray) t).line;

							} else if (name.equals("ast.type.StringType")) {
								l = ((ast.type.IntArray) t).line;
							}
							System.out
									.println("-----------------Warning: variable "
											+ key
											+ " declared at line "
											+ l
											+ " never used  in class "
											+ ((ast.classs.Class) c).id
											+ "----------------");
							System.out.println();
						}
					}

				}
			}
		}
	}

	@Override
	public void visit(ParenExp e) {
		// TODO Auto-generated method stub
		e.exp.accept(this);
	}

}
