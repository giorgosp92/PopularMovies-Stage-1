package eu.gpatsiaouras.popularmovies;

public class MovieVideo {
    private String mKey;
    private String mName;
    private String mSite;
    private String mType;

    public MovieVideo(String key, String name, String site, String type) {
        mKey = key;
        mName = name;
        mSite = site;
        mType = type;
    }

    public String getKey() {
        return this.mKey;
    }
}
