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
import java.util.List;

/**
 * This class transforms Text into Html
 * It reads from the @{@link ValueBetweenTagType} objects how to compose back to HTML
 */
public class Text2HtmlTransformer extends JCasTransformer_ImplBase {
    public static final Logger logger = LoggerFactory.getLogger(Text2HtmlTransformer.class);

    @Override
    public void process(JCas aInput, JCas aOutput) throws AnalysisEngineProcessException {
        Collection<ValueBetweenTagType> collection = JCasUtil.select(aInput, ValueBetweenTagType.class);
        List<ValueBetweenTagType> list = new ArrayList<>(collection);

        // Text to HTML
        for (ValueBetweenTagType vbtt : list) {
            // Insert opening tag
            insert(vbtt.getBegin(), getOpeningTag(vbtt));

            // Insert closing tag
            if (vbtt.getEnd() == aInput.getDocumentText().length()) {

                // Workaround: Insert spaces according to how long the closing tag is and replace that by the tag
                String lastChar = aInput.getDocumentText().substring(aInput.getDocumentText().length()-1);
                replace(aInput.getDocumentText().length()-1, aInput.getDocumentText().length(), lastChar +
                        getClosingTag(vbtt), null, null, 1);
            } else {
                insert(vbtt.getEnd(), getClosingTag(vbtt));
            }
        }
    }

    public static String getOpeningTag(ValueBetweenTagType vbtt) {
        if (!vbtt.getAttributes().isEmpty()) {
            return "<"+vbtt.getTagName()+" "+vbtt.getAttributes()+">";
        } else {
            return "<"+vbtt.getTagName()+">";
        }
    }

    public static String getClosingTag(ValueBetweenTagType vbtt) {
        return "</"+vbtt.getTagName()+">";
    }
}