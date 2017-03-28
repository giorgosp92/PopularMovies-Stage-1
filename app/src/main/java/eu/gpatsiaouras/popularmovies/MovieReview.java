package eu.gpatsiaouras.popularmovies;

public class MovieReview {
    private String mId;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    public MovieReview(String id, String author, String content, String url) {
        mId = id;
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    public String getAuthor() {
        return this.mAuthor;
    }
    public String getContent() {
        return this.mContent;
    }
}
