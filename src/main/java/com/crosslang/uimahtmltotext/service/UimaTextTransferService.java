package com.crosslang.uimahtmltotext.service;

import com.crosslang.uimahtmltotext.model.HtmlInput;

public interface UimaTextTransferService {
    String textToHtml(String text);
    byte[] htmlToText(HtmlInput input);
}
