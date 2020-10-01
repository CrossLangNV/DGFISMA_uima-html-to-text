package com.crosslang.uimahtmltotext.service;

import org.springframework.web.multipart.MultipartFile;

import com.crosslang.uimahtmltotext.model.HtmlInput;

public interface UimaTextTransferService {
    byte[] textToHtml(HtmlInput input);
    byte[] htmlToText(HtmlInput input);
    byte[] getTypeSystemFile();
	byte[] htmlToText(MultipartFile documentFile);
	byte[] textToHtml(MultipartFile documentFile);
}
