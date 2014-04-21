package cfg.optimizations;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import cfg.operand.IntArray;
import cfg.optimizations.LivenessVisitor.young_and_old;
import cfg.stm.And;
import cfg.stm.MoveArray;
import cfg.stm.NewIntArray;
import cfg.stm.Not;

public class ReachingDefinition implements cfg.Visitor {
	
	// gen, kill for one statement
	private java.util.HashSet<cfg.stm.T> oneStmGen;
	private java.util.HashSet<cfg.stm.T> oneStmKill;

	// gen, kill for one transfer
	private java.util.HashSet<cfg.stm.T> oneTransferGen;
	private java.util.HashSet<cfg.stm.T> oneTransferKill;

	// gen, kill for statements
	private java.util.HashMap<cfg.stm.T, java.util.HashSet<cfg.stm.T>> stmGen;
	private java.util.HashMap<cfg.stm.T, java.util.HashSet<cfg.stm.T>> stmKill;

	// gen, kill for transfers
	private java.util.HashMap<cfg.transfer.T, java.util.HashSet<cfg.stm.T>> transferGen;
	private java.util.HashMap<cfg.transfer.T, java.util.HashSet<cfg.stm.T>> transferKill;

	// gen, kill for blocks
	private java.util.HashMap<cfg.block.T, java.util.HashSet<cfg.stm.T>> blockGen;
	private java.util.HashMap<cfg.block.T, java.util.HashSet<cfg.stm.T>> blockKill;

	// in, out for blocks
	private java.util.HashMap<cfg.block.T, java.util.HashSet<cfg.stm.T>> blockIn;
	private java.util.HashMap<cfg.block.T, java.util.HashSet<cfg.stm.T>> blockOut;

	// in, out for statements
	public java.util.HashMap<cfg.stm.T, java.util.HashSet<cfg.stm.T>> stmIn;
	public java.util.HashMap<cfg.stm.T, java.util.HashSet<cfg.stm.T>> stmOut;

	// liveIn, liveOut for transfer
	public java.util.HashMap<cfg.transfer.T, java.util.HashSet<cfg.stm.T>> transferIn;
	public java.util.HashMap<cfg.transfer.T, java.util.HashSet<cfg.stm.T>> transferOut;

	public int def_genkill;
	public java.util.HashSet<String> visit;
	public java.util.HashMap<String, cfg.block.T> lable;
	public java.util.LinkedList<String> list;
	public java.util.HashMap<String, java.util.LinkedList<String>> map;
	public java.util.HashMap<String, java.util.LinkedList<String>> map_re;
	public java.util.HashMap<String, java.util.LinkedList<cfg.transfer.T>> re;

	public java.util.HashMap<Integer, cfg.stm.T> index_stm;
	public java.util.HashMap<cfg.stm.T, Integer> stm_index;
	public java.util.HashMap<Integer, cfg.transfer.T> index_transfer;
	public java.util.HashMap<cfg.transfer.T, Integer> transfer_index;
	public java.util.HashMap<String, java.util.HashSet<Integer>> def;

	public java.util.HashMap<cfg.stm.T, young_and_old> stm_set;
	public java.util.HashMap<cfg.transfer.T, young_and_old> tran_set;
	
	public ReachingDefinition() {
		this.oneStmGen = new java.util.HashSet<>();
		this.oneStmKill = new java.util.HashSet<>();

		this.oneTransferGen = new java.util.HashSet<>();
		this.oneTransferKill = new java.util.HashSet<>();

		this.stmGen = new java.util.HashMap<>();
		this.stmKill = new java.util.HashMap<>();

		this.transferGen = new java.util.HashMap<>();
		this.transferKill = new java.util.HashMap<>();

		this.blockGen = new java.util.HashMap<>();
		this.blockKill = new java.util.HashMap<>();

		this.blockIn = new java.util.HashMap<>();
		this.blockOut = new java.util.HashMap<>();

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
	// utilities

	// /////////////////////////////////////////////////////
	// operand
	@Override
	public void visit(cfg.operand.Int operand) {
	}

	@Override
	public void visit(cfg.operand.IntArray operand) {

	}

	@Override
	public void visit(cfg.operand.Var operand) {
	}

	// statements
	@Override
	public void visit(cfg.stm.Add s) {
		cfg.stm.T stm = s;
		if (0 == this.def_genkill) {
			int index = this.stm_index.get(stm);
			if (this.def.containsKey(s.dst)) {
				this.def.get(s.dst).add(index);
			} else {
				this.def.put(s.dst, new java.util.HashSet<Integer>());
				this.def.get(s.dst).add(index);
			}
		} else { // 1 == this.def_genkill
			this.oneStmGen.add(stm);
			int index = this.stm_index.get(stm);
			java.util.HashSet<Integer> set = this.def.get(s.dst);
			for (Iterator it = set.iterator(); it.hasNext();) {
				int member = (int) it.next();
				if (member == index)
					continue;
				cfg.stm.T add = this.index_stm.get(member);
				this.oneStmKill.add(add);
			}
		}
	}

	@Override
	public void visit(cfg.stm.InvokeVirtual s) {
		cfg.stm.T stm = s;
		if (0 == this.def_genkill) {
			int index = this.stm_index.get(stm);
			if (this.def.containsKey(s.dst)) {
				this.def.get(s.dst).add(index);
			} else {
				this.def.put(s.dst, new java.util.HashSet<Integer>());
				this.def.get(s.dst).add(index);
			}
		} else { // 1 == this.def_genkill
			this.oneStmGen.add(stm);
			int index = this.stm_index.get(stm);
			java.util.HashSet<Integer> set = this.def.get(s.dst);
			for (Iterator it = set.iterator(); it.hasNext();) {
				int member = (int) it.next();
				if (member == index)
					continue;
				cfg.stm.T add = this.index_stm.get(member);
				this.oneStmKill.add(add);
			}
		}
	}

	@Override
	public void visit(cfg.stm.Lt s) {
		cfg.stm.T stm = s;
		if (0 == this.def_genkill) {
			int index = this.stm_index.get(stm);
			if (this.def.containsKey(s.dst)) {
				this.def.get(s.dst).add(index);
			} else {
				this.def.put(s.dst, new java.util.HashSet<Integer>());
				this.def.get(s.dst).add(index);
			}
		} else { // 1 == this.def_genkill
			this.oneStmGen.add(stm);
			int index = this.stm_index.get(stm);
			java.util.HashSet<Integer> set = this.def.get(s.dst);
			for (Iterator it = set.iterator(); it.hasNext();) {
				int member = (int) it.next();
				if (member == index)
					continue;
				cfg.stm.T add = this.index_stm.get(member);
				this.oneStmKill.add(add);
			}
		}
	}

	@Override
	public void visit(cfg.stm.Not s) {
		cfg.stm.T stm = s;
		if (0 == this.def_genkill) {
			int index = this.stm_index.get(stm);
			if (this.def.containsKey(s.dst)) {
				this.def.get(s.dst).add(index);
			} else {
				this.def.put(s.dst, new java.util.HashSet<Integer>());
				this.def.get(s.dst).add(index);
			}
		} else { // 1 == this.def_genkill
			this.oneStmGen.add(stm);
			int index = this.stm_index.get(stm);
			java.util.HashSet<Integer> set = this.def.get(s.dst);
			for (Iterator it = set.iterator(); it.hasNext();) {
				int member = (int) it.next();
				if (member == index)
					continue;
				cfg.stm.T add = this.index_stm.get(member);
				this.oneStmKill.add(add);
			}
		}
	}

	@Override
	public void visit(cfg.stm.And s) {
		cfg.stm.T stm = s;
		if (0 == this.def_genkill) {
			int index = this.stm_index.get(stm);
			if (this.def.containsKey(s.dst)) {
				this.def.get(s.dst).add(index);
			} else {
				this.def.put(s.dst, new java.util.HashSet<Integer>());
				this.def.get(s.dst).add(index);
			}
		} else { // 1 == this.def_genkill
			this.oneStmGen.add(stm);
			int index = this.stm_index.get(stm);
			java.util.HashSet<Integer> set = this.def.get(s.dst);
			for (Iterator it = set.iterator(); it.hasNext();) {
				int member = (int) it.next();
				if (member == index)
					continue;
				cfg.stm.T add = this.index_stm.get(member);
				this.oneStmKill.add(add);
			}
		}
	}

	@Override
	public void visit(cfg.stm.Move s) {
		if (s.isField)
			return;
		cfg.stm.T stm = s;
		if (0 == this.def_genkill) {
			int index = this.stm_index.get(stm);
			if (this.def.containsKey(s.dst)) {
				this.def.get(s.dst).add(index);
			} else {
				this.def.put(s.dst, new java.util.HashSet<Integer>());
				this.def.get(s.dst).add(index);
			}
		} else { // 1 == this.def_genkill
			this.oneStmGen.add(stm);
			int index = this.stm_index.get(stm);
			java.util.HashSet<Integer> set = this.def.get(s.dst);
			for (Iterator it = set.iterator(); it.hasNext();) {
				int member = (int) it.next();
				if (member == index)
					continue;
				cfg.stm.T add = this.index_stm.get(member);
				this.oneStmKill.add(add);
			}
		}
	}

	@Override
	public void visit(cfg.stm.MoveArray s) {
		if (s.isField)
			return;
		cfg.stm.T stm = s;
		if (0 == this.def_genkill) {
			int index = this.stm_index.get(stm);
			if (this.def.containsKey(s.dst)) {
				this.def.get(s.dst).add(index);
			} else {
				this.def.put(s.dst, new java.util.HashSet<Integer>());
				this.def.get(s.dst).add(index);
			}
		} else { // 1 == this.def_genkill
			this.oneStmGen.add(stm);
			int index = this.stm_index.get(stm);
			java.util.HashSet<Integer> set = this.def.get(s.dst);
			for (Iterator it = set.iterator(); it.hasNext();) {
				int member = (int) it.next();
				if (member == index)
					continue;
				cfg.stm.T add = this.index_stm.get(member);
				this.oneStmKill.add(add);
			}
		}
	}

	@Override
	public void visit(cfg.stm.NewObject s) {
		cfg.stm.T stm = s;
		if (0 == this.def_genkill) {
			int index = this.stm_index.get(stm);
			if (this.def.containsKey(s.dst)) {
				this.def.get(s.dst).add(index);
			} else {
				this.def.put(s.dst, new java.util.HashSet<Integer>());
				this.def.get(s.dst).add(index);
			}
		} else { // 1 == this.def_genkill
			this.oneStmGen.add(stm);
			int index = this.stm_index.get(stm);
			java.util.HashSet<Integer> set = this.def.get(s.dst);
			for (Iterator it = set.iterator(); it.hasNext();) {
				int member = (int) it.next();
				if (member == index)
					continue;
				cfg.stm.T add = this.index_stm.get(member);
				this.oneStmKill.add(add);
			}
		}
	}

	@Override
	public void visit(cfg.stm.NewIntArray s) {
		cfg.stm.T stm = s;
		if (0 == this.def_genkill) {
			int index = this.stm_index.get(stm);
			if (this.def.containsKey(s.dst)) {
				this.def.get(s.dst).add(index);
			} else {
				this.def.put(s.dst, new java.util.HashSet<Integer>());
				this.def.get(s.dst).add(index);
			}
		} else { // 1 == this.def_genkill
			this.oneStmGen.add(stm);
			int index = this.stm_index.get(stm);
			java.util.HashSet<Integer> set = this.def.get(s.dst);
			for (Iterator it = set.iterator(); it.hasNext();) {
				int member = (int) it.next();
				if (member == index)
					continue;
				cfg.stm.T add = this.index_stm.get(member);
				this.oneStmKill.add(add);
			}
		}
	}

	@Override
	public void visit(cfg.stm.Print s) {
	}

	@Override
	public void visit(cfg.stm.Sub s) {
		cfg.stm.T stm = s;
		if (0 == this.def_genkill) {
			int index = this.stm_index.get(stm);
			if (this.def.containsKey(s.dst)) {
				this.def.get(s.dst).add(index);
			} else {
				this.def.put(s.dst, new java.util.HashSet<Integer>());
				this.def.get(s.dst).add(index);
			}
		} else { // 1 == this.def_genkill
			this.oneStmGen.add(stm);
			int index = this.stm_index.get(stm);
			java.util.HashSet<Integer> set = this.def.get(s.dst);
			for (Iterator it = set.iterator(); it.hasNext();) {
				int member = (int) it.next();
				if (member == index)
					continue;
				cfg.stm.T add = this.index_stm.get(member);
				this.oneStmKill.add(add);
			}
		}
	}

	@Override
	public void visit(cfg.stm.Times s) {
		cfg.stm.T stm = s;
		if (0 == this.def_genkill) {
			int index = this.stm_index.get(stm);
			if (this.def.containsKey(s.dst)) {
				this.def.get(s.dst).add(index);
			} else {
				this.def.put(s.dst, new java.util.HashSet<Integer>());
				this.def.get(s.dst).add(index);
			}
		} else { // 1 == this.def_genkill
			this.oneStmGen.add(stm);
			int index = this.stm_index.get(stm);
			java.util.HashSet<Integer> set = this.def.get(s.dst);
			for (Iterator it = set.iterator(); it.hasNext();) {
				int member = (int) it.next();
				if (member == index)
					continue;
				cfg.stm.T add = this.index_stm.get(member);
				this.oneStmKill.add(add);
			}
		}
	}

	// transfer
	@Override
	public void visit(cfg.transfer.If s) {
		if (1 == this.def_genkill) {
			this.list.add(s.truee.toString());
			this.list.add(s.falsee.toString());

			String label = s.truee.toString();
			cfg.transfer.T tmp = s;
			this.re.get(label).add(tmp);
			label = s.falsee.toString();
			this.re.get(label).add(tmp);
		}
	}

	@Override
	public void visit(cfg.transfer.Goto s) {
		if (1 == this.def_genkill) {
			String label = s.label.toString();
			this.list.add(label);

			cfg.transfer.T tmp = s;
			this.re.get(label).add(tmp);
		}
		return;
	}

	@Override
	public void visit(cfg.transfer.Return s) {
		s.operand.accept(this);
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
		for (cfg.stm.T stm : b.stms) {
			if (1 == this.def_genkill) {
				this.oneStmGen = new java.util.HashSet<>();
				this.oneStmKill = new java.util.HashSet<>();
			}
			stm.accept(this);
			if (1 == this.def_genkill) {
				this.stmGen.put(stm, this.oneStmGen);
				this.stmKill.put(stm, this.oneStmKill);
				this.stm_set.put(stm, new young_and_old());
			}
		}

		if (1 == this.def_genkill) {
			this.oneStmGen = new java.util.HashSet<>();
			this.oneStmKill = new java.util.HashSet<>();
			list = new java.util.LinkedList<String>();
		}
		b.transfer.accept(this);
		if (1 == this.def_genkill) {
			String label = b.label.toString();
			this.map.put(label, this.list);
			this.transferGen.put(b.transfer, this.oneStmGen);
			this.transferKill.put(b.transfer, this.oneStmKill);
			this.tran_set.put(b.transfer, new young_and_old());
		}
		if (0 == this.def_genkill)
			return;

		java.util.HashSet<cfg.stm.T> kill_set = new java.util.HashSet<cfg.stm.T>();
		java.util.HashSet<cfg.stm.T> gen_set = new java.util.HashSet<cfg.stm.T>();

		if (0 == b.stms.size()) {
			this.blockGen.put(b, gen_set);
			this.blockKill.put(b, kill_set);
			return;
		}

		int size = b.stms.size();
		kill_set.addAll(this.stmKill.get(b.stms.get(size - 1)));
		gen_set.addAll(this.stmGen.get(b.stms.get(size - 1)));

		for (int i = b.stms.size() - 2; i >= 0; i--) {
			java.util.HashSet<cfg.stm.T> gen = new java.util.HashSet<cfg.stm.T>();
			gen.addAll(this.stmGen.get(b.stms.get(i)));
			gen.removeAll(kill_set);
			gen_set.addAll(gen);
			kill_set.addAll(this.stmKill.get(b.stms.get(i)));
		}
		this.blockGen.put(b, gen_set);
		this.blockKill.put(b, kill_set);
	}

	// method
	@Override
	public void visit(cfg.method.Method m) {
		// Five steps:
		// Step 0: for each argument or local variable "x" in the
		// method m, calculate x's definition site set def(x).
		// Your code here:
		this.def_genkill = 0;
		int cnt = 0;
		this.def = new java.util.HashMap<String, HashSet<Integer>>();
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
				cnt++;
			}

			this.transfer_index.put(block.transfer, cnt);
			this.index_transfer.put(cnt, block.transfer);
			cnt++;
		}
		for (cfg.block.T bb : m.blocks) {
			bb.accept(this);
		}
		if (control.Control.isTracing("ReachingDefinition.step0")) {
			System.out.print("\n\n" + m.id + ":\n");
			for (Iterator def_it = def.keySet().iterator(); def_it.hasNext();) {
				String key = (String) def_it.next();
				System.out.print("\n" + key + ":\n");
				System.out.print("    ");
				java.util.HashSet<Integer> set = def.get(key);
				for (Iterator int_it = set.iterator(); int_it.hasNext();) {
					int k = (int) int_it.next();
					System.out.print(k + ",");
				}
			}
		}
		// Step 1: calculate the "gen" and "kill" sets for each
		// statement and transfer
		this.def_genkill = 1;
		this.map = new java.util.HashMap<>();
		this.map_re = new java.util.HashMap<>();
		for (cfg.block.T bb : m.blocks) {
			bb.accept(this);
		}
		if (control.Control.isTracing("ReachingDefinition.step1")) {
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				System.out.print("\n" + block.label.toString() + ":");
				for (cfg.stm.T stm : block.stms) {
					int index = this.stm_index.get(stm);
					System.out.print("\n" + index + ":");
					System.out.print("\ngen:");
					java.util.HashSet<cfg.stm.T> gen_set = this.stmGen.get(stm);
					for (Iterator it = gen_set.iterator(); it.hasNext();) {
						cfg.stm.T member = (cfg.stm.T) it.next();
						int k = this.stm_index.get(member);
						System.out.print(k + ",");
					}
					System.out.print("\nkill:");
					java.util.HashSet<cfg.stm.T> kill_set = this.stmKill
							.get(stm);
					for (Iterator it = kill_set.iterator(); it.hasNext();) {
						cfg.stm.T member = (cfg.stm.T) it.next();
						int k = this.stm_index.get(member);
						System.out.print(k + ",");
					}
				}
				int index = this.transfer_index.get(block.transfer);
				System.out.print("\n" + index + ":");
				System.out.print("\ngen:");
				java.util.HashSet<cfg.stm.T> gen_set = this.transferGen
						.get(block.transfer);
				for (Iterator it = gen_set.iterator(); it.hasNext();) {
					cfg.stm.T member = (cfg.stm.T) it.next();
					int k = this.stm_index.get(member);
					System.out.print(k + ",");
				}
				System.out.print("\nkill:");
				java.util.HashSet<cfg.stm.T> kill_set = this.transferKill
						.get(block.transfer);
				for (Iterator it = kill_set.iterator(); it.hasNext();) {
					cfg.stm.T member = (cfg.stm.T) it.next();
					int k = this.stm_index.get(member);
					System.out.print(k + ",");
				}
			}
		}

		// Step 2: calculate the "gen" and "kill" sets for each block.
		// For this, you should visit statements and transfers in a
		// block sequentially.
		// Your code here:
		if (control.Control.isTracing("ReachingDefinition.step2")) {
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				System.out.print("\n" + block.label.toString() + ":");
				System.out.print("\ngen:");
				java.util.HashSet<cfg.stm.T> gen = this.blockGen.get(bb);
				for (Iterator it = gen.iterator(); it.hasNext();) {
					cfg.stm.T member = (cfg.stm.T) it.next();
					int index = this.stm_index.get(member);
					System.out.print("," + index);
				}
				System.out.print("\nkill:");
				java.util.HashSet<cfg.stm.T> kill = this.blockKill.get(bb);
				for (Iterator it = kill.iterator(); it.hasNext();) {
					cfg.stm.T member = (cfg.stm.T) it.next();
					int index = this.stm_index.get(member);
					System.out.print("," + index);
				}
			}
		}
		// Step 3: calculate the "in" and "out" sets for each block
		// Note that to speed up the calculation, you should use
		// a topo-sort order of the CFG blocks, and
		// crawl through the blocks in that order.
		// And also you should loop until a fix-point is reached.
		// Your code here:
		java.util.HashSet<String> visit = new java.util.HashSet<String>();
		java.util.LinkedList<String> worklist = new java.util.LinkedList<String>();
		this.lable = new java.util.HashMap<>();
		for (cfg.block.T bb : m.blocks) {
			this.blockIn.put(bb, new java.util.HashSet<cfg.stm.T>());
			this.blockOut.put(bb, new java.util.HashSet<cfg.stm.T>());
			cfg.block.Block block = (cfg.block.Block) bb;
			String label = block.label.toString();
			this.lable.put(label, bb);
			visit.add(label);
			worklist.add(label);
			this.map_re.put(label, new java.util.LinkedList<String>());
		}
		for (Iterator it = this.map.keySet().iterator(); it.hasNext();) {
			String a = (String) it.next();
			java.util.LinkedList<String> to = this.map.get(a);
			for (String b : to) {
				map_re.get(b).add(a);
			}
		}
		while (!worklist.isEmpty()) {
			String head = worklist.getFirst();
			worklist.removeFirst();
			visit.remove(head);
			cfg.block.Block b = (cfg.block.Block) this.lable.get(head);
			java.util.HashSet<cfg.stm.T> out = this.blockOut.get(b);
			java.util.HashSet<cfg.stm.T> in = this.blockIn.get(b);
			java.util.HashSet<cfg.stm.T> gen = this.blockGen.get(b);
			java.util.HashSet<cfg.stm.T> kill = this.blockKill.get(b);
			java.util.HashSet<cfg.stm.T> old = new java.util.HashSet<cfg.stm.T>();

			old.addAll(out);
			java.util.LinkedList<String> to = this.map_re.get(head);
			for (String p : to) {
				cfg.block.T tmp = this.lable.get(p);
				java.util.HashSet<cfg.stm.T> out_p = this.blockOut.get(tmp);// /
				in.addAll(out_p);
			}
			out.addAll(gen);
			java.util.HashSet<cfg.stm.T> tmp = new java.util.HashSet<>();
			tmp.addAll(in);
			tmp.removeAll(kill);
			out.addAll(tmp);
			if (!old.equals(out)) {
				to = this.map.get(head);
				for (String s : to) {
					if (!visit.contains(s)) {
						worklist.addLast(s);
						visit.add(s);
					}
				}
			}
		}
		if (control.Control.isTracing("ReachingDefinition.step3")) {
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				System.out.print("\n" + block.label.toString());
				System.out.print("\nin:\n   ");
				java.util.HashSet<cfg.stm.T> in = this.blockIn.get(bb);
				for (Iterator it = in.iterator(); it.hasNext();) {
					cfg.stm.T stm = (cfg.stm.T) it.next();
					int index = this.stm_index.get(stm);
					System.out.print("," + index);
				}
				System.out.print("\nout:\n   ");
				java.util.HashSet<cfg.stm.T> out = this.blockOut.get(bb);
				for (Iterator it = out.iterator(); it.hasNext();) {
					cfg.stm.T stm = (cfg.stm.T) it.next();
					int index = this.stm_index.get(stm);
					System.out.print("," + index);
				}
			}
		}
		// Step 4: calculate the "in" and "out" sets for each
		// statement and transfer
		// Your code here:
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
					java.util.HashSet<cfg.stm.T> gen = this.stmGen.get(stm);
					java.util.HashSet<cfg.stm.T> kill = this.stmKill.get(stm);
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
					young_out.addAll(gen);
					java.util.HashSet<cfg.stm.T> tmp = new java.util.HashSet<>();
					tmp.addAll(young_in);
					tmp.removeAll(kill);
					young_out.addAll(tmp);
					last = stm;
				}
				java.util.HashSet<cfg.stm.T> gen = this.transferGen
						.get(block.transfer);
				java.util.HashSet<cfg.stm.T> kill = this.transferKill
						.get(block.transfer);
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
				young_out.addAll(gen);
				java.util.HashSet<cfg.stm.T> tmp = new java.util.HashSet<>();
				tmp.addAll(young_in);
				tmp.removeAll(kill);
				young_out.addAll(tmp);
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
		if (control.Control.isTracing("ReachingDefinition.step4")) {
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

	@Override
	public void visit(cfg.mainMethod.MainMethod m) {
		// Five steps:
		// Step 0: for each argument or local variable "x" in the
		// method m, calculate x's definition site set def(x).
		// Your code here:
		int cnt = 0;
		this.def_genkill = 0;
		this.def = new java.util.HashMap<String, HashSet<Integer>>();
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
				cnt++;
			}

			this.transfer_index.put(block.transfer, cnt);
			this.index_transfer.put(cnt, block.transfer);
			cnt++;
		}
		for (cfg.block.T bb : m.blocks) {
			bb.accept(this);
		}
		if (control.Control.isTracing("ReachingDefinition.step0")) {
			System.out.print("Main:\n");
			for (Iterator def_it = def.keySet().iterator(); def_it.hasNext();) {
				String key = (String) def_it.next();
				System.out.print("\n" + key + ":\n");
				System.out.print("    ");
				java.util.HashSet<Integer> set = def.get(key);
				for (Iterator int_it = set.iterator(); int_it.hasNext();) {
					int k = (int) int_it.next();
					System.out.print(k + ",");
				}
			}
		}
		// Step 1: calculate the "gen" and "kill" sets for each
		// statement and transfer
		this.def_genkill = 1;
		this.map = new java.util.HashMap<>();
		this.map_re = new java.util.HashMap<>();
		for (cfg.block.T bb : m.blocks) {
			bb.accept(this);
		}
		if (control.Control.isTracing("ReachingDefinition.step1")) {
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				System.out.print("\n" + block.label.toString() + ":");
				for (cfg.stm.T stm : block.stms) {
					int index = this.stm_index.get(stm);
					System.out.print("\n" + index + ":");
					System.out.print("\ngen:");
					java.util.HashSet<cfg.stm.T> gen_set = this.stmGen.get(stm);
					for (Iterator it = gen_set.iterator(); it.hasNext();) {
						cfg.stm.T member = (cfg.stm.T) it.next();
						int k = this.stm_index.get(member);
						System.out.print(k + ",");
					}
					System.out.print("\nkill:");
					java.util.HashSet<cfg.stm.T> kill_set = this.stmKill
							.get(stm);
					for (Iterator it = kill_set.iterator(); it.hasNext();) {
						cfg.stm.T member = (cfg.stm.T) it.next();
						int k = this.stm_index.get(member);
						System.out.print(k + ",");
					}
				}
				int index = this.transfer_index.get(block.transfer);
				System.out.print("\n" + index + ":");
				System.out.print("\ngen:");
				java.util.HashSet<cfg.stm.T> gen_set = this.transferGen
						.get(block.transfer);
				for (Iterator it = gen_set.iterator(); it.hasNext();) {
					cfg.stm.T member = (cfg.stm.T) it.next();
					int k = this.stm_index.get(member);
					System.out.print(k + ",");
				}
				System.out.print("\nkill:");
				java.util.HashSet<cfg.stm.T> kill_set = this.transferKill
						.get(block.transfer);
				for (Iterator it = kill_set.iterator(); it.hasNext();) {
					cfg.stm.T member = (cfg.stm.T) it.next();
					int k = this.stm_index.get(member);
					System.out.print(k + ",");
				}
			}
		}
		// Step 2: calculate the "gen" and "kill" sets for each block.
		// For this, you should visit statements and transfers in a
		// block sequentially.
		// Your code here:
		if (control.Control.isTracing("ReachingDefinition.step2")) {
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				System.out.print("\n" + block.label.toString() + ":");
				System.out.print("\ngen:");
				java.util.HashSet<cfg.stm.T> gen = this.blockGen.get(bb);
				for (Iterator it = gen.iterator(); it.hasNext();) {
					cfg.stm.T member = (cfg.stm.T) it.next();
					int index = this.stm_index.get(member);
					System.out.print("," + index);
				}
				System.out.print("\nkill:");
				java.util.HashSet<cfg.stm.T> kill = this.blockKill.get(bb);
				for (Iterator it = kill.iterator(); it.hasNext();) {
					cfg.stm.T member = (cfg.stm.T) it.next();
					int index = this.stm_index.get(member);
					System.out.print("," + index);
				}
			}
		}
		// Step 3: calculate the "in" and "out" sets for each block
		// Note that to speed up the calculation, you should use
		// a topo-sort order of the CFG blocks, and
		// crawl through the blocks in that order.
		// And also you should loop until a fix-point is reached.
		// Your code here:
		java.util.HashSet<String> visit = new java.util.HashSet<String>();
		java.util.LinkedList<String> worklist = new java.util.LinkedList<String>();
		this.lable = new java.util.HashMap<>();
		for (cfg.block.T bb : m.blocks) {
			this.blockIn.put(bb, new java.util.HashSet<cfg.stm.T>());
			this.blockOut.put(bb, new java.util.HashSet<cfg.stm.T>());
			cfg.block.Block block = (cfg.block.Block) bb;
			String label = block.label.toString();
			this.lable.put(label, bb);
			visit.add(label);
			worklist.add(label);
			this.map_re.put(label, new java.util.LinkedList<String>());
		}
		for (Iterator it = this.map.keySet().iterator(); it.hasNext();) {
			String a = (String) it.next();
			java.util.LinkedList<String> to = this.map.get(a);
			for (String b : to) {
				map_re.get(b).add(a);
			}
		} // make graph
		while (!worklist.isEmpty()) {
			String head = worklist.getFirst();
			worklist.removeFirst();
			visit.remove(head);
			cfg.block.Block b = (cfg.block.Block) this.lable.get(head);
			java.util.HashSet<cfg.stm.T> out = this.blockOut.get(b);
			java.util.HashSet<cfg.stm.T> in = this.blockIn.get(b);
			java.util.HashSet<cfg.stm.T> gen = this.blockGen.get(b);
			java.util.HashSet<cfg.stm.T> kill = this.blockKill.get(b);
			java.util.HashSet<cfg.stm.T> old = new java.util.HashSet<cfg.stm.T>();

			old.addAll(out);
			java.util.LinkedList<String> to = this.map_re.get(head);
			for (String p : to) {
				cfg.block.T tmp = this.lable.get(p);
				java.util.HashSet<cfg.stm.T> out_p = this.blockOut.get(tmp);// /
				in.addAll(out_p);
			}
			out.addAll(gen);
			java.util.HashSet<cfg.stm.T> tmp = new java.util.HashSet<>();
			tmp.addAll(in);
			tmp.removeAll(kill);
			out.addAll(tmp);
			if (!old.equals(out)) {
				to = this.map.get(head);
				for (String s : to) {
					if (!visit.contains(s)) {
						worklist.addLast(s);
						visit.add(s);
					}
				}
			}
		}
		if (control.Control.isTracing("ReachingDefinition.step3")) {
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				System.out.print("\n" + block.label.toString());
				System.out.print("\nin:\n   ");
				java.util.HashSet<cfg.stm.T> in = this.blockIn.get(bb);
				for (Iterator it = in.iterator(); it.hasNext();) {
					cfg.stm.T stm = (cfg.stm.T) it.next();
					int index = this.stm_index.get(stm);
					System.out.print("," + index);
				}
				System.out.print("\nout:\n   ");
				java.util.HashSet<cfg.stm.T> out = this.blockOut.get(bb);
				for (Iterator it = out.iterator(); it.hasNext();) {
					cfg.stm.T stm = (cfg.stm.T) it.next();
					int index = this.stm_index.get(stm);
					System.out.print("," + index);
				}
			}
		}
		// Step 4: calculate the "in" and "out" sets for each
		// statement and transfer
		// Your code here:
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
					java.util.HashSet<cfg.stm.T> gen = this.stmGen.get(stm);
					java.util.HashSet<cfg.stm.T> kill = this.stmKill.get(stm);
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
					young_out.addAll(gen);
					java.util.HashSet<cfg.stm.T> tmp = new java.util.HashSet<>();
					tmp.addAll(young_in);
					tmp.removeAll(kill);
					young_out.addAll(tmp);
					last = stm;
				}
				java.util.HashSet<cfg.stm.T> gen = this.transferGen
						.get(block.transfer);
				java.util.HashSet<cfg.stm.T> kill = this.transferKill
						.get(block.transfer);
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
				young_out.addAll(gen);
				java.util.HashSet<cfg.stm.T> tmp = new java.util.HashSet<>();
				tmp.addAll(young_in);
				tmp.removeAll(kill);
				young_out.addAll(tmp);
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
		if (control.Control.isTracing("ReachingDefinition.step4")) {
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
	}

}
