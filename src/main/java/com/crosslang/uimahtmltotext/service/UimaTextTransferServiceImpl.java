package com.crosslang.uimahtmltotext.service;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crosslang.sdk.segmentation.ae.html.annotator.HtmlAnnotator;
import com.crosslang.uimahtmltotext.model.HtmlInput;
import com.crosslang.uimahtmltotext.uima.Html2TextAnnotator;
import com.crosslang.uimahtmltotext.uima.Text2HtmlTransformer;
import com.crosslang.uimahtmltotext.uima.type.ValueBetweenTagType;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.tfidf.type.Tfidf;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;

@Service
public class UimaTextTransferServiceImpl implements UimaTextTransferService {
	public static final Logger logger = LoggerFactory.getLogger(UimaTextTransferServiceImpl.class);
	static final String TARGET_VIEW_NAME = "html2textView";
	static final String TEXT2HTML_VIEW_NAME = "text2htmlView";
	static final String PATH_TO_XMI = System.getProperty("java.io.tmpdir") + "/docId.xmi";
	static final String PATH_TO_TYPESYSTEM = "typesystem.xml";
	static final String PATH_TO_CACHE = System.getProperty("java.io.tmpdir");

	@Override
	public byte[] htmlToText(HtmlInput input) {
		JCas cas;
		try {
			logger.debug("Got text: " + input.getText());
			cas = JCasFactory.createJCas();
			cas.setDocumentText(clean(input.getText()));
			cas.setDocumentLanguage("en");

			AggregateBuilder ab = new AggregateBuilder();

			boolean showAttributes = false;

			if (input.getAttributes()) {
				showAttributes = true;
			}

			// Create and add AED's
			AnalysisEngineDescription html = AnalysisEngineFactory.createEngineDescription(HtmlAnnotator.class,
					HtmlAnnotator.PARAM_WRITE_ATTRIBUTES, showAttributes);
			AnalysisEngineDescription aed1 = AnalysisEngineFactory.createEngineDescription(Html2TextAnnotator.class,
					Html2TextAnnotator.PARAM_TARGET_VIEW_NAME, TARGET_VIEW_NAME);

			ab.add(html);
			ab.add(aed1);

			SimplePipeline.runPipeline(cas, ab.createAggregateDescription());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XmiCasSerializer.serialize(cas.getCas(), baos);
           
            return baos.toByteArray();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new byte[0];
	}
  private String clean(String text) {
		return text.replaceAll("\\h"," ");
	}

	@Override
	public byte[] textToHtml(HtmlInput input) {
		try {
			logger.info("Got text: " + input.getText());

			// Write output to XMI
			// FIXME: not thread-safe!
			Files.write(Paths.get(PATH_TO_XMI), input.getText().getBytes());
			File xmlFile = new File(PATH_TO_XMI);

			// Read XMI
			CollectionReaderDescription description = CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
					xmlFile);

			AggregateBuilder ab = new AggregateBuilder();

			List<String> types = Arrays.asList(Tfidf.class.getName(), ValueBetweenTagType.class.getName(),
					Sentence.class.getName());

			// Create aed's
			AnalysisEngineDescription text2htmlTransformer = AnalysisEngineFactory.createEngineDescription(
					Text2HtmlTransformer.class, Text2HtmlTransformer.PARAM_TARGET_VIEW_NAME, TEXT2HTML_VIEW_NAME, Text2HtmlTransformer.PARAM_TYPES_TO_COPY, types,
					Text2HtmlTransformer.PARAM_REMOVE_OVERLAPPING, false);

			// Write output to XMI to return to ResponseBody
			AnalysisEngineDescription xmiWriter = AnalysisEngineFactory.createEngineDescription(XmiWriter.class,
					XmiWriter.PARAM_OVERWRITE, true, XmiWriter.PARAM_TARGET_LOCATION, PATH_TO_CACHE);

			ab.add(text2htmlTransformer, CAS.NAME_DEFAULT_SOFA, TARGET_VIEW_NAME);
			ab.add(xmiWriter);

			AnalysisEngineDescription aed = ab.createAggregateDescription();

			SimplePipeline.runPipeline(description, aed);

			InputStream in = new FileInputStream(xmlFile);
			return IOUtils.toByteArray(in);
		} catch (UIMAException | IOException e) {
			logger.error(e.getMessage(), e);
		}
		return new byte[0];
	}

	@Override
	public byte[] getTypeSystemFile() {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(PATH_TO_TYPESYSTEM)) {
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return new byte[0];
	}

	@Override
	public byte[] htmlToText(MultipartFile documentFile) {
		HtmlInput in = new HtmlInput();
		try {
			in.setText(new String(documentFile.getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return htmlToText(in);
	}

	@Override
	public byte[] textToHtml(MultipartFile documentFile) {
		HtmlInput in = new HtmlInput();
		try {
			in.setText(new String(documentFile.getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return textToHtml(in);
	}
}
