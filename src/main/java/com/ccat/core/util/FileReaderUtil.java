package com.ccat.core.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class FileReaderUtil {
    public static String readFile(String filepath) throws IOException {
        try(InputStream inStream = FileReaderUtil.class.getClassLoader().getResourceAsStream(filepath)) {

            if(inStream == null) throw new RuntimeException("Stream cannot be empty");

            return new String(inStream.readAllBytes());
        }
    }

    public static ByteBuffer readImage(String filepath) throws IOException {
        try(InputStream inStream = FileReaderUtil.class.getClassLoader().getResourceAsStream(filepath)) {
            if(inStream == null) throw new RuntimeException("Stream cannot be empty");

            BufferedImage bufferedImage = ImageIO.read(inStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            String fileEnding = filepath.split("\\.")[1];
            if(fileEnding.equalsIgnoreCase("jpg")) {
                ImageIO.write(bufferedImage, "jpg", baos);
            }
            else if(fileEnding.equalsIgnoreCase("png")) {
                ImageIO.write(bufferedImage, "png", baos);
            } else {
                throw new RuntimeException("Invalid file-format:" + fileEnding + " for file:"+filepath);
            }
            baos.flush();
            byte[] bytes = baos.toByteArray();

            baos.close();

            return ByteBuffer.allocateDirect(bytes.length)
                    .put(bytes)
                    .flip();
        }
    }
}
