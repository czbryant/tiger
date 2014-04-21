package ast.exp;

public class This extends T {

	public int line;

	public This() {
	}

	public This(int line) {
		this.line = line;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
		return;
	}
}
