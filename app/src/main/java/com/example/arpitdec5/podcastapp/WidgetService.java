package com.example.arpitdec5.podcastapp;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

/**
 * Created by arpitdec5 on 09-10-2016.
 */
public class WidgetService extends RemoteViewsService {

    private static final String TAG = "WidgetService";
    @Override public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //return remote view factory
        ArrayList list= new ArrayList();
        Cursor cursor = this.getContentResolver().query(PodcastContentProvider.CONTENT_URI, new String[] {
                PodcastDescriptionHandler.KEY_ID, PodcastDescriptionHandler.KEY_ARTIST_NAME, PodcastDescriptionHandler.KEY_COUNTRY, PodcastDescriptionHandler.KEY_IMAGE_URL,
                PodcastDescriptionHandler.KEY_COLLECTION_NAME, PodcastDescriptionHandler.KEY_GENRE_NAME, PodcastDescriptionHandler.KEY_TRACK_COUNT}, PodcastDescriptionHandler.KEY_GENRE_NAME + " = ?", new String[] { "1" }, null);
        return new WidgetDataProvider(this, intent,cursor);
    }
}
