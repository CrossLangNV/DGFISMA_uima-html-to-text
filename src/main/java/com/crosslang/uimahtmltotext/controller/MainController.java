package com.crosslang.uimahtmltotext.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.crosslang.uimahtmltotext.model.HtmlInput;
import com.crosslang.uimahtmltotext.service.UimaTextTransferService;

@Controller
public class MainController {
	public static final Logger logger = LoggerFactory.getLogger(MainController.class);

	private final UimaTextTransferService uimaTextTransferService;

	public MainController(UimaTextTransferService uimaTextTransferService) {
		this.uimaTextTransferService = uimaTextTransferService;
	}

	@PostMapping(value = "/html2text", produces = { "application/xml", "text/xml" })
	@ResponseBody
	public byte[] html2text(@RequestBody HtmlInput input) {
		return uimaTextTransferService.htmlToText(input);
	}

	@PostMapping(value = "/html2textdoc", produces = { "application/xml", "text/xml" })
	@ResponseBody
	public byte[] html2textDoc(@RequestParam(value = "document") MultipartFile documentFile) {
		long startTime = System.nanoTime();
		byte[] bytes = uimaTextTransferService.htmlToText(documentFile);
		long endTime = System.nanoTime();
		logger.info("Took: {}ms", (endTime - startTime) / 1000000);
		return bytes;
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

	@PostMapping(value = "/text2htmldoc", produces = { "application/xml", "text/xml" })
	@ResponseBody
	public byte[] text2htmlDoc(@RequestParam(value = "document") MultipartFile documentFile) {
		return uimaTextTransferService.textToHtml(documentFile);
	}
}
