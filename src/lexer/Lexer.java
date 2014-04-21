package lexer;

import java.io.InputStream;
import util.Todo;
import lexer.Token.Kind;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class Lexer
{
	String fname; // the input file name to be compiled
	InputStream fstream; // input stream for the above file
	private static int linenum = 1;
	private static int columnnum = 0;
	private int skipcolumn = 0;
	private boolean isprechar = false;
	private int prechar;
	
	Hashtable hashtable = new Hashtable();

	
	public Lexer(String fname, InputStream fstream)
	{
		this.fname = fname;
		this.fstream = fstream;		
		hashtable.put("void", Kind.TOKEN_VOID);
		hashtable.put("true", Kind.TOKEN_TRUE);
		hashtable.put("false", Kind.TOKEN_FALSE);
		hashtable.put("if", Kind.TOKEN_IF);
		hashtable.put("else", Kind.TOKEN_ELSE);
		hashtable.put("while", Kind.TOKEN_WHILE);
		hashtable.put("println", Kind.TOKEN_PRINTLN);
		hashtable.put("System", Kind.TOKEN_SYSTEM);
		hashtable.put("out", Kind.TOKEN_OUT);
		hashtable.put("class", Kind.TOKEN_CLASS);
		hashtable.put("boolean", Kind.TOKEN_BOOLEAN);
		hashtable.put("int", Kind.TOKEN_INT);
		hashtable.put("extends", Kind.TOKEN_EXTENDS);
		hashtable.put("return", Kind.TOKEN_RETURN);
		hashtable.put("static", Kind.TOKEN_STATIC);
		hashtable.put("this", Kind.TOKEN_THIS);
		hashtable.put("String", Kind.TOKEN_STRING);
		hashtable.put("new", Kind.TOKEN_NEW);
		hashtable.put("main", Kind.TOKEN_MAIN);
		hashtable.put("public", Kind.TOKEN_PUBLIC);
		hashtable.put("length", Kind.TOKEN_LENGTH);
	}

	// When called, return the next token (refer to the code "Token.java")
	// from the input stream.
	// Return TOKEN_EOF when reaching the end of the input stream.
	private Token nextTokenInternal() throws Exception
	{
		int c;
		if(isprechar == true ){
			isprechar = false;
			c = prechar;
			columnnum = columnnum + skipcolumn;
			skipcolumn = 0;
		}
		else {
			c = this.fstream.read();
			columnnum = columnnum + 1;
		}
		
		// The value for "lineNum" is now "null",
		// you should modify this to an appropriate
		// line number for the "EOF" token.	
		// skip all kinds of "blanks"		
		while (' ' == c || '\t' == c || '\r' == c || '\n' == c) {
			if (' ' == c)
				columnnum = columnnum + 1;
			else if ('\t' == c)
				columnnum = columnnum + 4;
			else if ('\n' == c)
			{
				linenum = linenum + 1;
				columnnum = 1;
			}
			c = this.fstream.read();
		}
		if (-1 == c)
			return new Token(Kind.TOKEN_EOF, linenum, columnnum);				//1
		
		switch (c) {
		case '/':
			if((c = this.fstream.read()) == '/') {
				do {
					c = this.fstream.read();
				}while ('\n' != c);
			}
			isprechar = true;
			prechar = c;
			return new Token(Kind.TOKEN_ANNOTATION, linenum, columnnum);	
			
		case '+':	
			c = this.fstream.read();
			if ('+' == c)
				return new Token(Kind.TOKEN_OPERATION, linenum, columnnum++,"++");
			else { 
				isprechar = true;
				prechar = c;	
				skipcolumn++;
				return new Token(Kind.TOKEN_ADD, linenum, columnnum);
			}					
		case '-':
			c = this.fstream.read();
			if ('-' == c)
				return new Token(Kind.TOKEN_OPERATION, linenum, columnnum++,"--");
			else { 
				isprechar = true;
				prechar = c;	
				skipcolumn++;
				return new Token(Kind.TOKEN_SUB, linenum, columnnum);
			}		
		case '*':
			return new Token(Kind.TOKEN_TIMES, linenum, columnnum);
		case '=':
			c = this.fstream.read();
			if ('=' == c)
				return new Token(Kind.TOKEN_OPERATION, linenum, columnnum++,"==");
			else { 
				isprechar = true;
				prechar = c;	
				skipcolumn++;
				return new Token(Kind.TOKEN_ASSIGN, linenum, columnnum);
			}
		case ',':
			return new Token(Kind.TOKEN_COMMER, linenum, columnnum);
		case ';':
			return new Token(Kind.TOKEN_SEMI, linenum, columnnum);
		case '.':
			return new Token(Kind.TOKEN_DOT, linenum, columnnum);
		case '{':
			return new Token(Kind.TOKEN_LBRACE, linenum, columnnum);
		case '[':
			return new Token(Kind.TOKEN_LBRACK, linenum, columnnum);
		case '(':
			return new Token(Kind.TOKEN_LPAREN, linenum, columnnum);
		case ')':
			return new Token(Kind.TOKEN_RPAREN, linenum, columnnum);
		case ']':
			return new Token(Kind.TOKEN_RBRACK, linenum, columnnum);
		case '}':
			return new Token(Kind.TOKEN_RBRACE, linenum, columnnum);
		case '!':
			return new Token(Kind.TOKEN_NOT, linenum, columnnum);
		case '&':										
			c = this.fstream.read();
			if ('&' == c)
				return new Token(Kind.TOKEN_AND, linenum, columnnum++,"&");
			else { 
				isprechar = true;
				prechar = c;	
				skipcolumn++;
				return new Token(Kind.TOKEN_OPERATION, linenum, columnnum);
			}
		case '<':
			c = this.fstream.read();
			if(c == '=') 
				return new Token(Kind.TOKEN_LT_EQUAL, linenum, columnnum++);			
			else {
				isprechar = true;
				prechar = c;	
				skipcolumn++;
				return new Token(Kind.TOKEN_LT, linenum, columnnum);	 
			}
		case '>':
			c = this.fstream.read();
			if(c == '=')
				return new Token(Kind.TOKEN_GT_EQUAL, linenum, columnnum++);	
			else {
				isprechar = true;
				prechar = c;
				skipcolumn++;
				return new Token(Kind.TOKEN_GT, linenum, columnnum);	
			}	
		default:	
			if(Character.isDigit(c)){
				String num = "";
				do{
					num+=(char) c;
					c = this.fstream.read();
					skipcolumn++;
				}while(Character.isDigit(c));
				isprechar = true;
				prechar = c;
				return new Token(Kind.TOKEN_NUM, linenum, columnnum, num);			//1				
			}
			else if (Character.isLetter(c) || '_' == c) {
				String str = "";
				do{
					str += (char) c;
					c = this.fstream.read();
					skipcolumn++;
				}while (Character.isLetter(c)|| Character.isDigit(c)|| '_' == c);
				isprechar = true;
				prechar = c;
				if(hashtable.get(str.toString())!= null)
					return new Token((Kind)hashtable.get(str.toString()),linenum,columnnum,str);
				else
					return new Token(Kind.TOKEN_ID, linenum, columnnum, str);			//21+1				
			}
			new Todo();
			return null;
		}
	}
	// Lab 1, exercise 2: supply missing code to
	// lex other kinds of tokens.
	// Hint: think carefully about the basic
	// data structure and algorithms. The code
	// is not that much and may be less than 50 lines. If you
	// find you are writing a lot of code, you
	// are on the wrong way.
	public Token nextToken()
	{
		Token t = null;
		
		try {
			t = this.nextTokenInternal();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if (control.Control.lex)
			System.out.println(t.toString());
		return t;
	}
}
