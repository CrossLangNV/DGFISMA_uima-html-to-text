package com.crosslang.uimahtmltotext.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

}
