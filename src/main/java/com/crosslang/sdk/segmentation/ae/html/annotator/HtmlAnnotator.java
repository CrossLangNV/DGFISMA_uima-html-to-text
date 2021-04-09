package com.crosslang.sdk.segmentation.ae.html.annotator;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import com.crosslang.sdk.types.html.HtmlTag;
import com.crosslang.sdk.types.html.TagAttribute;
import com.crosslang.sdk.types.html.TagRole;

import net.htmlparser.jericho.EndTagType;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagType;
import net.htmlparser.jericho.Tag;

/**
 * This annotator annotates HTML-tags that occur in the CAS document text.
 * 
 * <p>
 * Currently only supports opening, closing and empty HTML-tags (no
 * HTML-comments and other special tags).
 * 
 * <p>
 * Based upon http://sujitpal.blogspot.be/2011/04/annotating-text-in-html
 * -with-uima-and.html
 *
 * @author annabardadym
 */
public class HtmlAnnotator extends JCasAnnotator_ImplBase {

	public static final String PARAM_TAG_NAMES = "tagNames";
	@ConfigurationParameter(name = PARAM_TAG_NAMES, mandatory = false, defaultValue = {})
	protected String[] tagNames;

	public static final String PARAM_WRITE_ATTRIBUTES = "writeAttributes";
	@ConfigurationParameter(name = PARAM_WRITE_ATTRIBUTES, mandatory = false, defaultValue = "true")
	protected boolean writeAttributes;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		String documentText = jcas.getDocumentText();
		Source source = new Source(documentText);
		for (Iterator<Segment> it = source.getNodeIterator(); it.hasNext();) {
			Segment segment = it.next();
			if (segment instanceof Tag) {
				int start = segment.getBegin();
				int end = segment.getEnd();
				Tag tag = (Tag) segment;
				String tagName = StringUtils.lowerCase(tag.getName());
				TagRole tagRole = TagRole.UNKNOWN;
				FSArray attributes = null;
				if (tag.getTagType() == StartTagType.NORMAL) {
					tagRole = TagRole.OPENING;
					if (writeAttributes) {
						StartTag startTag = (StartTag) tag;
						attributes = new FSArray(jcas, startTag.getAttributes().size());
						for (int i = 0; i < startTag.getAttributes().size(); i++) {
							TagAttribute tagAttribute = createTagAttribute(jcas, start, end,
									startTag.getAttributes().get(i).getName(),
									startTag.getAttributes().get(i).getValue());
							attributes.set(i, tagAttribute);
						}
					}
				} else if (tag.getTagType() == EndTagType.NORMAL) {
					tagRole = TagRole.CLOSING;
				}
				annotateHtmlTag(jcas, start, end, tagName, tagRole, attributes);
			}
		}
	}

	private TagAttribute createTagAttribute(JCas jcas, int start, int end, String name, String value) {
		TagAttribute tagAttribute = new TagAttribute(jcas);
		tagAttribute.setName(name);
		tagAttribute.setValue(value);
		tagAttribute.setBegin(start);
		tagAttribute.setEnd(end);
		return tagAttribute;
	}

	private void annotateHtmlTag(JCas jcas, int start, int end, String tagName, TagRole tagRole, FSArray attributes) {
		if (tagNames.length == 0 || (tagNames.length > 0 && Arrays.asList(tagNames).contains(tagName))) {
			HtmlTag annotation = new HtmlTag(jcas);
			annotation.setBegin(start);
			annotation.setEnd(end);
			annotation.setTagName(tagName);
			annotation.setTagRole(tagRole.toString());
			annotation.setAttributes(attributes);
			annotation.addToIndexes(jcas);
		}
	}

}
