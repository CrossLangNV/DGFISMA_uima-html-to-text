package com.crosslang.uimahtmltotext.uima;

import com.crosslang.sdk.types.html.HtmlTag;
import com.crosslang.uimahtmltotext.uima.type.ValueBetweenTagType;
import com.crosslang.uimahtmltotext.utils.HtmlTagUtils;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public static final String TAG_CLOSING = "CLOSING";

    @Override
    public void process(JCas aInput, JCas aOutput) {
        logger.info("Html2Text Transformer is processing... Document bytes: {}", aInput.getDocumentText().length());
        // Metadata for the XmiWriter
        DocumentMetaData metaData = DocumentMetaData.create(aInput);
        metaData.setDocumentId("docId");
        metaData.setDocumentTitle("docTitle");
        metaData.setCollectionId("colId");
        metaData.addToIndexes();

        // Add open/close tags to list
        List<HtmlTag> htmlTagOpened = HtmlTagUtils.getTagsByState(aInput, TAG_OPENING);
        List<HtmlTag> htmlTagClosed = HtmlTagUtils.getTagsByState(aInput, TAG_CLOSING);

        for (HtmlTag e : htmlTagClosed) {
            // Find the latest opened tag of the same type to match the close with
            Optional<HtmlTag> opt = HtmlTagUtils.getLastObjectByTagType(htmlTagOpened, e);

            // Insert operations
            if (opt.isPresent()) {
                HtmlTag lastTag = opt.get();
                int beginPos = lastTag.getEnd();
                int endPos = e.getBegin();

                ValueBetweenTagType vbtt = new ValueBetweenTagType(aInput);
                vbtt.setBegin(beginPos);
                vbtt.setEnd(endPos);
                vbtt.setTagName(e.getTagName());

                Map<String, String> tagAttributesMap = new LinkedMap<>();

                // Get attributes
                StringBuilder sb = new StringBuilder();

                if (lastTag.getAttributes() != null) {
                    for (int i=0; i < lastTag.getAttributes().size(); i++) {
                        tagAttributesMap.put(lastTag.getAttributes(i).getName(), lastTag.getAttributes(i).getValue());
                        sb.append(lastTag.getAttributes(i).getName()).append("='").append(lastTag.getAttributes(i)
                                .getValue()).append("'");
                    }
                }

                vbtt.setAttributes(sb.toString());
                vbtt.addToIndexes();

                htmlTagOpened.remove(lastTag);
            }
        }

        removeHtmlTagsFromCas(aInput);
        logger.info("Html2Text Transformer is DONE");
    }

    public void removeHtmlTagsFromCas(JCas cas) {
        for (HtmlTag s : JCasUtil.select(cas, HtmlTag.class)) {
        	logger.info("Removed html tag: " + s.getTagName());
            delete(s.getBegin(), s.getEnd());
        }
    }
}
