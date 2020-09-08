package com.crosslang.uimahtmltotext.utils;

import com.crosslang.sdk.types.html.HtmlTag;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HtmlTagUtils {

    private HtmlTagUtils() {
    }

    public static Optional<HtmlTag> getLastObjectByTagType(List<HtmlTag> openedList, HtmlTag endTag) {
        return IntStream.range(0, openedList.size()).mapToObj(i -> openedList.get(openedList.size() - i - 1))
                .filter(Objects::nonNull)
                .filter(t -> t.getBegin() <= endTag.getBegin())
                .filter(t -> t.getTagName().equals(endTag.getTagName()))
                .findFirst();
    }

    public static List<HtmlTag> getTagsByState(JCas jCas, String state) {
        return  JCasUtil.select(jCas, HtmlTag.class).stream()
                .filter(s -> s.getTagRole().equals(state))
                .collect(Collectors.toList());
    }
}
