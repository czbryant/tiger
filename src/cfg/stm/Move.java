package cfg.stm;

import cfg.Visitor;

public class Move extends T {
	public String dst;
	// type of the destination variable
	public cfg.type.T ty;
	public cfg.operand.T src;
	public boolean isField;

	public Move(String dst, cfg.type.T ty, cfg.operand.T src, boolean isField) {
		this.dst = dst;
		this.ty = ty;
		this.src = src;
		this.isField = isField;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
