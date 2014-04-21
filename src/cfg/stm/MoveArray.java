package cfg.stm;

import cfg.Visitor;

public class MoveArray extends T {
	public String dst;
	// type of the destination variable
	public cfg.type.T ty;
	public cfg.operand.T index;
	public cfg.operand.T src;
	public boolean isField;

	public MoveArray(String dst, cfg.type.T ty, cfg.operand.T index,
			cfg.operand.T src, boolean isField) {
		this.dst = dst;
		this.ty = ty;
		this.index = index;
		this.src = src;
		this.isField = isField;
	}

	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		v.visit(this);
	}

}
