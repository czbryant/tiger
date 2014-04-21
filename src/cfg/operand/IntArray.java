package cfg.operand;

import cfg.Visitor;

public class IntArray extends T {
	public cfg.operand.T array;
	public cfg.operand.T index;
	public IntArray(cfg.operand.T array, cfg.operand.T index)
	{
 	   this.array = array;
 	   this.index = index;
    }
	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		v.visit(this);
	}

}
