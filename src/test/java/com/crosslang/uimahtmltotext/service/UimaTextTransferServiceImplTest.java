package com.crosslang.uimahtmltotext.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UimaTextTransferServiceImplTest {

    private final UimaTextTransferService uimaTextTransferService;

    UimaTextTransferServiceImplTest(UimaTextTransferService uimaTextTransferService) {
        this.uimaTextTransferService = uimaTextTransferService;
    }

    @Test
    public void textToHtml() {
        String result = uimaTextTransferService.textToHtml("Test");
        Assertions.assertNotEquals(result, null);
    }

    @Test
    public void htmlToText() {
        String result = uimaTextTransferService.htmlToText("Test");
        Assertions.assertNotEquals(result, null);
    }
}