package ua.cc.cupsfacebook.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ua.cc.cupsfacebook.util.Global;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class helps us to have to deal with database
 * 
 * @version 1.3 28-10-2013
 * @author Taras Melon
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	public static final String TABLE_USERS = "user_info";
	public static final String TABLE_CONTACTS = "contact_info";

	public static final String USER_COLUMN_ID = "id";
	public static final String USER_COLUMN_NAME = "name";
	public static final String USER_COLUMN_SURNAME = "surname";
	public static final String USER_COLUMN_BIO = "bio";
	public static final String USER_COLUMN_DATEOFBIRTH = "dateOfBirth";
	public static final String USER_COLUMN_USER_ID = "userId";

	public static final String CONTACT_COLUMN_ID = "id";
	public static final String CONTACT_COLUMN_USER_ID = "userId";
	public static final String CONTACT_COLUMN_FULL_NAME = "fullName";
	public static final String CONTACT_COLUMN_FACEBOOK_ID = "facebookId";
	public static final String CONTACT_COLUMN_PRIORITY = "priority";

	public MySQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, Global.DATABASE_NAME, factory, Global.DATABASE_VERSION);
	}

	/*
	 * Creating two tables: users and contacts
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
				+ USER_COLUMN_ID + " INTEGER PRIMARY KEY," + USER_COLUMN_NAME
				+ " TEXT," + USER_COLUMN_SURNAME + " TEXT," + USER_COLUMN_BIO
				+ " TEXT," + USER_COLUMN_DATEOFBIRTH + " TEXT,"
				+ USER_COLUMN_USER_ID + " TEXT" + ")";

		String createContactsTable = "CREATE TABLE " + TABLE_CONTACTS + "("
				+ CONTACT_COLUMN_ID + " INTEGER PRIMARY KEY,"
				+ CONTACT_COLUMN_USER_ID + " INTEGER,"
				+ CONTACT_COLUMN_FULL_NAME + " TEXT,"
				+ CONTACT_COLUMN_FACEBOOK_ID + " TEXT,"
				+ CONTACT_COLUMN_PRIORITY + " INTEGER" + ")";

		db.execSQL(createUsersTable);
		db.execSQL(createContactsTable);
	}

	/*
	 * Deleting old tables and creating new, when database version is out of
	 * date
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String startSql = "DROP TABLE IF EXISTS ";
		db.execSQL(startSql + TABLE_USERS);
		db.execSQL(startSql + TABLE_CONTACTS);
		db.setVersion(newVersion);
		onCreate(db);
	}

	/**
	 * Adding user info to database from argument Data object
	 * 
	 * @param data
	 *            object represents user info
	 */
	public void addData(Data data) {
		ContentValues values = new ContentValues();

		values.put(USER_COLUMN_NAME, data.getName());
		values.put(USER_COLUMN_SURNAME, data.getSurname());
		values.put(USER_COLUMN_BIO, data.getBio());
		values.put(USER_COLUMN_DATEOFBIRTH, data.getDateOfBirth());
		values.put(USER_COLUMN_USER_ID, data.getUserId());

		SQLiteDatabase db = this.getWritableDatabase();

		long currentId = db.insert(TABLE_USERS, null, values);

		ArrayList<String> contacts = data.getContacts();
		for (String contact : contacts) {
			ContentValues contactValues = new ContentValues();
			contactValues.put(CONTACT_COLUMN_USER_ID, currentId);

			String[] splitted = contact.split(";");
			if (splitted.length == 3) {
				contactValues.put(CONTACT_COLUMN_FULL_NAME, splitted[0]);
				contactValues.put(CONTACT_COLUMN_FACEBOOK_ID, splitted[1]);
				contactValues.put(CONTACT_COLUMN_PRIORITY,
						Integer.valueOf(splitted[2]));
			} else if (splitted.length == 1) {
				contactValues.put(CONTACT_COLUMN_FULL_NAME, splitted[0]);
			}

			db.insert(TABLE_CONTACTS, null, contactValues);
		}

		db.close();
	}

	/**
	 * Fetching info about user and me from database
	 * 
	 * @return object represents user info
	 */
	public ArrayList<Data> findData() {
		ArrayList<Data> dataList = new ArrayList<Data>();
		try {
			String query = "Select * FROM " + TABLE_USERS;

			SQLiteDatabase db = this.getReadableDatabase();

			Cursor cursor = db.rawQuery(query, null);

			getUserAndMyData(dataList, cursor);

			String startQuery = "Select " + CONTACT_COLUMN_FULL_NAME + ","
					+ CONTACT_COLUMN_FACEBOOK_ID + ","
					+ CONTACT_COLUMN_PRIORITY + " FROM " + TABLE_CONTACTS
					+ " WHERE " + CONTACT_COLUMN_USER_ID + "=";

			query = startQuery + dataList.get(0).getId();
			cursor = db.rawQuery(query, null);
			addContactsFromCursor(dataList.get(0), cursor);

			query = startQuery + dataList.get(1).getId();
			cursor = db.rawQuery(query, null);
			addContactsFromCursor(dataList.get(1), cursor);

			db.close();
		} catch (SQLiteException ex) {
			Log.e(Global.TAG, ex.getMessage());
		}
		return dataList;
	}

	/**
	 * Fetching and setting info without contacts about user and me
	 * 
	 * @param dataList
	 *            list which contains info about me and user
	 * @param cursor
	 *            object represents result rows from executed query
	 */
	private void getUserAndMyData(ArrayList<Data> dataList, Cursor cursor) {
		Data userData = new Data();
		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			addDataWithoutContactsFromCursor(userData, cursor);
		}
		dataList.add(userData);

		Data myData = new Data();
		if (cursor.moveToNext()) {
			addDataWithoutContactsFromCursor(myData, cursor);
		}
		dataList.add(myData);

		cursor.close();
	}

	/**
	 * Updating user name
	 * 
	 * @param id
	 *            user's id in table Users
	 * @param newName
	 *            new user name
	 * @return if success return true, else false
	 */
	public boolean updateName(int id, String newName) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues cv = new ContentValues();
			cv.put(USER_COLUMN_NAME, newName);
			db.update(TABLE_USERS, cv, USER_COLUMN_ID + "=?",
					new String[] { Integer.toString(id) });

			db.close();
		} catch (SQLiteException ex) {
			Log.e(Global.TAG, ex.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Updating user surname
	 * 
	 * @param id
	 *            user's id in table Users
	 * @param newSurname
	 *            new user surname
	 * @return if success return true, else false
	 */
	public boolean updateSurname(int id, String newSurname) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues cv = new ContentValues();
			cv.put(USER_COLUMN_SURNAME, newSurname);
			db.update(TABLE_USERS, cv, USER_COLUMN_ID + "=?",
					new String[] { Integer.toString(id) });

			db.close();
		} catch (SQLiteException ex) {
			Log.e(Global.TAG, ex.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Updating user date of birth
	 * 
	 * @param id
	 *            user's id in table Users
	 * @param newDate
	 *            new user date of birth
	 * @return if success return true, else false
	 */
	public boolean updateDateOfBirth(int id, String newDate) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues cv = new ContentValues();
			cv.put(USER_COLUMN_DATEOFBIRTH, newDate);
			db.update(TABLE_USERS, cv, USER_COLUMN_ID + "=?",
					new String[] { Integer.toString(id) });

			db.close();
		} catch (SQLiteException ex) {
			Log.e(Global.TAG, ex.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Updating user biography
	 * 
	 * @param id
	 *            user's id in table Users
	 * @param newBio
	 *            new user biography
	 * @return if success return true, else false
	 */
	public boolean updateBio(int id, String newBio) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues cv = new ContentValues();
			cv.put(USER_COLUMN_BIO, newBio);
			db.update(TABLE_USERS, cv, USER_COLUMN_ID + "=?",
					new String[] { Integer.toString(id) });

			db.close();
		} catch (SQLiteException ex) {
			Log.e(Global.TAG, ex.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Updating priority of user contacts
	 * 
	 * @param id
	 *            user's id in table Users
	 * @param items
	 *            new friend priorities
	 * @return if success return true, else false
	 */
	public boolean updateContacts(int id, Friend[] items) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			for (int i = 0; i < items.length; i++) {
				Friend friend = items[i];
				ContentValues cv = new ContentValues();
				cv.put(CONTACT_COLUMN_PRIORITY, friend.getPriority());
				db.update(TABLE_CONTACTS, cv, CONTACT_COLUMN_FACEBOOK_ID
						+ "=? AND " + CONTACT_COLUMN_USER_ID + "=?",
						new String[] { friend.getId(), Integer.toString(id) });
			}

			db.close();
		} catch (SQLiteException ex) {
			Log.e(Global.TAG, ex.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Adding contacts from cursor to data object
	 * 
	 * @param data
	 *            object represents user info
	 * @param cursor
	 *            object represents result rows from executed query
	 */
	private void addContactsFromCursor(Data data, Cursor cursor) {
		if (cursor.moveToFirst()) {
			ArrayList<String> contacts = new ArrayList<String>();
			cursor.moveToFirst();

			boolean bool = false;
			if (cursor.getString(1) != null)
				bool = true;

			for (int i = 0; i < cursor.getCount(); i++) {
				if (bool) {
					contacts.add(cursor.getString(0) + ";"
							+ cursor.getString(1) + ";" + cursor.getString(2));
				} else {
					contacts.add(cursor.getString(0));
				}

				cursor.moveToNext();
			}

			cursor.close();

			if (bool) {
				sortContactsViaPriority(contacts);
			}

			data.setContacts(contacts);
		}
	}

	/**
	 * Sorting contact list of user via priority
	 * 
	 * @param contacts
	 *            list of contacts
	 */
	private void sortContactsViaPriority(ArrayList<String> contacts) {
		Collections.sort(contacts, new Comparator<String>() {

			@Override
			public int compare(String lhs, String rhs) {
				Integer rValue = Integer.valueOf(rhs.split(";")[2]);
				Integer lValue = Integer.valueOf(lhs.split(";")[2]);
				if (rValue > lValue)
					return 1;
				else if (rValue < lValue)
					return -1;
				else
					return 0;
			}

		});
	}

	/**
	 * Adding all data without contacts from cursor to data object
	 * 
	 * @param data
	 *            object represents user info
	 * @param cursor
	 *            object represents result rows from executed query
	 */
	private void addDataWithoutContactsFromCursor(Data data, Cursor cursor) {
		data.setId(Integer.parseInt(cursor.getString(0)));
		data.setName(cursor.getString(1));
		data.setSurname(cursor.getString(2));
		data.setBio(cursor.getString(3));
		data.setDateOfBirth(cursor.getString(4));
		data.setUserId(cursor.getString(5));
	}
}
