package ua.cc.cupsfacebook.database;

import java.util.ArrayList;

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
 * @version 1.2 28-10-2013
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
				+ CONTACT_COLUMN_FULL_NAME + " TEXT" + ")";

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
			contactValues.put(CONTACT_COLUMN_FULL_NAME, contact);

			db.insert(TABLE_CONTACTS, null, contactValues);
		}

		db.close();
	}

	/**
	 * Fetching user info from database
	 * 
	 * @return object represents user info
	 */
	public ArrayList<Data> findData() {
		ArrayList<Data> dataList = new ArrayList<Data>();
		try {
			String query = "Select * FROM " + TABLE_USERS;

			SQLiteDatabase db = this.getReadableDatabase();

			Cursor cursor = db.rawQuery(query, null);

			Data userData = new Data();
			if (cursor.moveToFirst()) {
				cursor.moveToFirst();
				addDataWithoutContactsFromCursor(userData, cursor);
			}

			Data myData = new Data();
			if (cursor.moveToNext()) {
				addDataWithoutContactsFromCursor(myData, cursor);
			}

			cursor.close();

			String startQuery = "Select " + CONTACT_COLUMN_FULL_NAME + " FROM "
					+ TABLE_CONTACTS + " WHERE " + CONTACT_COLUMN_USER_ID + "=";

			query = startQuery + userData.getId();

			cursor = db.rawQuery(query, null);

			addContactsFromCursor(userData, cursor);

			dataList.add(userData);

			query = startQuery + myData.getId();

			cursor = db.rawQuery(query, null);

			addContactsFromCursor(myData, cursor);

			dataList.add(myData);

			db.close();
		} catch (SQLiteException ex) {
			Log.e(Global.TAG, ex.getMessage());
		}
		return dataList;
	}

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

			for (int i = 0; i < cursor.getCount(); i++) {
				contacts.add(cursor.getString(0));

				cursor.moveToNext();
			}

			cursor.close();

			data.setContacts(contacts);
		}
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
