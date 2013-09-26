package ua.cc.cupsfacebook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "infoDB.db";
	private static final String TABLE_PRODUCTS = "user_info";
	private static final String TAG = "[CUPS_FACEBOOK]";
	
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_SURNAME = "surname";
	public static final String COLUMN_BIO = "bio";
	public static final String COLUMN_DATEOFBIRTH = "dateOfBirth";
	public static final String COLUMN_USERID = "userId";
	
	public MySQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
	             TABLE_PRODUCTS + "("
	             + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME 
	             + " TEXT," + COLUMN_SURNAME + " TEXT," + COLUMN_BIO + " TEXT," + COLUMN_DATEOFBIRTH + " TEXT," + COLUMN_USERID + " TEXT" + ")";
		db.execSQL(CREATE_PRODUCTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
		db.setVersion(newVersion);
	    onCreate(db);
	}
	
	public void addData(Data data)
	{
		ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, data.getName());
        values.put(COLUMN_SURNAME, data.getSurname());
        values.put(COLUMN_BIO, data.getBio());
        values.put(COLUMN_DATEOFBIRTH, data.getDateOfBirth());
        values.put(COLUMN_USERID, data.getUserId());
 
        SQLiteDatabase db = this.getWritableDatabase();
        
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
	}

	public Data findData() {
		Data data = null;
		try {
			String query = "Select * FROM " + TABLE_PRODUCTS;
			
			SQLiteDatabase db = this.getReadableDatabase();
			
			Cursor cursor = db.rawQuery(query, null);
			
			data = new Data();
			
			if (cursor.moveToFirst())
			{
				cursor.moveToFirst();
				data.setId(Integer.parseInt(cursor.getString(0)));
				data.setName(cursor.getString(1));
				data.setSurname(cursor.getString(2));
				data.setBio(cursor.getString(3));
				data.setDateOfBirth(cursor.getString(4));
				data.setUserId(cursor.getString(5));
				cursor.close();
			}
			
		    db.close();
		}
		catch (SQLiteException ex)
		{
			Log.e(TAG, ex.getMessage());
		}
		return data;
	}
	
}
