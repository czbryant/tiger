package ast.exp;

public class Id extends T {
	public String id; // name of the id
	public ast.type.T type; // type of the id
	public boolean isField; // whether or not this is a class field
	public int line;

	public Id(String id) {
		this.id = id;
		this.type = null;
		this.isField = false;
	}

	public Id(String id, ast.type.T type, boolean isField) {
		this.id = id;
		this.type = type;
		this.isField = isField;
	}

	public Id(String id, boolean isField, int line) {
		this.id = id;
		this.type = null;
		this.isField = isField;
		this.line = line;
	}

	public Id(String id, ast.type.T type, boolean isField, int line) {
		this.id = id;
		this.type = type;
		this.isField = isField;
		this.line = line;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
		return;
	}
}
