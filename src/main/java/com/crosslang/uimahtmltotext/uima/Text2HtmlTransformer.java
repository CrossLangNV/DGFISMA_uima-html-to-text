package com.crosslang.uimahtmltotext.uima;

import com.crosslang.sdk.transfer.ae.model.tag.handler.JCasTransformer_ImplBase;
import com.crosslang.uimahtmltotext.uima.type.ValueBetweenTagType;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Text2HtmlTransformer extends JCasTransformer_ImplBase {
    public static final Logger logger = LoggerFactory.getLogger(TokenToTfidfAnnotator.class);

    @Override
    public void process(JCas aInput, JCas aOutput) throws AnalysisEngineProcessException {
        // TODO: Hier maak je met die insert() methodes van de TEXT terug een HTML
        // Hello Test Bo  l  d  World
        // Hello <html>Test <b class='.nice'>Bo <i> l </b> d </i></html> World

//        replace(5, 10, "<html>Hello</html>", "Hello", "Change", 1);

        for (ValueBetweenTagType vbtt : JCasUtil.select(aInput, ValueBetweenTagType.class)) {
            replace(vbtt.getBegin(), vbtt.getEnd(), "<html "+vbtt.getAttributes()+">"+vbtt.getCoveredText()+"</html>", "", "Test", 1);
        }



//        insert(5, "<html>");
//        insert(19, "</html>");




    }

    public static String getOpeningTag(String tagName) {
        return "<"+tagName+">";
    }

    public static String getClosingTag(String tagName) {
        return "</"+tagName+">";
    }
}
