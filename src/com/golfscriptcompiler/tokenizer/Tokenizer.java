package com.golfscriptcompiler.tokenizer;

import java.util.ArrayList;


public class Tokenizer {
	
	private enum State {
		DEFAULT, NAME, NUMBER, SINGLEQUOTESTRING, DOUBLEQUOTESTRING
	};
	
	private String golfScript;
	
	public Tokenizer(String code){
		golfScript = code;
	}
	
	public Token[] tokenize(){
		String[] split = golfScript.split("\\r?\\n"); //Split up the string by looking for newlines.
		ArrayList<Token> tokens = new ArrayList<Token>(); //Prepare the token arraylist.
		stringloop:/*Label so that we can skip iterations of this loop from within a sub-loop*/ for(int lineNum = 0; lineNum < split.length; lineNum++){ //For each line,
			State currentState = State.DEFAULT;
			char[] chars = split[lineNum].toCharArray();
			String tempString = ""; //Variables that need to persist between iterations of the char loop.
			boolean hasSeenStart = false; //See the DOUBLEQUOTESTRING SINGLEQUOTESTRING, and NUMBER cases.
			for(int i = 0; i < chars.length; i++){ //For each character in the string (plus a bit of flexibility -- we don't know if each character maps exactly on to a token)
				char c = chars[i]; //Shorthand.
				switch(currentState){
				case DEFAULT:
					hasSeenStart = false; //Control may have been handed back from something that uses hasSeenStart, which means that we need to set it to false.
					if(c == '#') continue stringloop;
					if(Character.isAlphabetic(c) || c == '_'){ //If this starts off a name,
						currentState = State.NAME;
					}else if(c == '"'){ //If this starts off a double-quote string,
						currentState = State.DOUBLEQUOTESTRING;
					}else if(c == '\''){ //If this starts off a single-quote string,
						currentState = State.SINGLEQUOTESTRING;
					}else if(Character.isDigit(c)){ //Two IF statements for switching to NUMBER, because the criteria are complicated.  This, the first, is self-explanatory.
						currentState = State.NUMBER;
					}else if((c == '+' || c == '-') //If the character COULD start a number,
							&& (i < chars.length -1) //And it's not last in the line of code (this is mainly to avoid array out of bounds errors),
							&& (Character.isDigit(chars[i+1]))){ //And the next character's a number,
							currentState = State.NUMBER;
					}else if(c == ' '){
						//Do nothing.  We don't want space tokens, given that we've dumped the rule about whitespace being like any other variable.
						break;
					}else{
						//We still haven't managed to delegate anything to another case.  Therefore, this must be a single-character token.
						Token t = new Token(Character.toString(c), lineNum);
						if(c == '{') t.type = Type.STARTBLOCK; //Set type.  Some tokens have special types for ease of parsing.
						else if (c == '}') t.type = Type.ENDBLOCK;
						else t.type = Type.CHAR;
						tokens.add(t);
						break; //Go to the next loop iteration.
					}
					//The break above just means that we don't do this if we have already processed the token:
					i--; //Reprocess with the newly changed state.
					break;
				case NAME:
					if(!Character.isLetterOrDigit(c) && c != '_'){ //If this is no longer a NAME,
						currentState = State.DEFAULT;
						i--; //Reprocess.
						//We now have a NAME token, so save it.
						Token t = new Token(tempString, Type.NAME, lineNum);
						tokens.add(t); //Add this token to the token array.
						tempString = ""; //We don't need the tempString any more just yet, so erase it.
					}else if(i == chars.length - 1){ //If we have reached a newline,
						//We now have a NAME token, so save it.
						tempString += c; //The character is a valid NAME -- if it weren't, it would have been truncated at the first IF statement.
						Token t = new Token(tempString, Type.NAME, lineNum);
						tokens.add(t); //Add this token to the token array.
					}else{ //If the character is still part of a NAME,
						tempString += c; //Record it.
					}
					break;
				case NUMBER:
					if(!Character.isDigit(c) && c != '-' && c != '+'){ //If this is no longer a NUMBER,
						currentState = State.DEFAULT; //Return control to the default state for the next char.
						i--; //Reprocess.
						//We now have a NUMBER token, so save it.
						NumberToken t = new NumberToken(tempString, lineNum);
						tokens.add(t); //Add this token to the token array.
						tempString = ""; //We don't need the tempString any more just yet, so erase it.
					}else if(i == chars.length - 1){ //If we have reached a newline,
						//We now have a NUMBER token, so save it.
						tempString += c; //The character is a valid NUMBER -- if it weren't, it would have been truncated at the first IF statement.
						NumberToken t = new NumberToken(tempString, lineNum);
						tokens.add(t); //Add this token to the token array.
						tempString = "";
					}else if((c == '-' || c == '+') && hasSeenStart){ //If this digit is a sign inside a number (invalid),
						currentState = State.DEFAULT; //Give up control.
						i--; //Reprocess in DEFAULT state.
						NumberToken t = new NumberToken(tempString, lineNum);  //We're done here.
						tokens.add(t); //Add this token to the token array.
						tempString = "";
					}else{ //If the character is still part of a NUMBER,
						tempString += c; //Record it.
					}
					hasSeenStart = true;
					break;
				case SINGLEQUOTESTRING:
					//The first character fed to this will be '.  Then, there will be other characters, then a second '.
					if(c == '\'' && hasSeenStart){
						if(chars[i-1] == '\\') { //If this quote has been escaped,
							tempString = tempString.substring(0, tempString.length() - 1); //Cut off the last character in the string, because it will be a \ for the escaped ' character.  The parser doesn't need it.
							tempString += c; //And, add the current quote to the temporary string.
						}else{
						currentState = State.DEFAULT; //Return to the default state.  We're done with the string.
						//This is a raw string, so we don't need to do any pre-processing on the tempString.  
						//We now have a STRING token, so save it.
						Token t = new Token(tempString, Type.STRING, lineNum);
						tokens.add(t); //Add this token to the token array.
						tempString = ""; //We don't need the tempString any more just yet, so erase it.
						}
					}else if(c == '\\' && chars[i+1] != '\'' && chars[i+1] != '\\'){
						tempString += "\\\\";
					}else if(c != '\''){ //Why the !=?  Because we don't want to include it in the token.  It's enough for the parser to know that the token is of type string without having to remove string characters.
						tempString += c;
					}
					hasSeenStart = true; //Do this outside any if statement to avoid getting caught out by empty strings.
					break;
				case DOUBLEQUOTESTRING:
					//The first character fed to this will be ".  Then, there will be other characters, then a second ".
					if(c == '"' && hasSeenStart){
						if(chars[i-1] == '\\') { //If this quote has been escaped,
							tempString = tempString.substring(0, tempString.length() - 1); //Cut off the last character in the string, because it will be a \ for the escaped " character.  The parser doesn't need it.
							tempString += c; //And, add the current quote to the temporary string.
						}else{
						currentState = State.DEFAULT; //Return to the default state.  We're done with the string.
						//We now have a STRING token, so save it.
						Token t = new Token(tempString, Type.STRING, lineNum);
						tokens.add(t); //Add this token to the token array.
						tempString = ""; //We don't need the tempString any more just yet, so erase it.
						}
					}else if(c != '"'){ //Why the !=?  Because we don't want to include quotation marks in the token.  It's enough for the parser to know that the token is of type string without having to remove string characters.
						tempString += c;
					}
					hasSeenStart = true; //Do this outside any if statement to avoid getting caught out by empty strings.
					break;
				}
			}
		}
		tokens.add(new Token("", Type.NOMORETOKENS, -1)); //No more tokens.  This is just to prevent outofbounds exceptions in the parser.
		return tokens.toArray(new Token[1]); //Create an imaginary token array so that the toArray would have a type.
	}
}
