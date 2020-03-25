package com.golfscriptcompiler.parser;

import com.golfscriptcompiler.tokenizer.Token;

//This is a recursive wrapper class, containing a token array and a block array.

public class Block{
	public Token[] tokens;
	public Block[] subBlocks;
}
