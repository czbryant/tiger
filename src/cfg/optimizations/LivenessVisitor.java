package cfg.optimizations;

import java.util.*;

public class LivenessVisitor implements cfg.Visitor {

	// gen, kill for one statement
	private java.util.HashSet<String> oneStmGen;
	private java.util.HashSet<String> oneStmKill;

	// gen, kill for one transfer
	private java.util.HashSet<String> oneTransferGen;
	private java.util.HashSet<String> oneTransferKill;

	// gen, kill for statements
	private java.util.HashMap<cfg.stm.T, java.util.HashSet<String>> stmGen;
	private java.util.HashMap<cfg.stm.T, java.util.HashSet<String>> stmKill;

	// gen, kill for transfers
	private java.util.HashMap<cfg.transfer.T, java.util.HashSet<String>> transferGen;
	private java.util.HashMap<cfg.transfer.T, java.util.HashSet<String>> transferKill;

	// gen, kill for blocks
	private java.util.HashMap<cfg.block.T, java.util.HashSet<String>> blockGen;
	private java.util.HashMap<cfg.block.T, java.util.HashSet<String>> blockKill;

	// liveIn, liveOut for blocks
	private java.util.HashMap<cfg.block.T, java.util.HashSet<String>> blockLiveIn;
	private java.util.HashMap<cfg.block.T, java.util.HashSet<String>> blockLiveOut;

	// liveIn, liveOut for statements
	public java.util.HashMap<cfg.stm.T, java.util.HashSet<String>> stmLiveIn;
	public java.util.HashMap<cfg.stm.T, java.util.HashSet<String>> stmLiveOut;

	// liveIn, liveOut for transfer
	public java.util.HashMap<cfg.transfer.T, java.util.HashSet<String>> transferLiveIn;
	public java.util.HashMap<cfg.transfer.T, java.util.HashSet<String>> transferLiveOut;

	// As you will walk the tree for many times, so
	// it will be useful to recored which is which:
	enum Liveness_Kind_t {
		None, StmGenKill, BlockGenKill, BlockInOut, StmInOut,
	}

	private Liveness_Kind_t kind = Liveness_Kind_t.None;

	public java.util.HashSet<String> visit;
	public java.util.HashMap<String, cfg.block.T> lable;
	public java.util.HashMap<String, java.util.LinkedList<String>> map;
	public java.util.HashMap<String, java.util.LinkedList<String>> map_re;

	public java.util.LinkedList<String> list;

	public java.util.HashMap<cfg.stm.T, young_and_old> stm_set;
	public java.util.HashMap<cfg.transfer.T, young_and_old> tran_set;

	public boolean statement_or_transfer;

	public LivenessVisitor() {
		this.lable = new java.util.HashMap<String, cfg.block.T>();
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

		this.blockLiveIn = new java.util.HashMap<>();
		this.blockLiveOut = new java.util.HashMap<>();

		this.stmLiveIn = new java.util.HashMap<>();
		this.stmLiveOut = new java.util.HashMap<>();

		this.transferLiveIn = new java.util.HashMap<>();
		this.transferLiveOut = new java.util.HashMap<>();

		this.kind = Liveness_Kind_t.None;
	}

	public class young_and_old {
		public java.util.HashSet<String> young_in;
		public java.util.HashSet<String> young_out;
		public java.util.HashSet<String> old_in;
		public java.util.HashSet<String> old_out;

		public young_and_old() {
			this.young_in = new java.util.HashSet<String>();
			this.old_in = new java.util.HashSet<String>();
			this.young_out = new java.util.HashSet<String>();
			this.old_out = new java.util.HashSet<String>();
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

	// /////////////////////////////////////////////////////
	// utilities

	private java.util.HashSet<String> getOneStmGenAndClear() {
		java.util.HashSet<String> temp = this.oneStmGen;
		this.oneStmGen = new java.util.HashSet<>();
		return temp;
	}

	private java.util.HashSet<String> getOneStmKillAndClear() {
		java.util.HashSet<String> temp = this.oneStmKill;
		this.oneStmKill = new java.util.HashSet<>();
		return temp;
	}

	private java.util.HashSet<String> getOneTransferGenAndClear() {
		java.util.HashSet<String> temp = this.oneTransferGen;
		this.oneTransferGen = new java.util.HashSet<>();
		return temp;
	}

	private java.util.HashSet<String> getOneTransferKillAndClear() {
		java.util.HashSet<String> temp = this.oneTransferKill;
		this.oneTransferKill = new java.util.HashSet<>();
		return temp;
	}

	// /////////////////////////////////////////////////////
	// operand
	@Override
	public void visit(cfg.operand.Int operand) {
		return;
	}

	@Override
	public void visit(cfg.operand.Var operand) {
		this.oneStmGen.add(operand.id);
		return;
	}

	// statements
	@Override
	public void visit(cfg.stm.Add s) {
		this.oneStmKill.add(s.dst);
		// Invariant: accept() of operand modifies "gen"
		s.left.accept(this);
		s.right.accept(this);
		return;
	}

	@Override
	public void visit(cfg.stm.InvokeVirtual s) {
		this.oneStmKill.add(s.dst);
		this.oneStmGen.add(s.obj);
		for (cfg.operand.T arg : s.args) {
			arg.accept(this);
		}
		return;
	}

	@Override
	public void visit(cfg.stm.Lt s) {
		this.oneStmKill.add(s.dst);
		// Invariant: accept() of operand modifies "gen"
		s.left.accept(this);
		s.right.accept(this);
		return;
	}

	@Override
	public void visit(cfg.stm.NewObject s) {
		this.oneStmKill.add(s.dst);
		return;
	}

	@Override
	public void visit(cfg.stm.Print s) {
		s.arg.accept(this);
		return;
	}

	@Override
	public void visit(cfg.stm.Sub s) {
		this.oneStmKill.add(s.dst);
		// Invariant: accept() of operand modifies "gen"
		s.left.accept(this);
		s.right.accept(this);
		return;
	}

	@Override
	public void visit(cfg.stm.Times s) {
		this.oneStmKill.add(s.dst);
		// Invariant: accept() of operand modifies "gen"
		s.left.accept(this);
		s.right.accept(this);
		return;
	}

	// transfer
	@Override
	public void visit(cfg.transfer.If s) {
		// Invariant: accept() of operand modifies "gen"
		s.operand.accept(this);
		this.list.add(s.truee.toString());
		this.list.add(s.falsee.toString());
		return;
	}

	@Override
	public void visit(cfg.transfer.Goto s) {
		this.list.add(s.label.toString());
		return;
	}

	@Override
	public void visit(cfg.transfer.Return s) {
		// Invariant: accept() of operand modifies "gen"
		s.operand.accept(this);
		return;
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

	// utility functions:
	private void calculateStmTransferGenKill(cfg.block.Block b) {
		if (control.Control.isTracing("liveness.step1")
				|| control.Control.isTracing("liveness.step2")) {
			System.out.print("\n" + b.label.toString());
		}
		this.list = new java.util.LinkedList<String>();
		this.lable.put(b.label.toString(), b);
		java.util.HashSet<String> block_gen = new java.util.HashSet<String>();
		java.util.HashSet<String> block_kill = new java.util.HashSet<String>();

		for (cfg.stm.T s : b.stms) {
			this.stm_set.put(s, new young_and_old());
			this.oneStmGen = new java.util.HashSet<>();
			this.oneStmKill = new java.util.HashSet<>();
			s.accept(this);
			this.stmGen.put(s, this.oneStmGen);
			this.stmKill.put(s, this.oneStmKill);
			// this.stmLiveIn.put(s, new java.util.HashSet<String>());
			// this.stmLiveOut.put(s, new java.util.HashSet<String>());
			if (control.Control.isTracing("liveness.step1")) {
				System.out.print("\ngen, kill for statement:");
				s.toString();
				System.out.print("\ngen is:");
				for (String str : this.oneStmGen) {
					System.out.print(str + ", ");
				}
				System.out.print("\nkill is:");
				for (String str : this.oneStmKill) {
					System.out.print(str + ", ");
				}
			}
			java.util.HashSet<String> set = new java.util.HashSet<String>();
			for (Iterator it = this.oneStmGen.iterator(); it.hasNext();) {
				String member = (String) it.next();
				set.add(member);
			}
			set.removeAll(block_kill);
			block_gen.addAll(set);
			block_kill.addAll(this.oneStmKill);
		}

		this.oneStmGen = new java.util.HashSet<>();
		this.oneStmKill = new java.util.HashSet<>();
		// this.oneTransferGen = new java.util.HashSet<>();
		// this.oneTransferKill = new java.util.HashSet<>();
		b.transfer.accept(this);
		this.tran_set.put(b.transfer, new young_and_old());
		this.oneTransferGen = this.oneStmGen;
		this.oneTransferKill = this.oneStmKill;
		this.transferGen.put(b.transfer, this.oneTransferGen);
		this.transferKill.put(b.transfer, this.oneTransferKill);

		java.util.HashSet<String> set = new java.util.HashSet<String>();
		for (Iterator it = this.oneStmGen.iterator(); it.hasNext();) {
			String member = (String) it.next();
			set.add(member);
		}
		set.removeAll(block_kill);
		block_gen.addAll(set);
		block_kill.addAll(this.oneStmKill);

		blockGen.put(b, block_gen);
		blockKill.put(b, block_kill);
		this.map.put(b.label.toString(), this.list);
		this.blockLiveIn.put(b, new java.util.HashSet<String>());
		this.blockLiveOut.put(b, new java.util.HashSet<String>());
		this.transferLiveIn.put(b.transfer, new java.util.HashSet<String>());
		this.transferLiveOut.put(b.transfer, new java.util.HashSet<String>());
		if (control.Control.isTracing("liveness.step1")) {
			System.out.print("\ngen, kill for transfer:");
			b.toString();
			System.out.print("\ngen is:");
			for (String str : this.oneTransferGen) {
				System.out.print(str + ", ");
			}
			System.out.println("\nkill is:");
			for (String str : this.oneTransferKill) {
				System.out.print(str + ", ");
			}
		} else if (control.Control.isTracing("liveness.step2")) {
			System.out.print("\ngen, kill for block:");
			System.out.print("\ngen is:");
			for (String str : block_gen) {
				System.out.print(str + ", ");
			}
			System.out.print("\nkill is:");
			for (String str : block_kill) {
				System.out.print(str + ", ");
			}
		}
		return;
	}

	// block
	@Override
	public void visit(cfg.block.Block b) {
		calculateStmTransferGenKill(b);
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

	// method
	@Override
	public void visit(cfg.method.Method m) {
		this.stm_set = new java.util.HashMap<>();
		this.tran_set = new java.util.HashMap<>();

		this.map = new java.util.HashMap<>();
		for (cfg.block.T block : m.blocks) {
			block.accept(this);
		}
		java.util.HashSet<String> visit = new java.util.HashSet<String>();
		java.util.LinkedList<String> worklist = new java.util.LinkedList<String>();
		this.map_re = new java.util.HashMap<String, java.util.LinkedList<String>>();
		for (cfg.block.T block : m.blocks) {
			String label = ((cfg.block.Block) block).label.toString();
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
			java.util.HashSet<String> out = this.blockLiveOut.get(b);
			java.util.HashSet<String> in = this.blockLiveIn.get(b);
			java.util.HashSet<String> gen = this.blockGen.get(b);
			java.util.HashSet<String> kill = this.blockKill.get(b);
			java.util.HashSet<String> old = new java.util.HashSet<String>();

			for (Iterator it = in.iterator(); it.hasNext();) {
				String member = (String) it.next();
				old.add(member);
			}

			java.util.LinkedList<String> to = this.map.get(head);
			for (String p : to) {
				cfg.block.Block tmp = (cfg.block.Block) this.lable.get(p);
				java.util.HashSet<String> in_p = this.blockLiveIn.get(tmp);// /
				out.addAll(in_p);
			}

			in.addAll(gen);
			for (Iterator it = out.iterator(); it.hasNext();) {
				String member = (String) it.next();
				if (!kill.contains(member)) {
					in.add(member);
				}
			}

			if (!old.equals(in)) {
				to = this.map_re.get(head);
				for (String s : to) {
					if (!visit.contains(s)) {
						worklist.addLast(s);
						visit.add(s);
					}
				}
			}
		}
		if (control.Control.isTracing("liveness.step3")) {
			for (cfg.block.T block : m.blocks) {
				String label = ((cfg.block.Block) block).label.toString();
				System.out.print("\n\n" + label);
				java.util.HashSet<String> live_in = this.blockLiveIn.get(block);
				System.out.print("\nIn:");
				for (Iterator it = live_in.iterator(); it.hasNext();) {
					String member = (String) it.next();
					System.out.print(member + ",");
				}
				java.util.HashSet<String> live_out = this.blockLiveOut
						.get(block);
				System.out.print("\nOut:");
				for (Iterator it = live_out.iterator(); it.hasNext();) {
					String member = (String) it.next();
					System.out.print(member + ",");
				}
			}
		}
		do {
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				for (cfg.stm.T s : block.stms) {
					this.stm_set.get(s).give();
				}
				this.tran_set.get(block.transfer).give();
			}
			for (int i = m.blocks.size() - 1; i >= 0; i--) {
				cfg.block.Block block = (cfg.block.Block) m.blocks.get(i);

				{
					cfg.transfer.T tran = block.transfer;
					java.util.HashSet<String> gen = this.transferGen.get(tran);
					java.util.HashSet<String> kill = this.transferKill
							.get(tran);
					java.util.HashSet<String> young_in = this.tran_set
							.get(tran).young_in;
					java.util.HashSet<String> young_out = this.tran_set
							.get(tran).young_out;
					young_in.addAll(gen);
					for (Iterator it = young_out.iterator(); it.hasNext();) {
						String member = (String) it.next();
						if (!kill.contains(member)) {
							young_in.add(member);
						}
					}
					if (!(tran instanceof cfg.transfer.Return)) {
						if (tran instanceof cfg.transfer.Goto) {
							cfg.transfer.Goto tt = (cfg.transfer.Goto) tran;

							String label = tt.label.toString();
							cfg.block.Block to_block = (cfg.block.Block) this.lable
									.get(label);
							if (to_block.stms.size() > 0) {
								cfg.stm.T first_stm = to_block.stms.get(0);
								java.util.HashSet<String> first_in = this.stm_set
										.get(first_stm).old_in;
								young_out.addAll(first_in);
							} else {
								java.util.HashSet<String> first_in = this.tran_set
										.get(to_block.transfer).old_in;
								young_out.addAll(first_in);
							}
						} else {
							cfg.transfer.If tt = (cfg.transfer.If) tran;

							String label_true = tt.truee.toString();
							cfg.block.Block true_block = (cfg.block.Block) this.lable
									.get(label_true);
							if (true_block.stms.size() > 0) {
								cfg.stm.T first_stm = true_block.stms.get(0);
								java.util.HashSet<String> first_in = this.stm_set
										.get(first_stm).old_in;
								young_out.addAll(first_in);
							} else {
								java.util.HashSet<String> first_in = this.tran_set
										.get(true_block.transfer).old_in;
								young_out.addAll(first_in);
							}

							String label_false = tt.falsee.toString();
							cfg.block.Block false_block = (cfg.block.Block) this.lable
									.get(label_false);
							if (false_block.stms.size() > 0) {
								cfg.stm.T first_stm = false_block.stms.get(0);
								java.util.HashSet<String> first_in = this.stm_set
										.get(first_stm).old_in;
								young_out.addAll(first_in);
							} else {
								java.util.HashSet<String> first_in = this.tran_set
										.get(false_block.transfer).old_in;
								young_out.addAll(first_in);
							}
						}
					}
				}

				boolean last = true;
				cfg.stm.T last_stm = null;
				for (int j = block.stms.size() - 1; j >= 0; j--) {
					cfg.stm.T stm = block.stms.get(j);
					java.util.HashSet<String> gen = this.stmGen.get(stm);
					java.util.HashSet<String> kill = this.stmKill.get(stm);
					java.util.HashSet<String> young_in = this.stm_set.get(stm).young_in;
					java.util.HashSet<String> young_out = this.stm_set.get(stm).young_out;
					young_in.addAll(gen);
					for (Iterator it = young_out.iterator(); it.hasNext();) {
						String member = (String) it.next();
						if (!kill.contains(member)) {
							young_in.add(member);
						}
					}
					if (last) {
						last = false;
						cfg.transfer.T tran = block.transfer;
						java.util.HashSet<String> last_in = this.tran_set
								.get(tran).young_in;
						young_out.addAll(last_in);
						last_stm = stm;
					} else {
						java.util.HashSet<String> last_in = this.stm_set
								.get(last_stm).young_in;
						young_out.addAll(last_in);
						last_stm = stm;
					}
				}
			}
			// //debug
			/*
			 * for (cfg.block.T bb : m.blocks) { cfg.block.Block block =
			 * (cfg.block.Block)bb;
			 * System.out.print("\n"+block.label.toString()); for (cfg.stm.T
			 * s:block.stms){
			 * 
			 * System.out.print("\n   in: "); java.util.HashSet<String> set =
			 * this.stm_set.get(s).young_in; for (Iterator it = set.iterator();
			 * it.hasNext();){ String member = (String)it.next();
			 * System.out.print(member + ","); }
			 * 
			 * System.out.print("\n  out: "); set =
			 * this.stm_set.get(s).young_out; for (Iterator it = set.iterator();
			 * it.hasNext();){ String member = (String)it.next();
			 * System.out.print(member + ","); } } block.transfer.toString();
			 * java.util.HashSet<String> set =
			 * this.tran_set.get(block.transfer).young_in;
			 * System.out.print("\n   in: "); for (Iterator it = set.iterator();
			 * it.hasNext();){ String member = (String)it.next();
			 * System.out.print(member + ","); }
			 * 
			 * System.out.print("\n  out: "); set =
			 * this.tran_set.get(block.transfer).young_out; for (Iterator it =
			 * set.iterator(); it.hasNext();){ String member =
			 * (String)it.next(); System.out.print(member + ","); } }
			 */

		} while (!not_change());
		for (cfg.block.T bb : m.blocks) {
			cfg.block.Block block = (cfg.block.Block) bb;
			for (cfg.stm.T s : block.stms) {
				this.stmLiveIn.put(s, this.stm_set.get(s).old_in);
				this.stmLiveOut.put(s, this.stm_set.get(s).old_out);
			}
			java.util.HashSet<String> set = this.tran_set.get(block.transfer).old_in;
			this.transferLiveIn.put(block.transfer, set);
			set = this.tran_set.get(block.transfer).old_out;
			this.transferLiveOut.put(block.transfer, set);
		}
		if (control.Control.isTracing("liveness.step4")) {
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				System.out.print("\n" + block.label.toString());
				for (cfg.stm.T s : block.stms) {

					System.out.print("\n\n   in: ");
					java.util.HashSet<String> set = this.stm_set.get(s).old_in;
					for (Iterator it = set.iterator(); it.hasNext();) {
						String member = (String) it.next();
						System.out.print(member + ",");
					}

					System.out.print("\n  out: ");
					set = this.stm_set.get(s).old_out;
					for (Iterator it = set.iterator(); it.hasNext();) {
						String member = (String) it.next();
						System.out.print(member + ",");
					}
				}
				block.transfer.toString();
				java.util.HashSet<String> set = this.tran_set
						.get(block.transfer).old_in;
				System.out.print("\n\n   in: ");
				for (Iterator it = set.iterator(); it.hasNext();) {
					String member = (String) it.next();
					System.out.print(member + ",");
				}

				System.out.print("\n  out: ");
				set = this.tran_set.get(block.transfer).old_out;
				for (Iterator it = set.iterator(); it.hasNext();) {
					String member = (String) it.next();
					System.out.print(member + ",");
				}
			}
		}
	}

	@Override
	public void visit(cfg.mainMethod.MainMethod m) {
		this.stm_set = new java.util.HashMap<>();
		this.tran_set = new java.util.HashMap<>();

		this.map = new java.util.HashMap<>();
		for (cfg.block.T block : m.blocks) {
			block.accept(this);
		}
		java.util.HashSet<String> visit = new java.util.HashSet<String>();
		java.util.LinkedList<String> worklist = new java.util.LinkedList<String>();
		this.map_re = new java.util.HashMap<String, java.util.LinkedList<String>>();
		for (cfg.block.T block : m.blocks) {
			String label = ((cfg.block.Block) block).label.toString();
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
			java.util.HashSet<String> out = this.blockLiveOut.get(b);
			java.util.HashSet<String> in = this.blockLiveIn.get(b);
			java.util.HashSet<String> gen = this.blockGen.get(b);
			java.util.HashSet<String> kill = this.blockKill.get(b);
			java.util.HashSet<String> old = new java.util.HashSet<String>();

			for (Iterator it = in.iterator(); it.hasNext();) {
				String member = (String) it.next();
				old.add(member);
			}

			java.util.LinkedList<String> to = this.map.get(head);
			for (String p : to) {
				cfg.block.Block tmp = (cfg.block.Block) this.lable.get(p);
				java.util.HashSet<String> in_p = this.blockLiveIn.get(tmp);// /
				out.addAll(in_p);
			}

			in.addAll(gen);
			for (Iterator it = out.iterator(); it.hasNext();) {
				String member = (String) it.next();
				if (!kill.contains(member)) {
					in.add(member);
				}
			}

			if (!old.equals(in)) {
				to = this.map_re.get(head);
				for (String s : to) {
					if (!visit.contains(s)) {
						worklist.addLast(s);
						visit.add(s);
					}
				}
			}
		}
		if (control.Control.isTracing("liveness.step3")) {
			for (cfg.block.T block : m.blocks) {
				String label = ((cfg.block.Block) block).label.toString();
				System.out.print("\n\n" + label);
				java.util.HashSet<String> live_in = this.blockLiveIn.get(block);
				System.out.print("\nIn:");
				for (Iterator it = live_in.iterator(); it.hasNext();) {
					String member = (String) it.next();
					System.out.print(member + ",");
				}
				java.util.HashSet<String> live_out = this.blockLiveOut
						.get(block);
				System.out.print("\nOut:");
				for (Iterator it = live_out.iterator(); it.hasNext();) {
					String member = (String) it.next();
					System.out.print(member + ",");
				}
			}
		}
		do {
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				for (cfg.stm.T s : block.stms) {
					this.stm_set.get(s).give();
				}
				this.tran_set.get(block.transfer).give();
			}
			for (int i = m.blocks.size() - 1; i >= 0; i--) {
				cfg.block.Block block = (cfg.block.Block) m.blocks.get(i);

				{
					cfg.transfer.T tran = block.transfer;
					java.util.HashSet<String> gen = this.transferGen.get(tran);
					java.util.HashSet<String> kill = this.transferKill
							.get(tran);
					java.util.HashSet<String> young_in = this.tran_set
							.get(tran).young_in;
					java.util.HashSet<String> young_out = this.tran_set
							.get(tran).young_out;
					young_in.addAll(gen);
					for (Iterator it = young_out.iterator(); it.hasNext();) {
						String member = (String) it.next();
						if (!kill.contains(member)) {
							young_in.add(member);
						}
					}
					if (!(tran instanceof cfg.transfer.Return)) {
						if (tran instanceof cfg.transfer.Goto) {
							cfg.transfer.Goto tt = (cfg.transfer.Goto) tran;

							String label = tt.label.toString();
							cfg.block.Block to_block = (cfg.block.Block) this.lable
									.get(label);
							if (to_block.stms.size() > 0) {
								cfg.stm.T first_stm = to_block.stms.get(0);
								java.util.HashSet<String> first_in = this.stm_set
										.get(first_stm).old_in;
								young_out.addAll(first_in);
							} else {
								java.util.HashSet<String> first_in = this.tran_set
										.get(to_block.transfer).old_in;
								young_out.addAll(first_in);
							}
						} else {
							cfg.transfer.If tt = (cfg.transfer.If) tran;

							String label_true = tt.truee.toString();
							cfg.block.Block true_block = (cfg.block.Block) this.lable
									.get(label_true);
							if (true_block.stms.size() > 0) {
								cfg.stm.T first_stm = true_block.stms.get(0);
								java.util.HashSet<String> first_in = this.stm_set
										.get(first_stm).old_in;
								young_out.addAll(first_in);
							} else {
								java.util.HashSet<String> first_in = this.tran_set
										.get(true_block.transfer).old_in;
								young_out.addAll(first_in);
							}

							String label_false = tt.falsee.toString();
							cfg.block.Block false_block = (cfg.block.Block) this.lable
									.get(label_false);
							if (false_block.stms.size() > 0) {
								cfg.stm.T first_stm = false_block.stms.get(0);
								java.util.HashSet<String> first_in = this.stm_set
										.get(first_stm).old_in;
								young_out.addAll(first_in);
							} else {
								java.util.HashSet<String> first_in = this.tran_set
										.get(false_block.transfer).old_in;
								young_out.addAll(first_in);
							}
						}
					}
				}

				boolean last = true;
				cfg.stm.T last_stm = null;
				for (int j = block.stms.size() - 1; j >= 0; j--) {
					cfg.stm.T stm = block.stms.get(j);
					java.util.HashSet<String> gen = this.stmGen.get(stm);
					java.util.HashSet<String> kill = this.stmKill.get(stm);
					java.util.HashSet<String> young_in = this.stm_set.get(stm).young_in;
					java.util.HashSet<String> young_out = this.stm_set.get(stm).young_out;
					young_in.addAll(gen);
					for (Iterator it = young_out.iterator(); it.hasNext();) {
						String member = (String) it.next();
						if (!kill.contains(member)) {
							young_in.add(member);
						}
					}
					if (last) {
						last = false;
						cfg.transfer.T tran = block.transfer;
						java.util.HashSet<String> last_in = this.tran_set
								.get(tran).young_in;
						young_out.addAll(last_in);
						last_stm = stm;
					} else {
						java.util.HashSet<String> last_in = this.stm_set
								.get(last_stm).young_in;
						young_out.addAll(last_in);
						last_stm = stm;
					}
				}
			}

		} while (!not_change());
		for (cfg.block.T bb : m.blocks) {
			cfg.block.Block block = (cfg.block.Block) bb;
			for (cfg.stm.T s : block.stms) {
				this.stmLiveIn.put(s, this.stm_set.get(s).old_in);
				this.stmLiveOut.put(s, this.stm_set.get(s).old_out);
			}
			java.util.HashSet<String> set = this.tran_set.get(block.transfer).old_in;
			this.transferLiveIn.put(block.transfer, set);
			set = this.tran_set.get(block.transfer).old_out;
			this.transferLiveOut.put(block.transfer, set);
		}
		if (control.Control.isTracing("liveness.step4")) {
			for (cfg.block.T bb : m.blocks) {
				cfg.block.Block block = (cfg.block.Block) bb;
				System.out.print("\n" + block.label.toString());
				for (cfg.stm.T s : block.stms) {

					System.out.print("\n\n   in: ");
					java.util.HashSet<String> set = this.stm_set.get(s).old_in;
					for (Iterator it = set.iterator(); it.hasNext();) {
						String member = (String) it.next();
						System.out.print(member + ",");
					}

					System.out.print("\n  out: ");
					set = this.stm_set.get(s).old_out;
					for (Iterator it = set.iterator(); it.hasNext();) {
						String member = (String) it.next();
						System.out.print(member + ",");
					}
				}
				block.transfer.toString();
				java.util.HashSet<String> set = this.tran_set
						.get(block.transfer).old_in;
				System.out.print("\n\n   in: ");
				for (Iterator it = set.iterator(); it.hasNext();) {
					String member = (String) it.next();
					System.out.print(member + ",");
				}

				System.out.print("\n  out: ");
				set = this.tran_set.get(block.transfer).old_out;
				for (Iterator it = set.iterator(); it.hasNext();) {
					String member = (String) it.next();
					System.out.print(member + ",");
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

	@Override
	public void visit(cfg.stm.And s) {
		// TODO Auto-generated method stub
		this.oneStmKill.add(s.dst);
		// Invariant: accept() of operand modifies "gen"
		s.left.accept(this);
		s.right.accept(this);
		return;
	}

	@Override
	public void visit(cfg.stm.Move s) {
		this.oneStmKill.add(s.dst);
		// Invariant: accept() of operand modifies "gen"
		s.src.accept(this);
		return;
	}

	@Override
	public void visit(cfg.stm.MoveArray m) {
		// TODO Auto-generated method stub
		this.oneStmKill.add(m.dst);
		m.index.accept(this);
		m.src.accept(this);
	}

	@Override
	public void visit(cfg.stm.Not m) {
		// TODO Auto-generated method stub
		this.oneStmKill.add(m.dst);
		m.exp.accept(this);
	}

	@Override
	public void visit(cfg.operand.IntArray operand) {
		// TODO Auto-generated method stub
		operand.array.accept(this);
		operand.index.accept(this);
	}

	@Override
	public void visit(cfg.stm.NewIntArray s) {
		// TODO Auto-generated method stub
		this.oneStmKill.add(s.dst);
		return;
	}

}
