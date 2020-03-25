package com.golfscriptcompiler.tokenizer;

public enum Type {
	STRING, NUMBER, CHAR, NAME, STARTBLOCK, ENDBLOCK, NOMORETOKENS,
	
	SUBBLOCK, ASSIGNMENT //These last three are for use with the parser.
}
