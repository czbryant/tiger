package cfg;

import cfg.mainMethod.MainMethod;
import cfg.method.Method;
import cfg.operand.IntArray;
import cfg.program.Program;
import cfg.vtable.Vtable;

public class BlockPrettyPrintVisitor implements Visitor {
	public String pp;

	public BlockPrettyPrintVisitor() {
		pp = "";
	}

	private void printSpaces() {
		this.say("  ");
	}

	private void sayln(String s) {
		say(s);
		try {
			say("\n");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void say(String s) {
		try {
			pp = pp + s;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	// /////////////////////////////////////////////////////
	// operand
	@Override
	public void visit(cfg.operand.Int operand) {

		this.say(new Integer(operand.i).toString());
	}

	@Override
	public void visit(cfg.operand.Var operand) {
		if (operand.isField)
			this.say("this->");
		this.say(operand.id);
	}

	@Override
	public void visit(IntArray operand) {

		// TODO Auto-generated method stub
		operand.array.accept(this);
		this.say("[");
		operand.index.accept(this);
		this.say("+ 1]");
	}

	// statements
	@Override
	public void visit(cfg.stm.Add s) {
		this.printSpaces();
		this.say(s.dst + " = ");
		s.left.accept(this);
		this.say(" + ");
		s.right.accept(this);
		this.say(";");
		return;
	}

	@Override
	public void visit(cfg.stm.InvokeVirtual s) {
		this.printSpaces();
		if (s.isField) {
			this.say(s.dst + " = " + "this->" + s.obj);
			this.say("->vptr->" + s.f + "(this->" + s.obj);
		} else {
			this.say(s.dst + " = " + s.obj);
			this.say("->vptr->" + s.f + "(" + s.obj);
		}
		for (cfg.operand.T x : s.args) {
			this.say(", ");
			x.accept(this);
		}
		this.say(");");
		return;
	}

	@Override
	public void visit(cfg.stm.Lt s) {
		this.printSpaces();
		this.say(s.dst + " = ");
		s.left.accept(this);
		this.say(" < ");
		s.right.accept(this);
		this.say(";");
		return;
	}

	@Override
	public void visit(cfg.stm.And s) {
		this.printSpaces();
		this.say(s.dst + " = ");
		s.left.accept(this);
		this.say(" && ");
		s.right.accept(this);
		this.say(";");
		return;
	}

	public void visit(cfg.stm.MoveArray s) {
		this.printSpaces();
		if (s.isField)
			this.say("this->");
		this.say(s.dst + "[");
		s.index.accept(this);
		this.say(" + 1] = ");
		s.src.accept(this);
		this.say(";\n");
	}

	@Override
	public void visit(cfg.stm.Move s) {
		this.printSpaces();
		if (s.isField)
			this.say("this->");
		this.say(s.dst + " = ");
		s.src.accept(this);
		this.say(";");
		return;
	}

	@Override
	public void visit(cfg.stm.Not s) {
		this.printSpaces();
		this.say(s.dst);
		this.say(" = !");
		s.exp.accept(this);
		this.say(";");
		return;
	}

	@Override
	public void visit(cfg.stm.NewObject s) {
		this.printSpaces();
		this.say(s.dst + " = ((struct " + s.c + "*)(Tiger_new (&" + s.c
				+ "_vtable_, sizeof(struct " + s.c + "))));");
		return;
	}

	@Override
	public void visit(cfg.stm.NewIntArray s) {
		this.printSpaces();
		this.say(s.dst + "=(int*)malloc(sizeof(int)*(");
		s.exp.accept(this);
		this.say(" + 1));");
		this.say(s.dst + "[0] = ");
		s.exp.accept(this);
		this.say(";\n");
	}

	@Override
	public void visit(cfg.stm.Print s) {
		this.printSpaces();
		this.say("System_out_println (");
		s.arg.accept(this);
		this.sayln(");");
		return;
	}

	@Override
	public void visit(cfg.stm.Sub s) {
		this.printSpaces();
		this.say(s.dst + " = ");
		s.left.accept(this);
		this.say(" - ");
		s.right.accept(this);
		this.say(";");
		return;
	}

	@Override
	public void visit(cfg.stm.Times s) {
		this.printSpaces();
		this.say(s.dst + " = ");
		s.left.accept(this);
		this.say(" * ");
		s.right.accept(this);
		this.say(";");
		return;
	}

	// transfer
	@Override
	public void visit(cfg.transfer.If s) {
		this.printSpaces();
		this.say("if (");
		s.operand.accept(this);
		this.say(")\n");
		this.printSpaces();
		this.say("  goto " + s.truee.toString() + ";\n");
		this.printSpaces();
		this.say("else\n");
		this.printSpaces();
		this.say("  goto " + s.falsee.toString() + ";\n");
		return;
	}

	@Override
	public void visit(cfg.transfer.Goto s) {
		this.printSpaces();
		this.say("goto " + s.label.toString() + ";\n");
		return;
	}

	@Override
	public void visit(cfg.transfer.Return s) {
		this.printSpaces();
		this.say("return ");
		s.operand.accept(this);
		this.say(";");
		return;
	}

	// type
	@Override
	public void visit(cfg.type.Class t) {
		this.say("struct " + t.id + " *");
	}

	@Override
	public void visit(cfg.type.Int t) {
		this.say("int");
	}

	@Override
	public void visit(cfg.type.IntArray t) {
		this.say("int*");
	}

	// dec
	@Override
	public void visit(cfg.dec.Dec d) {
		d.type.accept(this);
		this.say(" " + d.id);
		return;
	}

	// dec
	@Override
	public void visit(cfg.block.Block b) {
		this.say(b.label.toString() + ":\n");
		for (cfg.stm.T s : b.stms) {
			s.accept(this);
			this.say("\n");
		}
		b.transfer.accept(this);
		return;
	}

	@Override
	public void visit(Method m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Vtable v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(cfg.classs.Class c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(MainMethod c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Program p) {
		// TODO Auto-generated method stub

	}

}
