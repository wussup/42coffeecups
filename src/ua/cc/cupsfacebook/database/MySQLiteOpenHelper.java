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

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	private static final String TABLE_USERS = "user_info";
	private static final String TABLE_CONTACTS = "contact_info";

	private static final String USER_COLUMN_ID = "id";
	private static final String USER_COLUMN_NAME = "name";
	private static final String USER_COLUMN_SURNAME = "surname";
	private static final String USER_COLUMN_BIO = "bio";
	private static final String USER_COLUMN_DATEOFBIRTH = "dateOfBirth";

	private static final String CONTACT_COLUMN_ID = "id";
	private static final String CONTACT_COLUMN_USER_ID = "userId";
	private static final String CONTACT_COLUMN_FULL_NAME = "fullName";

	public MySQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, Global.DATABASE_NAME, factory, Global.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
				+ USER_COLUMN_ID + " INTEGER PRIMARY KEY," + USER_COLUMN_NAME
				+ " TEXT," + USER_COLUMN_SURNAME + " TEXT," + USER_COLUMN_BIO
				+ " TEXT," + USER_COLUMN_DATEOFBIRTH + " TEXT" + ")";

		String createContactsTable = "CREATE TABLE " + TABLE_CONTACTS + "("
				+ CONTACT_COLUMN_ID + " INTEGER PRIMARY KEY,"
				+ CONTACT_COLUMN_USER_ID + " INTEGER,"
				+ CONTACT_COLUMN_FULL_NAME + " TEXT" + ")";

		db.execSQL(createUsersTable);
		db.execSQL(createContactsTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String startSql = "DROP TABLE IF EXISTS ";
		db.execSQL(startSql + TABLE_USERS);
		db.execSQL(startSql + TABLE_CONTACTS);
		db.setVersion(newVersion);
		onCreate(db);
	}

	public void addData(Data data) {
		ContentValues values = new ContentValues();
		values.put(USER_COLUMN_NAME, data.getName());
		values.put(USER_COLUMN_SURNAME, data.getSurname());
		values.put(USER_COLUMN_BIO, data.getBio());
		values.put(USER_COLUMN_DATEOFBIRTH, data.getDateOfBirth());

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

	public Data findData() {
		Data data = null;
		try {
			String query = "Select * FROM " + TABLE_USERS;

			SQLiteDatabase db = this.getReadableDatabase();

			Cursor cursor = db.rawQuery(query, null);

			data = new Data();

			if (cursor.moveToFirst()) {
				cursor.moveToFirst();
				data.setId(Integer.parseInt(cursor.getString(0)));
				data.setName(cursor.getString(1));
				data.setSurname(cursor.getString(2));
				data.setBio(cursor.getString(3));
				data.setDateOfBirth(cursor.getString(4));
				cursor.close();
			}

			query = "Select " + CONTACT_COLUMN_FULL_NAME + " FROM "
					+ TABLE_CONTACTS + " WHERE " + CONTACT_COLUMN_USER_ID + "="
					+ data.getId();

			cursor = db.rawQuery(query, null);

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

			db.close();
		} catch (SQLiteException ex) {
			Log.e(Global.TAG, ex.getMessage());
		}
		return data;
	}

}
