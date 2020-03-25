package com.golfscriptcompiler.parser;

import com.golfscriptcompiler.tokenizer.Token;
import com.golfscriptcompiler.tokenizer.Type;

public class Assignment extends Token{
	public Assignment(String c, boolean b, int line) {
		super(c, line);
		hasBeenDefined = b;
		type = Type.ASSIGNMENT;
	}
	public boolean hasBeenDefined; //Do we have to create the variable here or has it been created already?
}
