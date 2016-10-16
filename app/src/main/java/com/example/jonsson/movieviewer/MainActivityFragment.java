package com.example.jonsson.movieviewer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public final static String INTENT_EXTRA_MESSAGE = "com.example.jonsson.movieviewer.EXTRA_MESSAGE";

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private ArrayAdapter<Integer> mMoviePosterAdapter;
    private ArrayList<Integer> mMoviePosterList;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout.
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMoviePosterList = new ArrayList<>();
        mMoviePosterList.add(R.drawable.anal_machine_intelligence);
        mMoviePosterList.add(R.drawable.fint_vader_april);
        mMoviePosterList.add(R.drawable.print2x);
        mMoviePosterList.add(R.drawable.chessboard);
        mMoviePosterList.add(R.drawable.starwhawk);
        mMoviePosterList.add(R.drawable.figcone);
        mMoviePosterList.add(R.drawable.eqn1270838124);
        mMoviePosterList.add(R.drawable.exjobbsframlaggning_crop);
        mMoviePosterList.add(R.drawable.parsplgv);
        mMoviePosterList.add(R.drawable.spaps);

        // Create an adapter to present the images.
        mMoviePosterAdapter = new ArrayAdapter<Integer>(
                // Current context (fragment's parent activity)
                getActivity(),
                // ID of the layout to use to populate the GridView.
                R.layout.posterimages,
                // ID of the specific ImageView to populate.
                R.id.gridImageView,
                new ArrayList<Integer>()) {

            public int getCount() {
                return mMoviePosterList.size();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.posterimages, parent, false);
                }

                ImageView imageView = (ImageView) convertView.findViewById(R.id.gridImageView);
                Drawable myImage = ResourcesCompat.getDrawable(getResources(), R.drawable.anal_machine_intelligence, null);

                if (position <= mMoviePosterList.size()) {
                    // Loading image with Picasso.
                    Picasso.with(getActivity())
                            .load(mMoviePosterList.get(position))
                            .into(imageView);
                }

                return convertView;
            }
        };

        // Get the gridview and assign the adapter and click listener.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        if (gridView != null) {
            gridView.setAdapter(mMoviePosterAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getActivity(), "Item " + position + " clicked.", Toast.LENGTH_SHORT).show();

                    // Create intent to launch a detailed activity.
                    Intent detailActivityIntent = new Intent(getActivity(), DetailActivity.class);
                    // Include which movie was clicked in the Intent info (temporarily).
                    detailActivityIntent.putExtra(INTENT_EXTRA_MESSAGE, String.valueOf(position));
                    // Include the movie Uri in the Intent.
                    //detailActivityIntent.setData()
                    startActivity(detailActivityIntent);
                }
            });
        }
        else {
            Log.e(LOG_TAG, "GridView not found.");
        }


        // TODO Write function that updates the content (using the mMoviePosterAdapter)

        return rootView;
    }

    //private class ImageAdapter extends
}
