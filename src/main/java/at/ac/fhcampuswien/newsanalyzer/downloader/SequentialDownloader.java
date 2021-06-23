package at.ac.fhcampuswien.newsanalyzer.downloader;

import at.ac.fhcampuswien.newsanalyzer.ctrl.NewsAPIException;

import java.util.List;

public class SequentialDownloader extends Downloader {

    @Override
    public int process(List<String> urls) throws NewsAPIException {
        int count = 0;
        String fileName = null;
        for (String url : urls) {
            fileName = saveUrl2File(url);
            if(fileName != null)
                count++;
        }
        return count;
    }
}
