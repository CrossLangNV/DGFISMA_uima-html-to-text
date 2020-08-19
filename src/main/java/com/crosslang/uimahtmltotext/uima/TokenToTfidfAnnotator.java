package com.crosslang.uimahtmltotext.uima;

import cassis.Token;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.tfidf.type.Tfidf;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will read @{@link Token} objects, check for their BIO tags, and when a BI (Begin Outside) tag has been
 * found, it will merge these Tokens into a @{@link Tfidf} object. Which will hold a Term/Concept
 */
public class TokenToTfidfAnnotator extends JCasAnnotator_ImplBase {
    public static final Logger logger = LoggerFactory.getLogger(TokenToTfidfAnnotator.class);

    @Override
    public void process(JCas aJCas) {
        List<Tfidf> terms = new ArrayList<>();
        List<Token> termBuilder = new ArrayList<>();

        int max = countTotalTokens(aJCas);

        int x = 0;

        for (int i=0; i< max; i++) {
            Token token = JCasUtil.selectByIndex(aJCas, Token.class, i);

            if (token.getPos() != null) {
                if (token.getPos().equals("B")) {
                    termBuilder.add(token);
                    if (max > 1) {
                        for (int j = i + 1; j < max; j++) {
                            Token nextToken = JCasUtil.selectByIndex(aJCas, Token.class, j);
                            if (nextToken.getPos() != null) {
                                if (nextToken.getPos().equals("I")) {
                                    termBuilder.add(nextToken);
                                } else {
                                    // TODO Als i laatste is, werkt het niet meer
                                    Tfidf tfidf = new Tfidf(aJCas, token.getBegin(), JCasUtil.selectByIndex(aJCas, Token.class, j-1).getEnd());
                                    terms.add(tfidf);
                                    tfidf.addToIndexes();
                                    termBuilder.clear();
                                    break;
                                }
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

    public int countTotalTokens(JCas cas) {
        return JCasUtil.select(cas, Token.class).size();
    }
}
