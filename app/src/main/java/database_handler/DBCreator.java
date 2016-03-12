package database_handler;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBCreator {

	private static DatabaseHelper sInstance;

	public static final String DATABASE_NAME = "askhow_db";
	public static final int DATABASE_VERSION = 13;

//	public static final String CREATE_users_table = User_Handler.CREATE_user_table;
	public static final String createPostTable = PostHandler.CREATE_POST_TABLE;
//	public static final String CREATE_ROOM_TABLE = Group_Handler.CREATE_group_table;
//	public static final String CREATE_SOCIAL_TABLE = SocialHandler.CREATE_social_table;

	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	/**
	 * Constructor
	 * 
	 * @param ctx
	 */

	public static DatabaseHelper getInstance(Context context) {

		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (sInstance == null) {
			sInstance = new DatabaseHelper(context.getApplicationContext());
		}
		return sInstance;
	}

	public DBCreator(Context ctx) {
		this.context = ctx;
		this.DBHelper = DatabaseHelper.getInstance(this.context);

	}

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
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
//			db.execSQL(CREATE_users_table);
			db.execSQL(createPostTable);
//			db.execSQL(CREATE_ROOM_TABLE);
//			db.execSQL(CREATE_SOCIAL_TABLE);
		}
		

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			db.execSQL("DROP TABLE IF EXISTS " + User_Handler.user_table);
			db.execSQL("DROP TABLE IF EXISTS " + PostHandler.post_table);
//			db.execSQL("DROP TABLE IF EXISTS " + Group_Handler.group_table);
//			db.execSQL("DROP TABLE IF EXISTS " + SocialHandler.social_table);
			onCreate(db);
		}
	}

	/**
	 * open the db
	 * 
	 * @return this
	 * @throws SQLException
	 *             return type: DBCreator
	 */
	public DBCreator open() throws SQLException {
		this.db = this.DBHelper.getWritableDatabase();
		return this;
	}

	/**
	 * close the db return type: void
	 */
	public void close() {
		this.DBHelper.close();
	}

}