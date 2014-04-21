package ast.exp;

public class Call extends T {
	public T exp;
	public String id;
	public java.util.LinkedList<T> args;
	public String type; // type of first field "exp"
	// the following two fields records the argument types
	// and return type for the method being called, all these
	// types are declared types, but not actural types of the
	// arguments.
	// Both these two fields are filled in by the elaborator.
	public java.util.LinkedList<ast.type.T> at; // arg's type
	public ast.type.T rt;
	public int line;

	public Call(T exp, String id, java.util.LinkedList<T> args) {
		this.exp = exp;
		this.id = id;
		this.args = args;
		this.type = null;
		this.at = null;
		this.rt = null;
	}

	public Call(T exp, String id, java.util.LinkedList<T> args, int line) {
		this.exp = exp;
		this.id = id;
		this.args = args;
		this.type = null;
		this.line = line;
	}

	public Call(T exp, String id, java.util.LinkedList<T> args, String type,
			java.util.LinkedList<ast.type.T> at, ast.type.T rt, int line) {
		this.exp = exp;
		this.id = id;
		this.args = args;
		this.type = type;
		this.rt = rt;
		this.at = at;
		this.line = line;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
		return;
	}
}
