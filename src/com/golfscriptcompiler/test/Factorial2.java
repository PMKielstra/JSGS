package com.golfscriptcompiler.test;

public class Factorial2 {
	public static void main(String[] args) {
		
		//For details of how the tester works, see the Tester.java class.
		
		Tester.test("{1+,1>{*}*}:factorial; #Other method of calculating factorial\r\n"
				+ "5 factorial", "Factorial 2"); //Push 5 and calculate factorial.
	}
}
