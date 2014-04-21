package cfg.optimizations;

public class DeadCode implements cfg.Visitor {
	private cfg.stm.T stm;
	private cfg.method.T method;
	private cfg.block.T block;
	private cfg.mainMethod.T mainMethod;
	public cfg.program.T program;
	public java.util.HashMap<cfg.stm.T, java.util.HashSet<String>> stmLiveOut;

	public DeadCode() {
		this.method = null;
		this.stm = null;
		this.mainMethod = null;
		this.program = null;
		this.program = null;
	}

	// /////////////////////////////////////////////////////
	// operand
	@Override
	public void visit(cfg.operand.Int operand) {
	}

	@Override
	public void visit(cfg.operand.Var operand) {
	}

	@Override
	public void visit(cfg.operand.IntArray o) {

	}

	// statements
	@Override
	public void visit(cfg.stm.Add s) {
		java.util.HashSet<String> out = this.stmLiveOut.get(s);
		if (!out.contains(s.dst)) {
			this.stm = null;
		} else
			this.stm = s;
	}

	@Override
	public void visit(cfg.stm.InvokeVirtual s) {
		this.stm = s;
	}

	@Override
	public void visit(cfg.stm.Lt s) {
		java.util.HashSet<String> out = this.stmLiveOut.get(s);
		if (!out.contains(s.dst)) {
			this.stm = null;
		} else
			this.stm = s;
	}

	@Override
	public void visit(cfg.stm.Move s) {
		java.util.HashSet<String> out = this.stmLiveOut.get(s);
		if (!out.contains(s.dst) && !s.isField) {
			this.stm = null;
		} else
			this.stm = s;
	}

	@Override
	public void visit(cfg.stm.MoveArray s) {
		this.stm = s;
	}

	@Override
	public void visit(cfg.stm.NewObject s) {
		java.util.HashSet<String> out = this.stmLiveOut.get(s);
		if (!out.contains(s.dst)) {
			this.stm = null;
		} else
			this.stm = s;
	}

	@Override
	public void visit(cfg.stm.NewIntArray s) {
		java.util.HashSet<String> out = this.stmLiveOut.get(s);
		if (!out.contains(s.dst)) {
			this.stm = null;
		} else
			this.stm = s;
	}

	@Override
	public void visit(cfg.stm.Print s) {
		this.stm = s;
	}

	@Override
	public void visit(cfg.stm.Sub s) {
		java.util.HashSet<String> out = this.stmLiveOut.get(s);
		if (!out.contains(s.dst)) {
			this.stm = null;
		} else
			this.stm = s;
	}

	@Override
	public void visit(cfg.stm.Not s) {
		// TODO Auto-generated method stub
		java.util.HashSet<String> out = this.stmLiveOut.get(s);
		if (!out.contains(s.dst)) {
			this.stm = null;
		} else
			this.stm = s;
	}

	@Override
	public void visit(cfg.stm.Times s) {
		java.util.HashSet<String> out = this.stmLiveOut.get(s);
		if (!out.contains(s.dst)) {
			this.stm = null;
		} else
			this.stm = s;
	}

	@Override
	public void visit(cfg.stm.And s) {
		// TODO Auto-generated method stub
		java.util.HashSet<String> out = this.stmLiveOut.get(s);
		if (!out.contains(s.dst)) {
			this.stm = null;
		} else
			this.stm = s;
	}

	// transfer
	@Override
	public void visit(cfg.transfer.If s) {
	}

	@Override
	public void visit(cfg.transfer.Goto s) {
	}

	@Override
	public void visit(cfg.transfer.Return s) {
	}

	// type
	@Override
	public void visit(cfg.type.Class t) {
	}

	@Override
	public void visit(cfg.type.Int t) {
	}

	@Override
	public void visit(cfg.type.IntArray t) {
	}

	// dec
	@Override
	public void visit(cfg.dec.Dec d) {
	}

	// block
	@Override
	public void visit(cfg.block.Block b) {
		java.util.LinkedList<cfg.stm.T> new_stms = new java.util.LinkedList<>();
		for (cfg.stm.T s : b.stms) {
			s.accept(this);
			if (null != this.stm)
				new_stms.add(this.stm);
		}
		this.block = new cfg.block.Block(b.label, new_stms, b.transfer);
	}

	// method
	@Override
	public void visit(cfg.method.Method m) {
		java.util.LinkedList<cfg.block.T> new_blocks = new java.util.LinkedList<>();
		for (cfg.block.T block : m.blocks) {
			block.accept(this);
			new_blocks.add(this.block);
		}
		this.method = new cfg.method.Method(m.retType, m.id, m.classId,
				m.formals, m.locals, new_blocks, m.entry, m.exit, m.retValue);
	}

	@Override
	public void visit(cfg.mainMethod.MainMethod m) {
		java.util.LinkedList<cfg.block.T> new_blocks = new java.util.LinkedList<>();
		for (cfg.block.T block : m.blocks) {
			block.accept(this);
			new_blocks.add(this.block);
		}
		this.mainMethod = new cfg.mainMethod.MainMethod(m.locals, new_blocks);
	}

	// vtables
	@Override
	public void visit(cfg.vtable.Vtable v) {
	}

	// class
	@Override
	public void visit(cfg.classs.Class c) {
	}

	// program
	@Override
	public void visit(cfg.program.Program p) {
		p.mainMethod.accept(this);
		java.util.LinkedList<cfg.method.T> new_method = new java.util.LinkedList<>();
		for (cfg.method.T mth : p.methods) {
			mth.accept(this);
			new_method.add(this.method);
		}
		this.program = new cfg.program.Program(p.classes, p.vtables,
				new_method, this.mainMethod);

	}
}
