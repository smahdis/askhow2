package database_handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Items.Post;

public class PostHandler {


	// Common for all tables
	public static final String post_table = "post_table";

	public static final String KEY_ID = "_id";
	public static final String KEY_POST_MYSQL_ID= "post_mysql_id";
	public static final String KEY_POSTER_NAME= "poster_name";
	public static final String KEY_POSTER_AVATAR = "poster_avatar";
	public static final String KEY_POSTER_DEGREE = "poster_degreed";
	public static final String KEY_POSTER_MYSQL_ID = "poster_mysql_id";
	public static final String KEY_POST_TITLE = "post_title";
	public static final String KEY_POST_DESC = "post_desc";
	public static final String KEY_VOTES = "votes";
	public static final String KEY_ANSWERS = "answers";
	public static final String KEY_VIEWS = "views";
	public static final String KEY_VOTE_TYPE = "vote_type";
	public static final String KEY_BOOKMARKED = "bookmarked";
	public static final String KEY_IS_READ = "is_read";
	public static final String KEY_IS_MINE = "is_mine";
	public static final String KEY_IS_PUBLISHED = "is_published";
    public static final String KEY_IS_LOCKED = "is_locked";
	public static final String KEY_IS_QUESTION = "is_question";
    public static final String KEY_QUESTION_MYSQL_ID = "question_mysql_id";

	public static final String KEY_DATE = "date";
	public static final String KEY_TIME = "time";
	public static final String KEY_TAGS = "tags";


	public static final String KEY_RESERVED1 = "reserved1";
	public static final String KEY_RESERVED2 = "reserved2";
	public static final String KEY_RESERVED3 = "reserved3";


	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private final Context mCtx;

	public static final String CREATE_POST_TABLE = "CREATE TABLE "
			+ post_table + "("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_POST_MYSQL_ID + " INTEGER,"
			+ KEY_POSTER_NAME + " TEXT,"
			+ KEY_POSTER_AVATAR + " TEXT,"
			+ KEY_POSTER_DEGREE + " TEXT,"
			+ KEY_POSTER_MYSQL_ID + " INTEGER,"
			+ KEY_POST_TITLE + " TEXT,"
			+ KEY_POST_DESC + " TEXT,"
			+ KEY_VOTES + " INTEGER,"
			+ KEY_ANSWERS + " INTEGER,"
			+ KEY_VIEWS + " INTEGER,"
			+ KEY_VOTE_TYPE + " INTEGER,"
			+ KEY_BOOKMARKED + " INTEGER,"
			+ KEY_IS_READ + " INTEGER,"
			+ KEY_IS_MINE + " INTEGER,"
            + KEY_IS_PUBLISHED + " INTEGER,"
            + KEY_IS_LOCKED + " INTEGER,"
            + KEY_IS_QUESTION + " INTEGER,"
            + KEY_QUESTION_MYSQL_ID + " INTEGER,"
			+ KEY_DATE + " TEXT,"
			+ KEY_TIME + " TEXT,"
			+ KEY_TAGS + " TEXT,"
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

	public PostHandler open() throws SQLException {
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

	public PostHandler(Context context) {

		this.mCtx = context;
		// this.mDbHelper = new DatabaseHelper(this.mCtx);
		// this.mDb = this.mDbHelper.getWritableDatabase();
	}

    //1 => add, 0 => update
	public void addPost(Post post, boolean add) {

        open();

		ContentValues values = new ContentValues();

        values.put(KEY_POST_MYSQL_ID, post.getPost_mysql_id());
        values.put(KEY_POSTER_NAME, post.getPosterName());
        values.put(KEY_POSTER_AVATAR, post.getPosterAvatar());
        values.put(KEY_POSTER_DEGREE, post.getPosterDegree());
        values.put(KEY_POSTER_MYSQL_ID, post.getPosterMysqlID());
        values.put(KEY_POST_TITLE, post.getPostTitle());
        values.put(KEY_POST_DESC, post.getPostDesc());
        values.put(KEY_VOTES, post.getVotes());
        values.put(KEY_ANSWERS, post.getAnswers());
        values.put(KEY_VIEWS, post.getViews());
        values.put(KEY_VOTE_TYPE, post.getVoteType());
        values.put(KEY_BOOKMARKED, post.isBookmarked());
        values.put(KEY_IS_READ, post.isRead());
        values.put(KEY_IS_MINE, post.isMine());
        values.put(KEY_IS_PUBLISHED, post.isPublished());
        values.put(KEY_IS_LOCKED, post.isLocked());
        values.put(KEY_IS_QUESTION, post.isQuestion());
        values.put(KEY_QUESTION_MYSQL_ID, post.getQuestionMySqlID());
        values.put(KEY_TAGS, post.getTags());
		values.put(KEY_DATE, post.getDate());
		values.put(KEY_TIME,  post.getTime());
		values.put(KEY_RESERVED1, post.getReserved1());
		values.put(KEY_RESERVED2, post.getReserved2());
		values.put(KEY_RESERVED3, post.getReserved3());

		if (add)
            //check if post already exists, if it does, update with the new data
            if(!postExists(post.getPost_mysql_id()))
			    this.mDb.insert(post_table, null, values);
            else
                this.mDb.update(post_table, values, KEY_POST_MYSQL_ID + " =? ",new String[] { String.valueOf(post.getPost_mysql_id()) });
		else
		{
			this.mDb.update(post_table, values, KEY_ID + " =? ",new String[] { String.valueOf(post.getId()) });
		}
	}

	// Return post for a specific user

	public Post getPost(int mysql_id) {
		open();

		String sql = "select * from " + post_table + " where " + KEY_POST_MYSQL_ID + " = " + mysql_id;

		Cursor cursor = mDb.rawQuery(sql, null);
		Post post = new Post();

		if (cursor != null && cursor.moveToFirst()) {
			post.setId(cursor.getInt(0));
			post.setPost_mysql_id(cursor.getInt(1));
			post.setPosterName(cursor.getString(2));
			post.setPosterAvatar(cursor.getString(3));
			post.setPosterDegree(cursor.getString(4));
			post.setPosterMysqlID(cursor.getInt(5));
			post.setPostTitle(cursor.getString(6));
			post.setPostDesc(cursor.getString(7));
			post.setVotes(cursor.getInt(8));
			post.setAnswers(cursor.getInt(9));
			post.setViews(cursor.getInt(10));

			post.setVoteType(cursor.getInt(11));
			post.setBookmarked(cursor.getInt(12)!= 0);
			post.setIsRead(cursor.getInt(13)!= 0);
			post.setIsMine(cursor.getInt(14)!= 0);
            post.setPublished(cursor.getInt(15)!= 0);
            post.setLocked(cursor.getInt(16)!= 0);
            post.setQuestion(cursor.getInt(17)!= 0);
            post.setQuestionMySqlID(cursor.getInt(18));
			post.setDate(cursor.getString(19));
			post.setTime(cursor.getString(20));

            post.setTags(cursor.getString(21));

            post.setReserved1(cursor.getString(22));
            post.setReserved2(cursor.getString(23));
            post.setReserved3(cursor.getString(24));
		}

		cursor.close();

        return post;
	}


	public void deleteAllPosts() {
		open();
		mDb.execSQL("delete from " + post_table);
	}


    /*
    1 = > questions
    0 = > answers
     */
	public List<Post> getAllPosts(int type) {
		open();
		List<Post> postList = new ArrayList<>();
		String selectQuery = "SELECT  * FROM " + post_table + " WHERE " + KEY_IS_QUESTION + " = " + type + " order by _id ASC ";

        Cursor cursor = mDb.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
                Post post = new Post();
                post.setId(cursor.getInt(0));
                post.setPost_mysql_id(cursor.getInt(1));
                post.setPosterName(cursor.getString(2));
                post.setPosterAvatar(cursor.getString(3));
                post.setPosterDegree(cursor.getString(4));
                post.setPosterMysqlID(cursor.getInt(5));
                post.setPostTitle(cursor.getString(6));
                post.setPostDesc(cursor.getString(7));
                post.setVotes(cursor.getInt(8));
                post.setAnswers(cursor.getInt(9));
                post.setViews(cursor.getInt(10));

                post.setVoteType(cursor.getInt(11));
                post.setBookmarked(cursor.getInt(12) != 0);
                post.setIsRead(cursor.getInt(13) != 0);
                post.setIsMine(cursor.getInt(14)!= 0);
                post.setPublished(cursor.getInt(15)!= 0);
                post.setLocked(cursor.getInt(16)!= 0);
                post.setQuestion(cursor.getInt(17)!= 0);
                post.setQuestionMySqlID(cursor.getInt(18));
                post.setDate(cursor.getString(19));
                post.setTime(cursor.getString(20));

                post.setTags(cursor.getString(21));

                post.setReserved1(cursor.getString(22));
                post.setReserved2(cursor.getString(23));
                post.setReserved3(cursor.getString(24));

				// Adding contact to list
				postList.add(post);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return postList;

	}

	public int getLastId() {
		open();
		String countQuery = "SELECT  * FROM " + post_table + ";";

		Cursor cursor = mDb.rawQuery(countQuery, null);

		int last_id = 0;

		if (cursor.getCount() != 0) {
			cursor.moveToPosition(cursor.getCount() - 1);

			last_id = cursor.getInt(0);

		}
		cursor.close();

		return last_id;

	}

	//Does post with this mysql id exist?
	public boolean postExists(int mysql_id)
	{
		open();
		String countQuery = "SELECT  * FROM " + post_table + " WHERE " + KEY_POST_MYSQL_ID + " = " + mysql_id + ";";

		Cursor cursor = mDb.rawQuery(countQuery, null);

		boolean  exists;
		exists = (cursor.getCount() != 0);
        Log.d("DATABASE", exists + "");
        cursor.close();

		return exists;
	}

	public void updateVoteType(int postID, int voteType, int votes) {
		open();
		ContentValues values = new ContentValues();
		values.put(KEY_VOTE_TYPE, voteType);
        values.put(KEY_VOTES, votes);
        this.mDb.update(post_table, values, KEY_POST_MYSQL_ID + " =? ",
                new String[] { String.valueOf(postID) });
	}
}
