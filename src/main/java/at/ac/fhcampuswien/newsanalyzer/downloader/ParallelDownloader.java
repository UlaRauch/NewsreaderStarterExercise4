package at.ac.fhcampuswien.newsanalyzer.downloader;

import at.ac.fhcampuswien.newsanalyzer.ctrl.NewsAPIException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelDownloader extends Downloader {

    @Override
    public int process(List<String> urls) throws NewsAPIException, ExecutionException, InterruptedException {

        int downloadedTotal = 0;

        if (!urls.isEmpty()) {
            int numArticles = urls.size(); //number of articles to download (tasks)
            int numWorkers = Runtime.getRuntime().availableProcessors(); //number of available threads for pool

            ExecutorService pool = Executors.newFixedThreadPool(numWorkers); //create pool

            List<Callable<String>> callableList = new ArrayList<>();
            for (int i = 0; i < numArticles; i++) {
                int finalIndex = i; //why does it have to be final?
                Callable<String> task = () -> saveUrl2File(urls.get(finalIndex)); //task = save the article from url
                callableList.add(task);
            }

            //let threads go to work
            List<Future<String>> futureFiles = null;
            try {
                futureFiles = pool.invokeAll(callableList);
            } catch (InterruptedException e) {
                throw new NewsAPIException("File download has been interrupted: " + e.getMessage(), e.getCause());
            }
            //count number of downloaded articles
            finally {
                /*
             if (!pool.isShutdown())
                    System.out.println("shutdown pool");
                    */
                pool.shutdown(); //close pool
            }
            for (Future<String> f : futureFiles) {
                if (f != null) {
                    downloadedTotal++;
                    System.out.println("Article: " + f.get() + "saved"); // get() throws ExecutionException and InterruptedException - specified in method
                }
            }
        }
        return downloadedTotal;
    }
}
