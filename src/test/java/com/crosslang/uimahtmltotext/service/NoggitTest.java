package com.crosslang.uimahtmltotext.service;

import org.junit.jupiter.api.Test;

class NoggitTest {

	@Test
	void test() {
		
		char str ='<';
		int ch = str;

		System.out.println(isUnquotedStringStart(ch));
		if (!isUnquotedStringStart(ch)) {
			System.out.println("error");
		}
	}

	private static boolean isUnquotedStringStart(int ch) {
		return Character.isJavaIdentifierStart(ch);
	}
}
