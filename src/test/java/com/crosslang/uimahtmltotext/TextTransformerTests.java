package com.crosslang.uimahtmltotext;

import com.crosslang.sdk.segmentation.ae.html.annotator.HtmlAnnotator;
import com.crosslang.sdk.utils.commons.CasDumperReadable;
import com.crosslang.sdk.utils.commons.CasDumperReadableAnnotator;
import com.crosslang.uimahtmltotext.uima.Html2TextTransformer;
import com.crosslang.uimahtmltotext.uima.NlpAnnotator;
import com.crosslang.uimahtmltotext.uima.Text2HtmlTransformer;
import com.crosslang.uimahtmltotext.uima.TokenToTfidfAnnotator;
import com.crosslang.uimahtmltotext.uima.type.ValueBetweenTagType;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.tfidf.type.Tfidf;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

public class TextTransformerTests {

    @Test
    public void html2textTest() throws Exception {
        // First view with HTML
        JCas cas = JCasFactory.createJCas();
        cas.setDocumentText("Hello <html>Test <b class='.nice'>Bo <p><i> l </b> d </i></p></html> World");
//        cas.setDocumentText("Hello <html>World</html>");
        cas.setDocumentLanguage("en");

        AggregateBuilder ab = new AggregateBuilder();

        List<String> types = Arrays.asList(ValueBetweenTagType.class.getName());

        // Create and add AED's
        AnalysisEngineDescription html = AnalysisEngineFactory.createEngineDescription(HtmlAnnotator.class);
        AnalysisEngineDescription tok = AnalysisEngineFactory.createEngineDescription(OpenNlpSegmenter.class);
        AnalysisEngineDescription nlpAnnotator = AnalysisEngineFactory.createEngineDescription(NlpAnnotator.class);
        AnalysisEngineDescription aed1 = AnalysisEngineFactory.createEngineDescription(Html2TextTransformer.class,
                Html2TextTransformer.PARAM_TARGET_VIEW_NAME, "targetViewName", Html2TextTransformer.PARAM_TYPES_TO_COPY, types);
        AnalysisEngineDescription xmiWriter =
                AnalysisEngineFactory.createEngineDescription(
                        XmiWriter.class,
                        XmiWriter.PARAM_OVERWRITE, true,
                        XmiWriter.PARAM_TARGET_LOCATION, "./target/cache"
                        );

        ab.add(html);
        ab.add(aed1);
        ab.add(tok);
        ab.add(nlpAnnotator);
        ab.add(xmiWriter);

        AnalysisEngineDescription aed = ab.createAggregateDescription();

        SimplePipeline.runPipeline(cas, aed);
        CasDumperReadable.dump(cas);
    }

    @Test
    public void testXmlTerms() throws UIMAException, IOException {
        File xmlFile = new File("/Users/oanstultjens/IdeaProjects/uima-html-to-text/target/cache/docId.xmi");

        // Read XMI
        CollectionReaderDescription description = createReaderDescription(
                XmiReader.class,
                XmiReader.PARAM_SOURCE_LOCATION, xmlFile
        );

        AggregateBuilder ab = new AggregateBuilder();

        List<String> types = Arrays.asList(Tfidf.class.getName());

        // Create aed's
        AnalysisEngineDescription aedDump = CasDumperReadableAnnotator.create();
        AnalysisEngineDescription tokenToTfidAnnotator = AnalysisEngineFactory.createEngineDescription(TokenToTfidfAnnotator.class);
        AnalysisEngineDescription text2htmlTransformer = AnalysisEngineFactory.createEngineDescription(Text2HtmlTransformer.class,
                Html2TextTransformer.PARAM_TARGET_VIEW_NAME,
                "text2htmlView",
                Html2TextTransformer.PARAM_TYPES_TO_COPY,
                types,
                Html2TextTransformer.PARAM_REMOVE_OVERLAPPING,
                false);
        ab.add(tokenToTfidAnnotator, CAS.NAME_DEFAULT_SOFA, "targetViewName");
        ab.add(text2htmlTransformer, CAS.NAME_DEFAULT_SOFA, "targetViewName");

        ab.add(aedDump);

        AnalysisEngineDescription aed = ab.createAggregateDescription();

        SimplePipeline.runPipeline(description, aed);
    }
}
