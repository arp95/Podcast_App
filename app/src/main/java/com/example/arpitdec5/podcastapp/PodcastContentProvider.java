package com.example.arpitdec5.podcastapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by arpitdec5 on 08-10-2016.
 */
public class PodcastContentProvider extends ContentProvider {

    private PodcastDescriptionHandler database;

    // used for the UriMacher
    private static final String AUTHORITY = "com.example.arpitdec5.podcastapp.PodcastContentProvider";
    private static final String PODCASTS_TABLE = "podcasts";

    //uri to access the podcasts table
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PODCASTS_TABLE);

    //first uri to access the entire podcast table and the second for accessing a specific row
    public static final int PODCASTS = 1;
    public static final int PODCASTS_ID = 2;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, PODCASTS_TABLE, PODCASTS);
        sURIMatcher.addURI(AUTHORITY, PODCASTS_TABLE + "/#", PODCASTS_ID);
    }

    @Override
    public boolean onCreate() {
        database = new PodcastDescriptionHandler(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(PodcastDescriptionHandler.TABLE_NAME);

        //getting the uri id
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case PODCASTS:
                break;
            case PODCASTS_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(PodcastDescriptionHandler.KEY_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        //getting the uri type
        int uriType = sURIMatcher.match(uri);

        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case PODCASTS:
                id = sqlDB.insert(PodcastDescriptionHandler.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(PODCASTS_TABLE + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        //getting the uri id
        int uriType = sURIMatcher.match(uri);

        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case PODCASTS:
                rowsDeleted = sqlDB.delete(PodcastDescriptionHandler.TABLE_NAME, selection, selectionArgs);
                break;
            case PODCASTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(PodcastDescriptionHandler.TABLE_NAME, PodcastDescriptionHandler.KEY_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(PodcastDescriptionHandler.TABLE_NAME, PodcastDescriptionHandler.KEY_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //getting the uri id
        int uriType = sURIMatcher.match(uri);

        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case PODCASTS:
                rowsUpdated = sqlDB.update(PodcastDescriptionHandler.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PODCASTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(PodcastDescriptionHandler.TABLE_NAME, values, PodcastDescriptionHandler.KEY_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(PodcastDescriptionHandler.TABLE_NAME, values, PodcastDescriptionHandler.KEY_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

}