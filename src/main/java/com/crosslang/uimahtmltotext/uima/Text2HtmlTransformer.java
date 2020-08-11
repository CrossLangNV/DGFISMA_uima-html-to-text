package com.crosslang.uimahtmltotext.uima;

import com.crosslang.sdk.transfer.ae.model.tag.handler.JCasTransformer_ImplBase;
import com.crosslang.uimahtmltotext.uima.type.ValueBetweenTagType;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Text2HtmlTransformer extends JCasTransformer_ImplBase {
    public static final Logger logger = LoggerFactory.getLogger(TokenToTfidfAnnotator.class);

    /*** Example:
    * Hello Test Bo  l  d  World
    * Hello <html>Test <b class='.nice'>Bo <i> l </b> d </i></html> World
    * */
    @Override
    public void process(JCas aInput, JCas aOutput) throws AnalysisEngineProcessException {
        Collection<ValueBetweenTagType> collection = JCasUtil.select(aInput, ValueBetweenTagType.class);
        List<ValueBetweenTagType> list = new ArrayList<>(collection);
        Collections.reverse(list);

        // Text to HTML
        // TODO Use Untangle Tags for more accurate HTML conversion
        for (ValueBetweenTagType vbtt : list) {
            if (!vbtt.getAttributes().isEmpty()) {
                insert(vbtt.getBegin(), getOpeningTag(vbtt.getTagName(), vbtt.getAttributes()));
            } else {
                insert(vbtt.getBegin(), getOpeningTag(vbtt.getTagName()));
            }
            insert(vbtt.getEnd(), getClosingTag(vbtt.getTagName()));
        }
    }

    public static String getOpeningTag(String tagName) {
        return "<"+tagName+">";
    }

    public static String getClosingTag(String tagName) {
        return "</"+tagName+">";
    }

    public static String getOpeningTag(String tagName, String attributes) {
        return "<"+tagName+" "+attributes+">";
    }
}
