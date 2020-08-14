package com.crosslang.uimahtmltotext.service;

import com.crosslang.sdk.segmentation.ae.html.annotator.HtmlAnnotator;
import com.crosslang.sdk.utils.commons.CasDumperReadable;
import com.crosslang.sdk.utils.commons.CasDumperReadableAnnotator;
import com.crosslang.uimahtmltotext.model.HtmlInput;
import com.crosslang.uimahtmltotext.uima.Html2TextTransformer;
import com.crosslang.uimahtmltotext.uima.Text2HtmlTransformer;
import com.crosslang.uimahtmltotext.uima.TokenToTfidfAnnotator;
import com.crosslang.uimahtmltotext.uima.type.ValueBetweenTagType;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.tfidf.type.Tfidf;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static com.crosslang.sdk.transfer.ae.model.tag.handler.JCasTransformer_ImplBase.*;
import static de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase.PARAM_OVERWRITE;
import static de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase.PARAM_TARGET_LOCATION;
import static de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase.PARAM_SOURCE_LOCATION;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

@Service
public class UimaTextTransferServiceImpl implements UimaTextTransferService {
    public static final Logger logger = LoggerFactory.getLogger(UimaTextTransferServiceImpl.class);
    static final String TARGET_VIEW_NAME = "targetViewName";
    static final String PATH_TO_XMI = "./target/cache/docId.xmi";

    @Override
    public byte[] textToHtml(HtmlInput input) {
        try {
            // Write output to XMI
            Files.write(Paths.get(PATH_TO_XMI), input.getText().getBytes());
            File xmlFile = new File(PATH_TO_XMI);

            // Read XMI
            CollectionReaderDescription description = createReaderDescription(
                    XmiReader.class,
                    PARAM_SOURCE_LOCATION, xmlFile
            );

            AggregateBuilder ab = new AggregateBuilder();

            List<String> types = Collections.singletonList(Tfidf.class.getName());

            // Create aed's
            AnalysisEngineDescription aedDump = CasDumperReadableAnnotator.create();
            AnalysisEngineDescription tokenToTfidAnnotator = AnalysisEngineFactory.createEngineDescription(TokenToTfidfAnnotator.class);

            AnalysisEngineDescription text2htmlTransformer = AnalysisEngineFactory.createEngineDescription(Text2HtmlTransformer.class,
                    PARAM_TARGET_VIEW_NAME, "text2htmlView",
                    PARAM_TYPES_TO_COPY, types,
                    PARAM_REMOVE_OVERLAPPING, false);

            // Write output to XMI to return to ResponseBody
            AnalysisEngineDescription xmiWriter =
                    AnalysisEngineFactory.createEngineDescription(
                            XmiWriter.class,
                            PARAM_OVERWRITE, true,
                            PARAM_TARGET_LOCATION, "./target/cache"
                    );

            ab.add(tokenToTfidAnnotator, CAS.NAME_DEFAULT_SOFA, TARGET_VIEW_NAME);
            ab.add(text2htmlTransformer, CAS.NAME_DEFAULT_SOFA, TARGET_VIEW_NAME);

            ab.add(aedDump);
            ab.add(xmiWriter);

            AnalysisEngineDescription aed = ab.createAggregateDescription();

            SimplePipeline.runPipeline(description, aed);

            InputStream in = new FileInputStream(xmlFile);
            return IOUtils.toByteArray(in);
        } catch (UIMAException | IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    @Override
    public byte[] htmlToText(HtmlInput input) {
        JCas cas = null;
        try {
            cas = JCasFactory.createJCas();
            cas.setDocumentText(input.getText());
            cas.setDocumentLanguage("en");

            AggregateBuilder ab = new AggregateBuilder();

            List<String> types = Collections.singletonList(ValueBetweenTagType.class.getName());

            // Create and add AED's
            AnalysisEngineDescription html = AnalysisEngineFactory.createEngineDescription(HtmlAnnotator.class);
            AnalysisEngineDescription tok = AnalysisEngineFactory.createEngineDescription(OpenNlpSegmenter.class);
            AnalysisEngineDescription aed1 = AnalysisEngineFactory.createEngineDescription(Html2TextTransformer.class,
                    PARAM_TARGET_VIEW_NAME, TARGET_VIEW_NAME, PARAM_TYPES_TO_COPY, types);
            AnalysisEngineDescription xmiWriter =
                    AnalysisEngineFactory.createEngineDescription(
                            XmiWriter.class,
                            PARAM_OVERWRITE, true,
                            PARAM_TARGET_LOCATION, PATH_TO_XMI
                    );

            ab.add(html);
            ab.add(aed1);
            ab.add(tok);
            ab.add(xmiWriter);

            AnalysisEngineDescription aed = ab.createAggregateDescription();

            SimplePipeline.runPipeline(cas, aed);
            CasDumperReadable.dump(cas);

            File file = new File(PATH_TO_XMI);
            InputStream in = new FileInputStream(file);
            return IOUtils.toByteArray(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public void writeCasDumpToFile(JCas cas, String path) throws FileNotFoundException, CASException {
        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {
            CasDumperReadable.dump(cas.getView("text2html"), printWriter);
        }
    }
}
