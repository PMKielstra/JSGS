package com.golfscriptcompiler.frontend;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.golfscriptcompiler.codegenerator.CodeGenerator;
import com.golfscriptcompiler.parser.Block;
import com.golfscriptcompiler.parser.Parser;
import com.golfscriptcompiler.tokenizer.Token;
import com.golfscriptcompiler.tokenizer.Tokenizer;

public class Compiler {
	
	/**
	 * A helper class which combines the tokenizer, parser, and code generator all in one.
	 * */
	
	private static String currentName;
	
	public static String compileCode(String code, String programName){
		currentName = programName;
		Tokenizer t = new Tokenizer(code);
		Token[] tokens = t.tokenize();
		Block block = Parser.parse(tokens);
		CodeGenerator cg = new CodeGenerator(block, programName);
		return cg.generateCode();
	}
	
	public static String compileCode(String code){
		return compileCode(code, currentName);
	}
	
	public static String generateHTML(){
		return generateHTML(currentName);
	}
	
	
	//These contain the code from index.html up to the point where it needs the program name, and then the code from index.html from directly after that point to the end.
	private static String firstHTML = "<!DOCTYPE html>\n<html>\n  <head>\n    <meta content=\"text/html; charset=windows-1252\" http-equiv=\"content-type\">\n    <title>JSGS 0.1b1</title>\n    <style type=\"text/css\">\n      body{\n        padding:5px;\n      }\n      span#title{\n        text-decoration:underline;\n        font-weight:bold;\n      }\n      span#progressReadout{\n        font-style:italic;\n      }\n      span#output{\n        background-color:#e8e8e8;\n        border:1px solid black;\n        padding:5px;\n      }\n      button{\n        margin-bottom:20px;\n      }\n    </style>\n    <script type=\"text/javascript\" src=\"";
	private static String secondHTML = ".js\"></script>\n    <!--The compiler changes this src element when it generates this page from this template.-->\n    <script type=\"text/javascript\">\n      //This has a bunch of code in it that is always used by all programs.  If anything needed explicit DOM access, it would be here too.\n      var stack = new Array(); //Prepare the stack.\n      var outputChanged = false; //Has any output happened?\n      function handle(x){\n        if(typeof x == \"function\"){\n          x();\n        }else{\n          stack.push(x);\n        }\n      }\n      function print(){\n        var output = document.getElementById(\"output\"); //Get output.\n        if(outputChanged){\n          output.innerHTML += stack.pop(); //If the output has previously been changed, add the top of the stack to it.\n        }else{\n          output.innerHTML = stack.pop(); //Otherwise (i.e. if the output is still saying \'no output yet\'), replace this with the top of the stack.\n        }\n        output.innerHTML += \"<br>\";\n        outputChanged = true; //The output has now been changed.\n      }\n      function run(){\n        //Prepare input.\n        eval(\"stack.push(\" + document.getElementById(\"input\").value + \");\");\n        //Run program.\n        main();\n        //Print stack.\n        stack.forEach(function(e){\n          print();\n        });\n        //Reset the stack so as not to cause errors.\n        stack = null;\n        stack = new Array();\n      }\n    </script>\n  </head>\n  <body>\n    <div id=\"box\"><span id=\"title\">JSGS 0.1b1</span><br>\n      <span id=\"progressReadout\">Program loading...</span><br>\n      <textarea id=\"input\" placeholder=\"Program input\"></textarea> <br>\n      <button onclick=\"run()\" disabled=\"disabled\" id=\"button\">Run</button><br>\n      <span id=\"output\">No output yet. Please run program.</span></div>\n    <script type=\"text/javascript\">\n      var readout = document.getElementById(\"progressReadout\");\n      readout.innerHTML = \"Program: \" + programName;\n      document.getElementById(\"button\").disabled = false;\n    </script>\n  </body>\n</html>";
	
	public static String generateHTML(String programName){
		currentName = programName;
		return firstHTML + toCamelCase(programName) + secondHTML;
	}
	
	public static void compileWholeProgram(String code, String programName, String filepath) throws IOException{
		String JS = compileCode(code, programName);
		String HTML = generateHTML(programName);
		//Create directories.
		(new File(filepath + "/" + programName + "/")).mkdirs();
		//Try-with-resources.  Requires Java 7 or later.
		try(PrintWriter html = new PrintWriter(filepath + "/" + programName + "/index.html"); PrintWriter js = new PrintWriter(filepath + "/" + programName + "/" + toCamelCase(programName) + ".js")){
			html.print(HTML);
			js.print(JS);
			html.flush();
			js.flush();
			//Close the streams just in case the try-with-resources doesn't do its thing.
			html.close();
			js.close();
		}
	}
	
	private static String toCamelCase(String s){
		String[] parts = s.split(" ");
		String camel = parts[0].toLowerCase();
		for(int i = 1; i < parts.length; i++){
			camel += parts[i].substring(0, 1).toUpperCase();
			camel += parts[i].substring(1).toLowerCase();
		}
		return camel;
	}
	
}
