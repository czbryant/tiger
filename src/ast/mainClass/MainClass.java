package ast.mainClass;

import java.util.LinkedList;

import ast.Visitor;

public class MainClass extends T {
	public String id;
	public String arg;
	public LinkedList<ast.dec.T> locals;
	public LinkedList<ast.stm.T> stms;

	public MainClass(String id, String arg, LinkedList<ast.dec.T> locals,
			LinkedList<ast.stm.T> stms) {
		this.id = id;
		this.arg = arg;
		this.locals = locals;
		this.stms = stms;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
		return;
	}

}
