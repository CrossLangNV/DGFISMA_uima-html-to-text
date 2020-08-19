package com.crosslang.uimahtmltotext.controller;

import com.crosslang.uimahtmltotext.model.HtmlInput;
import com.crosslang.uimahtmltotext.service.UimaTextTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    public static final Logger log = LoggerFactory.getLogger(MainController.class);

    private final UimaTextTransferService uimaTextTransferService;

    public MainController(UimaTextTransferService uimaTextTransferService) {
        this.uimaTextTransferService = uimaTextTransferService;
    }

    @PostMapping(value = "/html2text", produces = { "application/xml", "text/xml" })
    @ResponseBody
    public byte[] html2text(@RequestBody HtmlInput input) {
        return uimaTextTransferService.htmlToText(input);
    }

    @GetMapping(value = "/html2text/typesystem", produces = { "application/xml", "text/xml" })
    @ResponseBody
    public byte[] getTypesystem() {
        return uimaTextTransferService.getTypeSystemFile();
    }

    @PostMapping(value = "/text2html", produces = { "application/xml", "text/xml" })
    @ResponseBody
    public byte[] text2html(@RequestBody HtmlInput input) {
        return uimaTextTransferService.textToHtml(input);
    }
}
