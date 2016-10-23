package com.example.jonsson.movieviewer;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private enum MovieDisplayMode {TOP_RATED, MOST_POPULAR}

    public final static String INTENT_EXTRA_MESSAGE = "com.example.jonsson.movieviewer.EXTRA_MESSAGE";

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String MOVIEDB_API_KEY = "c3904d3a83cbe46c87a641fcbb7673e5";
    private ArrayAdapter<MovieData> mMovieDataAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        updateMovieScreen(MovieDisplayMode.MOST_POPULAR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout.
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Create an adapter to present the images.
        mMovieDataAdapter = new ArrayAdapter<MovieData>(
                // Current context (fragment's parent activity)
                getActivity(),
                // ID of the layout to use to populate the GridView.
                R.layout.posterimages,
                // ID of the specific ImageView to populate.
                R.id.gridImageView,
                new ArrayList<MovieData>()) {

            /*
             * Here we override the getView method to instruct the adapter where to get data to
             * display in its View.
             */
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.posterimages, parent, false);
                }

                //Drawable myImage = ResourcesCompat.getDrawable(getResources(), R.drawable.anal_machine_intelligence, null);

                if (position <= getCount()) {
                    // Get the ImageView and TextView in the view layout to post in.
                    ImageView imageView = (ImageView) convertView.findViewById(R.id.gridImageView);
                    TextView textView = (TextView) convertView.findViewById(R.id.gridTextView);

                    // Loading image with Picasso.
                    Picasso.with(getActivity())
                            .load(getItem(position).posterURL)
                            .into(imageView);

                    // Set movie title as subtext.
                    textView.setText(getItem(position).title);
                }

                return convertView;
            }
        };

        // Get the gridview and assign the adapter and click listener.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        if (gridView != null) {
            gridView.setAdapter(mMovieDataAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Create intent to launch a detailed activity.
                    Intent detailActivityIntent = new Intent(getActivity(), DetailActivity.class);

                    // Include which movie was clicked in the Intent info (temporarily).
                    detailActivityIntent.putExtra(INTENT_EXTRA_MESSAGE, String.valueOf(position));

                    // Include the movie info.
                    MovieData clickedMovieData = mMovieDataAdapter.getItem(position);
                    detailActivityIntent.putExtra(DetailActivity.EXTRA_MOVIE_INFO, clickedMovieData.toStringArray());

                    // Include the movie Uri in the Intent.
                    //detailActivityIntent.setData()
                    startActivity(detailActivityIntent);
                }
            });
        }
        else {
            Log.e(LOG_TAG, "GridView not found.");
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_display_popular) {
            updateMovieScreen(MovieDisplayMode.MOST_POPULAR);
            return true;
        }
        else if (id == R.id.action_display_toprated) {
            updateMovieScreen(MovieDisplayMode.TOP_RATED);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getPopularMoviesURL() {
        return createMovieDataBaseURL("popular");
    }

    private String getTopRatedMoviesURL() {
        return createMovieDataBaseURL("top_rated");
    }

    private String createMovieDataBaseURL(String listingType) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(listingType) // A bit weird that this is a path since it doesn't end with /.
                .appendQueryParameter("api_key", MOVIEDB_API_KEY);

        String urlString = uriBuilder.build().toString();
        return urlString;
    }

    /*
     * Returns the whole URL path to a poster, given the name.
     */
    private String getPosterURL(String posterName) {
        String URLbase = "http://image.tmdb.org/t/p/";
        String imageWidth = "w185/";
        String posterURL = URLbase + imageWidth + posterName;
        return posterURL;
    }

    /*
     * Updates content on main movie display screen.
     */
    private void updateMovieScreen(MovieDisplayMode displayMode) {
        String queryString;
        switch (displayMode) {
            case MOST_POPULAR:
                queryString = getPopularMoviesURL();
                break;
            case TOP_RATED:
                queryString = getTopRatedMoviesURL();
                break;
            default:
                queryString = "";
                break;
        }

        Log.i(LOG_TAG, queryString);

        new FetchMovieDBTask().execute(queryString);

    }

    private class FetchMovieDBTask extends AsyncTask<String, Void, MovieData[]> {

        // Performs API call and parses the data to readable JSON format.

        @Override
        protected MovieData[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String returnJsonStr = null;
            //String[] movieStrings;
            MovieData[] moviesData;

            try {
                String URLstring = params[0];
                //Log.v(LOG_TAG, URLstring);

                URL url = new URL(URLstring);

                // Check permissions for internet.
                // checkSelfPermission();

                // Create the request to API, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                returnJsonStr = buffer.toString();
                moviesData = getMovieDataFromJson(returnJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting
                // to parse it.
                return null;
            } catch (SecurityException e) {
                Log.e(LOG_TAG, "Error ", e);
                // Permissions were not set correctly.
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error: ", e);
                // Error in JSON parsing.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return moviesData;
        }

        @Override
        protected void onPostExecute(MovieData[] result) {
            if (result != null) {
                mMovieDataAdapter.clear();
                for (MovieData movieData : result) {
                    Log.i(LOG_TAG, movieData.title);
                    // Post movies to the display adapter.
                    mMovieDataAdapter.add(movieData);
                }
            }
        }

        /**
         * Take the String representing the complete movie list in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private MovieData[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MDB_RESULTS = "results";
            final String MDB_TITLE = "title";
            final String MDB_VOTE_AVERAGE = "vote_average";
            final String MDB_POSTER_PATH = "poster_path";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_RESULTS);

            //String[] resultStrs = new String[numDays];
            final int numberOfMovies = movieArray.length();
            String[] movieStrs = new String[numberOfMovies]; // TEMP for display.
            MovieData[] moviesData = new MovieData[numberOfMovies];
            for(int i = 0; i < numberOfMovies; i++) {
                // Get the JSON object representing the movie.
                JSONObject movieInfo = movieArray.getJSONObject(i);

                String movieTitle = movieInfo.getString(MDB_TITLE);
                String movieVoteAverage = movieInfo.getString(MDB_VOTE_AVERAGE);
                String moviePosterPath = movieInfo.getString(MDB_POSTER_PATH);

                MovieData movieData = new MovieData(); // Initialize empty.
                movieData.title = movieTitle;
                movieData.averageVote = movieVoteAverage;
                movieData.posterURL = getPosterURL(moviePosterPath);

                movieStrs[i] = movieTitle + " with average: " + movieVoteAverage;
                moviesData[i] = movieData;
            }

            for (String s : movieStrs) {
                Log.v(LOG_TAG, "Movie entry: " + s);
            }

            return moviesData;
        }
    }

    // API stuff
    // https://api.themoviedb.org/3/movie/550?api_key=c3904d3a83cbe46c87a641fcbb7673e5
    // v3 auth c3904d3a83cbe46c87a641fcbb7673e5
    // v4 auth iYjc2NzNlNSIsInN1YiI6IjU4MDNkYzAyYzNhMzY4MTZmMjAwM2E1OCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.3S6w_cw_hIU31F0euDUUatBTzEIzxEND1bOmzcTEIIQ
}
