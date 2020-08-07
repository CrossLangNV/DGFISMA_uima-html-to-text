package com.crosslang.uimahtmltotext.uima;

import cassis.Token;
import com.crosslang.sdk.transfer.ae.model.tag.handler.JCasTransformer_ImplBase;
import com.crosslang.sdk.types.html.HtmlTag;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.tfidf.type.Tfidf;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Text2HtmlTransformer extends JCasTransformer_ImplBase {
    public static final Logger logger = LoggerFactory.getLogger(Text2HtmlTransformer.class);

    @Override
    public void process(JCas aInput, JCas aOutput) throws AnalysisEngineProcessException {
        logger.info("Text2HtmlTransformer has been called");

        List<Tfidf> terms = new ArrayList<>();
        List<Token> termBuilder = new ArrayList<>();

        int max = (int) JCasUtil.select(aInput, Token.class).stream().count();

        logger.info("Max: "+max);

        for (int i=0; i< max; i++) {
            Token token = JCasUtil.selectByIndex(aInput, Token.class, i);
            if (token.getPos().equals("B")) {
                logger.info("Found begin of a term. ");
                termBuilder.add(token);
                for (int j=i+1; j<max; j++) {
                    Token nextToken = JCasUtil.selectByIndex(aInput, Token.class, j);
                    if (nextToken.getPos().equals("I")) {
                        termBuilder.add(nextToken);
                        logger.info("Found middle of a term. ");
                    } else {
                        Tfidf tfidf = new Tfidf(aInput);
                        tfidf.setBegin(token.getBegin());
                        tfidf.setEnd(nextToken.getEnd());
                        terms.add(tfidf);
                        logger.info("Found end of a term. Term index: "+tfidf.getBegin() + " | " + tfidf.getEnd());
                        termBuilder.removeAll(termBuilder);
                        break;
                    }
                }
            }
        }

        for (Tfidf term : terms) {
            term.addToIndexes();
        }
    }

}
