package com.ccat.core.util;

import java.io.IOException;
import java.io.InputStream;

public class FileReaderUtil {
    public static String readFile(String filepath) throws IOException {
        try(InputStream inStream = FileReaderUtil.class.getClassLoader().getResourceAsStream(filepath)) {

            if(inStream == null) throw new RuntimeException("Stream cannot be empty");

            return new String(inStream.readAllBytes());
        }
    }
}
