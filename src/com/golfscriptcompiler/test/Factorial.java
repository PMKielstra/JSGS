package com.golfscriptcompiler.test;


public class Factorial {

	public static void main(String[] args) {
		
		//For details of how the tester works, see the Tester.java class.
		
		Tester.test("{:x; #Factorial function \r\n" + //Factorial function.
				"0:i\r\n" +
				";1\r\n" +
				"{i x<}{i -1+:i *}while\r\n" +
				"}:calculate_factorial;\r\n" +
				"'The factorial\\'s value (Calculated on #{Date()}) is: '\r\n" + //Push string to stack.
				"print;\r\n" + //Print string and pop it from stack.
				"'\\n'\r\n" + //Push string to stack
				"print;\r\n" + //Print string and pop it from stack
				"[5 {[1 {\"A\"} 3]} \"A\"];\r\n"+ //Push and pop an array to show off the parser's understanding of arrays.
				"5 calculate_factorial\r\n" +
				"print;", "Factorial"); //Push 5 and calculate factorial.
	}

}
