package eu.gpatsiaouras.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;

import eu.gpatsiaouras.popularmovies.Utilities.NetworkUtilities;


public class Movie implements Parcelable {
    private static final String TAG = Movie.class.getSimpleName();
    /* Movie Title */
    private String title;
    /* Movie ID */
    private int id;
    private String image_path;

    public Movie(int id, String title, String image_path) throws ParseException {
        this.id = id;
        this.title = title;
        if (String.valueOf(image_path.charAt(0)).equals("/")) {
            this.image_path = image_path.substring(1);/* First letter is a slash. Remove the slash*/
        } else {
            this.image_path = image_path;
        }
    }

    /* Returns the image url of this object*/
    public String getImagePath() { return this.image_path;}
    public Uri getImageUri() {
        return NetworkUtilities.buildImageURI(this.image_path);
    }
    public String getTitle() {
        return this.title;
    }
    public int getId() {
        return this.id;
    }
    /* Remaining functions for the rest of the attributes will be created upon need*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(image_path);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            try {
                Movie movie = new Movie(source.readInt(), source.readString(), source.readString());
                return movie;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
