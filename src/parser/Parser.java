package parser;

import java.util.LinkedList;

import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;

public class Parser {
	Lexer lexer;
	Token current;
	public String filename;

	public Token preToken = null;

	public Parser(String fname, java.io.InputStream fstream) {
		lexer = new Lexer(fname, fstream);
		current = lexer.nextToken();
		while (current.kind == Kind.TOKEN_ANNOTATION)
			current = lexer.nextToken();
		filename = fname;
	}

	// /////////////////////////////////////////////
	// utility methods to connect the lexer
	// and the parser.

	private void advance() {
		current = lexer.nextToken();
		while (current.kind == Kind.TOKEN_ANNOTATION)
			current = lexer.nextToken();
	}

	private void eatToken(Kind kind) {
		if (kind == current.kind) {
			advance();
		} else {
			String str[] = filename.split("/");
			System.out.println("error at " + str[str.length - 1] + " at line "
					+ current.lineNum + ",coloumn " + current.columnNum);
			System.out.println("Expects: " + kind.toString());
			System.out.println("But got: " + current.kind.toString());
			System.exit(1);
		}
	}

	private void error() {
		System.out.println("error at " + filename + " " + current.lineNum + ":"
				+ current.columnNum);
		// System.out.println("Syntax error: compilation aborting...\n");
		System.exit(1);
		return;
	}

	// ////////////////////////////////////////////////////////////
	// below are method for parsing.

	// A bunch of parsing methods to parse expressions. The messy
	// parts are to deal with precedence and associativity.

	// ExpList -> Exp ExpRest*
	// ->
	// ExpRest -> , Exp
	private LinkedList<ast.exp.T> parseExpList() {

		LinkedList<ast.exp.T> list = new LinkedList<ast.exp.T>();
		ast.exp.T exp = null;
		if (current.kind == Kind.TOKEN_RPAREN)
			return null;
		exp = parseExp();
		list.add(exp);
		while (current.kind == Kind.TOKEN_COMMER) {
			advance();
			exp = parseExp();
			list.add(exp);
		}
		return list;
	}

	// AtomExp -> (exp)
	// -> INTEGER_LITERAL
	// -> true
	// -> false
	// -> this
	// -> id
	// -> new int [exp]
	// -> new id ()
	private ast.exp.T parseAtomExp() {
		ast.exp.T exp;
		String id;
		switch (current.kind) {
		case TOKEN_LPAREN:
			advance();
			exp = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			return new ast.exp.ParenExp(exp, current.lineNum);
		case TOKEN_NUM:
			int num = Integer.parseInt(current.lexeme);
			advance();
			return new ast.exp.Num(num, current.lineNum);
		case TOKEN_TRUE:
			advance();
			return new ast.exp.True(current.lineNum);
		case TOKEN_FALSE:
			eatToken(Kind.TOKEN_FALSE);
			return new ast.exp.False(current.lineNum);
		case TOKEN_THIS:
			advance();
			return new ast.exp.This(current.lineNum);
		case TOKEN_ID:
			id = current.lexeme;
			advance();
			return new ast.exp.Id(id);
		case TOKEN_NEW: {
			advance();
			switch (current.kind) {
			case TOKEN_INT:
				advance();
				eatToken(Kind.TOKEN_LBRACK);
				exp = parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				return new ast.exp.NewIntArray(exp, current.lineNum);
			case TOKEN_ID:
				id = current.lexeme;
				advance();
				eatToken(Kind.TOKEN_LPAREN);
				eatToken(Kind.TOKEN_RPAREN);
				return new ast.exp.NewObject(id, current.lineNum);
			default:
				error();
				return null;
			}
		}
		default:
			error();
			return null;
		}
	}

	// NotExp -> AtomExp
	// -> AtomExp .id (expList) //call
	// -> AtomExp .length
	// -> AtomExp [exp]

	private ast.exp.T parseNotExp() {
		ast.exp.T exp = parseAtomExp();
		if (current.kind == Kind.TOKEN_DOT) {
			advance();
			if (current.kind == Kind.TOKEN_LENGTH) {
				advance();
				return new ast.exp.Length(exp, current.lineNum);
			} else if (current.kind == Kind.TOKEN_ID) {
				String id = current.lexeme;
				eatToken(Kind.TOKEN_ID);
				eatToken(Kind.TOKEN_LPAREN);
				LinkedList<ast.exp.T> args = parseExpList();
				eatToken(Kind.TOKEN_RPAREN);
				return new ast.exp.Call(exp, id, args);
			} else {
				error();
				return null;
			}

		} else if (current.kind == Kind.TOKEN_LBRACK) {
			advance();
			ast.exp.T index = parseExp();
			eatToken(Kind.TOKEN_RBRACK);
			return new ast.exp.ArraySelect(exp, index, current.lineNum);
		}

		return exp;

	}

	// TimesExp -> ! TimesExp
	// -> NotExp
	private ast.exp.T parseTimesExp() {
		ast.exp.T exp;
		if (current.kind == Kind.TOKEN_NOT) {
			advance();
			exp = parseTimesExp();
		} else
			exp = parseNotExp();
		return exp;
	}

	// AddSubExp -> TimesExp * TimesExp
	// -> TimesExp
	private ast.exp.T parseAddSubExp() {
		ast.exp.T left = parseTimesExp();
		if (current.kind == Kind.TOKEN_TIMES) {
			advance();
			ast.exp.T right = parseTimesExp();
			return new ast.exp.Times(left, right, current.lineNum);
		}
		return left;
	}

	// LtExp -> AddSubExp + AddSubExp
	// -> AddSubExp - AddSubExp
	// -> AddSubExp
	private ast.exp.T parseLtExp() {
		ast.exp.T left = parseAddSubExp();
		if (current.kind == Kind.TOKEN_ADD) {
			advance();
			ast.exp.T right = parseAddSubExp();
			return new ast.exp.Add(left, right, current.lineNum);
		} else if (current.kind == Kind.TOKEN_SUB) {
			advance();
			ast.exp.T right = parseAddSubExp();
			return new ast.exp.Sub(left, right, current.lineNum);
		}
		else
			return left;
	}

	// AndExp -> LtExp < LtExp
	// -> LtExp
	private ast.exp.T parseAndExp() {
		ast.exp.T left = parseLtExp();
		if (current.kind == Kind.TOKEN_LT) {
			advance();
			ast.exp.T right = parseLtExp();
			return new ast.exp.Lt(left, right, current.lineNum);
		}
		return left;
	}

	// Exp -> AndExp && AndExp
	// -> AndExp
	private ast.exp.T parseExp() {
		ast.exp.T left = parseAndExp();
		if (current.kind == Kind.TOKEN_AND) {
			advance();
			ast.exp.T right = parseAndExp();
			return new ast.exp.And(left, right, current.lineNum);
		} else
			return left;

	}

	// Statement -> { Statement* }
	// -> if ( Exp ) Statement else Statement
	// -> while ( Exp ) Statement
	// -> System.out.println ( Exp ) ;
	// -> id = Exp ;
	// -> id [ Exp ]= Exp ;
	private ast.stm.T parseStatement() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a statement.

		ast.exp.T condition;
		ast.exp.T exp;

		switch (current.kind) {
		case TOKEN_IF:
			advance();
			eatToken(Kind.TOKEN_LPAREN);
			condition = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			ast.stm.T thenn = parseStatement();
			eatToken(Kind.TOKEN_ELSE);
			ast.stm.T elsee = parseStatement();
			return new ast.stm.If(condition, thenn, elsee);
		case TOKEN_WHILE:
			advance();
			eatToken(Kind.TOKEN_LPAREN);
			condition = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			ast.stm.T body = parseStatement();
			return new ast.stm.While(condition, body);
		case TOKEN_SYSTEM:
			advance();
			eatToken(Kind.TOKEN_DOT);
			eatToken(Kind.TOKEN_OUT);
			eatToken(Kind.TOKEN_DOT);
			eatToken(Kind.TOKEN_PRINTLN);
			eatToken(Kind.TOKEN_LPAREN);
			exp = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			eatToken(Kind.TOKEN_SEMI);
			return new ast.stm.Print(exp);
		case TOKEN_ID:
			// -> id = Exp ;
			// -> id [ Exp ]= Exp ;

			String id = current.lexeme;
			advance();
			switch (current.kind) {
			case TOKEN_ASSIGN:
				advance();
				exp = parseExp();
				eatToken(Kind.TOKEN_SEMI);
				return new ast.stm.Assign(id, exp, current.lineNum);
			case TOKEN_LBRACK:
				advance();
				exp = parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				eatToken(Kind.TOKEN_ASSIGN);
				ast.exp.T expTwo = parseExp();
				eatToken(Kind.TOKEN_SEMI);
				return new ast.stm.AssignArray(id, exp, expTwo, current.lineNum);
			default:
				error();
				return null;
			}
		case TOKEN_LBRACE:
			advance();
			LinkedList<ast.stm.T> stms = parseStatements();
			eatToken(Kind.TOKEN_RBRACE);
			return new ast.stm.Block(stms);
		default:
			error();
			return null;
		}
	}

	// Statements -> Statement Statements
	// ->
	private LinkedList<ast.stm.T> parseStatements() {
		LinkedList<ast.stm.T> stms = new LinkedList<ast.stm.T>();
		if (preToken != null) {
			stms.add(parseStatementPre(preToken));
			preToken = null;
		}
			

		while (current.kind == Kind.TOKEN_LBRACE
				|| current.kind == Kind.TOKEN_IF
				|| current.kind == Kind.TOKEN_WHILE
				|| current.kind == Kind.TOKEN_SYSTEM
				|| current.kind == Kind.TOKEN_ID) {

			stms.add(parseStatement());
		}
		return stms;
	}

	// Type -> int []
	// -> boolean
	// -> int
	// -> id
	private ast.type.T parseType() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a type.
		switch (current.kind) {
		case TOKEN_BOOLEAN:
			eatToken(Kind.TOKEN_BOOLEAN);
			return new ast.type.Boolean(current.lineNum);
		case TOKEN_INT:
			eatToken(Kind.TOKEN_INT);
			if (current.kind == Kind.TOKEN_LBRACK) {
				advance();
				eatToken(Kind.TOKEN_RBRACK);
				return new ast.type.IntArray(current.lineNum);
			} else
				return new ast.type.Int(current.lineNum);
		case TOKEN_ID:
			String id = current.lexeme;
			advance();
			return new ast.type.Class(id, current.lineNum);
		default:
			error();
			return null;
		}
	}

	// VarDecl -> Type id ;
	private ast.dec.Dec parseVarDecl() {
		// to parse the "Type" nonterminal in this method, instead of writing
		// a fresh one.
		ast.type.T type = parseType();
		String id = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_SEMI);
		return new ast.dec.Dec(type, id, current.lineNum);
	}

	// VarDecls -> VarDecl VarDecls
	// ->
	private LinkedList<ast.dec.T> parseVarDecls() {

		LinkedList<ast.dec.T> list = new LinkedList<ast.dec.T>();
		while (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_STRING
				|| current.kind == Kind.TOKEN_ID) {
			ast.dec.Dec dec = null;
			if (current.kind == Kind.TOKEN_ID) {
				preToken = current;
				advance();
				if (current.kind == Kind.TOKEN_ID) {
					dec = new ast.dec.Dec(new ast.type.Class(preToken.lexeme,
							preToken.lineNum), current.lexeme, current.lineNum);
					advance();
					eatToken(Kind.TOKEN_SEMI);
					preToken = null;
				} else if (current.kind == Kind.TOKEN_ASSIGN) {
					break;
				} else {
					error();
					System.out.println("only assign like this 'id = exp'");
					return null;
				}
			} else {
				dec = parseVarDecl();
			}

			if (dec != null) {
				list.add(dec);
			}
		}
		return list;
	}
	
	// -> id = Exp ;
	private ast.stm.T parseStatementPre(Token pre) {
		ast.exp.T exp;
		String id = preToken.lexeme;
		advance();
		exp = parseExp();
		eatToken(Kind.TOKEN_SEMI);
		return new ast.stm.Assign(id, exp, current.lineNum);

	}

	// FormalList -> Type id FormalRest*
	// ->
	// FormalRest -> , Type id
	private LinkedList<ast.dec.T> parseFormalList() {
		
		LinkedList<ast.dec.T> list = new LinkedList<ast.dec.T>();
		ast.type.T type = null;
		String id = null;

		if (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_ID) {
			type = parseType();
			id = current.lexeme;
			eatToken(Kind.TOKEN_ID);
			list.add(new ast.dec.Dec(type, id, current.lineNum));
			while (current.kind == Kind.TOKEN_COMMER) {
				advance();
				type = parseType();
				id = current.lexeme;
				eatToken(Kind.TOKEN_ID);
				list.add(new ast.dec.Dec(type, id,
						current.lineNum));
			}
		}
		return list;
		
		
	}

	// Method -> public Type id ( FormalList )
	// { VarDecl* Statement* return Exp ;}
	private ast.method.T parseMethod() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a method.
		ast.type.T type = null;
		String id;
		LinkedList<ast.dec.T> formals = new LinkedList<ast.dec.T>();
		LinkedList<ast.dec.T> locals = new LinkedList<ast.dec.T>();
		LinkedList<ast.stm.T> stms = new LinkedList<ast.stm.T>();
		ast.exp.T retExp;

		eatToken(Kind.TOKEN_PUBLIC);
		type = parseType();
		id = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_LPAREN); // (
		formals = parseFormalList();
		eatToken(Kind.TOKEN_RPAREN);// )
		eatToken(Kind.TOKEN_LBRACE);// {
		locals = parseVarDecls(); // variable
		stms = parseStatements(); // statements
		eatToken(Kind.TOKEN_RETURN);
		retExp = parseExp();
		eatToken(Kind.TOKEN_SEMI);
		eatToken(Kind.TOKEN_RBRACE);
		return new ast.method.Method(type, id, formals, locals, stms, retExp);

	}

	// MethodDecls -> MethodDecl MethodDecls
	// ->
	private LinkedList<ast.method.T> parseMethodDecls() {
		LinkedList<ast.method.T> methods = new LinkedList<ast.method.T>();
		while (current.kind == Kind.TOKEN_PUBLIC) {
			methods.add(parseMethod());
		}
		return methods;
	}

	// ClassDecl -> class id { VarDecl* MethodDecl* }
	// -> class id extends id { VarDecl* MethodDecl* }
	private ast.classs.T parseClassDecl() {
		String id;
		String extendstr = null;
		LinkedList<ast.dec.T> decs = new LinkedList<ast.dec.T>();
		LinkedList<ast.method.T> methods = new LinkedList<ast.method.T>();
		eatToken(Kind.TOKEN_CLASS);
		id = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		if (current.kind == Kind.TOKEN_EXTENDS) {
			eatToken(Kind.TOKEN_EXTENDS);
			extendstr = current.lexeme;
			eatToken(Kind.TOKEN_ID);
		}
		eatToken(Kind.TOKEN_LBRACE);
		decs = parseVarDecls();
		methods = parseMethodDecls();
		eatToken(Kind.TOKEN_RBRACE);
		return new ast.classs.Class(id, extendstr, decs, methods);
	}

	// ClassDecls -> ClassDecl ClassDecls
	// ->
	private LinkedList<ast.classs.T> parseClassDecls() {
		LinkedList<ast.classs.T> list = new LinkedList<ast.classs.T>();

		while (current.kind == Kind.TOKEN_CLASS) {
			list.add(parseClassDecl());
		}
		return list;
	
	}

	// MainClass -> class id
	// {
	// public static void main ( String [] id )
	// {
	// Statement
	// }
	// }
	private ast.mainClass.T parseMainClass() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a main class as described by the
		// grammar above.

		String id;
		String args;
		LinkedList<ast.dec.T> locals = null;
		LinkedList<ast.stm.T> stms = null;
		eatToken(Kind.TOKEN_CLASS);
		id = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_LBRACE);
		eatToken(Kind.TOKEN_PUBLIC);
		eatToken(Kind.TOKEN_STATIC);
		eatToken(Kind.TOKEN_VOID);
		eatToken(Kind.TOKEN_MAIN);
		eatToken(Kind.TOKEN_LPAREN);
		eatToken(Kind.TOKEN_STRING);
		eatToken(Kind.TOKEN_LBRACK);
		eatToken(Kind.TOKEN_RBRACK);
		eatToken(Kind.TOKEN_ID);
		args = current.lexeme;
		eatToken(Kind.TOKEN_RPAREN);
		eatToken(Kind.TOKEN_LBRACE);
		locals = parseVarDecls();
		stms = parseStatements();
		eatToken(Kind.TOKEN_RBRACE);
		eatToken(Kind.TOKEN_RBRACE);

		return new ast.mainClass.MainClass(id, args, locals, stms);

	}

	// Program -> MainClass ClassDecl*
	private ast.program.T parseProgram() {
		ast.mainClass.T main = parseMainClass();
		LinkedList<ast.classs.T> classes = parseClassDecls();
		eatToken(Kind.TOKEN_EOF);
		return new ast.program.Program(main, classes);
	}

	public ast.program.T parse() {
		return parseProgram();
	}
}
