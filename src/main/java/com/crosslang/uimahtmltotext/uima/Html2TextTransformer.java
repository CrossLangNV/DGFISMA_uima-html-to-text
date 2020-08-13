package com.crosslang.uimahtmltotext.uima;

import com.crosslang.sdk.transfer.ae.model.tag.handler.JCasTransformer_ImplBase;
import com.crosslang.sdk.types.html.HtmlTag;
import com.crosslang.uimahtmltotext.uima.type.ValueBetweenTagType;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class will transform Html into Text
 * The metadata for @{@link DocumentMetaData} is filled in because it is necessary for the XMI.
 * Basically, two Lists will be populated with Open and Close HTML tags
 * We will iterate these Lists, find their matching tags, and check if they're not closed yet, in case a match has been
 * found, we will add them to the closed List. Avoiding duplicates.
 *
 * A @{@link ValueBetweenTagType} object will be populated, also adding the HTML attributes
 *
 * Finally, we remove the HTML tags from the view with the delete() operation
 */
public class Html2TextTransformer extends JCasTransformer_ImplBase {
    public static final Logger logger = LoggerFactory.getLogger(Html2TextTransformer.class);
    public static final String TAG_OPENING = "OPENING";

    @Override
    public void process(JCas aInput, JCas aOutput) {
        // Metadata for the XmiWriter
        DocumentMetaData metaData = DocumentMetaData.create(aInput);
        metaData.setDocumentId("docId");
        metaData.setDocumentTitle("docTitle");
        metaData.setCollectionId("colId");
        metaData.addToIndexes();

        // Add open/close tags to list
        List<HtmlTag> htmlTagOpeningList = new ArrayList<>();
        List<HtmlTag> htmlTagClosingList = new ArrayList<>();
        for (HtmlTag s : JCasUtil.select(aInput, HtmlTag.class)) {
            if (s.getTagRole().equals(TAG_OPENING)) {
                htmlTagOpeningList.add(s);
            } else {
                htmlTagClosingList.add(s);
            }
        }

        List<HtmlTag> closed = new ArrayList<>();

        for (HtmlTag s : htmlTagOpeningList) {
            for (HtmlTag e : htmlTagClosingList) {
                if (e.getTagName().equals(s.getTagName()) && s.getBegin() < e.getEnd() && !closed.contains(s)) {

                    // We set these objects here, to pass them to the PARAM_TYPES_TO_COPY
                    ValueBetweenTagType vbtt = new ValueBetweenTagType(aInput);
                    vbtt.setBegin(s.getEnd());
                    vbtt.setEnd(e.getBegin());
                    vbtt.setTagName(e.getTagName());

                    Map<String, String> tagAttributesMap = new LinkedMap<>();

                    // Get attributes
                    StringBuilder sb = new StringBuilder();
                    for (int i=0; i < s.getAttributes().size(); i++) {
                        tagAttributesMap.put(s.getAttributes(i).getName(), s.getAttributes(i).getValue());
                        sb.append(s.getAttributes(i).getName()).append("='").append(s.getAttributes(i).getValue()).append("' ");
                    }

                    vbtt.setAttributes(sb.toString());
                    vbtt.addToIndexes();

                    closed.add(s);
                }
            }
        }

        removeHtmlTagsFromCas(aInput);

    }

    public void removeHtmlTagsFromCas(JCas cas) {
        for (HtmlTag s : JCasUtil.select(cas, HtmlTag.class)) {
            delete(s.getBegin(), s.getEnd());
        }
    }

    public String getDocSubstring(JCas cas, int x, int y) {
        return cas.getDocumentText().substring(x, y);
    }
}
