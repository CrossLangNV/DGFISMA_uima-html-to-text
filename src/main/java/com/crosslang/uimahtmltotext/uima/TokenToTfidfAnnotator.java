package com.crosslang.uimahtmltotext.uima;

import cassis.Token;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.tfidf.type.Tfidf;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TokenToTfidfAnnotator extends JCasAnnotator_ImplBase {
    public static final Logger logger = LoggerFactory.getLogger(TokenToTfidfAnnotator.class);

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {

        List<Tfidf> terms = new ArrayList<>();
        List<Token> termBuilder = new ArrayList<>();

        int max = (int) JCasUtil.select(aJCas, Token.class).stream().count();

        logger.info("Max: "+max);

        for (int i=0; i< max; i++) {
            Token token = JCasUtil.selectByIndex(aJCas, Token.class, i);
            if (token.getPos().equals("B")) {
                logger.info("Found begin of a term. ");
                termBuilder.add(token);
                if (max > 1) {
                    for (int j = i + 1; j < max; j++) {
                        Token nextToken = JCasUtil.selectByIndex(aJCas, Token.class, j);
                        if (nextToken.getPos().equals("I")) {
                            termBuilder.add(nextToken);
                            logger.info("Found middle of a term. ");
                        } else {
                            Tfidf tfidf = new Tfidf(aJCas, token.getBegin(), nextToken.getEnd());
                            terms.add(tfidf);
                            tfidf.addToIndexes();
                            logger.info("Found end of a term. Term index: " + tfidf.getBegin() + " | " + tfidf.getEnd());
                            termBuilder.removeAll(termBuilder);
                            break;
                        }
                    }
                } else {
                    // This will probably only happen in the unit test, when only 1 term has been found
                    Tfidf tfidf = new Tfidf(aJCas, token.getBegin(), token.getEnd());
                    tfidf.addToIndexes();
                }
            }
        }
    }
}
