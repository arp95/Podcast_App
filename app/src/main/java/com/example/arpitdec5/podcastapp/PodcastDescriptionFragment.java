package com.example.arpitdec5.podcastapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PodcastDescriptionFragment extends Fragment {

    public PodcastDescriptionFragment() {
    }

    String url1,url2;
    String image,podcast_name = "Global News Podcast",podcast_genre = "News",podcast_track="35",podcast_country = "India";
    Activity mActivity;
    ImageView imageView;
    TextView collection , genre , country , track;
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    ArrayList<String> arrayList;
    RecyclerView recyclerView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_podcast_description, container, false);

        Intent intent  = mActivity.getIntent();
        Bundle bundle = intent.getExtras();
        image = bundle.getString("imageUrl");

        recyclerView = (RecyclerView) view.findViewById(R.id.episode);
        arrayList = new ArrayList<String>();
        requestQueue = Volley.newRequestQueue(mActivity);
        imageView = (ImageView) view.findViewById(R.id.podcastImage);
        collection = (TextView) view.findViewById(R.id.podCollection);
        genre = (TextView) view.findViewById(R.id.podGenre);
        track = (TextView) view.findViewById(R.id.podTrack);
        country = (TextView) view.findViewById(R.id.podCountry);

        Picasso.with(mActivity)
                .load(image)
                .error(R.drawable.error)
                .placeholder(R.drawable.error)
                .resize(100,100)
                .into(imageView);

        Cursor cursor = mActivity.getContentResolver().query(PodcastContentProvider.CONTENT_URI , null , null , null , "artistName");
        while(cursor.getCount()!=0 && cursor.moveToNext())
        {
            if(cursor.getString(2).equals(image))
            {
                podcast_name = cursor.getString(1);
                podcast_genre = cursor.getString(4);
                podcast_track = cursor.getString(5);
                podcast_country = cursor.getString(6);
            }
        }

        collection.setText(podcast_name);
        genre.setText(podcast_genre);
        track.setText(podcast_track);
        country.setText(podcast_country);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        url1 = "https://itunes.apple.com/search?term=";
        url2 = "&limit=25";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET , url1 + podcast_name + url2 , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray array = response.getJSONArray("results");
                            if(array!=null) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject c = array.getJSONObject(i);
                                    if (c != null && c.getString("artworkUrl100")!=null) {

                                        String imageUrl = c.getString("artworkUrl100");
                                        arrayList.add(imageUrl);
                                        ListAdapter listAdapter = new ListAdapter(mActivity, arrayList);
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity , LinearLayoutManager.HORIZONTAL , false);
                                        recyclerView.setLayoutManager(linearLayoutManager);
                                        recyclerView.setAdapter(listAdapter);
                                    }
                                }
                            }
                            //grid.addItemDecoration(new com.example.arpitdec5.popularmovies.DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mActivity, R.string.error_taking_data, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mActivity , "Error while taking data !!" , Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        ArrayList<String> array;
        Activity mActivity;

        public ListAdapter(Activity activity , ArrayList<String> arrayList)
        {
            mActivity = activity;
            array = arrayList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.image);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridlayout_item , parent ,false);
            return (new ViewHolder(view));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            final String text = array.get(position);

            Picasso.with(mActivity)
                    .load(text)
                    .placeholder(R.drawable.error)
                    .error(R.drawable.error)
                    .resize(350,350)
                    .into(holder.imageView);

            /*
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mActivity , PodcastDescription.class);
                    intent.putExtra("imageUrl" , text);
                    startActivity(intent);
                }
            });
            */
        }

        @Override
        public int getItemCount() {
            return array.size();
        }
    }

}