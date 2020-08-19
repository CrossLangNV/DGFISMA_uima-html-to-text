package com.crosslang.uimahtmltotext.uima;

import cassis.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

public class NlpAnnotator extends JCasAnnotator_ImplBase {
    @Override
    public void process(JCas cas) throws AnalysisEngineProcessException {
        try {
            JCas target = cas.getView("html2textView");
            Token t = new Token(target);
            t.setBegin(21);
            t.setEnd(26);
            t.setPos("B");
            t.addToIndexes();
        } catch (CASException e) {
            e.printStackTrace();
        }


    }
}
