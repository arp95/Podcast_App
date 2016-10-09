package com.example.arpitdec5.podcastapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by arpitdec5 on 05-10-2016.
 */
public class PodcastDescriptionHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PODCASTDB";
    public static final String TABLE_NAME = "podcasts";
    public static final String KEY_ID = "id";
    public static final String KEY_ARTIST_NAME = "artistName";
    public static final String KEY_IMAGE_URL = "imageUrl";
    public static final String KEY_COLLECTION_NAME = "collectionName";
    public static final String KEY_GENRE_NAME = "genreName";
    public static final String KEY_TRACK_COUNT = "trackCount";
    public static final String KEY_COUNTRY = "country";

    public PodcastDescriptionHandler(Context context) {
        super(context , DATABASE_NAME , null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_ARTIST_NAME + " VARCHAR ," + KEY_IMAGE_URL + " VARCHAR ," + KEY_COLLECTION_NAME + " VARCHAR ," + KEY_GENRE_NAME + " VARCHAR ," + KEY_TRACK_COUNT + " VARCHAR ," + KEY_COUNTRY + " VARCHAR);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /*
    public void insert(String artist_name ,String image_url , String collection_name , String genre_name , String track_count , String country , String fav ){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(KEY_ARTIST_NAME , artist_name);
        cv.put(KEY_IMAGE_URL , image_url);
        cv.put(KEY_COLLECTION_NAME , collection_name);
        cv.put(KEY_GENRE_NAME , genre_name);
        cv.put(KEY_TRACK_COUNT  , track_count);
        cv.put(KEY_COUNTRY , country);
        cv.put(KEY_FAVORITE , fav);

        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    public boolean present_title(String artist_name)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String SELECT_QUERY = "SELECT * FROM " + TABLE_NAME + ";";
        Cursor c = db.rawQuery(SELECT_QUERY , null);
        int f=0;
        while (c.getCount()!=0 && c.moveToNext())
        {
            String Artist_Name = c.getString(0);
            if(Artist_Name.equals(artist_name))
            {
                f=1;
                break;
            }
        }

        if(f==0)
            return true;
        else
            return false;
    }

    public String get_artistName(String image)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String SELECT_QUERY = "SELECT * FROM " + TABLE_NAME + ";";
        Cursor c = db.rawQuery(SELECT_QUERY , null);

        String str = null;
        while (c.getCount()!=0 && c.moveToNext())
        {
            String Image = c.getString(1);
            String artist_name = c.getString(0);
            if(Image.equals(image))
            {
                str = artist_name;
            }
        }
        return str;
    }

    public String get_collectionName(String image)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String SELECT_QUERY = "SELECT * FROM " + TABLE_NAME + ";";
        Cursor c = db.rawQuery(SELECT_QUERY , null);

        String str = null;
        while (c.getCount()!=0 && c.moveToNext())
        {
            String Image = c.getString(1);
            String collection_name = c.getString(2);
            if(Image.equals(image))
            {
                str = collection_name;
            }
        }
        return str;
    }

    public String get_genreName(String image)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String SELECT_QUERY = "SELECT * FROM " + TABLE_NAME + ";";
        Cursor c = db.rawQuery(SELECT_QUERY , null);

        String str = null;
        while (c.getCount()!=0 && c.moveToNext())
        {
            String Image = c.getString(1);
            String genre_name = c.getString(3);
            if(Image.equals(image))
            {
                str = genre_name;
            }
        }
        return str;
    }

    public String get_trackCount(String image)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String SELECT_QUERY = "SELECT * FROM " + TABLE_NAME + ";";
        Cursor c = db.rawQuery(SELECT_QUERY , null);

        String str = null;
        while (c.getCount()!=0 && c.moveToNext())
        {
            String Image = c.getString(1);
            String track_count = c.getString(4);
            if(Image.equals(image))
            {
                str = track_count;
            }
        }
        return str;
    }

    public String get_country(String image)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String SELECT_QUERY = "SELECT * FROM " + TABLE_NAME + ";";
        Cursor c = db.rawQuery(SELECT_QUERY , null);

        String str = null;
        while (c.getCount()!=0 && c.moveToNext())
        {
            String Image = c.getString(1);
            String country = c.getString(5);
            if(Image.equals(image))
            {
                str = country;
            }
        }
        return str;
    }
    */
}
