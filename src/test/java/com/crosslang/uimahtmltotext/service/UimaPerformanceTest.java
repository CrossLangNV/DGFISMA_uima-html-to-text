package com.crosslang.uimahtmltotext.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.crosslang.uimahtmltotext.model.HtmlInput;

@SpringBootTest
@AutoConfigureMockMvc
class UimaPerformanceTest {
	public static final Logger logger = LoggerFactory.getLogger(UimaPerformanceTest.class);

	@Autowired
	private UimaTextTransferService uimaTextTransferService;

	@Test
	@DisplayName("Unit test: Html2Text")
	void htmlToTextTest() {
		String input_html = readFile("src/test/resources/eurlex_files/sample_large.html", StandardCharsets.UTF_8);
		byte[] result = uimaTextTransferService.htmlToText(new HtmlInput(input_html));
		Assertions.assertNotEquals(0, result.length);
	}

	static String readFile(String path, Charset encoding) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(path));
			return new String(encoded, encoding);
		} catch (Exception ex) {
			return "";
		}

	}
}