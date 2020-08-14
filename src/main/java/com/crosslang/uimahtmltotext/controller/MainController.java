package com.crosslang.uimahtmltotext.controller;

import com.crosslang.uimahtmltotext.model.HtmlInput;
import com.crosslang.uimahtmltotext.service.UimaTextTransferService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class MainController {
    public static final Logger log = LoggerFactory.getLogger(MainController.class);

    private final UimaTextTransferService uimaTextTransferService;

    public MainController(UimaTextTransferService uimaTextTransferService) {
        this.uimaTextTransferService = uimaTextTransferService;
    }

    @RequestMapping(value = "/html2text", method = RequestMethod.POST, produces = { "application/xml", "text/xml" })
    @ResponseBody
    public byte[] html2text(@RequestBody HtmlInput input) {
        return uimaTextTransferService.htmlToText(input);
    }

    @RequestMapping(value = "/html2text/typesystem", method = RequestMethod.GET, produces = { "application/xml", "text/xml" })
    @ResponseBody
    public byte[] getTypesystem() throws IOException {
        File file = new File("./target/cache/typesystem.xml");
        InputStream in = new FileInputStream(file);
        return IOUtils.toByteArray(in);
    }

    @RequestMapping(value = "/text2html", method = RequestMethod.POST, produces = { "application/xml", "text/xml" })
    @ResponseBody
    public byte[] text2html(@RequestBody HtmlInput input) {
        return uimaTextTransferService.textToHtml(input);
    }
}
