package util;

import lexer.Token;
import lexer.Token.Kind;

public class Todo
{
  public Todo()
  {
    System.out.println("You should add your code here:\n");
    throw new java.lang.Error ();
  }
  
  Token getToken(){
	  
	  return new Token(Kind.TOKEN_ADD,1,null);
  }
}
