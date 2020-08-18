package com.crosslang.uimahtmltotext.service;

import com.crosslang.uimahtmltotext.model.HtmlInput;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.greaterThan;

@SpringBootTest
@AutoConfigureMockMvc
class UimaTextTransferServiceImplTest {
    public static final Logger logger = LoggerFactory.getLogger(UimaTextTransferServiceImplTest.class);

    @Autowired
    private UimaTextTransferService uimaTextTransferService;

    public static String input_html = "<html><p>Hello <b>World</b></p></html>";

    @Test
    @DisplayName("Unit test: Typesystem")
    void unitTestTypesystem() throws Exception {
        byte[] typeSystemFile = uimaTextTransferService.getTypeSystemFile();
        MatcherAssert.assertThat(typeSystemFile.length, greaterThan(5));
    }

    @Test
    @DisplayName("Unit test: Html2Text")
    void htmlToTextTest() {
        byte[] result = uimaTextTransferService.htmlToText(new HtmlInput(input_html));
        Assertions.assertNotEquals(0, result.length);
    }

    @Test
    @DisplayName("Unit test: Text2Html")
    void textToHtmlTest() {
        byte[] bytes = uimaTextTransferService.htmlToText(new HtmlInput(input_html));
        String s = new String(bytes, StandardCharsets.UTF_8);

        byte[] result = uimaTextTransferService.textToHtml(new HtmlInput(s));
        Assertions.assertNotEquals(0, result.length);
    }
}