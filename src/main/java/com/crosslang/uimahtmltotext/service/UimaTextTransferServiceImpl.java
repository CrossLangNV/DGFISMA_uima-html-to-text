package com.crosslang.uimahtmltotext.service;

import com.crosslang.sdk.segmentation.ae.html.annotator.HtmlAnnotator;
import com.crosslang.sdk.utils.commons.CasDumperReadable;
import com.crosslang.uimahtmltotext.model.HtmlInput;
import com.crosslang.uimahtmltotext.uima.Html2TextTransformer;
import com.crosslang.uimahtmltotext.uima.type.ValueBetweenTagType;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.crosslang.sdk.transfer.ae.model.tag.handler.JCasTransformer_ImplBase.PARAM_TARGET_VIEW_NAME;
import static com.crosslang.sdk.transfer.ae.model.tag.handler.JCasTransformer_ImplBase.PARAM_TYPES_TO_COPY;
import static de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase.PARAM_OVERWRITE;
import static de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase.PARAM_TARGET_LOCATION;

@Service
public class UimaTextTransferServiceImpl implements UimaTextTransferService {
    @Override
    public String textToHtml(String text) {
        return null;
    }

    @Override
    public byte[] htmlToText(HtmlInput input) {
        JCas cas = null;
        try {
            cas = JCasFactory.createJCas();
            cas.setDocumentText(input.getText());
            cas.setDocumentLanguage("en");

            AggregateBuilder ab = new AggregateBuilder();

            List<String> types = Arrays.asList(ValueBetweenTagType.class.getName());

            // Create and add AED's
            AnalysisEngineDescription html = AnalysisEngineFactory.createEngineDescription(HtmlAnnotator.class);
            AnalysisEngineDescription tok = AnalysisEngineFactory.createEngineDescription(OpenNlpSegmenter.class);
            AnalysisEngineDescription aed1 = AnalysisEngineFactory.createEngineDescription(Html2TextTransformer.class,
                    PARAM_TARGET_VIEW_NAME, "targetViewName", PARAM_TYPES_TO_COPY, types);
            AnalysisEngineDescription xmiWriter =
                    AnalysisEngineFactory.createEngineDescription(
                            XmiWriter.class,
                            PARAM_OVERWRITE, true,
                            PARAM_TARGET_LOCATION, "./target/cache"
                    );

            ab.add(html);
            ab.add(aed1);
            ab.add(tok);
            ab.add(xmiWriter);

            AnalysisEngineDescription aed = ab.createAggregateDescription();

            SimplePipeline.runPipeline(cas, aed);
            CasDumperReadable.dump(cas);


            File file = new File("./target/cache/docId.xmi");
            InputStream in = new FileInputStream(file);
            return IOUtils.toByteArray(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public void writeCasDumpToFile(JCas cas, String path) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8));
        CasDumperReadable.dump(cas, printWriter);
    }
}
