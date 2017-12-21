package com.dummy.trivia.util;

import org.springframework.http.MediaType;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

    private static final String FILE_SUFFIX_JPEG = ".jpeg";
    private static final String FILE_SUFFIX_PNG = ".png";

    public static void base64ToFile(String base64Code, String targetPath, String fileName) throws IOException {
        if (base64Code.startsWith("data")) {
            String typeSuffix = getTypeSuffix(base64Code);
            File targetFile = createNewFile(targetPath, fileName, typeSuffix);
            byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code.substring(base64Code.indexOf(",") + 1));
            FileOutputStream out = new FileOutputStream(targetFile);
            out.write(buffer);
            out.close();
        } else {
            throw new IOException("illegal base64");
        }
    }

    private static String getTypeSuffix(String base64Code) throws IOException {
        int separator = base64Code.indexOf(",");
        String metaInfo = base64Code.substring(0, separator);
        String fileType = metaInfo.substring(5, metaInfo.indexOf(";"));
        String typeSuffix;
        if (MediaType.IMAGE_JPEG_VALUE.equals(fileType)) {
            typeSuffix = FILE_SUFFIX_JPEG;
        } else if (MediaType.IMAGE_PNG_VALUE.equals(fileType)) {
            typeSuffix = FILE_SUFFIX_PNG;
        } else {
            throw new IOException("illegal content type");
        }
        return typeSuffix;
    }

    private static File createNewFile(String targetPath, String fileName, String typeSuffix) throws IOException {
        File targetFile = new File(targetPath, fileName + typeSuffix);
        if (targetFile.exists()) {
            throw new IOException("illegal file path");
        }
        if (!targetFile.getParentFile().exists()) {
            if (!targetFile.getParentFile().mkdirs()) {
                throw new IOException("mkdirs failed");
            }
        }
        if (targetFile.exists()) {
            targetFile.delete();
        }
        if (!targetFile.createNewFile()) {
            throw new IOException("create new file failed");
        }
        return targetFile;
    }
}
