package codegen.C;

import java.util.Hashtable;

import codegen.C.exp.ParanExp;
import codegen.C.type.Boolean;
import control.Control;
public class PrettyPrintVisitor implements Visitor {
	private int indentLevel;
	private java.io.BufferedWriter writer;

	private Hashtable<String, String> table = new Hashtable<String, String>();
	// store the method in which class

	public PrettyPrintVisitor() {
		this.indentLevel = 2;
	}

	private void indent() {
		this.indentLevel += 2;
	}

	private void unIndent() {
		this.indentLevel -= 2;
	}

	private void printSpaces() {
		int i = this.indentLevel;
		while (i-- != 0)
			this.say(" ");
	}

	private void sayln(String s) {
		say(s);
		try {
			this.writer.write("\n");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void say(String s) {
		try {
			this.writer.write(s);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	// /////////////////////////////////////////////////////
	// expressions

	public void visit(codegen.C.exp.Add e) {
		e.left.accept(this);
		this.say("+");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(codegen.C.exp.And e) {
		e.left.accept(this);
		this.say("&&");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(codegen.C.exp.ArraySelect e) {
		// e.array.accept(this);
		// this.say("[");
		// e.index.accept(this);
		// this.say("]");
		this.say("*(");
		e.array.accept(this);
		this.say(" + ");
		e.index.accept(this);
		this.say(" + 4");
		this.say(")");
	}

	@Override
	public void visit(codegen.C.exp.Call e) {

		String assign = "frame." + e.assign;
		this.say("(" + assign + "=");
		e.exp.accept(this);
		this.say(", ");

		// if(fieldtable.get(e.assign) != null){
		// this.say("((struct "+fieldtable.get(e.assign)+"_vtable*)("+e.assign +
		// "->vptr))->" + e.id + "(" + e.assign);
		// }else
		this.say(assign + "->vptr->" + e.id + "(" + assign);

		int size = e.args.size();
		if (size == 0) {
			this.say("))");
			return;
		}
		for (codegen.C.exp.T x : e.args) {
			this.say(", ");
			x.accept(this);
		}
		this.say("))");
		return;
	}

	@Override
	public void visit(codegen.C.exp.Id e) {
		// if (e.isField) {
		// this.say("this->");
		// }
		this.say(e.id);
	}

	@Override
	public void visit(codegen.C.exp.Length e) {
		// this.say("sizeof(");
		// e.array.accept(this);
		// this.say(")/sizeof(int)");

		e.array.accept(this);
		this.sayln("[2]");
	}

	@Override
	public void visit(codegen.C.exp.Lt e) {
		e.left.accept(this);
		this.say(" < ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(codegen.C.exp.NewIntArray e) {
		// this.say("(int *)(malloc(");
		// e.exp.accept(this);
		// this.say("* sizeof(int)))");

		this.say("Tiger_new_array(");
		e.exp.accept(this);
		this.say(")");
	}

	@Override
	public void visit(codegen.C.exp.NewObject e) {

		this.say("((struct " + e.id + "*)(Tiger_new (&" + e.id
				+ "_vtable_, sizeof(struct " + e.id + "))))");
		return;
	}

	@Override
	public void visit(codegen.C.exp.Not e) {
		this.say("!");
		e.exp.accept(this);
		return;
	}

	@Override
	public void visit(codegen.C.exp.Num e) {
		this.say(Integer.toString(e.num));
		return;
	}

	@Override
	public void visit(codegen.C.exp.Sub e) {
		e.left.accept(this);
		this.say(" - ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(codegen.C.exp.This e) {
		this.say("this");
	}

	@Override
	public void visit(codegen.C.exp.Times e) {
		e.left.accept(this);
		this.say(" * ");
		e.right.accept(this);
		return;
	}

	// statements
	@Override
	public void visit(codegen.C.stm.Assign s) {
		this.printSpaces();
		// if (s.isField)
		// this.say("this->");
		this.say(s.id + " = ");
		s.exp.accept(this);
		this.say(";\n");
		return;
	}

	@Override
	public void visit(codegen.C.stm.AssignArray s) {
		this.printSpaces();
		// if (s.isField)
		// this.say("this->");
		this.say(s.id + "[");
		s.index.accept(this);
		this.say(" + 4 ");
		this.say("] = ");
		s.exp.accept(this);
		this.say(";\n");
	}

	@Override
	public void visit(codegen.C.stm.Block s) {
	
		this.printSpaces();
		this.sayln("{");
		this.indent();

		for (codegen.C.stm.T stm : s.stms)
			stm.accept(this);

		this.unIndent();
		this.printSpaces();
		this.sayln("}");
	}

	@Override
	public void visit(codegen.C.stm.If s) {
		this.printSpaces();
		this.say("if (");
		s.condition.accept(this);
		this.sayln(")");
		this.indent();
		s.thenn.accept(this);
		this.unIndent();
		this.sayln("");
		this.printSpaces();
		this.sayln("else");
		this.indent();
		s.elsee.accept(this);
		this.sayln("");
		this.unIndent();
		return;
	}

	@Override
	public void visit(codegen.C.stm.Print s) {
		// this.printSpaces();
		// this.say("System_out_println (");
		// s.exp.accept(this);
		// this.sayln(");");
		this.printSpaces();
		this.say("System_out_println (");
		s.exp.accept(this);
		this.sayln(");");
		return;
	}

	@Override
	public void visit(codegen.C.stm.While s) {
		this.printSpaces();
		this.say("while");
		this.say("(");
		s.condition.accept(this);
		this.say(")\n");
		s.body.accept(this);

	}

	// type
	@Override
	public void visit(codegen.C.type.Class t) {
		// this.say("struct " + t.id + " *");
		this.say("struct " + t.id + " *");
	}

	@Override
	public void visit(codegen.C.type.Int t) {
		this.say("int");
	}

	@Override
	public void visit(codegen.C.type.IntArray t) {
		this.say("int *");
	}

	// dec
	@Override
	public void visit(codegen.C.dec.Dec d) {
		d.type.accept(this);
		this.say(" ");
		this.say(d.id);
	}

	// method
	@Override
	public void visit(codegen.C.method.Method m) {

		this.sayln("//the gc frame of the method");

		this.sayln("    struct " + m.classId + "_" + m.id + "_gc_frame{");
		this.sayln("    void *prev;");
		this.sayln("    char *arguments_gc_map;  ");
		this.sayln("    int *arguments_base_address;");
		this.sayln("    char *locals_gc_map;");
		for (codegen.C.dec.T d : m.locals) {
			codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;
			this.say("   ");
			if (dec.type instanceof codegen.C.type.Class
					|| dec.type instanceof codegen.C.type.IntArray) {
				dec.type.accept(this);
				this.say(" " + dec.id + ";\n");
			}
		}

		this.sayln("};");

		m.retType.accept(this);
		this.say(" " + m.classId + "_" + m.id + "(");
		int size = m.formals.size();
		for (codegen.C.dec.T d : m.formals) {
			codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;
			size--;
			dec.type.accept(this);
			this.say(" " + dec.id);
			if (size > 0)
				this.say(", ");
		}
		this.sayln(")");
		this.sayln("{");

		// the above is the function name
		String sb = "";
		for (codegen.C.dec.T d : m.formals) {
			codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;

			if (dec.type instanceof codegen.C.type.Class)
				sb += "1";
			else
				sb += "0";

		}

		// arguments_gc_map
		String args_gc_map = m.id + "_arguments_gc_map";
		String locals_gc_map = m.id + "_locals_gc_map";

		this.sayln("	char *" + args_gc_map + " = \"" + sb.toString() + "\";");

		sb = "";
		for (codegen.C.dec.T d : m.locals) {
			codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;

			if (dec.type instanceof codegen.C.type.Class)
				sb += "1";
			else
				sb += "0";

		}
		this.sayln("	char *" + locals_gc_map + " = \"" + sb.toString() + "\";");

		this.sayln("   struct " + m.classId + "_" + m.id + "_gc_frame frame;");
		this.sayln("   memset(&frame,0,sizeof(frame));");
		this.sayln("   frame.prev =prev;");
		this.sayln("   prev = &frame;");
		this.sayln("   frame.arguments_gc_map = " + args_gc_map + ";");
		this.sayln("   frame.arguments_base_address = &this;");
		this.sayln("   frame.locals_gc_map = " + locals_gc_map + ";");

		// the below is the locals and stms
		for (codegen.C.dec.T d : m.locals) {
			codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;
			this.say("  ");
			if (dec.type instanceof codegen.C.type.Class
					|| dec.type instanceof codegen.C.type.IntArray) {

			} else {
				dec.type.accept(this);
				this.say(" " + dec.id + ";\n");
			}
		}
		this.sayln("");
		for (codegen.C.stm.T s : m.stms) {
			if (s != null)
				s.accept(this);
		}

		this.sayln("    prev =frame.prev; ");

		this.say("  return ");
		m.retExp.accept(this);
		this.sayln(";");
		this.sayln("}");

		return;
	}

	@Override
	public void visit(codegen.C.mainMethod.MainMethod m) {

		String locals_gc = "";
		this.sayln("struct " + "Tiger_main_gc_frame{");
		this.sayln("    void *prev;");
		this.sayln("    char *arguments_gc_map;  ");
		this.sayln("    int *arguments_base_address;");
		this.sayln("    char *locals_gc_map;");
		for (codegen.C.dec.T dec : m.locals) {

			codegen.C.dec.Dec d = (codegen.C.dec.Dec) dec;

			if (d.type instanceof codegen.C.type.Class
					|| d.type instanceof codegen.C.type.IntArray) {
				this.say("  ");
				d.type.accept(this);
				this.say(" ");
				this.sayln(d.id + ";");
				locals_gc += "1";
			} else
				locals_gc += "0";
		}
		this.sayln("};");

		this.sayln("int Tiger_main ()");
		this.sayln("{");

		this.sayln("    struct " + "Tiger_main_gc_frame frame;");
		this.sayln("     memset(&frame,0,sizeof(frame));");
		this.sayln("    frame.prev =prev;");
		this.sayln("    prev = &frame;");
		this.sayln("    frame.arguments_gc_map = " + "\"\";");
		this.sayln("    frame.locals_gc_map = \"" + locals_gc.toString()
				+ "\";");

		for (codegen.C.dec.T dec : m.locals) {
			this.say("  ");
			codegen.C.dec.Dec d = (codegen.C.dec.Dec) dec;

			if (d.type instanceof codegen.C.type.Class
					|| d.type instanceof codegen.C.type.IntArray) {
				// codegen.C.type.Class cl = (codegen.C.type.Class) d.type;
				// fieldtable.put(d.id, cl.id);
			} else {
				d.type.accept(this);
				this.say(" ");
				this.sayln(d.id + ";");
			}

		}
		// add
		if (m.stm != null)
			m.stm.accept(this);

		this.sayln("    prev =frame.prev; ");
		this.sayln("}\n");
		return;
	}

	// vtables
	@Override
	public void visit(codegen.C.vtable.Vtable v) {
		this.sayln("struct " + v.id + "_vtable");
		this.sayln("{");
		this.say("  ");
		this.sayln("char *" + v.id + "_gc_map;");

		for (codegen.C.Ftuple t : v.ms) {
			this.say("  ");
			t.ret.accept(this);
			this.sayln(" (*" + t.id + ")();");
		}

		this.sayln("};\n");
		return;
	}

	private void outputVtable(codegen.C.vtable.Vtable v) {
		this.sayln("struct " + v.id + "_vtable " + v.id + "_vtable_ = ");
		this.sayln("{");

		String fieldmap = table.get(v.id);
		this.say("  ");
		// if (fieldmap != null && fieldmap.length() != 0) {
		// this.sayln("\"" + fieldmap + "\"" + ",");
		// } else {
		// this.sayln("\"-1\"" + ",");
		// }
		if (fieldmap != null && fieldmap.length() != 0) {
			this.sayln("\"" + fieldmap + "\"" + ",");
		} else {
			this.sayln("\"\"" + ",");
		}
		for (codegen.C.Ftuple t : v.ms) {
			this.say("  ");
			this.sayln(t.classs + "_" + t.id + ",");
		}

		this.sayln("};\n");
		return;
	}

	// class
	@Override
	public void visit(codegen.C.classs.Class c) {

		this.sayln("struct " + c.id + "{");
		this.say("  ");
		this.sayln("struct " + c.id + "_vtable" + " *vptr; ");
		this.say("  ");
		this.sayln("int isObjOrArray; ");
		this.say("  ");
		this.sayln("unsigned length; ");
		this.say("  ");
		this.sayln("void *forwarding; ");
		for (codegen.C.Tuple t : c.decs) {

			// if ((t.type instanceof codegen.C.type.Class)
			// || (t.type instanceof codegen.C.type.IntArray)) {
			this.say("  ");
			t.type.accept(this);
			this.say(" ");
			this.sayln(t.id + ";");
			// }
		}
		// this.sayln("};");

		// this.sayln("struct " + c.id);
		// this.sayln("{");
		// this.sayln("  struct " + c.id + "_vtable *vptr;");
		for (codegen.C.Tuple t : c.decs) {
			// this.say("  ");
			// t.type.accept(this);
			// this.say(" ");

			String classid = table.get(c.id);
			if (classid == null) {
				if (t.type instanceof codegen.C.type.Class)
					table.put(c.id, "1");
				else
					table.put(c.id, "0");
			} else {
				if (t.type instanceof codegen.C.type.Class)
					table.put(c.id, classid + "1");
				else
					table.put(c.id, classid + "0");
			}

		}
		this.sayln("};");

		return;
	}

	// program
	@Override
	public void visit(codegen.C.program.Program p) {
		// we'd like to output to a file, rather than the "stdout".
		try {
			String outputName = null;
			if (Control.outputName != null)
				outputName = Control.outputName;
			else if (Control.fileName != null)
				outputName = Control.fileName + ".c";
			else
				outputName = "a.c";

			this.writer = new java.io.BufferedWriter(
					new java.io.OutputStreamWriter(
							new java.io.FileOutputStream(outputName)));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		this.sayln("// This is automatically generated by the Tiger compiler.");
		this.sayln("// Do NOT modify!\n");
		this.sayln("#include <memory.h>");
		this.sayln("// a global pointer");
		this.sayln("extern void *prev;");

		this.sayln("// vtables structures");
		for (codegen.C.vtable.T v : p.vtables) {
			v.accept(this);
		}

		this.sayln("// structures");
		for (codegen.C.classs.T c : p.classes) {
			c.accept(this);
		}
		this.sayln("");
		this.sayln("// methods");
		for (codegen.C.method.T m : p.methods) {
			codegen.C.method.Method method = (codegen.C.method.Method) m;
			method.retType.accept(this);
			this.say(" " + method.classId + "_" + method.id + "(");
			int size = method.formals.size();
			for (codegen.C.dec.T d : method.formals) {
				codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;
				size--;
				dec.type.accept(this);
				this.say(" " + dec.id);
				if (size > 0)
					this.say(", ");
			}
			this.sayln(");");

		}
		// this.sayln("//the gc frame of the method");

		this.sayln("// vtables");
		for (codegen.C.vtable.T v : p.vtables) {
			outputVtable((codegen.C.vtable.Vtable) v);
		}
		this.sayln("");

		this.sayln("// methods");
		for (codegen.C.method.T m : p.methods) {
			m.accept(this);
		}
		this.sayln("");

		this.sayln("// main method");
		p.mainMethod.accept(this);
		this.sayln("");

		this.say("\n\n");

		try {
			this.writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void visit(codegen.C.exp.True e) {
		// TODO Auto-generated method stub
		// this.say("true");
		this.say("1");
	}

	@Override
	public void visit(codegen.C.exp.False e) {
		// TODO Auto-generated method stub
		// this.say("false");
		this.say("0");
	}

	@Override
	public void visit(Boolean t) {
		// TODO Auto-generated method stub
		// this.say("bool");
		this.say("int");
	}

	@Override
	public void visit(ParanExp e) {
		// TODO Auto-generated method stub
		this.say("( ");
		e.exp.accept(this);
		this.say(" )");
	}
//>>>>>>> Lab4

}
