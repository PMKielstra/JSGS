package com.golfscriptcompiler.test;

import java.util.Scanner;

public class DynamicTest {

	public static void main(String[] args) {
		try(Scanner s = new Scanner(System.in)){
			System.out.println("Enter program name:");
			String name = s.nextLine();
			System.out.println("Enter program:");
			String code = s.nextLine();
			Tester.test(code, name);
		}
	}

}
