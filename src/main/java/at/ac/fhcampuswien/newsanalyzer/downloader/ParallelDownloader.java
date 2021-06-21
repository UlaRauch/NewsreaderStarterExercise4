package at.ac.fhcampuswien.newsanalyzer.downloader;

import at.ac.fhcampuswien.newsanalyzer.ctrl.NewsAPIException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelDownloader extends Downloader {

    @Override
    public int process(List<String> urls) throws NewsAPIException {

        int downloadedTotal = 0;

        if (!urls.isEmpty()) {
            int numArticles = urls.size(); //number of articles to download (tasks)
            int numWorkers = Runtime.getRuntime().availableProcessors(); //number of available threads for pool

            ExecutorService pool = Executors.newFixedThreadPool(numWorkers); //create pool

            List<Callable<String>> callableList = new ArrayList<>();
            for (int i = 0; i < numArticles; i++) {
                int finalIndex = i; //warum wird das benötigt?
                Callable<String> task = () -> saveUrl2File(urls.get(finalIndex));
                callableList.add(task);
            }

            //let threads go to work
            List<Future<String>> futureFiles;
            try {
                futureFiles = pool.invokeAll(callableList);
            } catch (Exception e) {
                throw new NewsAPIException(e.getMessage());
            }

            //count number of downloaded articles
            for (Future<String> f : futureFiles) {
                if (f != null) {
                    downloadedTotal++;
                }
            }
            pool.shutdown(); //close pool

        }
        return downloadedTotal;
    }
}
