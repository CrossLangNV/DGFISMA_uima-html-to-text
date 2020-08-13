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
        JCas cas = JCasFactory.createJCas();
//        String txt = "<div id='text' class='panel-body'><div id='textTabContent'><div id='document1' class='tabContent'><div class='tabContent'><div lang=''><div id='banner'><a name='top'></a><div class='bglang'><p class='bglang'><a class='langue' href='../../../editorial/legal_notice.htm' accesskey='8'><b>Avis juridique important</b></a><br></p></div><div class='bgtool'><em class='none'>|</em></div></div><a name='top'></a><h1>31999D0737</h1><p><strong>1999/737/EC: Council Decision of 8 November 1999 appointing Spanish members and alternate members of the Committee of the Regions  </strong><br><em><br>Official Journal L 294 , 17/11/1999 P. 0024 - 0024<br> </em></p><br><div id='TexteOnly'><p><txt_te><p>COUNCIL DECISION</p><p>of 8 November 1999</p><p>appointing Spanish members and alternate members of the Committee of the Regions</p><p>(1999/737/EC)</p><p></p><p>THE COUNCIL OF THE EUROPEAN UNION,</p><p>Having regard to the Treaty establishing the European Community, and in particular Article 263 thereof,</p><p>Having regard to the Council Decision of 26 January 1998(1) appointing the members and alternate members of the Committee of the Regions,</p><p>Whereas seats as members of the Committee of the Regions have become vacant following the resignation of Mr Santiago Lanzuela Marina and Mr Jaume Matas i Palou, members, and Ms María Rosa Estaras Farragut, Ms Ana Gómez Gómez and Mr Emilio del Valle Rodríguez, alternate members, notified to the Council on 1 and 18 October 1999;</p><p>Having regard to the proposal from the Spanish Government,</p><p>HAS DECIDED AS FOLLOWS:</p><p></p><p>Sole Article</p><p>1. Mr Francesc Antich i Oliver and Mr Marcelino Iglesias Ricoui shall be appointed members of the Committee of the Regions in place of Mr Jaume Matas i Palou and Mr Santiago Lanzuela Marina, who have resigned for the remainder of their term of office, which runs until 25 January 2002.</p><p>2. Mr Antonio Garcías i Coll, Mr Joaquín Rivas Rubiales and Mr Juan José Fernández Gómez shall be appointed alternate members of the Committee of the Regions in place of Ms María Rosa Estaras Farragut, Ms Ana Gómez Gómez and Mr Emilio del Valle Rodríguez, who have resigned for the remainder of their term of office, which runs until 25 January 2002.</p><p></p><p>Done at Brussels, 8 November 1999.</p><p></p><p>For the Council</p><p>The President</p><p>S. NIINISTÖ</p><p></p><p>(1) OJ L 28, 4.2.1998, p. 19.</p><p> </p></txt_te></p></div></div><a class='linkToTop' href='#document1'>Top</a></div></div></div></div>;";
//        String txt = "<html><p>paragraph <p>test <p>test2</p> </p></p></html>";
        String txt = "<html>Test <b class='.nice'>Bo <i> l </b> d </i></html> World";
        cas.setDocumentText(txt);
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
                Text2HtmlTransformer.PARAM_TARGET_VIEW_NAME,
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
