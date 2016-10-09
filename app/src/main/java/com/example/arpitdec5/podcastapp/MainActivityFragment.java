package com.example.arpitdec5.podcastapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
public class MainActivityFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener , LoaderManager.LoaderCallbacks<Cursor> {

    RecyclerView recyclerView ;
    Activity mActivity;
    String url1 = "https://itunes.apple.com/search?term=podcast&country=";
    String url2="us";
    String[] images;
    GoogleApiClient googleApiClient;
    double l1=27.90;
    double l2=45.80;
    String country="United States";
    TelephonyManager tm;
    AdView adView;
    private int LOADER_ID=1;
    ListAdapterr adapter;

    public MainActivityFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.grid);
        country = mActivity.getResources().getConfiguration().locale.getDisplayCountry();
        tm = (TelephonyManager) mActivity.getSystemService(Context.TELEPHONY_SERVICE);
        adView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        //getting the latitude and longitude of users location
        if(checkPlayServices()) {
            googleApiClient = new GoogleApiClient.Builder(mActivity)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        //by default arranging the movies by popularity
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width/175.00);

        //setting the adapter for siplaying results
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager linearLayoutManager = new GridLayoutManager(mActivity,spanCount);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ListAdapterr(mActivity , null);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID , null , this);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, mActivity, 1000).show();
            } else {
                Toast.makeText(mActivity,R.string.device_not_supported, Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(googleApiClient!=null)
            googleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(adView!=null)
            adView.pause();
        googleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adView!=null)
            adView.resume();

        //getting country name
        url2 = tm.getSimCountryIso();

        //getting internet info
        ConnectivityManager conn = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            new FetchPodcastTask().execute(url1 + url2);
        } else
            Toast.makeText(mActivity, R.string.wifi_off , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        checkPlayServices();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(adView!=null)
            adView.destroy();
    }

    //performing the action to fetch json string from the api through a seperate thread
    public class FetchPodcastTask extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = FetchPodcastTask.class.getSimpleName();
        //performs the required action to fetch json string
        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadUrl(params[0]);
            } catch (IOException e) {
                return "invalid !";
            }
        }
        //fetches json string from the inputBuffer
        // this is where you get the json string and do whatever you want to do over here to fetch the info .
        @Override
        protected void onPostExecute(String s) {

            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray array = jsonObject.getJSONArray("results");
                    if(array!=null) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject c = array.getJSONObject(i);
                            if (c != null && c.getString("artworkUrl600")!=null && c.getString("artistName")!=null && c.getString("collectionName")!=null && c.getString("primaryGenreName")!=null && c.getString("country")!=null) {
                                String imageUrl = c.getString("artworkUrl600");
                                String title = c.getString("artistName");
                                String CollectionName = c.getString("collectionName");
                                String GenreName = c.getString("primaryGenreName");
                                String TrackCount = c.getString("trackCount");
                                String country = c.getString("country");
                                //adding into the database provided none of these values are null
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(PodcastDescriptionHandler.KEY_ARTIST_NAME, title);
                                contentValues.put(PodcastDescriptionHandler.KEY_COLLECTION_NAME, CollectionName);
                                contentValues.put(PodcastDescriptionHandler.KEY_GENRE_NAME, GenreName);
                                contentValues.put(PodcastDescriptionHandler.KEY_TRACK_COUNT, TrackCount);
                                contentValues.put(PodcastDescriptionHandler.KEY_COUNTRY, country);
                                contentValues.put(PodcastDescriptionHandler.KEY_IMAGE_URL, imageUrl);
                                Cursor cursor = mActivity.getContentResolver().query(PodcastContentProvider.CONTENT_URI, null, null, null, "artistName");
                                int f = 0;
                                while (cursor.getCount() != 0 && cursor.moveToNext()) {
                                    if (cursor.getString(1).equals(title)) {
                                        f = 1;
                                        break;
                                    }
                                }
                                if (f == 0) {
                                    Uri uri = mActivity.getContentResolver().insert(PodcastContentProvider.CONTENT_URI, contentValues);
                                }
                                //arrayList.add(imageUrl);
                            }
                        }
                    }
                    //grid.addItemDecoration(new com.example.arpitdec5.popularmovies.DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mActivity, R.string.error_taking_data , Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        String result = null;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } catch (Exception e) {
        } finally {
            try {
                if (is != null) is.close();
            } catch (Exception squish) {
            }
        }
        return result;
    }

    public class ListAdapterr extends RecyclerView.Adapter<ListAdapterr.ViewHolder> {

        Cursor dataCursor;
        Activity mActivity;

        public ListAdapterr(Activity activity , Cursor cursor)
        {
            mActivity = activity;
            dataCursor = cursor;
        }

        public Cursor swapCursor(Cursor cursor) {
            if (dataCursor == cursor) {
                return null;
            }
            Cursor oldCursor = dataCursor;
            this.dataCursor = cursor;
            if (cursor != null) {
                this.notifyDataSetChanged();
            }
            return oldCursor;
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

            dataCursor.moveToPosition(position);
            final String text = dataCursor.getString(2);

            Picasso.with(mActivity)
                    .load(text)
                    .placeholder(R.drawable.error)
                    .error(R.drawable.error)
                    .resize(350,350)
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mActivity , PodcastDescription.class);
                    intent.putExtra("imageUrl" , text);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            if(dataCursor == null)
                return 0;
            else
                return dataCursor.getCount();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(mActivity, R.string.connection_failed , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(mActivity, R.string.connection_success , Toast.LENGTH_SHORT).show();

        Location l = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (googleApiClient != null) {
            l1 = (double) (l.getLatitude());
            l2 = (double) (l.getLongitude());
        }
        else {
            //tr1.setText("Not Available");
            //tr2.setText("Not Available");
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        googleApiClient.connect();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), PodcastContentProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}