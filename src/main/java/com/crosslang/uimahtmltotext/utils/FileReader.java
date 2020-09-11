package com.crosslang.uimahtmltotext.utils;

import com.crosslang.sdk.utils.commons.CasDumperReadable;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {
    public static final Logger logger = LoggerFactory.getLogger(FileReader.class);

    private FileReader() {
    }

    public static String readAllBytes(String filePath) {
        String content = "";
        try {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return content;
    }

    public static void writeCasDumpToFile(JCas cas, String path, String view) throws FileNotFoundException, CASException {
        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {
            CasDumperReadable.dump(cas.getView(view), printWriter);
        }
    }
}
