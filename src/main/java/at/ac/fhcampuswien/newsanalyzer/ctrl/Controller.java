package at.ac.fhcampuswien.newsanalyzer.ctrl;

import at.ac.fhcampuswien.newsanalyzer.downloader.Downloader;
import at.ac.fhcampuswien.newsanalyzer.downloader.ParallelDownloader;
import at.ac.fhcampuswien.newsanalyzer.downloader.SequentialDownloader;
import at.ac.fhcampuswien.newsapi.NewsApi;
import at.ac.fhcampuswien.newsapi.beans.Article;
import at.ac.fhcampuswien.newsapi.beans.NewsResponse;
import at.ac.fhcampuswien.newsapi.beans.Source;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Controller {

	public static final String APIKEY = "0038b5ccc1124e94b01d19b0d5982697";  //0038b5ccc1124e94b01d19b0d5982697

	private List<Article> articles = null;

	public String process(NewsApi newsApi) throws NewsAPIException, IllegalArgumentException{
		System.out.println("Start process");

		if(newsApi == null)
			throw new IllegalArgumentException();

		articles = getArticles(newsApi);

		System.out.println("End process");

		return getArticlesPrintReady();
	}

	public List<Article> getArticles(NewsApi newsApi) throws NewsAPIException {
		NewsResponse newsResponse = newsApi.getNews();

		if(!newsResponse.getStatus().equals("ok")){
			throw new NewsAPIException("News Response returned status " + newsResponse.getStatus());
		}
		
		return newsResponse.getArticles();
	}

	private String getArticlesPrintReady() {
		return articles.stream()
				.map(Article::print)
				.collect(Collectors.joining("\n"));
	}

	public long getArticleCount() throws NewsAPIException {
		if(articles == null)
			throw new NewsAPIException("Load articles first");
		return articles.size();
	}

	public String getSortArticlesByLongestTitle() throws NewsAPIException {
		if(articles == null)
			throw new NewsAPIException("Load articles first");
		return articles.stream()
				.map(Article::getTitle)
				.filter(Objects::nonNull)
				.sorted(Comparator.comparing(String::length).reversed())
				.collect(Collectors.joining("\n"));
	}

	public String getShortestNameOfAuthors() throws NewsAPIException {
		if(articles == null)
			throw new NewsAPIException("Load data first");

		return articles.stream()
				.map(Article::getAuthor)
				.filter(Objects::nonNull)
				.min(Comparator.comparing(String::length))
				.orElseThrow();
	}

	public String getProviderWithMostArticles() throws NewsAPIException {
		if(articles == null)
			throw new NewsAPIException("Load data first");

		return articles.stream()
				.map(Article::getSource)
				.collect(Collectors.groupingBy(Source::getName))
				.entrySet()
				.stream()
				.max(Comparator.comparingInt(o -> o.getValue().size()))
				.map(stringListEntry -> stringListEntry.getKey() + " " + stringListEntry.getValue().size())
				.orElseThrow();
	}

	public List<String> downloadURLs() throws NewsAPIException {
		if (articles == null)
			throw new NewsAPIException("Load data first");

		return articles.stream()
				.map(Article::getUrl)
				.collect(Collectors.toList());
	}

	public int downloadArticles(Downloader downloader) throws NewsAPIException {
		if (articles == null)
			throw new NewsAPIException("Load data first");
		List<String> urls = downloadURLs();
		int downloadedTotal = 0;
		try {
			downloadedTotal = downloader.process(urls);
		} catch (ExecutionException | InterruptedException e) {
			//exceptions thrown by .get() method while trying to access future filename
			throw new NewsAPIException("One or more files could not be downloaded properly: " + e.getMessage(), e.getCause());
		}
		return downloadedTotal; //NewsApiException might be thrown with process
	}

}
