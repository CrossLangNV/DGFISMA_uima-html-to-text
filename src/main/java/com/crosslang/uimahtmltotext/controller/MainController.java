package com.crosslang.uimahtmltotext.controller;

import com.crosslang.sdk.segmentation.ae.html.annotator.HtmlAnnotator;
import com.crosslang.sdk.utils.commons.CasDumperReadable;
import com.crosslang.uimahtmltotext.model.HtmlInput;
import com.crosslang.uimahtmltotext.uima.Html2TextTransformer;
import com.crosslang.uimahtmltotext.uima.type.ValueBetweenTagType;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Controller
public class MainController {
    public static final Logger log = LoggerFactory.getLogger(MainController.class);

    @RequestMapping(value = "/html2text", method = RequestMethod.POST, produces = { "application/xml", "text/xml" })
    @ResponseBody
    public byte[] html2text(@RequestBody HtmlInput input) throws UIMAException, IOException {
        JCas cas = JCasFactory.createJCas();
        cas.setDocumentText(input.getText());
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


        File file = new File("./target/cache/docId.xmi");
        InputStream in = new FileInputStream(file);
        return IOUtils.toByteArray(in);
    }

    @RequestMapping(value = "/html2text/typesystem", method = RequestMethod.GET, produces = { "application/xml", "text/xml" })
    @ResponseBody
    public byte[] getTypesystem() throws UIMAException, IOException {
        File file = new File("./target/cache/typesystem.xml");
        InputStream in = new FileInputStream(file);
        return IOUtils.toByteArray(in);
    }

    @RequestMapping(value = "/text2html", method = RequestMethod.POST, produces = { "application/xml", "text/xml" }, consumes = MediaType.ALL_VALUE )
    @ResponseBody
    public String text2html() {
        return "Not implemented yet.";
    }
}
