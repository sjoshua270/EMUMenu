package edu.emuapps.emuapplite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by sjoshua270 on 10/3/2014.
 */
public class SQLReader extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "FoodRatings.db";
	public static abstract class Entry implements BaseColumns{
		public static final String TABLE_NAME = "ratings";
		public static final String FOOD_ID = "foodid";
		public static final String FOOD_NAME = "foodname";
		public static final String FOOD_RATING = "foodrating";
	}

	private static final String TEXT_TYPE = " TEXT";
	private static final String INT_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	public static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE " + Entry.TABLE_NAME + " (" +
					Entry.FOOD_ID + " INTEGER PRIMARY KEY," +
					Entry.FOOD_NAME + TEXT_TYPE + COMMA_SEP +
					Entry.FOOD_RATING + INT_TYPE + COMMA_SEP +
					" )";
	public static final String SQL_DELETE_ENTRIES =
			"DROP TABLE IF EXISTS " + Entry.TABLE_NAME;


	public void onCreate(SQLiteDatabase db){
		db.execSQL(SQL_CREATE_ENTRIES);
	}
	public SQLReader(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}
}