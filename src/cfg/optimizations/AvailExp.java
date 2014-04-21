package cfg.optimizations;

import java.util.Iterator;

public class AvailExp implements cfg.Visitor {

	public String def;
	public cfg.stm.T exp;
	public java.util.HashMap<String, java.util.LinkedList<cfg.transfer.T>> re;
	public java.util.HashMap<Integer, cfg.stm.T> index_stm;
	public java.util.HashMap<cfg.stm.T, Integer> stm_index;
	public java.util.HashMap<Integer, cfg.transfer.T> index_transfer;
	public java.util.HashMap<cfg.transfer.T, Integer> transfer_index;
	public java.util.HashMap<cfg.stm.T, young_and_old> stm_set;
	public java.util.HashMap<cfg.transfer.T, young_and_old> tran_set;
	public java.util.HashMap<cfg.stm.T, java.util.HashSet<cfg.stm.T>> stmIn;
	public java.util.HashMap<cfg.stm.T, java.util.HashSet<cfg.stm.T>> stmOut;
	public java.util.HashMap<cfg.transfer.T, java.util.HashSet<cfg.stm.T>> transferIn;
	public java.util.HashMap<cfg.transfer.T, java.util.HashSet<cfg.stm.T>> transferOut;

	public AvailExp() {
		this.stmIn = new java.util.HashMap<>();
		this.stmOut = new java.util.HashMap<>();

		this.stm_set = new java.util.HashMap<>();
		this.tran_set = new java.util.HashMap<>();

		this.transferIn = new java.util.HashMap<>();
		this.transferOut = new java.util.HashMap<>();
	}

	public class young_and_old {
		public java.util.HashSet<cfg.stm.T> young_in;
		public java.util.HashSet<cfg.stm.T> young_out;
		public java.util.HashSet<cfg.stm.T> old_in;
		public java.util.HashSet<cfg.stm.T> old_out;

		public young_and_old() {
			this.young_in = new java.util.HashSet<>();
			this.old_in = new java.util.HashSet<>();
			this.young_out = new java.util.HashSet<>();
			this.old_out = new java.util.HashSet<>();
		}

		public void give() {
			old_in.addAll(young_in);
			old_out.addAll(young_out);
		}

		public boolean equal() {
			return (this.old_in.equals(this.young_in))
					&& (this.old_out.equals(this.young_out));
		}
	}

	public boolean not_change() {
		for (Iterator it = this.stm_set.keySet().iterator(); it.hasNext();) {
			cfg.stm.T stm = (cfg.stm.T) it.next();
			young_and_old set = this.stm_set.get(stm);
			if (!set.equal())
				return false;
		}
		for (Iterator it = this.tran_set.keySet().iterator(); it.hasNext();) {
			cfg.transfer.T tran = (cfg.transfer.T) it.next();
			young_and_old set = this.tran_set.get(tran);
			if (!set.equal())
				return false;
		}
		return true;
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
		if (s.left instanceof cfg.operand.Var) {
			cfg.operand.Var LL = (cfg.operand.Var) s.left;
			if (LL.id.equals(this.def)) {
				this.exp = s;
				return;
			}
		}
		if (s.right instanceof cfg.operand.Var) {
			cfg.operand.Var RR = (cfg.operand.Var) s.right;
			if (RR.id.equals(this.def)) {
				this.exp = s;
				return;
			}
		}
	}

	@Override
	public void visit(cfg.stm.InvokeVirtual s) {
	}

	@Override
	public void visit(cfg.stm.Lt s) {
		if (s.left instanceof cfg.operand.Var) {
			cfg.operand.Var LL = (cfg.operand.Var) s.left;
			if (LL.id.equals(this.def)) {
				this.exp = s;
				return;
			}
		}
		if (s.right instanceof cfg.operand.Var) {
			cfg.operand.Var RR = (cfg.operand.Var) s.right;
			if (RR.id.equals(this.def)) {
				this.exp = s;
				return;
			}
		}
	}

	@Override
	public void visit(cfg.stm.Move s) {
		if (s.dst.equals(this.def))
			this.exp = s;
	}

	@Override
	public void visit(cfg.stm.MoveArray s) {
	}

	@Override
	public void visit(cfg.stm.NewObject s) {
	}

	@Override
	public void visit(cfg.stm.NewIntArray s) {
	}

	@Override
	public void visit(cfg.stm.Print s) {
	}

	@Override
	public void visit(cfg.stm.Sub s) {
		if (s.left instanceof cfg.operand.Var) {
			cfg.operand.Var LL = (cfg.operand.Var) s.left;
			if (LL.id.equals(this.def)) {
				this.exp = s;
				return;
			}
		}
		if (s.right instanceof cfg.operand.Var) {
			cfg.operand.Var RR = (cfg.operand.Var) s.right;
			if (RR.id.equals(this.def)) {
				this.exp = s;
				return;
			}
		}
	}

	@Override
	public void visit(cfg.stm.Not s) {
		if (s.exp instanceof cfg.operand.Var) {
			cfg.operand.Var LL = (cfg.operand.Var) s.exp;
			if (LL.id.equals(this.def)) {
				this.exp = s;
				return;
			}
		}
	}

	@Override
	public void visit(cfg.stm.Times s) {
		if (s.left instanceof cfg.operand.Var) {
			cfg.operand.Var LL = (cfg.operand.Var) s.left;
			if (LL.id.equals(this.def)) {
				this.exp = s;
				return;
			}
		}
		if (s.right instanceof cfg.operand.Var) {
			cfg.operand.Var RR = (cfg.operand.Var) s.right;
			if (RR.id.equals(this.def)) {
				this.exp = s;
				return;
			}
		}
	}

	@Override
	public void visit(cfg.stm.And s) {
		if (s.left instanceof cfg.operand.Var) {
			cfg.operand.Var LL = (cfg.operand.Var) s.left;
			if (LL.id.equals(this.def)) {
				this.exp = s;
				return;
			}
		}
		if (s.right instanceof cfg.operand.Var) {
			cfg.operand.Var RR = (cfg.operand.Var) s.right;
			if (RR.id.equals(this.def)) {
				this.exp = s;
				return;
			}
		}
	}

	// transfer
	@Override
	public void visit(cfg.transfer.If s) {
		String label = s.truee.toString();
		cfg.transfer.T tmp = s;
		this.re.get(label).add(tmp);
		label = s.falsee.toString();
		this.re.get(label).add(tmp);
	}

	@Override
	public void visit(cfg.transfer.Goto s) {
		String label = s.label.toString();
		cfg.transfer.T tmp = s;
		this.re.get(label).add(tmp);
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
	}

	// method
	@Override
	public void visit(cfg.method.Method m) {
		int cnt = 0;
		this.index_stm = new java.util.HashMap<>();
		this.stm_index = new java.util.HashMap<>();
		this.transfer_index = new java.util.HashMap<>();
		this.index_transfer = new java.util.HashMap<>();
		this.re = new java.util.HashMap<>();
		for (cfg.block.T bb : m.blocks) {
			cfg.block.Block block = (cfg.block.Block) bb;
			this.re.put(block.label.toString(),
					new java.util.LinkedList<cfg.transfer.T>());
			for (cfg.stm.T stm : block.stms) {
				this.index_stm.put(cnt, stm);
				this.stm_index.put(stm, cnt);
				this.stm_set.put(stm, new young_and_old());
				cnt++;
			}
			this.tran_set.put(block.transfer, new young_and_old());
			this.transfer_index.put(block.transfer, cnt);
			this.index_transfer.put(cnt, block.transfer);
			cnt++;
		}
		for (cfg.block.T bb : m.blocks) {
			cfg.block.Block block = (cfg.block.Block) bb;
			block.transfer.accept(this);
		}

		do {
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				for (cfg.stm.T s : block.stms) {
					this.stm_set.get(s).give();
				}
				this.tran_set.get(block.transfer).give();
			}
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				String label = block.label.toString();
				boolean first = true;
				cfg.stm.T last = null;
				for (cfg.stm.T stm : block.stms) {
					cfg.stm.T gen = null;
					cfg.stm.T kill = null;
					if (stm instanceof cfg.stm.Move) {
						gen = null;
						kill = stm;
					} else if (stm instanceof cfg.stm.MoveArray) {
						gen = null;
						kill = null;
					} else {
						gen = stm;
						kill = null;
					}
					java.util.HashSet<cfg.stm.T> young_in = this.stm_set
							.get(stm).young_in;
					java.util.HashSet<cfg.stm.T> young_out = this.stm_set
							.get(stm).young_out;
					if (first) {
						first = false;
						java.util.LinkedList<cfg.transfer.T> to = this.re
								.get(label);
						for (cfg.transfer.T tran : to) {
							java.util.HashSet<cfg.stm.T> tran_out = this.tran_set
									.get(tran).young_out;
							young_in.addAll(tran_out);
						}
					} else {
						java.util.HashSet<cfg.stm.T> out = this.stm_set
								.get(last).young_out;
						young_in.addAll(out);
					}
					if (null != gen) {
						young_out.add(gen);// young_out.addAll(gen);
					}
					java.util.HashSet<cfg.stm.T> tmp = new java.util.HashSet<cfg.stm.T>();
					tmp.addAll(young_in);
					if (null != kill) {
						String def = ((cfg.stm.Move) kill).dst;
						for (Iterator it = young_in.iterator(); it.hasNext();) {
							cfg.stm.T member = (cfg.stm.T) it.next();
							this.def = def;
							this.exp = null;
							member.accept(this);
							if (null != this.exp) {
								tmp.remove(this.exp);
							}
						}
					}
					young_out.addAll(tmp);
					last = stm;
				}
				// cfg.stm.T gen = null;
				// cfg.stm.T kill = null;
				java.util.HashSet<cfg.stm.T> young_in = this.tran_set
						.get(block.transfer).young_in;
				java.util.HashSet<cfg.stm.T> young_out = this.tran_set
						.get(block.transfer).young_out;
				if (0 == block.stms.size()) {
					java.util.LinkedList<cfg.transfer.T> to = this.re
							.get(label);
					for (cfg.transfer.T tran : to) {
						java.util.HashSet<cfg.stm.T> tran_out = this.tran_set
								.get(tran).young_out;
						young_in.addAll(tran_out);
					}
				} else {
					java.util.HashSet<cfg.stm.T> out = this.stm_set.get(last).young_out;
					young_in.addAll(out);
				}

				// young_out.addAll(gen);
				young_out.addAll(young_in);
			}
		} while (!not_change());
		for (cfg.block.T bb : m.blocks) {
			cfg.block.Block block = (cfg.block.Block) bb;
			for (cfg.stm.T stm : block.stms) {
				this.stmIn.put(stm, this.stm_set.get(stm).young_in);
				this.stmOut.put(stm, this.stm_set.get(stm).young_out);
			}
			this.transferIn.put(block.transfer,
					this.tran_set.get(block.transfer).young_in);
			this.transferOut.put(block.transfer,
					this.tran_set.get(block.transfer).young_out);
		}
		if (control.Control.isTracing("AvailableExpressions")) {
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				String label = block.label.toString();
				System.out.print("\n" + label + ":");
				for (cfg.stm.T stm : block.stms) {
					int index = this.stm_index.get(stm);
					System.out.print("\n" + index);
					System.out.print("\nIn:");
					java.util.HashSet<cfg.stm.T> in = this.stmIn.get(stm);
					for (Iterator it = in.iterator(); it.hasNext();) {
						cfg.stm.T member = (cfg.stm.T) it.next();
						int k = this.stm_index.get(member);
						System.out.print(k + ",");
					}
					System.out.print("\nOut:");
					java.util.HashSet<cfg.stm.T> out = this.stmOut.get(stm);
					for (Iterator it = out.iterator(); it.hasNext();) {
						cfg.stm.T member = (cfg.stm.T) it.next();
						int k = this.stm_index.get(member);
						System.out.print(k + ",");
					}
				}
				java.util.HashSet<cfg.stm.T> set = this.tran_set
						.get(block.transfer).old_in;
				System.out.print("\n\n   in: ");
				for (Iterator it = set.iterator(); it.hasNext();) {
					cfg.stm.T member = (cfg.stm.T) it.next();
					int k = this.stm_index.get(member);
					System.out.print(k + ",");
				}

				System.out.print("\n  out: ");
				set = this.tran_set.get(block.transfer).old_out;
				for (Iterator it = set.iterator(); it.hasNext();) {
					cfg.stm.T member = (cfg.stm.T) it.next();
					int k = this.stm_index.get(member);
					System.out.print(k + ",");
				}
			}
		}
	}

	@Override
	public void visit(cfg.mainMethod.MainMethod m) {
		int cnt = 0;
		this.index_stm = new java.util.HashMap<>();
		this.stm_index = new java.util.HashMap<>();
		this.transfer_index = new java.util.HashMap<>();
		this.index_transfer = new java.util.HashMap<>();
		this.re = new java.util.HashMap<>();
		for (cfg.block.T bb : m.blocks) {
			cfg.block.Block block = (cfg.block.Block) bb;
			this.re.put(block.label.toString(),
					new java.util.LinkedList<cfg.transfer.T>());
			for (cfg.stm.T stm : block.stms) {
				this.index_stm.put(cnt, stm);
				this.stm_index.put(stm, cnt);
				this.stm_set.put(stm, new young_and_old());
				cnt++;
			}
			this.tran_set.put(block.transfer, new young_and_old());
			this.transfer_index.put(block.transfer, cnt);
			this.index_transfer.put(cnt, block.transfer);
			cnt++;
		}
		for (cfg.block.T bb : m.blocks) {
			cfg.block.Block block = (cfg.block.Block) bb;
			block.transfer.accept(this);
		}

		do {
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				for (cfg.stm.T s : block.stms) {
					this.stm_set.get(s).give();
				}
				this.tran_set.get(block.transfer).give();
			}
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				String label = block.label.toString();
				boolean first = true;
				cfg.stm.T last = null;
				for (cfg.stm.T stm : block.stms) {
					cfg.stm.T gen = null;
					cfg.stm.T kill = null;
					if (stm instanceof cfg.stm.Move) {
						gen = null;
						kill = stm;
					} else if (stm instanceof cfg.stm.MoveArray) {
						gen = null;
						kill = null;
					} else {
						gen = stm;
						kill = null;
					}
					java.util.HashSet<cfg.stm.T> young_in = this.stm_set
							.get(stm).young_in;
					java.util.HashSet<cfg.stm.T> young_out = this.stm_set
							.get(stm).young_out;
					if (first) {
						first = false;
						java.util.LinkedList<cfg.transfer.T> to = this.re
								.get(label);
						for (cfg.transfer.T tran : to) {
							java.util.HashSet<cfg.stm.T> tran_out = this.tran_set
									.get(tran).young_out;
							young_in.addAll(tran_out);
						}
					} else {
						java.util.HashSet<cfg.stm.T> out = this.stm_set
								.get(last).young_out;
						young_in.addAll(out);
					}
					if (null != gen) {
						young_out.add(gen);// young_out.addAll(gen);
					}
					java.util.HashSet<cfg.stm.T> tmp = new java.util.HashSet<cfg.stm.T>();
					tmp.addAll(young_in);
					if (null != kill) {
						String def = ((cfg.stm.Move) kill).dst;
						for (Iterator it = young_in.iterator(); it.hasNext();) {
							cfg.stm.T member = (cfg.stm.T) it.next();
							this.def = def;
							this.exp = null;
							member.accept(this);
							if (null != this.exp) {
								tmp.remove(this.exp);
							}
						}
					}
					young_out.addAll(tmp);
					last = stm;
				}
				// cfg.stm.T gen = null;
				// cfg.stm.T kill = null;
				java.util.HashSet<cfg.stm.T> young_in = this.tran_set
						.get(block.transfer).young_in;
				java.util.HashSet<cfg.stm.T> young_out = this.tran_set
						.get(block.transfer).young_out;
				if (0 == block.stms.size()) {
					java.util.LinkedList<cfg.transfer.T> to = this.re
							.get(label);
					for (cfg.transfer.T tran : to) {
						java.util.HashSet<cfg.stm.T> tran_out = this.tran_set
								.get(tran).young_out;
						young_in.addAll(tran_out);
					}
				} else {
					java.util.HashSet<cfg.stm.T> out = this.stm_set.get(last).young_out;
					young_in.addAll(out);
				}

				// young_out.addAll(gen);
				young_out.addAll(young_in);
			}
		} while (!not_change());
		for (cfg.block.T bb : m.blocks) {
			cfg.block.Block block = (cfg.block.Block) bb;
			for (cfg.stm.T stm : block.stms) {
				this.stmIn.put(stm, this.stm_set.get(stm).young_in);
				this.stmOut.put(stm, this.stm_set.get(stm).young_out);
			}
			this.transferIn.put(block.transfer,
					this.tran_set.get(block.transfer).young_in);
			this.transferOut.put(block.transfer,
					this.tran_set.get(block.transfer).young_out);
		}
		if (control.Control.isTracing("AvailableExpressions")) {
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				String label = block.label.toString();
				System.out.print("\n" + label + ":");
				for (cfg.stm.T stm : block.stms) {
					int index = this.stm_index.get(stm);
					System.out.print("\n" + index);
					System.out.print("\nIn:");
					java.util.HashSet<cfg.stm.T> in = this.stmIn.get(stm);
					for (Iterator it = in.iterator(); it.hasNext();) {
						cfg.stm.T member = (cfg.stm.T) it.next();
						int k = this.stm_index.get(member);
						System.out.print(k + ",");
					}
					System.out.print("\nOut:");
					java.util.HashSet<cfg.stm.T> out = this.stmOut.get(stm);
					for (Iterator it = out.iterator(); it.hasNext();) {
						cfg.stm.T member = (cfg.stm.T) it.next();
						int k = this.stm_index.get(member);
						System.out.print(k + ",");
					}
				}
			}
		}
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
		for (cfg.method.T mth : p.methods) {
			mth.accept(this);
		}
		return;
	}
}
