package com.crosslang.uimahtmltotext.uima;

import cassis.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhitespaceTokenizer extends JCasAnnotator_ImplBase {
    private static final Pattern whitespace = Pattern.compile("[ \n]+");

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        String text = aJCas.getDocumentText();
        text = text.endsWith("\n") ? text : text + "\n";
        Matcher whitespaceMatcher = whitespace.matcher(text);

        int x=0;

        for(int previousStart = 0; whitespaceMatcher.find(); previousStart = whitespaceMatcher.end()) {
            int end = whitespaceMatcher.start();
            Token token = new Token(aJCas, previousStart, end);

            if (x==5) {
                token.setPos("B");
            } else if (x == 6) {
                token.setPos("I");
            } else if (x==7) {
                token.setPos("I");
                x=0;
            } else {
                token.setPos("O");
            }
            x++;

            token.addToIndexes(aJCas);
        }
    }
}
