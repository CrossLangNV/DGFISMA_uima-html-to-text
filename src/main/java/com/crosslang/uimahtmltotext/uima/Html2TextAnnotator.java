package com.crosslang.uimahtmltotext.uima;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.crosslang.sdk.types.html.HtmlTag;
import com.crosslang.sdk.utils.commons.StringManipulator;
import com.crosslang.uimahtmltotext.uima.type.ValueBetweenTagType;
import com.crosslang.uimahtmltotext.utils.HtmlTagUtils;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

/***
 * 
 * Transform html to plaintext
 *
 */
public class Html2TextAnnotator extends JCasAnnotator_ImplBase {

	public static final String TAG_OPENING = "OPENING";
	public static final String TAG_CLOSING = "CLOSING";

	/**
	 * Define the target view to work on.
	 */
	public static final String PARAM_TARGET_VIEW_NAME = "targetViewName";
	@ConfigurationParameter(name = PARAM_TARGET_VIEW_NAME, mandatory = true)
	private String targetViewName;

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		try {
			// Metadata for the XmiWriter
			DocumentMetaData metaData = DocumentMetaData.create(cas);
			metaData.setDocumentId("docId");
			metaData.setDocumentTitle("docTitle");
			metaData.setCollectionId("colId");
			metaData.addToIndexes();

			StringManipulator sm = new StringManipulator(cas.getDocumentText());

			for (HtmlTag tag : JCasUtil.select(cas, HtmlTag.class)) {
				sm.delete(tag.getBegin(), tag.getEnd());
			}

			JCas textView = cas.createView(targetViewName);
			textView.setDocumentText(sm.getCurrentString());

			// Add open/close tags to list
			List<HtmlTag> htmlTagOpened = HtmlTagUtils.getTagsByState(cas, TAG_OPENING);
			List<HtmlTag> htmlTagClosed = HtmlTagUtils.getTagsByState(cas, TAG_CLOSING);

			for (HtmlTag e : htmlTagClosed) {
				// Find the latest opened tag of the same type to match the close with
				Optional<HtmlTag> opt = HtmlTagUtils.getLastObjectByTagType(htmlTagOpened, e);

				// Insert operations
				if (opt.isPresent()) {
					HtmlTag lastTag = opt.get();
					int beginPos = lastTag.getEnd();
					int endPos = e.getBegin();

					ValueBetweenTagType vbtt = new ValueBetweenTagType(textView);
					vbtt.setBegin(sm.getNewOffsetPosition(beginPos));
					vbtt.setEnd(sm.getNewOffsetPosition(endPos));
					vbtt.setTagName(e.getTagName());

					StringBuilder sb = getHtmlAttributes(lastTag);

					vbtt.setAttributes(sb.toString());
					vbtt.addToIndexes();

					htmlTagOpened.remove(lastTag);
				}
			}
			// FIXME: should we remove HtmlTag annotations from the cas ?
			// does the NLP pipeline require them ?
//			removeHtmlTagsFromCas(cas);

		} catch (CASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private StringBuilder getHtmlAttributes(HtmlTag lastTag) {
		Map<String, String> tagAttributesMap = new LinkedMap<>();

		// Get attributes
		StringBuilder sb = new StringBuilder();

		if (lastTag.getAttributes() != null) {
			for (int i = 0; i < lastTag.getAttributes().size(); i++) {
				tagAttributesMap.put(lastTag.getAttributes(i).getName(), lastTag.getAttributes(i).getValue());
				sb.append(lastTag.getAttributes(i).getName()).append("='").append(lastTag.getAttributes(i).getValue())
						.append("'");
			}
		}
		return sb;
	}

	public void removeHtmlTagsFromCas(JCas cas) {
		for (HtmlTag s : JCasUtil.select(cas, HtmlTag.class)) {
			s.removeFromIndexes();
		}
	}
}