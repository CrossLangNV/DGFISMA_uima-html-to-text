package com.crosslang.uimahtmltotext.uima;

import com.crosslang.sdk.transfer.ae.model.tag.handler.JCasTransformer_ImplBase;
import com.crosslang.sdk.types.html.HtmlTag;
import com.crosslang.uimahtmltotext.uima.type.ValueBetweenTagType;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Html2TextTransformer extends JCasTransformer_ImplBase {
    public static final Logger logger = LoggerFactory.getLogger(Html2TextTransformer.class);
    public static final String TAG_OPENING = "OPENING";

    @Override
    public void process(JCas aInput, JCas aOutput) throws AnalysisEngineProcessException {
        List<HtmlTag> htmlTagOpeningList = new ArrayList<>();
        List<HtmlTag> htmlTagClosingList = new ArrayList<>();
        LinkedHashMap<HtmlTag, HtmlTag> matchingTags = new LinkedHashMap<>();

        // Metadata for the XmiWriter
        DocumentMetaData metaData = DocumentMetaData.create(aInput);
        metaData.setDocumentId("docId");
        metaData.setDocumentTitle("docTitle");
        metaData.setCollectionId("colId");

        metaData.addToIndexes();

        for (HtmlTag s : JCasUtil.select(aInput, HtmlTag.class)) {
            if (s.getTagRole().equals(TAG_OPENING)) {
                htmlTagOpeningList.add(s);
            } else {
                htmlTagClosingList.add(s);
            }
        }

        for (HtmlTag s : htmlTagOpeningList) {
            for (HtmlTag e : htmlTagClosingList) {
                if (e.getTagName().equals(s.getTagName())) {
                    matchingTags.put(s, e);

                    // Meegeven aan typestocopy
                    ValueBetweenTagType vbtt = new ValueBetweenTagType(aInput);
                    vbtt.setBegin(s.getEnd());
                    vbtt.setEnd(e.getBegin());
                    vbtt.setText(getDocSubstring(aInput, s.getEnd(), e.getBegin()));
                    vbtt.setTagName(e.getTagName());
                    vbtt.setAttributes(s.getAttributes());
                    vbtt.addToIndexes();
                }
            }
        }

        for (HtmlTag s : JCasUtil.select(aInput, HtmlTag.class)) {
            delete(s.getBegin(), s.getEnd());
        }
    }

    public String getDocSubstring(JCas cas, int x, int y) {
        return cas.getDocumentText().substring(x, y);
    }

}
