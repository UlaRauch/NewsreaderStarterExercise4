package at.ac.fhcampuswien.newsanalyzer.downloader;

import at.ac.fhcampuswien.newsanalyzer.ctrl.NewsAPIException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public abstract class Downloader {

    public static final String HTML_EXTENTION = ".html";
    public static final String DIRECTORY_DOWNLOAD = "C:\\Users\\urauc\\Documents\\Ausbildung\\FH\\Programmieren\\PROG2\\Exercises\\prog2-uebung4-newsreader-multithreading\\src\\download\\";

    public abstract int process(List<String> urls) throws NewsAPIException, ExecutionException, InterruptedException;

    public String saveUrl2File(String urlString) throws NewsAPIException {
        InputStream is = null;
        OutputStream os = null;
        String fileName = "";
        try {
            URL url4download = new URL(urlString);
            is = url4download.openStream();

            fileName = urlString.substring(urlString.lastIndexOf('/') + 1);
            if (fileName.isEmpty()) {
                fileName = url4download.getHost() + HTML_EXTENTION;
            }
            os = new FileOutputStream(DIRECTORY_DOWNLOAD + fileName);

            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw new NewsAPIException("There was a problem with reading from url or writing the file: " + e.getMessage() + ", " + e.getCause());
        } finally {
            try {
                Objects.requireNonNull(is).close();
                Objects.requireNonNull(os).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }
}
