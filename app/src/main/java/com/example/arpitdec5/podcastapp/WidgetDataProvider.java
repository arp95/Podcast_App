package com.example.arpitdec5.podcastapp;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arpitdec5 on 09-10-2016.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    List<String> collection = new ArrayList<>();
    private Context context;
    private Intent intent;
    private Cursor mCursor;
    private static final String TAG = "WidgetDataProvider";

    public WidgetDataProvider(Context context, Intent intent, Cursor cursor) {
        this.context = context;
        this.intent = intent;
        mCursor = cursor;
    }

    @Override public void onCreate() {
    }

    @Override public void onDataSetChanged() {
    }

    @Override public void onDestroy() {
        mCursor.close();
    }

    @Override public int getCount() {
        Log.e(TAG, "getCount: " + mCursor.getCount());
        return mCursor.getCount();
    }

    @Override public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        mCursor.moveToPosition(position);
        remoteViews.setTextViewText(R.id.symbol,
                mCursor.getString(mCursor.getColumnIndex(PodcastDescriptionHandler.KEY_ARTIST_NAME)));
        remoteViews.setTextViewText(R.id.change,
                mCursor.getString(mCursor.getColumnIndex(PodcastDescriptionHandler.KEY_COLLECTION_NAME)));
        Log.e(TAG, "getViewAt: " + mCursor.getString(mCursor.getColumnIndex(PodcastDescriptionHandler.KEY_GENRE_NAME)));
        return remoteViews;
    }

    @Override public RemoteViews getLoadingView() {
        return null;
    }

    @Override public int getViewTypeCount() {
        return 1;
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public boolean hasStableIds() {
        return true;
    }
}
