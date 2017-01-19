package com.example.jonsson.movieviewer;

/**
 * Created by Jonsson on 2016-10-18.
 */
public class MovieData {

    MovieData() {}

    MovieData(String[] data) {
        int counter = 0;
        this.title = data[counter++];
        this.averageVote = data[counter++];
        this.overview = data[counter++];
        this.releaseDate = data[counter++];
        this.posterSmallURL = data[counter++];
        this.posterLargeURL = data[counter++];
    }

    String[] toStringArray() {
        String[] strings = {
                this.title,
                this.averageVote,
                this.overview,
                this.releaseDate,
                this.posterSmallURL,
                this.posterLargeURL};

        return strings;
    }

    String title;
    String averageVote;
    String overview;
    String releaseDate;
    String posterSmallURL;
    String posterLargeURL;

}
