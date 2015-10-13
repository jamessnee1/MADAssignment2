package jamessnee.com.madassignment2.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jamessnee on 21/09/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    public static DatabaseHandler instance;
    public static final String DATABASE_NAME = "Moviedb.db";
    public static final String TABLE_NAME = "movies";

    public static final String COLUMN1 = "ID";
    public static final String COLUMN2 = "TITLE";
    public static final String COLUMN3 = "YEAR";
    public static final String COLUMN4 = "SHORT_PLOT";
    public static final String COLUMN5 = "FULL_PLOT";
    public static final String COLUMN6 = "POSTER";
    public static final String COLUMN7 = "RATING";



    //getter
    public static synchronized DatabaseHandler getInstance(Context context){

        if (instance == null){
            instance = new DatabaseHandler(context.getApplicationContext());
        }

        return instance;

    }


    private DatabaseHandler(Context context) {
        //context, name, version
        super(context, DATABASE_NAME, null, 1);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME + " (ID TEXT PRIMARY KEY, TITLE TEXT, YEAR INTEGER, SHORT_PLOT TEXT, FULL_PLOT TEXT, POSTER TEXT, RATING INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    //method to delete all data
    public void deleteAllData(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(this.TABLE_NAME, null, null);

    }

    //method to insert data
    public boolean insertMovieData(Movie movie){


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN1, movie.getId());
        contentValues.put(COLUMN2, movie.getTitle());
        contentValues.put(COLUMN3, movie.getYear());
        contentValues.put(COLUMN4, movie.getShort_plot());
        contentValues.put(COLUMN5, movie.getFull_plot());
        contentValues.put(COLUMN6, movie.getPoster());
        contentValues.put(COLUMN7, movie.getRating());

        long result = db.insert(TABLE_NAME, null, contentValues);

        //error checking
        if (result == -1){
            return false;
        }

        return true;
    }

    //retrieve data
    public Cursor retrieveAllData(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor retrieved = db.rawQuery("select * from " + TABLE_NAME, null);
        return retrieved;
    }

    public Cursor retrieveMovieData(String query){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor retrieved = db.rawQuery("select * from " + TABLE_NAME + " where title LIKE '" + query + "'", null);
        return retrieved;
    }

}
