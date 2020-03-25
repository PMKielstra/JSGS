package com.golfscriptcompiler.codegenerator;

import java.util.HashSet;
import java.util.Set;

import com.golfscriptcompiler.builtins.BuiltinManager;
import com.golfscriptcompiler.parser.Assignment;
import com.golfscriptcompiler.parser.Block;
import com.golfscriptcompiler.tokenizer.Token;

public class CodeGenerator {
	
	private Block mainBlock;
	
	private String name;
	
	private Set<Token> builtins = new HashSet<Token>();
	
	public CodeGenerator(Block b, String programName){
		mainBlock = b;
		name = programName;		
	}
	
	public String generateCode(){
		String main = makeCode(mainBlock, 1);
		StringBuilder header = new StringBuilder("var programName = \"" + name + "\";").append("\r\n");
		for(Token t : builtins){
			header.append(BuiltinManager.getBuiltinHeader(t)).append("\r\n");
		}
		return header.toString() + "function main(){\r\n" + main + "}";
	}
	
	private String makeCode(Block b, int tabNum){
		String tab = ""; //All this stuff with tab is just for formatting.  Get rid of it and the block code will still display, but it won't be indented.
		if(tabNum != 0){
			for(int i = 0; i < tabNum; i++){
				tab += "	";
			}
		}
		StringBuilder mainCode = new StringBuilder("");
		String end = "\r\n";
		for(Token t : b.tokens){
			if(BuiltinManager.isBuiltin(t)){
				if(!contains(t)) builtins.add(new Token(t.content, t.type, t.line));
				t.content = BuiltinManager.getBuiltinName(t);
			}
			mainCode.append(tab);
			switch(t.type){
			case SUBBLOCK:
				int subBlockIndex = Integer.parseInt(t.content);
				mainCode.append("stack.push(function(){\r\n" + makeCode(b.subBlocks[subBlockIndex], tabNum + 1) + tab + "});").append(end);
				break;
			case ASSIGNMENT:
				Assignment a = (Assignment) t;
				if(a.hasBeenDefined || BuiltinManager.isBuiltin(t)){
					mainCode.append(a.content + " = stack.pop();");
				}else{
					mainCode.append("var " + a.content + " = stack.pop();");
				}
				mainCode.append(end);
				mainCode.append(tab).append("stack.push(" + a.content + ");").append(end);
				break;
			case STRING:
				mainCode.append("stack.push(\"" + t.content + "\");").append(end);
				break;
			case NUMBER:
				mainCode.append("stack.push(" + t.content + ");").append(end);
				break;
			default:
				mainCode.append("handle(" + t.content + ");").append(end);
			}
		}
		return mainCode.toString();
	}
	
	
	private boolean contains(Token t){
		for(Token token : builtins){
			if(token.content.equals(t.content) && token.type.equals(t.type)) return true;
		}
		return false;
	}
}
