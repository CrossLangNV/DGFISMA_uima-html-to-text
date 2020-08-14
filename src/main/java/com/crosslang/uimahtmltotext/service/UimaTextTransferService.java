package com.crosslang.uimahtmltotext.service;

import com.crosslang.uimahtmltotext.model.HtmlInput;

public interface UimaTextTransferService {
    byte[] textToHtml(HtmlInput input);
    byte[] htmlToText(HtmlInput input);
}
