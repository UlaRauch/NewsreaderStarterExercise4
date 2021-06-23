package at.ac.fhcampuswien.newsanalyzer.ctrl;

public class NewsAPIException extends Exception{
    public NewsAPIException() {
        super();
    }
    public NewsAPIException(String message) {
        super(message);
    }
    public NewsAPIException(String message, Throwable cause) {
        super(message, cause);
    }

}
