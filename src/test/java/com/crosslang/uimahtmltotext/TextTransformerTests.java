package com.crosslang.uimahtmltotext;

import cassis.Sentence;
import cassis.Token;
import com.crosslang.sdk.segmentation.ae.html.annotator.HtmlAnnotator;
import com.crosslang.sdk.types.html.HtmlTag;
import com.crosslang.sdk.utils.commons.CasDumperReadable;
import com.crosslang.sdk.utils.commons.CasDumperReadableAnnotator;
import com.crosslang.uimahtmltotext.uima.Html2TextTransformer;
import com.crosslang.uimahtmltotext.uima.Text2HtmlTransformer;
import com.crosslang.uimahtmltotext.uima.type.ValueBetweenTagType;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.tfidf.type.Tfidf;
import de.tudarmstadt.ukp.dkpro.core.frequency.tfidf.TfidfAnnotator;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.CasIOUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

public class TextTransformerTests {

    @Test
    public void html2textTest() throws Exception {
        // First view with HTML
        JCas cas = JCasFactory.createJCas();
        cas.setDocumentText("Hello <html>Test <b class='.nice'>Bo <i> l </b> d </i></html> World");
        cas.setDocumentLanguage("en");

        AggregateBuilder ab = new AggregateBuilder();

        List<String> types = Arrays.asList(ValueBetweenTagType.class.getName());

        // Create and add AED's
        AnalysisEngineDescription html = AnalysisEngineFactory.createEngineDescription(HtmlAnnotator.class);
        AnalysisEngineDescription tok = AnalysisEngineFactory.createEngineDescription(OpenNlpSegmenter.class);
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
        ab.add(xmiWriter);

        AnalysisEngineDescription aed = ab.createAggregateDescription();

        SimplePipeline.runPipeline(cas, aed);
        CasDumperReadable.dump(cas);
    }

    @Test
    public void text2html() throws UIMAException, IOException {

        String xmi = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<xmi:XMI xmlns:pos=\"http:///de/tudarmstadt/ukp/dkpro/core/api/lexmorph/type/pos.ecore\" xmlns:type=\"http:///com/crosslang/uima/type.ecore\" xmlns:tcas=\"http:///uima/tcas.ecore\" xmlns:html=\"http:///com/crosslang/sdk/types/html.ecore\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:cas=\"http:///uima/cas.ecore\" xmlns:tweet=\"http:///de/tudarmstadt/ukp/dkpro/core/api/lexmorph/type/pos/tweet.ecore\" xmlns:v22=\"http:///com/crosslang/uima/sdk/type/copied/v2.ecore\" xmlns:segment=\"http:///com/crosslang/sdk/types/classification/bad/segment.ecore\" xmlns:dependency=\"http:///de/tudarmstadt/ukp/dkpro/core/api/syntax/type/dependency.ecore\" xmlns:v2=\"http:///com/crosslang/uima/sdk/type/clgw/v2.ecore\" xmlns:reordering=\"http:///com/crosslang/sdk/types/ruta/reordering.ecore\" xmlns:training=\"http:///com/crosslang/sdk/types/split/training.ecore\" xmlns:sub=\"http:///com/crosslang/sdk/types/splitsentence/sub.ecore\" xmlns:quotepos=\"http:///com/crosslang/sdk/types/classification/quotepos.ecore\" xmlns:type5=\"http:///de/tudarmstadt/ukp/dkpro/core/api/metadata/type.ecore\" xmlns:splitsentence=\"http:///com/crosslang/sdk/types/splitsentence.ecore\" xmlns:clgw=\"http:///com/crosslang/uima/sdk/type/clgw.ecore\" xmlns:type2=\"http:///com/crosslang/uimahtmltotext/uima/type.ecore\" xmlns:verbcomplex=\"http:///com/crosslang/sdk/types/classification/verbcomplex.ecore\" xmlns:ruta=\"http:///com/crosslang/sdk/types/ruta.ecore\" xmlns:token=\"http:///com/crosslang/sdk/types/moses/token.ecore\" xmlns:norm=\"http:///com/crosslang/sdk/types/norm.ecore\" xmlns:clause=\"http:///com/crosslang/sdk/types/classification/clause.ecore\" xmlns:morph=\"http:///de/tudarmstadt/ukp/dkpro/core/api/lexmorph/type/morph.ecore\" xmlns:type4=\"http:///de/tudarmstadt/ukp/dkpro/core/api/frequency/tfidf/type.ecore\" xmlns:regex=\"http:///com/crosslang/sdk/types/regex.ecore\" xmlns:type9=\"http:///de/tudarmstadt/ukp/dkpro/core/api/transform/type.ecore\" xmlns:type3=\"http:///de/tudarmstadt/ukp/dkpro/core/api/anomaly/type.ecore\" xmlns:type8=\"http:///de/tudarmstadt/ukp/dkpro/core/api/syntax/type.ecore\" xmlns:type6=\"http:///de/tudarmstadt/ukp/dkpro/core/api/ner/type.ecore\" xmlns:moses=\"http:///com/crosslang/sdk/types/moses.ecore\" xmlns:type7=\"http:///de/tudarmstadt/ukp/dkpro/core/api/segmentation/type.ecore\" xmlns:constituent=\"http:///de/tudarmstadt/ukp/dkpro/core/api/syntax/type/constituent.ecore\" xmlns:classification=\"http:///com/crosslang/sdk/types/classification.ecore\" xmlns:chunk=\"http:///de/tudarmstadt/ukp/dkpro/core/api/syntax/type/chunk.ecore\" xmlns:normalization=\"http:///com/crosslang/sdk/types/ruta/normalization.ecore\" xmi:version=\"2.0\">\n" +
                "<cas:NULL xmi:id=\"0\"/>\n" +
                "<html:HtmlTag xmi:id=\"15\" sofa=\"1\" begin=\"7\" end=\"13\" tagName=\"html\" tagRole=\"OPENING\" attributes=\"\"/>\n" +
                "<html:HtmlTag xmi:id=\"31\" sofa=\"1\" begin=\"19\" end=\"36\" tagName=\"b\" tagRole=\"OPENING\" attributes=\"25\"/>\n" +
                "<html:HtmlTag xmi:id=\"40\" sofa=\"1\" begin=\"39\" end=\"42\" tagName=\"i\" tagRole=\"OPENING\" attributes=\"\"/>\n" +
                "<html:HtmlTag xmi:id=\"47\" sofa=\"1\" begin=\"45\" end=\"49\" tagName=\"b\" tagRole=\"CLOSING\"/>\n" +
                "<html:HtmlTag xmi:id=\"54\" sofa=\"1\" begin=\"52\" end=\"56\" tagName=\"i\" tagRole=\"CLOSING\"/>\n" +
                "<html:HtmlTag xmi:id=\"61\" sofa=\"1\" begin=\"56\" end=\"63\" tagName=\"html\" tagRole=\"CLOSING\"/>\n" +
                "<type5:DocumentMetaData xmi:id=\"80\" sofa=\"1\" begin=\"0\" end=\"69\" language=\"en\" documentTitle=\"docTitle\" documentId=\"docId\" collectionId=\"colId\" isLastSegment=\"false\"/>\n" +
                "<type2:ValueBetweenTagType xmi:id=\"91\" sofa=\"1\" begin=\"13\" end=\"56\" text=\"Test2 &lt;b class='.nice'&gt;Bo &lt;i&gt; l &lt;/b&gt; d &lt;/i&gt;\" tagName=\"html\" attributes=\"\"/>\n" +
                "<type2:ValueBetweenTagType xmi:id=\"98\" sofa=\"1\" begin=\"36\" end=\"45\" text=\"Bo &lt;i&gt; l \" tagName=\"b\" attributes=\"25\"/>\n" +
                "<type2:ValueBetweenTagType xmi:id=\"105\" sofa=\"1\" begin=\"42\" end=\"52\" text=\" l &lt;/b&gt; d \" tagName=\"i\" attributes=\"\"/>\n" +
                "<type7:Sentence xmi:id=\"146\" sofa=\"1\" begin=\"0\" end=\"38\"/>\n" +
                "<type7:Sentence xmi:id=\"151\" sofa=\"1\" begin=\"39\" end=\"69\"/>\n" +
                "<type7:Token xmi:id=\"156\" sofa=\"1\" begin=\"0\" end=\"6\"/>\n" +
                "<type7:Token xmi:id=\"166\" sofa=\"1\" begin=\"7\" end=\"18\"/>\n" +
                "<type7:Token xmi:id=\"176\" sofa=\"1\" begin=\"19\" end=\"21\"/>\n" +
                "<type7:Token xmi:id=\"186\" sofa=\"1\" begin=\"22\" end=\"27\"/>\n" +
                "<type7:Token xmi:id=\"196\" sofa=\"1\" begin=\"27\" end=\"28\"/>\n" +
                "<type7:Token xmi:id=\"206\" sofa=\"1\" begin=\"28\" end=\"29\"/>\n" +
                "<type7:Token xmi:id=\"216\" sofa=\"1\" begin=\"29\" end=\"34\"/>\n" +
                "<type7:Token xmi:id=\"226\" sofa=\"1\" begin=\"34\" end=\"38\"/>\n" +
                "<type7:Token xmi:id=\"236\" sofa=\"1\" begin=\"39\" end=\"42\"/>\n" +
                "<type7:Token xmi:id=\"246\" sofa=\"1\" begin=\"43\" end=\"44\"/>\n" +
                "<type7:Token xmi:id=\"256\" sofa=\"1\" begin=\"45\" end=\"49\"/>\n" +
                "<type7:Token xmi:id=\"266\" sofa=\"1\" begin=\"50\" end=\"51\"/>\n" +
                "<type7:Token xmi:id=\"276\" sofa=\"1\" begin=\"52\" end=\"63\"/>\n" +
                "<type7:Token xmi:id=\"286\" sofa=\"1\" begin=\"64\" end=\"69\"/>\n" +
                "<tcas:DocumentAnnotation xmi:id=\"75\" sofa=\"68\" begin=\"0\" end=\"28\" language=\"en\"/>\n" +
                "<type2:ValueBetweenTagType xmi:id=\"112\" sofa=\"68\" begin=\"7\" end=\"22\" text=\"Test2 &lt;b class='.nice'&gt;Bo &lt;i&gt; l &lt;/b&gt; d &lt;/i&gt;\" tagName=\"html\" attributes=\"\"/>\n" +
                "<type2:ValueBetweenTagType xmi:id=\"121\" sofa=\"68\" begin=\"13\" end=\"19\" text=\"Bo &lt;i&gt; l \" tagName=\"b\" attributes=\"131\"/>\n" +
                "<type2:ValueBetweenTagType xmi:id=\"137\" sofa=\"68\" begin=\"16\" end=\"22\" text=\" l &lt;/b&gt; d \" tagName=\"i\" attributes=\"\"/>\n" +
                "<cas:Sofa xmi:id=\"1\" sofaNum=\"1\" sofaID=\"_InitialView\" mimeType=\"text\" sofaString=\"Helloo &lt;html&gt;Test2 &lt;b class='.nice'&gt;Bo &lt;i&gt; l &lt;/b&gt; d &lt;/i&gt;&lt;/html&gt; World\"/>\n" +
                "<cas:Sofa xmi:id=\"68\" sofaNum=\"2\" sofaID=\"targetViewName\" mimeType=\"text\" sofaString=\"Helloo Test2 Bo  l  d  World\"/>\n" +
                "<html:TagAttribute xmi:id=\"25\" sofa=\"1\" begin=\"19\" end=\"36\" name=\"class\" value=\".nice\"/>\n" +
                "<html:TagAttribute xmi:id=\"131\" sofa=\"68\" begin=\"19\" end=\"36\" name=\"class\" value=\".nice\"/>\n" +
                "<cas:View sofa=\"1\" members=\"15 31 40 47 54 61 80 91 98 105 146 151 156 166 176 186 196 206 216 226 236 246 256 266 276 286\"/>\n" +
                "<cas:View sofa=\"68\" members=\"75 112 121 137\"/>\n" +
                "</xmi:XMI>\n";

        // Create new file where we store the XML and read from it afterwards with XmiReader
        File dir = new File("./target/cache/convert/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File("./target/cache/convert/docId.xmi");

        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        writer.append(xmi);
        writer.close();

        // Read XMI
        String[] arr = {"*"};
        CollectionReaderDescription description = createReaderDescription(
                XmiReader.class,
                XmiReader.PARAM_SOURCE_LOCATION, dir,
                XmiReader.PARAM_PATTERNS, arr
        );

        AggregateBuilder ab = new AggregateBuilder();

        // Create aed's
        AnalysisEngineDescription aed1 = CasDumperReadableAnnotator.create();


        List<String> types = Arrays.asList(ValueBetweenTagType.class.getName());
        AnalysisEngineDescription transformer = AnalysisEngineFactory.createEngineDescription(Text2HtmlTransformer.class,
                Text2HtmlTransformer.PARAM_TARGET_VIEW_NAME, "text2htmlView", Text2HtmlTransformer.PARAM_TYPES_TO_COPY, types);
        ab.add(aed1);
        ab.add(transformer);


        AnalysisEngineDescription aed = ab.createAggregateDescription();

        SimplePipeline.runPipeline(description, aed);

        JCas cas = JCasFactory.createJCas(description.getCollectionReaderMetaData().getTypeSystem());
        CasDumperReadable.dump(cas);
    }

    @Test
    public void testXmlTerms() throws UIMAException, IOException {
        File xmlFile = new File("/Users/oanstultjens/dev/xmi/actual_xmi_html_test2(2).xmi");

        // Read XMI
        CollectionReaderDescription description = createReaderDescription(
                XmiReader.class,
                XmiReader.PARAM_SOURCE_LOCATION, xmlFile
        );

        AggregateBuilder ab = new AggregateBuilder();

        // Create aed's
        AnalysisEngineDescription aed1 = CasDumperReadableAnnotator.create();


        List<String> types = Arrays.asList(ValueBetweenTagType.class.getName());
        AnalysisEngineDescription transformer = AnalysisEngineFactory.createEngineDescription(Text2HtmlTransformer.class,
                Text2HtmlTransformer.PARAM_TARGET_VIEW_NAME, "text2htmlView", Text2HtmlTransformer.PARAM_TYPES_TO_COPY, types);
        ab.add(aed1);
        ab.add(transformer);


        AnalysisEngineDescription aed = ab.createAggregateDescription();

        SimplePipeline.runPipeline(description, aed);

        JCas cas = JCasFactory.createJCas(description.getCollectionReaderMetaData().getTypeSystem());
        CasDumperReadable.dump(cas);
    }
}
