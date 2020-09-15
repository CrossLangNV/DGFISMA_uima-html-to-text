package com.crosslang.uimahtmltotext.model;

import java.util.Objects;

public class HtmlInput {
    private String text;

    public HtmlInput() {
    }

    public HtmlInput(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "HtmlInput{" +
                "text='" + text + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlInput htmlInput = (HtmlInput) o;
        return Objects.equals(text, htmlInput.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
