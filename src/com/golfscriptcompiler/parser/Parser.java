package com.golfscriptcompiler.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.golfscriptcompiler.tokenizer.Token;
import com.golfscriptcompiler.tokenizer.Type;

public class Parser {
	
	private static Token[] tokens;
	
	public static Block parse(Token[] t){
		//Class-scope variable used for recursive descent.
		tokens = t;
		//The entire program is treated as a block of code.
		return parseBlock(0, null, State.OTHER);
	}
	
	//Where the parser is.
	private static int masterIndex;
	
	private enum State{
		BLOCK, OTHER
	}
	
	//We need the OTHER state because otherwise a program that starts "}" or a program that starts "]" would be valid.
	
		
	private static State state = State.OTHER;

	
	static Block parseBlock(int index, Set<String> vars, State s){
		//Used for keeping track of what variables we have defined.  We recursively pass the variables down block levels when we parse so that global-scope variables aren't redefined.
		Set<String> declaredVariables;
		if(vars != null){
			declaredVariables = vars;
		}else declaredVariables = new HashSet<String>();
		//Create a base block.
		Block b = new Block(); 
		//ArrayLists for tokens and subblocks.
		ArrayList<Token> blockTokens = new ArrayList<Token>();
		ArrayList<Block> subBlocks = new ArrayList<Block>();
		//While we still have tokens left,
		loop: /*Label for breaking*/ for(int i = index; tokens[i].type != Type.NOMORETOKENS; i++){
			switch(tokens[i].type){
			//Beginning of block
			case STARTBLOCK:
				State tempState = state;
				state = State.BLOCK;
				//Don't record this token, but set the master index to the start of the block proper.
				masterIndex = i + 1;
				//Recursively parse the master block.
				Block block = parseBlock(masterIndex, declaredVariables, tempState);
				//The parseBlock will have changed the master index.
				i = masterIndex;
				//Add the resultant block to the subBlocks arrayList.
				subBlocks.add(block);
				//Add a pointer token to the token array.
				//The minus one works like this: at the first subBlock, the length is 1, and the index that we need is 0.  At the second, the length is 2, and the index that we need is one, and so on.
				Token t = new Token(Integer.toString(subBlocks.size()-1), Type.SUBBLOCK, tokens[i].line);
				blockTokens.add(t);
				break;
			//End of block	
			case ENDBLOCK:
					if(state != State.BLOCK){
						throw new BadParseException("Error at line " + (tokens[i].line + 1) + ": detected } with no matching {.");
					}else{
						state = s;
						//Make sure that we don't reprocess this block.
						masterIndex = i;
						//Don't go on to try to process other blocks.
						break loop;
					}
					//No break command because it would be unreachable.
			//Char or variable assignment
			case CHAR:
				//If we have a variable assignment,
				if(tokens[i].content.equals(":")){
						//Have we defined the variable?
						boolean hasBeenDefined = declaredVariables.contains(tokens[i+1].content);
						if(!hasBeenDefined) declaredVariables.add(tokens[i+1].content);
						blockTokens.add(new Assignment(tokens[i+1].content, hasBeenDefined, tokens[i].line));
						//Don't process the token at i+1 because it's the name of the variable that we've just processed.
						i++;
						break;
				}
			//Anything else
			default:
				blockTokens.add(tokens[i]);
			}
		}
		//We use a new Token array to pass a type to the toArray method.
		b.tokens = blockTokens.toArray(new Token[1]);
		b.subBlocks = subBlocks.toArray(new Block[1]);
		return b;
	}
}
