package at.ac.fhcampuswien.newsanalyzer.ui;


import at.ac.fhcampuswien.newsanalyzer.ctrl.Controller;
import at.ac.fhcampuswien.newsanalyzer.ctrl.NewsAPIException;
import at.ac.fhcampuswien.newsanalyzer.downloader.ParallelDownloader;
import at.ac.fhcampuswien.newsanalyzer.downloader.SequentialDownloader;
import at.ac.fhcampuswien.newsapi.NewsApi;
import at.ac.fhcampuswien.newsapi.NewsApiBuilder;
import at.ac.fhcampuswien.newsapi.enums.Category;
import at.ac.fhcampuswien.newsapi.enums.Country;
import at.ac.fhcampuswien.newsapi.enums.Endpoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

public class UserInterface {

	private Controller ctrl = new Controller();

	public void getDataForCustomInput() {
		System.out.println("Enter search query:");
		String searchQuery = readLine();
		System.out.println("Enter amount of results (max 100):");
		Double resultsAnmount = readDouble(0,100);
		NewsApi newsApi = new NewsApiBuilder()
				.setApiKey(Controller.APIKEY)
				.setQ(searchQuery)
				.setEndPoint(Endpoint.EVERYTHING)
				.setPageSize(String.valueOf(resultsAnmount.intValue()))
				.createNewsApi();
		String result = null;
		try {
			result = ctrl.process(newsApi);
		} catch (NewsAPIException e) {
			System.err.println("Error occured: " + e.getMessage());
		}

		System.out.println(result);
	}


	public void start() {
		Menu<Runnable> menu = new Menu<>("User Interface");
		menu.insert("a", "Top headlines for Austria", this::getTopHeadlinesAustria);
		menu.insert("b", "All news for 'bitcoin' in June", this::getAllNewsBitcoin);
		menu.insert("d", "Search based on user input", this::getDataForCustomInput); // Exercise 3
		menu.insert("w", "Get provider with max articles", this::getProviderWithMostArticles);	// Exercise 3
		menu.insert("x", "Shortest author name", this::getShortestNameOfAuthors);	// Exercise 3
		menu.insert("y", "Get article count", this::getArticleCount);	// Exercise 3
		menu.insert("z", "Sort by longest title", this::getSortArticlesByLongestTitle); // Exercise 3
		menu.insert("g", "Download URLs", this::getDownloadArticles);
		menu.insert("q", "Quit", null);
		Runnable choice;
		while ((choice = menu.exec()) != null) {
			 choice.run();
		}
		System.out.println("Program finished");
	}


    protected String readLine() {
		String value = "\0";
		BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			value = inReader.readLine();
        } catch (IOException ignored) {

		}
		return value.trim();
	}

	protected Double readDouble(int lowerlimit, int upperlimit) 	{
		Double number = null;
        while (number == null) {
			String str = this.readLine();
			try {
				number = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                number = null;
				System.out.println("Please enter a valid number:");
				continue;
			}
            if (number < lowerlimit) {
				System.out.println("Please enter a higher number:");
                number = null;
            } else if (number > upperlimit) {
				System.out.println("Please enter a lower number:");
                number = null;
			}
		}
		return number;
	}

	/**
	 * create a list of URLs
	 */
	private List<String> getdownloadURLs() {
		List<String> urlList = null;
		try {
			urlList = ctrl.downloadURLs();
			System.out.println("download URLs:");
			System.out.println(urlList);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return urlList;
	}

	/**
	 * download the articles
	 */
	private void getDownloadArticles() {
		System.out.println("for sequential download type 's', for parallel type 'p', or any other key to get back to the menu");
		Scanner scanner = new Scanner(System.in);
		String choice = scanner.nextLine();
		int numberDownloads = 0;
		long start = 0;
		long end = 0;
		if (choice.equals("s")) {
			start = System.currentTimeMillis();
			try {
				numberDownloads = ctrl.downloadArticles(new SequentialDownloader());
			} catch (NewsAPIException e) {
				System.err.println(e.getMessage() + e.getCause());
			} catch (Exception e) {
				System.err.println("An unexpected exception has occurred: " + e.getCause());
				e.printStackTrace();
			}
			end = System.currentTimeMillis();
		} if (choice.equals("p")) {
			start = System.currentTimeMillis();
			try {
				numberDownloads = ctrl.downloadArticles(new ParallelDownloader());
			} catch (NewsAPIException e) {
				System.out.println(e.getMessage() + " " + e.getCause());
				e.printStackTrace();
			} catch (Exception e) {
				System.err.println("An unexpected exception has occurred: " + e.getCause());
				e.printStackTrace();
			}
			end = System.currentTimeMillis();
		}
		System.out.println(numberDownloads + " articles downloaded");
		System.out.println("Time: " + (end-start) + " ms");
	}

	/**
	 *  Get the author with the longest name
	 *
	 */
	private void getShortestNameOfAuthors() {
		try {
			String result = ctrl.getShortestNameOfAuthors();
			System.out.println("The shortest author name is " + result);
		} catch (NewsAPIException e) {
			System.out.println("Please load data first!");
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Get the amount of articles
	 *
	 */
	private void getArticleCount() {
		try {
			long result = ctrl.getArticleCount();
			System.out.println(result + " articles loaded");
		} catch (NewsAPIException e) {
			System.out.println("Please load data first!");
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
	}

	/**
	 *  Sort the articles based on the length of the title
	 *
	 */
	protected void getSortArticlesByLongestTitle() {
		try {
			String result = ctrl.getSortArticlesByLongestTitle();
			System.out.println("Longest title: " + result);
		} catch (NewsAPIException e) {
			System.out.println("Please load data first!");
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
	}


	/**
	 * Get the Provider with the most articles
	 *
	 */

	protected void getProviderWithMostArticles() {
		try {
			String result = ctrl.getProviderWithMostArticles();
			System.out.println("Most articles: " + result);
		} catch (NewsAPIException e) {
			System.out.println("Please load data first!");
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
	}

	/**
	 *  load the headline for austrian news
	 *
	 */
	public void getTopHeadlinesAustria() {
		NewsApi newsApi = new NewsApiBuilder()
				.setApiKey(Controller.APIKEY)
				.setQ("corona")
				.setEndPoint(Endpoint.TOP_HEADLINES)
				.setSourceCountry(Country.at)
				.createNewsApi();

		String result = null;
		try {
			result = ctrl.process(newsApi);
		} catch (NewsAPIException e) {
			System.err.println("Error occured: " + e.getMessage());
		} catch (Exception e){
			System.out.println(e.getMessage());
		}

		System.out.println(result);
	}

	/**
	 *  load all news from April
	 *
	 */
	public void getAllNewsBitcoin() {
		NewsApi newsApi = new NewsApiBuilder()
				.setApiKey(Controller.APIKEY)
				.setQ("bitcoin")
				.setEndPoint(Endpoint.EVERYTHING)
				.setFrom("2021-06-14")
				.setPageSize("100")
				.createNewsApi();

		String result = null;
		try {
			result = ctrl.process(newsApi);
		} catch (NewsAPIException e) {
			System.err.println("Error occured: " + e.getMessage());
		} catch (Exception e){
			System.out.println(e.getMessage());
		}

		System.out.println(result);
	}

}
