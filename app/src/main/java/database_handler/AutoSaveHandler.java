package database_handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Items.Post;
import Items.User;

public class AutoSaveHandler {


	// Common for all tables
	public static final String autosave_table = "autosave_table";

	public static final String KEY_ID = "_id";
	public static final String KEY_POST_ID= "post_id";
	public static final String KEY_POST_CONTENT= "post_content";
	public static final String KEY_POST_TITLE = "post_title";
	public static final String KEY_POST_TAGS = "poster_tags";
	public static final String KEY_POST_IMAGES = "post_images";
	public static final String KEY_DATE = "date";
	public static final String KEY_TIME = "time";
	public static final String KEY_RESERVED1 = "reserved1";
	public static final String KEY_RESERVED2 = "reserved2";
	public static final String KEY_RESERVED3 = "reserved3";


	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private final Context mCtx;

	public static final String CREATE_AUTOSAVE_TABLE = "CREATE TABLE "
			+ autosave_table + "("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_POST_ID + " INTEGER,"
			+ KEY_POST_CONTENT + " TEXT,"
			+ KEY_POST_TITLE + " TEXT,"
			+ KEY_POST_TAGS + " TEXT,"
			+ KEY_POST_IMAGES + " INTEGER,"
			+ KEY_DATE + " TEXT,"
			+ KEY_TIME + " TEXT,"
			+ KEY_RESERVED1 + " TEXT,"
			+ KEY_RESERVED2 + " TEXT,"
			+ KEY_RESERVED3 + " TEXT"
			+ ")";


	private static class DatabaseHelper extends SQLiteOpenHelper {
		private static DatabaseHelper sInstance;

		public static DatabaseHelper getInstance(Context context) {

			// Use the application context, which will ensure that you
			// don't accidentally leak an Activity's context.
			// See this article for more information: http://bit.ly/6LRzfx
			if (sInstance == null) {
				sInstance = new DatabaseHelper(context.getApplicationContext());
			}
			return sInstance;
		}

		DatabaseHelper(Context context) {
			super(context, DBCreator.DATABASE_NAME, null, DBCreator.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	public AutoSaveHandler open() throws SQLException {
		if (this.mDbHelper == null) {
			this.mDbHelper = DatabaseHelper.getInstance(this.mCtx);
			this.mDb = this.mDbHelper.getWritableDatabase();
		}
		return this;
	}

	/**
	 * close return type: void
	 */
	public void close() {
		this.mDbHelper.close();
	}

	public AutoSaveHandler(Context context) {

		this.mCtx = context;
		// this.mDbHelper = new DatabaseHelper(this.mCtx);
		// this.mDb = this.mDbHelper.getWritableDatabase();
	}

    //1 => add, 0 => update
	public void addPost(Post post, boolean add) {

        open();

		ContentValues values = new ContentValues();

        values.put(KEY_POST_ID, post.getPost_mysql_id());
        values.put(KEY_POST_CONTENT, post.getPostDesc());
        values.put(KEY_POST_TITLE, post.getPostTitle());
        values.put(KEY_POST_TAGS, post.getTags());
        values.put(KEY_POST_IMAGES, post.getReserved1());
		Calendar c = Calendar.getInstance();
		values.put(KEY_DATE, c.DATE);
		values.put(KEY_TIME,  post.getTime());
		values.put(KEY_RESERVED1, post.getReserved1());
		values.put(KEY_RESERVED2, post.getReserved2());
		values.put(KEY_RESERVED3, post.getReserved3());



		if (add)
            //check if post already exists, if it does, update with the new data
            if(!postExists(post.getPost_mysql_id()))
			    this.mDb.insert(autosave_table, null, values);
            else
                this.mDb.update(autosave_table, values, KEY_POST_ID + " =? ",new String[] { String.valueOf(post.getPost_mysql_id()) });
		else
		{
			this.mDb.update(autosave_table, values, KEY_ID + " =? ",new String[] { String.valueOf(post.getId()) });
		}
	}

	// Return post for a specific user

	public Post getPost(int mysql_id) {
		open();

		String sql = "select * from " + autosave_table + " where " + KEY_POST_ID + " = " + mysql_id;

		Cursor cursor = mDb.rawQuery(sql, null);
		Post post = new Post();

		if (cursor != null && cursor.moveToFirst()) {
			post.setId(cursor.getInt(0));
			post.setPost_mysql_id(cursor.getInt(1));
			post.setPostDesc(cursor.getString(2));
			post.setPostTitle(cursor.getString(3));
			post.setTags(cursor.getString(4));
			post.setDate(cursor.getString(5));
			post.setTime(cursor.getString(7));
            post.setReserved1(cursor.getString(8));
            post.setReserved2(cursor.getString(9));
            post.setReserved3(cursor.getString(10));
		}

		cursor.close();

        return post;
	}


	public void deleteAllPosts() {
		open();
		mDb.execSQL("delete from " + autosave_table);
	}


	//Does post with this mysql id exist?
	public boolean postExists(int mysql_id)
	{
		open();
		String countQuery = "SELECT  * FROM " + autosave_table + " WHERE " + KEY_POST_ID + " = " + mysql_id + ";";

		Cursor cursor = mDb.rawQuery(countQuery, null);

		boolean  exists;
		exists = (cursor.getCount() != 0);
        Log.d("DATABASE", exists + "");
        cursor.close();

		return exists;
	}

}
