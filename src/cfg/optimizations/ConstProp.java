package cfg.optimizations;

import java.util.Iterator;

public class ConstProp implements cfg.Visitor {
	public String dst;
	public boolean check;
	public cfg.program.T program;
	public java.util.HashMap<cfg.stm.T, java.util.HashSet<cfg.stm.T>> stmIn;
	public java.util.HashMap<cfg.stm.T, Pair> one_set;

	public ConstProp() {
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
	public void visit(cfg.operand.IntArray operand) {
		// TODO Auto-generated method stub
	}

	// statements

	@Override
	public void visit(cfg.stm.InvokeVirtual s) {
		if (this.check) {
			this.check = false;
			this.dst = s.dst;
			return;
		}
		java.util.HashSet<cfg.stm.T> in = this.stmIn.get(s);
		for (cfg.operand.T o : s.args) {
			if (!(o instanceof cfg.operand.Var))
				continue;
			for (Iterator it = in.iterator(); it.hasNext();) {
				cfg.stm.T member = (cfg.stm.T) it.next();
				if (!this.one_set.containsKey(member))
					continue;
				Pair pair = this.one_set.get(member);
				cfg.operand.Var operand = (cfg.operand.Var) o;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);
						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can)
						o = new cfg.operand.Int(pair.num);
				}
			}
		}
	}

	@Override
	public void visit(cfg.stm.Lt s) {
		if (this.check) {
			this.check = false;
			this.dst = s.dst;
			return;
		}
		cfg.stm.T stm = s;
		java.util.HashSet<cfg.stm.T> in = this.stmIn.get(stm);
		for (Iterator it = in.iterator(); it.hasNext();) {
			cfg.stm.T member = (cfg.stm.T) it.next();
			if (!this.one_set.containsKey(member))
				continue;
			Pair pair = this.one_set.get(member);
			if (s.left instanceof cfg.operand.Var) {
				cfg.operand.Var operand = (cfg.operand.Var) s.left;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);
						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can)
						s.left = new cfg.operand.Int(pair.num);
				}
			}
			if (s.right instanceof cfg.operand.Var) {
				cfg.operand.Var operand = (cfg.operand.Var) s.right;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);

						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can)
						s.right = new cfg.operand.Int(pair.num);
				}
			}
		}
		if ((s.left instanceof cfg.operand.Int)
				&& (s.right instanceof cfg.operand.Int)) {
			cfg.operand.Int L = (cfg.operand.Int) s.left;
			cfg.operand.Int R = (cfg.operand.Int) s.right;
			if (L.i < R.i) {
				this.one_set.put(s, new Pair(s.dst, 1));
			} else {
				this.one_set.put(s, new Pair(s.dst, 0));
			}
		}
	}

	@Override
	public void visit(cfg.stm.Not s) {
		if (this.check) {
			this.check = false;
			this.dst = s.dst;
			return;
		}
		java.util.HashSet<cfg.stm.T> in = this.stmIn.get(s);
		for (Iterator it = in.iterator(); it.hasNext();) {
			cfg.stm.T member = (cfg.stm.T) it.next();
			if (!this.one_set.containsKey(member))
				continue;
			Pair pair = this.one_set.get(member);
			if (s.exp instanceof cfg.operand.Var) {
				cfg.operand.Var operand = (cfg.operand.Var) s.exp;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);

						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can)
						s.exp = new cfg.operand.Int(pair.num);
				}
			}
		}
		if ((s.exp instanceof cfg.operand.Int)) {
			cfg.operand.Int exp = (cfg.operand.Int) s.exp;
			if (0 != exp.i) {
				this.one_set.put(s, new Pair(s.dst, 0));
			} else {
				this.one_set.put(s, new Pair(s.dst, 1));
			}
		}
	}

	@Override
	public void visit(cfg.stm.And s) {
		if (this.check) {
			this.check = false;
			this.dst = s.dst;
			return;
		}
		cfg.stm.T stm = s;
		java.util.HashSet<cfg.stm.T> in = this.stmIn.get(stm);
		for (Iterator it = in.iterator(); it.hasNext();) {
			cfg.stm.T member = (cfg.stm.T) it.next();
			if (!this.one_set.containsKey(member))
				continue;
			Pair pair = this.one_set.get(member);
			if (s.left instanceof cfg.operand.Var) {
				cfg.operand.Var operand = (cfg.operand.Var) s.left;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);
						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can)
						s.left = new cfg.operand.Int(pair.num);
				}
			}
			if (s.right instanceof cfg.operand.Var) {
				cfg.operand.Var operand = (cfg.operand.Var) s.right;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);

						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can)
						s.right = new cfg.operand.Int(pair.num);
				}
			}
		}
		if ((s.left instanceof cfg.operand.Int)
				&& (s.right instanceof cfg.operand.Int)) {
			cfg.operand.Int L = (cfg.operand.Int) s.left;
			cfg.operand.Int R = (cfg.operand.Int) s.right;
			if (0 != L.i && 0 != R.i) {
				this.one_set.put(s, new Pair(s.dst, 1));
			} else {
				this.one_set.put(s, new Pair(s.dst, 0));
			}
		}

	}

	@Override
	public void visit(cfg.stm.Move s) {
		if (this.check) {
			this.check = false;
			this.dst = s.dst;
			return;
		}
		if (s.isField)
			return;
		if (s.src instanceof cfg.operand.Var) {
			java.util.HashSet<cfg.stm.T> in = this.stmIn.get(s);
			for (Iterator it = in.iterator(); it.hasNext();) {
				cfg.stm.T member = (cfg.stm.T) it.next();
				if (!this.one_set.containsKey(member))
					continue;
				Pair pair = this.one_set.get(member);
				cfg.operand.Var operand = (cfg.operand.Var) s.src;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);
						this.check = false;
						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can) {
						s.src = new cfg.operand.Int(pair.num);
						this.one_set.put(s, new Pair(s.dst, pair.num));
						return;
					}
				}
			}
		}
	}

	@Override
	public void visit(cfg.stm.MoveArray s) {
		// TODO Auto-generated method stub
		if (this.check) {
			this.check = false;
			this.dst = s.dst;
			return;
		}
		java.util.HashSet<cfg.stm.T> in = this.stmIn.get(s);
		if (s.index instanceof cfg.operand.Var) {
			for (Iterator it = in.iterator(); it.hasNext();) {
				cfg.stm.T member = (cfg.stm.T) it.next();
				if (!this.one_set.containsKey(member))
					continue;
				Pair pair = this.one_set.get(member);
				cfg.operand.Var operand = (cfg.operand.Var) s.index;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);
						this.check = false;
						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can) {
						s.index = new cfg.operand.Int(pair.num);
						break;
					}
				}
			}
		}
		if (s.src instanceof cfg.operand.Var) {
			for (Iterator it = in.iterator(); it.hasNext();) {
				cfg.stm.T member = (cfg.stm.T) it.next();
				if (!this.one_set.containsKey(member))
					continue;
				Pair pair = this.one_set.get(member);
				cfg.operand.Var operand = (cfg.operand.Var) s.src;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);
						this.check = false;
						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can) {
						s.src = new cfg.operand.Int(pair.num);
						break;
					}
				}
			}
		}
	}

	@Override
	public void visit(cfg.stm.NewObject s) {
	}

	@Override
	public void visit(cfg.stm.NewIntArray s) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(cfg.stm.Print s) {
		if (this.check) {
			this.check = false;
			this.dst = "";
			return;
		}
		if (!(s.arg instanceof cfg.operand.Var))
			return;
		java.util.HashSet<cfg.stm.T> in = this.stmIn.get(s);
		for (Iterator it = in.iterator(); it.hasNext();) {
			cfg.stm.T member = (cfg.stm.T) it.next();
			if (!this.one_set.containsKey(member))
				continue;
			Pair pair = this.one_set.get(member);
			cfg.operand.Var operand = (cfg.operand.Var) s.arg;
			if ((!operand.isField) && operand.id.equals(pair.var)) {
				for (Iterator self_it = in.iterator(); self_it.hasNext();) {
					cfg.stm.T self_member = (cfg.stm.T) self_it.next();
					if (self_member == member)
						continue;
					this.check = true;
					self_member.accept(this);
					this.check = false;
					if (this.dst.equals(pair.var)) {
						return;
					}
				}
				s.arg = new cfg.operand.Int(pair.num);
			}
		}
	}

	@Override
	public void visit(cfg.stm.Sub s) {
		if (this.check) {
			this.check = false;
			this.dst = s.dst;
			return;
		}
		cfg.stm.T stm = s;
		java.util.HashSet<cfg.stm.T> in = this.stmIn.get(stm);
		for (Iterator it = in.iterator(); it.hasNext();) {
			cfg.stm.T member = (cfg.stm.T) it.next();
			if (!this.one_set.containsKey(member))
				continue;
			Pair pair = this.one_set.get(member);
			if (s.left instanceof cfg.operand.Var) {
				cfg.operand.Var operand = (cfg.operand.Var) s.left;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);
						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can)
						s.left = new cfg.operand.Int(pair.num);
				}
			}
			if (s.right instanceof cfg.operand.Var) {
				cfg.operand.Var operand = (cfg.operand.Var) s.right;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);

						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can)
						s.right = new cfg.operand.Int(pair.num);
				}
			}
		}
		if ((s.left instanceof cfg.operand.Int)
				&& (s.right instanceof cfg.operand.Int)) {
			cfg.operand.Int L = (cfg.operand.Int) s.left;
			cfg.operand.Int R = (cfg.operand.Int) s.right;
			this.one_set.put(s, new Pair(s.dst, L.i - R.i));
		}
	}

	@Override
	public void visit(cfg.stm.Times s) {
		if (this.check) {
			this.check = false;
			this.dst = s.dst;
			return;
		}
		cfg.stm.T stm = s;
		java.util.HashSet<cfg.stm.T> in = this.stmIn.get(stm);
		for (Iterator it = in.iterator(); it.hasNext();) {
			cfg.stm.T member = (cfg.stm.T) it.next();
			if (!this.one_set.containsKey(member))
				continue;
			Pair pair = this.one_set.get(member);
			if (s.left instanceof cfg.operand.Var) {
				cfg.operand.Var operand = (cfg.operand.Var) s.left;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);
						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can)
						s.left = new cfg.operand.Int(pair.num);
				}
			}
			if (s.right instanceof cfg.operand.Var) {
				cfg.operand.Var operand = (cfg.operand.Var) s.right;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);

						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can)
						s.right = new cfg.operand.Int(pair.num);
				}
			}
		}
		if ((s.left instanceof cfg.operand.Int)
				&& (s.right instanceof cfg.operand.Int)) {
			cfg.operand.Int L = (cfg.operand.Int) s.left;
			cfg.operand.Int R = (cfg.operand.Int) s.right;
			this.one_set.put(s, new Pair(s.dst, L.i * R.i));
		}
	}

	@Override
	public void visit(cfg.stm.Add s) {
		if (this.check) {
			this.check = false;
			this.dst = s.dst;
			return;
		}
		cfg.stm.T stm = s;
		java.util.HashSet<cfg.stm.T> in = this.stmIn.get(stm);
		for (Iterator it = in.iterator(); it.hasNext();) {
			cfg.stm.T member = (cfg.stm.T) it.next();
			if (!this.one_set.containsKey(member))
				continue;
			Pair pair = this.one_set.get(member);
			if (s.left instanceof cfg.operand.Var) {
				cfg.operand.Var operand = (cfg.operand.Var) s.left;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);
						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can)
						s.left = new cfg.operand.Int(pair.num);
				}
			}
			if (s.right instanceof cfg.operand.Var) {
				cfg.operand.Var operand = (cfg.operand.Var) s.right;
				if ((!operand.isField) && operand.id.equals(pair.var)) {
					boolean can = true;
					for (Iterator self_it = in.iterator(); self_it.hasNext();) {
						cfg.stm.T self_member = (cfg.stm.T) self_it.next();
						if (self_member == member)
							continue;
						this.check = true;
						self_member.accept(this);

						if (this.dst.equals(pair.var)) {
							can = false;
							break;
						}
					}
					if (can)
						s.right = new cfg.operand.Int(pair.num);
				}
			}
		}
		if ((s.left instanceof cfg.operand.Int)
				&& (s.right instanceof cfg.operand.Int)) {
			cfg.operand.Int L = (cfg.operand.Int) s.left;
			cfg.operand.Int R = (cfg.operand.Int) s.right;
			this.one_set.put(s, new Pair(s.dst, L.i + R.i));
		}
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
		for (cfg.stm.T s : b.stms) {
			s.accept(this);
		}
	}

	// method
	@Override
	public void visit(cfg.method.Method m) {
		this.one_set = new java.util.HashMap<>();
		for (cfg.block.T bb : m.blocks) {
			cfg.block.Block block = (cfg.block.Block) bb;
			for (cfg.stm.T s : block.stms) {
				if (s instanceof cfg.stm.Move) {
					cfg.stm.Move stm = (cfg.stm.Move) s;
					if (stm.isField)
						continue;
					if (stm.src instanceof cfg.operand.Int) {
						cfg.operand.Int src = (cfg.operand.Int) stm.src;
						this.one_set.put(s, new Pair(stm.dst, src.i));
					}
				}
			}
		}
		for (cfg.block.T block : m.blocks) {
			block.accept(this);
		}
	}

	@Override
	public void visit(cfg.mainMethod.MainMethod m) {
		this.one_set = new java.util.HashMap<>();
		for (cfg.block.T bb : m.blocks) {
			cfg.block.Block block = (cfg.block.Block) bb;
			for (cfg.stm.T s : block.stms) {
				if (s instanceof cfg.stm.Move) {
					cfg.stm.Move stm = (cfg.stm.Move) s;
					if (stm.isField)
						continue;
					if (stm.src instanceof cfg.operand.Int) {
						cfg.operand.Int src = (cfg.operand.Int) stm.src;
						this.one_set.put(s, new Pair(stm.dst, src.i));
					}
				}
			}
		}
		for (cfg.block.T block : m.blocks) {
			block.accept(this);
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
		this.program = p;
	}

	public class Pair {
		public String var;
		public int num;

		public Pair(String var, int num) {
			this.var = var;
			this.num = num;
		}
	}
}
