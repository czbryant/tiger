package ast;

import ast.exp.ParenExp;

public class PrettyPrintVisitor implements Visitor {
	private int indentLevel;

	public PrettyPrintVisitor() {
		this.indentLevel = 4;
	}

	private void indent() {
		this.indentLevel += 4;
	}

	private void unIndent() {
		this.indentLevel -= 4;
	}

	private void printSpaces() {
		int i = this.indentLevel;
		while (i-- != 0)
			this.say(" ");
	}

	private void sayln(String s) {
		System.out.println(s);
	}

	private void say(String s) {
		System.out.print(s);
	}

	// /////////////////////////////////////////////////////
	// expressions
	@Override
	public void visit(ast.exp.Add e) {
		// Lab2, exercise4: filling in missing code.
		// Similar for other methods with empty bodies.
		// Your code here:

		e.left.accept(this);
		this.say(" + ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(ast.exp.And e) {
		e.left.accept(this);
		this.say(" && ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(ast.exp.ArraySelect e) {
		e.array.accept(this);
		this.say("[");
		e.index.accept(this);
		this.say("]");
	}

	@Override
	public void visit(ast.exp.Call e) {
		e.exp.accept(this);
		this.say("." + e.id + "(");
		if (e.args != null) {
			for (ast.exp.T x : e.args) {
				x.accept(this);
				if (x != e.args.get(e.args.size() - 1))
					this.say(", ");
			}
		}
		this.say(")");
		return;
	}

	@Override
	public void visit(ast.exp.False e) {
		this.say("false");
		return;
	}

	@Override
	public void visit(ast.exp.Id e) {
		this.say(e.id);
	}

	@Override
	public void visit(ast.exp.Length e) {
		e.array.accept(this);
		this.say(".length()");
		return;
	}

	@Override
	public void visit(ast.exp.Lt e) {
		e.left.accept(this);
		this.say(" < ");
		e.right.accept(this);
		return;
	}

	// add
	@Override
	public void visit(ast.exp.NewIntArray e) {
		this.say("new int");

		this.say("[");
		e.exp.accept(this);
		this.say("]");
	}

	@Override
	public void visit(ast.exp.NewObject e) {
		this.say("new " + e.id + "()");
		return;
	}

	// add
	@Override
	public void visit(ast.exp.Not e) {
		this.say("!");
		this.say("(");
		e.exp.accept(this);
		this.say(")");
	}

	@Override
	public void visit(ast.exp.Num e) {
		System.out.print(e.num);
		return;
	}

	@Override
	public void visit(ast.exp.Sub e) {
		e.left.accept(this);
		this.say(" - ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(ast.exp.This e) {
		this.say("this");
	}

	@Override
	public void visit(ast.exp.Times e) {
		e.left.accept(this);
		this.say(" * ");
		e.right.accept(this);
		return;
	}

	// add
	@Override
	public void visit(ast.exp.True e) {
		this.say("true");
		return;
	}

	// statements
	@Override
	public void visit(ast.stm.Assign s) {
		this.printSpaces();
		this.say(s.id + " = ");
		s.exp.accept(this);
		this.say(";\n");
		return;
	}

	@Override
	public void visit(ast.stm.AssignArray s) {
		this.printSpaces();
		this.say(s.id + "[");
		s.index.accept(this);
		this.say("] = ");
		s.exp.accept(this);
		this.say(";\n");

	}

	@Override
	public void visit(ast.stm.Block s) {

		this.printSpaces();
		this.sayln("{");
		this.indent();

		for (ast.stm.T stm : s.stms)
			stm.accept(this);

		this.unIndent();
		this.printSpaces();
		this.sayln("}");

	}

	@Override
	public void visit(ast.stm.If s) {
		this.printSpaces();
		this.say("if (");
		s.condition.accept(this);
		this.sayln(")");
		s.thenn.accept(this);
		this.printSpaces();
		this.sayln("else");
		s.elsee.accept(this);
		return;
	}

	@Override
	public void visit(ast.stm.Print s) {
		this.printSpaces();
		this.say("System.out.println(");
		s.exp.accept(this);
		this.sayln(");");
		return;
	}

	@Override
	public void visit(ast.stm.While s) {
		this.sayln("");
		this.printSpaces();
		this.say("while (");
		s.condition.accept(this);
		this.sayln(") ");
		s.body.accept(this);
		return;
	}

	// type
	@Override
	public void visit(ast.type.Boolean t) {
		this.say("boolean");
	}

	@Override
	public void visit(ast.type.Class t) {
		this.say(t.id);
	}

	@Override
	public void visit(ast.type.Int t) {
		this.say("int");
	}

	@Override
	public void visit(ast.type.IntArray t) {
		this.say("int[]");
	}

	// dec
	@Override
	public void visit(ast.dec.Dec d) {
		this.printSpaces();
		d.type.accept(this);
		this.say(" ");
		this.say(d.id + ";\n");
	}

	// method
	@Override
	public void visit(ast.method.Method m) {
		this.printSpaces();
		this.say("public ");
		m.retType.accept(this);
		this.say(" " + m.id + "(");
		for (ast.dec.T d : m.formals) {
			ast.dec.Dec dec = (ast.dec.Dec) d;
			dec.type.accept(this);

			if (d == m.formals.get(m.formals.size() - 1))
				this.say(" " + dec.id);
			else
				this.say(" " + dec.id + ", ");
		}
		this.sayln(")");
		this.printSpaces();
		this.sayln("{");

		this.indent();
		if (m.locals != null) {
			for (ast.dec.T d : m.locals) {
				if (d != null) {
					ast.dec.Dec dec = (ast.dec.Dec) d;
					this.printSpaces();
					dec.type.accept(this);
					this.say(" " + dec.id + ";\n");
				}

			}
		}

		for (ast.stm.T s : m.stms)
			s.accept(this);
		this.printSpaces();
		this.say("return ");
		m.retExp.accept(this);
		this.sayln(";");
		this.unIndent();
		this.printSpaces();
		this.sayln("}\n");
		return;
	}

	// class
	@Override
	public void visit(ast.classs.Class c) {
		this.say("class " + c.id);
		if (c.extendss != null)
			this.sayln(" extends " + c.extendss);
		else
			this.sayln("");

		this.sayln("{");

		for (ast.dec.T d : c.decs) {
			ast.dec.Dec dec = (ast.dec.Dec) d;
			this.printSpaces();
			dec.type.accept(this);
			this.say(" ");
			this.sayln(dec.id + ";");
		}
		for (ast.method.T mthd : c.methods)
			mthd.accept(this);
		this.sayln("}");
		return;
	}

	// main class
	@Override
	public void visit(ast.mainClass.MainClass c) {
		this.sayln("class " + c.id);
		this.sayln("{");
		this.printSpaces();
		this.sayln("public static void main (String [] " + c.arg + ")");
		this.printSpaces();
		this.sayln("{");
		this.indent();
		for (ast.dec.T dec : c.locals) {
			dec.accept(this);
		}
		for(ast.stm.T s : c.stms)
			s.accept(this);
		this.unIndent();
		this.printSpaces();
		this.sayln("}");
		this.sayln("}");
		return;
	}

	// program
	@Override
	public void visit(ast.program.Program p) {
		p.mainClass.accept(this);
		this.sayln("");
		for (ast.classs.T classs : p.classes) {
			classs.accept(this);
		}
		System.out.println("\n\n");
	}

	
	@Override
	public void visit(ParenExp e) {
		// TODO Auto-generated method stub
		this.say("(");
		e.exp.accept(this);
		this.say("(");
	}

}
