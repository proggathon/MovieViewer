package com.example.jonsson.movieviewer;

/**
 * Created by Jonsson on 2016-10-18.
 */
public class MovieData {

    MovieData() {}

    MovieData(String[] data) {
        this.title = data[0];
        this.averageVote = data[1];
        this.posterURL = data[2];
    }

    String[] toStringArray() {
        String[] strings = {
                this.title,
                this.averageVote,
                this.posterURL};

        return strings;
    }

    String title;
    String averageVote;
    String posterURL;

}
