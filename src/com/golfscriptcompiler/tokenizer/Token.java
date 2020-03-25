package com.golfscriptcompiler.tokenizer;

public class Token {
	public Type type;
	public String content;
	public int line;
	public Token(String c, Type t, int l){
		type = t;
		content = c;
		line = l;
	}
	public Token(String c, int l){
		content = c;
		line = l;
	}
}