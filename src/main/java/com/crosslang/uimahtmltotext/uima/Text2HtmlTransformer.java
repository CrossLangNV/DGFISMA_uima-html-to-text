package com.crosslang.uimahtmltotext.uima;

import com.crosslang.sdk.transfer.ae.model.tag.handler.JCasTransformer_ImplBase;
import com.crosslang.sdk.types.html.HtmlTag;
import com.crosslang.uimahtmltotext.uima.type.ValueBetweenTagType;
import com.crosslang.uimahtmltotext.utils.HtmlTagUtils;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class transforms Text into Html by reading out the positions of {@link HtmlTag} annotations
 */
public class Text2HtmlTransformer extends JCasTransformer_ImplBase {
    public static final Logger logger = LoggerFactory.getLogger(Text2HtmlTransformer.class);

    @Override
    public void process(JCas aInput, JCas aOutput) {
        Collection<ValueBetweenTagType> collection = JCasUtil.select(aInput, ValueBetweenTagType.class);
        List<ValueBetweenTagType> list = new ArrayList<>(collection);
        Collections.reverse(list);

        for (HtmlTag tag : JCasUtil.select(aInput, HtmlTag.class)) {
            // Open tags
            if (tag.getTagRole().equals("OPENING")) {
                insert(tag.getBegin(), HtmlTagUtils.getOpeningTag(tag));
            }
            // Close tags
            else {
                if (tag.getEnd() < aInput.getDocumentText().length()) {
                    insert(tag.getEnd(), HtmlTagUtils.getClosingTag(tag));
                } else {
                    insert(tag.getEnd()-1, HtmlTagUtils.getClosingTag(tag));
                }
            }
        }
    }




}
