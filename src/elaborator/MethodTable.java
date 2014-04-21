package elaborator;

import java.util.Enumeration;

public class MethodTable {
	private java.util.Hashtable<String, ast.type.T> table;

	public MethodTable() {
		this.table = new java.util.Hashtable<String, ast.type.T>();
	}

	// Duplication is not allowed
	public void put(java.util.LinkedList<ast.dec.T> formals,
			java.util.LinkedList<ast.dec.T> locals) {
		for (ast.dec.T dec : formals) {
			ast.dec.Dec decc = (ast.dec.Dec) dec;
			if (this.table.get(decc.id) != null) {
				System.out.println("duplicated parameter: " + decc.id);
				System.exit(1);
			}
			if(decc.id !=null && decc.type !=null)
			this.table.put(decc.id, decc.type);
		}

		if (locals != null) {
			for (ast.dec.T dec : locals) {
				ast.dec.Dec decc = (ast.dec.Dec) dec;
				if (this.table.get(decc.id) != null) {
					System.out.println("duplicated variable: " + decc.id);
					System.exit(1);
				}
				if(decc.id !=null && decc.type !=null){
					if(decc.type.getClass().getName().equals("ast.type.Int"))
						((ast.type.Int)decc.type).line = decc.line;
					else if(decc.type.getClass().getName().equals("ast.type.Boolean"))
						((ast.type.Boolean)decc.type).line = decc.line;
					else if(decc.type.getClass().getName().equals("ast.type.Class"))
						((ast.type.Class)decc.type).line = decc.line;
					else if(decc.type.getClass().getName().equals("ast.type.IntArray"))
						((ast.type.IntArray)decc.type).line = decc.line;
		
					this.table.put(decc.id, decc.type);
				}
			}
		}

	}

	// return null for non-existing keys
	public ast.type.T get(String id) {
		return this.table.get(id);
	}

	public void dump() {
		Enumeration<String> keys = table.keys();
		if (table.size() != 0){
		System.out.println("---------the content of the method table ------ ");

		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			System.out.println("Type:" + table.get(key).toString());
			System.out.println("id:" + key);
		}
		
		System.out.println("---------the end of the method table ------ ");
		// new Todo();
		}
	}

	@Override
	public String toString() {
		return this.table.toString();
	}
	
	public void clear(){
		this.table = new java.util.Hashtable<String, ast.type.T>();
	}
}
