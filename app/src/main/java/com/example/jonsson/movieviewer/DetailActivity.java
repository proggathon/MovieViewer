package com.example.jonsson.movieviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    static final String EXTRA_MOVIE_INFO = "com.example.jonsson.movieviewer.EXTRA_MOVIE_INFO";
    private MovieData mMovieData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the intent extras.
        Intent spawnIntent = getIntent();
        //String intentMessage = spawnIntent.getStringExtra(MainActivityFragment.INTENT_EXTRA_MESSAGE);
        String[] movieMessage = spawnIntent.getStringArrayExtra(EXTRA_MOVIE_INFO);
        mMovieData = new MovieData(movieMessage);
        TextView headlineTextView = (TextView) findViewById(R.id.headlineTextView);
        TextView overviewTextView = (TextView) findViewById(R.id.overviewTextView);
        ImageView posterImageView = (ImageView) findViewById(R.id.posterImageView);
        headlineTextView.setText(mMovieData.title);
        overviewTextView.setText(mMovieData.overview);
        Picasso.with(this)
                .load(mMovieData.posterSmallURL)
                .into(posterImageView);
    }

}
