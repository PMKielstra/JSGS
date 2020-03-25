package com.golfscriptcompiler.test;

import java.io.IOException;

import com.golfscriptcompiler.codegenerator.CodeGenerator;
import com.golfscriptcompiler.parser.Assignment;
import com.golfscriptcompiler.parser.Block;
import com.golfscriptcompiler.parser.Parser;
import com.golfscriptcompiler.tokenizer.Token;
import com.golfscriptcompiler.tokenizer.Tokenizer;
import com.golfscriptcompiler.tokenizer.Type;
import com.golfscriptcompiler.frontend.Compiler;

//This class, along with its package, is used for testing the compiler.
//This class itself contains no main method.  It does, however, contain a standard function for running the compiler and displaying as much output as possible.
//The other classes in this package are just wrappers for GolfScript code.  They use this class to compile it.

public class Tester {
	
	public static void test(String testCode, String name){ //This is the method that all those other classes call.  They pass their example code.
		//First, tokenize the code.
		Tokenizer tokenizer = new Tokenizer(testCode);
		Token[] tokens = tokenizer.tokenize();
		System.out.println("TOKENIZED:"); //This program outputs to the console.
		for(Token t : tokens){ //For each token,
			System.out.println(t.content + " (" + capitalize(t.type.toString()) + ")" + " (Line " + (t.line + 1) + ") "); //Display its content, followed by its type in brackets.  The capitalize method is further down this class and is just used for some minimal formatting to make the output look good.
		}
		System.out.println("\nPARSED:");
		Block code = Parser.parse(tokens); //Parse the code.
		printBlocks(code, 0); //Recursively print the parsed code.
		System.out.println("\nCODE:");
		CodeGenerator cg = new CodeGenerator(code, name);
		System.out.println(cg.generateCode());
		System.out.println("\nGENERATED HTML UI");
		System.out.println(Compiler.generateHTML(name));
		try {
			Compiler.compileWholeProgram(testCode, name, System.getProperty("user.home") + "/Desktop");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void printBlocks(Block b, int tabNum){
		String tab = ""; //All this stuff with tab is just for formatting.  Get rid of it and the block code will still display, but it won't be indented.
		if(tabNum != 0){
			for(int i = 0; i < tabNum; i++){
				tab += "	";
			}
		}
		for(Token t : b.tokens){ //For each token in the block,
			if(t.type == Type.SUBBLOCK){ //If this is a sub-block,
				int indexOfSubBlock = Integer.parseInt(t.content); //Get the requisite sub-block.
				String blockLabel = tab + capitalize(t.type.name()) + ":"; //Generate the block label.  If it doesn't have a name, use sub-block, otherwise, use the name.
				System.out.println(blockLabel);
				printBlocks(b.subBlocks[indexOfSubBlock], tabNum + 1); //Recursively print the correct sub-block.
			}else if (t.type == Type.ASSIGNMENT){ //If this is an assignment statement,
				String toPrint = tab + "Assignment to variable: " + t.content; //Print the name of the variable,
				if(((Assignment)t).hasBeenDefined){ //And whether it has been defined.
					toPrint += " (Defined already)" + " (Line " + (t.line + 1) + ") ";
				}else toPrint += " (Not already defined)" + " (Line " + (t.line + 1) + ") ";
				System.out.println(toPrint);
			}else 	System.out.println(tab + t.content + " (" + capitalize(t.type.toString()) + ")" + " (Line " + (t.line + 1) + ") "); //Print the token.  The capitalize is just to make it look good.
		}
		
	}
	
	static String capitalize(String input){
		return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase(); //Capitalize the first letter of the string and make all the other letters lowercase.
	}
}
