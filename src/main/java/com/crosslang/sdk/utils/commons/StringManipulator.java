package com.crosslang.sdk.utils.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StringManipulator {

    private String string;
    private boolean[] deleted;
    private int[] inserted;
    private Map<Integer, ArrayList<String>> inserts = new HashMap<>();

    public StringManipulator(String text) {

        this.string = text;
        this.deleted = new boolean[text.length()];

        Arrays.fill(deleted, false);

    }

    public String getCurrentString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (!deleted[i]) {
                result.append(string.charAt(i));
            }
        }
        return result.toString();
    }

    public int getNewOffsetPosition(int position) {
        int result = position;
        for (int i = 0; i < position; i++) {
            if (deleted[i]) {
                result--;
            }
        }
        return result;
    }

    public void delete(int begin, int end) {
        for (int i = begin; i < end; i++) {
            if (!deleted[i]) {
                deleted[i] = true;
            }
        }
    }

    public void insert(int position, String text) {

    }
}
