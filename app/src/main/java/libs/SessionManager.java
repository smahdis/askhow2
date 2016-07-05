package libs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	public static boolean logged_out_condition = false;

	// Context
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;



	// Sharedpref file name
	private static final String PREF_NAME = "ASKHOW_PREF";

	// All Shared Preferences Keys
	private static final String IS_LOGGED_IN = "IsLoggedIn";


	 public static final String KEY_MYSQLID = "mysqlid";

	// User name (make variable public to access from outside)
	public static final String KEY_USERNAME = "username";
	public static final String KEY_FIRST_NAME = "firstname";
	public static final String KEY_LAST_NAME = "lastname";
	public static final String KEY_AVATAR = "avatar";
	public static final String KEY_PHONE = "phone";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PROFESSION = "profession";
	public static final String KEY_DEGREE = "degree";
	public static final String KEY_STATUS = "status";
	public static final String KEY_CITY = "city";

	public static final String KEY_NUMBER_OF_POSTS = "number_of_posts";
	public static final String KEY_POINTS = "points";
	public static final String KEY_ACTIVITION_STATUS = "activition_status";
	public static final String KEY_REG_DATE = "reg_date";
	public static final String KEY_LAST_LOGIN_DATE = "last_login_date";
	private static final String KEY_TOKEN = "token";

	// Constructor
	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	/**
	 * Create login session
	 * */
	public void createLoginSession(int mysql_id, String username, String phone, String token) {
		editor.putBoolean(IS_LOGGED_IN, true);
		editor.putInt(KEY_MYSQLID, mysql_id);
		editor.putString(KEY_USERNAME, username);
		editor.putString(KEY_PHONE, phone);
		editor.putString(KEY_TOKEN, token);
		// commit changes
		editor.commit();
	}
//
//	public void updateLoginSession(String firstname,
//			String lastname, String email, String mobile,
//			String country, String state, String city, String status, int gender) {
//
//		editor.putString(KEY_FIRST_NAME, firstname);
//		editor.putString(KEY_LAST_NAME, lastname);
//		editor.putString(KEY_EMAIL, email);
//		editor.putString(KEY_MOBILE, mobile);
//		editor.putString(KEY_COUNTRY, country);
//		editor.putString(KEY_STATE, state);
//		editor.putString(KEY_CITY, city);
//		editor.putString(KEY_STATUS, status);
//		// 0 = not set, 1 = male, 2 = female
//		editor.putInt(KEY_GENDER, gender);
//
//		editor.putBoolean(KEY_VCARD_LOADED, true);
//		// commit changes
//		editor.commit();
//	}
//
//	/**
//	 * Clear session details
//	 * */
//	public void logoutUser_clear() {
//		// Clearing all data from Shared Preferences
//
//		last_username = pref.getString(KEY_USERNAME, "");
//		editor.clear();
//		editor.putString(KEY_EMAIL, last_username);
//		editor.commit();
//
//	}
//
//	/**
//	 * Clear session details
//	 * */
//	public void logoutUser() {
//		// Clearing all data from Shared Preferences
//
//		last_username = pref.getString(KEY_USERNAME, "");
//		editor.clear();
//		editor.putString(KEY_USERNAME, last_username);
//		editor.commit();
//
//        User_Handler uh = new User_Handler(_context);
//        uh.delete_Users();
//         logged_out_condition = false;
//        // After logout redirect user to Loing Activity
//        Intent i = new Intent(_context, LoginActivity.class);
//        // Closing all the Activities
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//
//        // Staring Login Activity
//        _context.startActivity(i);
//
//	}
//
//	//
//	public void logoutUser_without_clearance() {
//		logged_out_condition = true;
//		Intent i = new Intent(_context, LoginActivity.class);
//		// Closing all the Activities
//		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//		// Add new Flag to start new Activity
//		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//		// Staring Login Activity
//		_context.startActivity(i);
//
//	}
//
//	/**
//	 * Quick check for login
//	 * **/
	// Get Login State
	public boolean isLoggedIn() {
		return pref.getBoolean(IS_LOGGED_IN, false);
	}
//
//	public void set_isLoggedIn(boolean isLoggedIn) {
//		editor.putBoolean(IS_LOGIN, isLoggedIn);
//		editor.commit();
//	}
//
//	// public void set_isLoggedIn(){
//	// editor.putBoolean(IS_LOGIN, true);
//	// editor.commit();
//	//
//	// }
//
//	// Get Login State
//	public boolean isSaved() {
//		return pref.getBoolean(IS_SAVED, false);
//	}
//
//	// Get Login State
//	public void setIsSaved() {
//		editor.putBoolean(IS_SAVED, true);
//		editor.commit();
//
//	}
//
//	public void setUsersLoaded() {
//		editor.putBoolean(USERS_LOADED, true);
//		editor.commit();
//	}
//
//	public boolean AreUsersLoaded() {
//		return pref.getBoolean(USERS_LOADED, false);
//	}
//
	public String getToken() {
		return pref.getString(KEY_TOKEN, "");
	}
//
	public String getUsername() {
		return pref.getString(KEY_USERNAME, "");
	}
//
	public int getKeyMysql_id() {
		return pref.getInt(KEY_MYSQLID,0);
	}
//
//	public String getLastName() {
//		return pref.getString(KEY_LAST_NAME, "");
//	}
//
//	public String getEmail() {
//		return pref.getString(KEY_EMAIL, "");
//	}
//	public String getAvatar() {
//		return pref.getString(KEY_AVATAR, "default.jpg");
//	}
//
	public void setMySQL_ID(int ID) {
		editor.putInt(KEY_MYSQLID, ID);
		editor.commit();
	}
//
//	public String getMobile() {
//		return pref.getString(KEY_MOBILE, "");
//	}
//
//	public String getCountry() {
//		return pref.getString(KEY_COUNTRY, "");
//	}
//
//	public String getState() {
//		return pref.getString(KEY_STATE, "");
//	}
//
//	public String getCity() {
//		return pref.getString(KEY_CITY, "");
//	}
//	public String getStatus() {
//		return pref.getString(KEY_STATUS, "");
//	}
//
//	public boolean isVcardLoaded() {
//		return pref.getBoolean(KEY_VCARD_LOADED, false);
//	}
//
//	public boolean areFriendsRegistered() {
//		return pref.getBoolean(KEY_FRIENDS_REGISTERED, false);
//	}
//	public void setFriendsRegistered(boolean value) {
//		editor.putBoolean(KEY_FRIENDS_REGISTERED, value);
//		editor.commit();
//	}
}
