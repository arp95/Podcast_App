package com.example.arpitdec5.podcastapp;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class PodcastDescriptionFragment extends Fragment {

    public PodcastDescriptionFragment() {
    }

    Activity mActivity;
    ImageView imageView;
    TextView collection , genre , country , track;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_podcast_description, container, false);

        String image,podcast_collection = "Global News Podcast",podcast_genre = "News",podcast_track="35",podcast_country = "India";
        Intent intent  = mActivity.getIntent();
        Bundle bundle = intent.getExtras();
        image = bundle.getString("imageUrl");

        imageView = (ImageView) view.findViewById(R.id.podcastImage);
        collection = (TextView) view.findViewById(R.id.podCollection);
        genre = (TextView) view.findViewById(R.id.podGenre);
        track = (TextView) view.findViewById(R.id.podTrack);
        country = (TextView) view.findViewById(R.id.podCountry);

        Picasso.with(mActivity)
                .load(image)
                .error(R.drawable.error)
                .placeholder(R.drawable.error)
                .resize(350,350)
                .into(imageView);

        Cursor cursor = mActivity.getContentResolver().query(PodcastContentProvider.CONTENT_URI , null , null , null , "artistName");
        while(cursor.getCount()!=0 && cursor.moveToNext())
        {
            if(cursor.getString(2).equals(image))
            {
                podcast_collection = cursor.getString(3);
                podcast_genre = cursor.getString(4);
                podcast_track = cursor.getString(5);
                podcast_country = cursor.getString(6);
            }
        }

        collection.setText(podcast_collection);
        genre.setText(podcast_genre);
        track.setText(podcast_track);
        country.setText(podcast_country);

        return view;
    }
}