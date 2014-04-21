package ast.optimizations;

import ast.exp.ParenExp;

// Constant folding optimizations on an AST.

public class ConstFold implements ast.Visitor {
	private ast.classs.T newClass;
	private ast.mainClass.T mainClass;
	public ast.program.T program;

	public ast.type.T type;
	public ast.dec.T dec;
	public ast.stm.T stm;
	public ast.exp.T exp;
	public ast.method.T method;
	public java.util.LinkedList<ast.classs.T> classes;
	public java.util.LinkedList<ast.method.T> methods;
	public java.util.LinkedList<ast.dec.T> decs;
	public java.util.LinkedList<ast.stm.T> stms;
	public boolean can_cal;
	public int int_res;
	public boolean bool_res;

	public ConstFold() {
		this.newClass = null;
		this.mainClass = null;
		this.program = null;
		this.type = null;
		this.dec = null;
		this.stm = null;
		this.exp = null;
		this.method = null;
	}

	// //////////////////////////////////////////////////////
	//
	public String genId() {
		return util.Temp.next();
	}

	// /////////////////////////////////////////////////////
	// expressions
	@Override
	public void visit(ast.exp.Add e) {
		int L_int = 0, R_int;
		e.left.accept(this);
		if (this.can_cal) {
			L_int = this.int_res;
		}
		ast.exp.T LL = this.exp;
		if (!this.can_cal) {
			this.can_cal = true;
			e.right.accept(this);
			if (this.can_cal) {
				if (0 == this.int_res) {
					this.can_cal = false;
					this.exp = LL;
					return;
				} else {
					this.can_cal = false;
					this.exp = new ast.exp.Add(this.exp, LL, e.line);
					return;
				}
			}
		} else {
			if (0 == this.int_res) {
				e.right.accept(this);
				return;
			}
		}
		e.right.accept(this);
		ast.exp.T RR = this.exp;
		if (this.can_cal) {
			R_int = this.int_res;
			this.int_res = L_int + R_int;
			this.exp = new ast.exp.Num(this.int_res);
			return;
		}
		this.exp = new ast.exp.Add(LL, RR, e.line);
	}

	@Override
	public void visit(ast.exp.And e) {
		boolean L_bool = true;
		e.left.accept(this);
		if (this.can_cal) {
			L_bool = this.bool_res;
		}
		ast.exp.T LL = this.exp;
		if (!this.can_cal) {
			this.can_cal = true;
			e.right.accept(this);
			if (this.can_cal) {
				if (this.bool_res)
					this.exp = LL;
				return;
			} else {
				this.exp = new ast.exp.And(LL, this.exp, e.line);
				return;
			}
		} else {
			if (false == L_bool) {
				this.exp = new ast.exp.False();
			} else { // true
				e.right.accept(this);
				if (this.can_cal) {
					if (this.bool_res) {
						this.exp = new ast.exp.True();
					} else {
						this.exp = new ast.exp.False();
					}
				}
			}
		}
	}

	@Override
	public void visit(ast.exp.ArraySelect e) {
		this.can_cal = false;
		e.array.accept(this);
		ast.exp.T id = this.exp;
		e.index.accept(this);
		this.exp = new ast.exp.ArraySelect(id, this.exp, e.line);
	}

	@Override
	public void visit(ast.exp.Call e) {
		this.can_cal = true;
		e.exp.accept(this);
		ast.exp.T new_exp = this.exp;
		java.util.LinkedList<ast.exp.T> args = new java.util.LinkedList<ast.exp.T>();
		for (ast.exp.T x : e.args) {
			this.can_cal = true;
			x.accept(this);
			args.add(this.exp);
		}
		this.can_cal = false;
		this.exp = new ast.exp.Call(new_exp, e.id, args, e.type, e.at, e.rt,
				e.line);
		return;
	}

	@Override
	public void visit(ast.exp.False e) {
		if (this.can_cal) {
			this.bool_res = false;
		}
		this.exp = new ast.exp.False();
	}

	@Override
	public void visit(ast.exp.Id e) {
		this.can_cal = false;
		this.exp = new ast.exp.Id(e.id, e.isField, e.line);
		return;
	}

	@Override
	public void visit(ast.exp.Length e) {
		this.can_cal = false;
		e.array.accept(this);
		this.exp = new ast.exp.Length(this.exp);
	}

	@Override
	public void visit(ast.exp.Lt e) {
		int L_int = 0, R_int;
		e.left.accept(this);
		if (this.can_cal) {
			L_int = this.int_res;
		}
		ast.exp.T LL = this.exp;
		if (!this.can_cal) {
			this.can_cal = true;
			e.right.accept(this);
			this.can_cal = false;
			this.exp = new ast.exp.Lt(LL, this.exp, e.line);
		} else { // can
			e.right.accept(this);
			if (this.can_cal) {
				R_int = this.int_res;
				if (L_int < R_int) {
					this.exp = new ast.exp.True();
				} else {
					this.exp = new ast.exp.False();
				}
			} else
				this.exp = new ast.exp.Lt(LL, this.exp, e.line);
		}
		return;
	}

	@Override
	public void visit(ast.exp.NewIntArray e) {
		this.can_cal = false;
		e.exp.accept(this);
		this.exp = new ast.exp.NewIntArray(this.exp);
	}

	@Override
	public void visit(ast.exp.NewObject e) {
		this.can_cal = false;
		this.exp = new ast.exp.NewObject(e.id, e.line);
		return;
	}

	@Override
	public void visit(ast.exp.Not e) {
		e.exp.accept(this);
		if (this.can_cal) {
			this.bool_res = (!this.bool_res);
			if (this.bool_res) {
				this.exp = new ast.exp.True();
			} else
				this.exp = new ast.exp.False();
			return;
		}
		this.exp = new ast.exp.Not(this.exp, e.line);
	}

	@Override
	public void visit(ast.exp.Num e) {
		if (this.can_cal) {
			this.int_res = e.num;
		}
		this.exp = new ast.exp.Num(e.num);
		return;
	}

	@Override
	public void visit(ast.exp.Sub e) {
		int L_int = 0, R_int;
		e.left.accept(this);
		if (this.can_cal) {
			L_int = this.int_res;
		}
		ast.exp.T LL = this.exp;
		if (!this.can_cal) {
			this.can_cal = true;
			e.right.accept(this);
			if (this.can_cal) {
				if (0 == this.int_res) {
					this.can_cal = false;
					this.exp = LL;
					return;
				} else {
					this.can_cal = false;
					this.exp = new ast.exp.Sub(LL, this.exp, e.line);
					return;
				}
			}
		}
		e.right.accept(this);
		ast.exp.T RR = this.exp;
		if (this.can_cal) {
			R_int = this.int_res;
			this.int_res = L_int - R_int;
			this.exp = new ast.exp.Num(this.int_res);
			return;
		}
		this.exp = new ast.exp.Sub(LL, RR, e.line);
	}

	@Override
	public void visit(ast.exp.This e) {
		this.can_cal = false;
		this.exp = new ast.exp.This();
		return;
	}

	@Override
	public void visit(ast.exp.Times e) {
		int L_int = 0, R_int;
		e.left.accept(this);
		if (this.can_cal) {
			L_int = this.int_res;
		}
		ast.exp.T LL = this.exp;
		if (!this.can_cal) {
			this.can_cal = true;
			e.right.accept(this);
			if (this.can_cal && 0 == this.int_res) {
				this.can_cal = true;
				this.exp = new ast.exp.Num(0);
				return;
			}
		} else {
			if (0 == this.int_res) {
				e.right.accept(this);
				return;
			}
		}
		e.right.accept(this);
		ast.exp.T RR = this.exp;
		if (this.can_cal) {
			R_int = this.int_res;
			this.int_res = L_int * R_int;
			this.exp = new ast.exp.Num(this.int_res);
			return;
		}
		this.exp = new ast.exp.Times(LL, RR, e.line);
		return;
	}

	@Override
	public void visit(ast.exp.True e) {
		if (this.can_cal) {
			this.bool_res = true;
		}
		this.exp = new ast.exp.True();
	}

	// statements
	@Override
	public void visit(ast.stm.Assign s) {
		this.can_cal = true;
		s.exp.accept(this);
		this.stm = new ast.stm.Assign(s.id, this.exp, s.isField, s.line);
		return;
	}

	@Override
	public void visit(ast.stm.AssignArray s) {
		this.can_cal = true;
		s.index.accept(this);
		ast.exp.T index = this.exp;

		this.can_cal = true;
		s.exp.accept(this);

		this.stm = new ast.stm.AssignArray(s.id, index, this.exp, s.isField,
				s.line);
	}

	@Override
	public void visit(ast.stm.Block s) {
		java.util.LinkedList<ast.stm.T> stms = new java.util.LinkedList<ast.stm.T>();
		for (ast.stm.T x : s.stms) {
			x.accept(this);
			if (null != this.stm)
				stms.add(this.stm);
		}
		this.stm = new ast.stm.Block(stms);
	}

	@Override
	public void visit(ast.stm.If s) {
		this.can_cal = true;
		s.condition.accept(this);
		if (!this.can_cal) {
			ast.exp.T condition = this.exp;
			s.thenn.accept(this);
			ast.stm.T thenn = this.stm;
			s.elsee.accept(this);
			ast.stm.T elsee = this.stm;
			this.stm = new ast.stm.If(condition, thenn, elsee);
		} else {
			if (this.bool_res) {
				s.thenn.accept(this);
			} else
				s.elsee.accept(this);
		}
		return;
	}

	@Override
	public void visit(ast.stm.Print s) {
		this.can_cal = true;
		s.exp.accept(this);
		this.stm = new ast.stm.Print(this.exp);
		return;
	}

	@Override
	public void visit(ast.stm.While s) {
		s.condition.accept(this);
		ast.exp.T condition = this.exp;
		if ((this.can_cal && this.bool_res) || (!this.can_cal)) {
			s.body.accept(this);
			ast.stm.T stm = this.stm;
			this.stm = new ast.stm.While(condition, stm);
		} else {
			this.stm = null;
		}
		return;
	}

	// type
	@Override
	public void visit(ast.type.Boolean t) {
		this.type = new ast.type.Boolean();
	}

	@Override
	public void visit(ast.type.Class t) {
		this.type = new ast.type.Class(t.id);
	}

	@Override
	public void visit(ast.type.Int t) {
		this.type = new ast.type.Int();
	}

	@Override
	public void visit(ast.type.IntArray t) {
		this.type = t;
	}

	// dec
	@Override
	public void visit(ast.dec.Dec d) {
		this.dec = d;
		return;
	}

	// method
	@Override
	public void visit(ast.method.Method m) {
		this.can_cal = true;
		this.stms = new java.util.LinkedList<ast.stm.T>();
		for (ast.stm.T s : m.stms) {
			s.accept(this);
			if (null != this.stm)
				this.stms.add(this.stm);
		}
		m.retExp.accept(this);
		ast.exp.T new_ret = this.exp;
		this.method = new ast.method.Method(m.retType, m.id, m.formals,
				m.locals, this.stms, new_ret);
		return;
	}

	// class
	@Override
	public void visit(ast.classs.Class c) {
		this.decs = new java.util.LinkedList<ast.dec.T>();
		for (ast.dec.T dec : c.decs) {
			dec.accept(this);
			this.decs.add(this.dec);
		}
		this.methods = new java.util.LinkedList<ast.method.T>();
		for (ast.method.T m : c.methods) {
			m.accept(this);
			this.methods.add(this.method);
		}
		this.newClass = new ast.classs.Class(c.id, c.extendss, this.decs,
				this.methods);
		return;
	}

	// main class
	@Override
	public void visit(ast.mainClass.MainClass c) {
		for (ast.stm.T stm : c.stms)
			stm.accept(this);
		this.mainClass = new ast.mainClass.MainClass(c.id, c.arg, c.locals,
				c.stms);
		return;
	}

	// program
	@Override
	public void visit(ast.program.Program p) {

		// You should comment out this line of code:
		this.program = p;
		this.classes = new java.util.LinkedList<ast.classs.T>();
		p.mainClass.accept(this);
		for (ast.classs.T classs : p.classes) {
			ast.classs.Class c = (ast.classs.Class) classs;
			c.accept(this);
			this.classes.add(this.newClass);
		}
		this.program = new ast.program.Program(this.mainClass, this.classes);

		if (control.Control.isTracing("ast.ConstFold")) {
			System.out.println("before optimization:");
			ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
			p.accept(pp);
			System.out.println("after optimization:");
			this.program.accept(pp);
		}
		return;
	}

	@Override
	public void visit(ParenExp e) {
		// TODO Auto-generated method stub
		e.exp.accept(this);
		this.exp = new ast.exp.ParenExp(this.exp);
	}
}
