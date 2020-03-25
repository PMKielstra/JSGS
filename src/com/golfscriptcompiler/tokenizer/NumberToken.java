package com.golfscriptcompiler.tokenizer;

public class NumberToken extends Token { //Most tokens are strings of some sort, so the Token base class will do for them.  Other tokens, however, are integers.  If we didn't store these in a special class, then +1, 1, and 001 would have different values.
//All the numberToken does differently is that (1) it sets its type to NUMBER automatically and (2) it simplifies integers so that 001 and 1 are evaluated to the same string.
	public NumberToken(String c, int line){
		super(c, Type.NUMBER, line);
		content = Integer.toString(Integer.parseInt(c)); //Simplify.
	}

}
