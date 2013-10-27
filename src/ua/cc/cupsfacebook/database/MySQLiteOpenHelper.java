package ua.cc.cupsfacebook.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "infoDB.db";
	private static final String TABLE_PRODUCTS = "user_info";
	private static final String TAG = "[CUPS]";
	
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_SURNAME = "surname";
	public static final String COLUMN_BIO = "bio";
	public static final String COLUMN_DATEOFBIRTH = "dateOfBirth";
	public static final String COLUMN_CONTACTS = "contacts";
	
	public MySQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
	             TABLE_PRODUCTS + "("
	             + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME 
	             + " TEXT," + COLUMN_SURNAME + " TEXT," + COLUMN_BIO + " TEXT," + COLUMN_DATEOFBIRTH + " TEXT," + COLUMN_CONTACTS + " BLOB" + ")";
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
        values.put(COLUMN_CONTACTS, arrayListToByteArray(data.getContacts()));
        SQLiteDatabase db = this.getWritableDatabase();
        
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
	}

	private byte[] arrayListToByteArray(ArrayList<String> list) {
		byte[] bytes = null;
		try {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        DataOutputStream out = new DataOutputStream(baos);
	        for (String element : list) {
	        	out.writeUTF(element);
	        }
	        bytes = baos.toByteArray();
        } catch (IOException e) {
        	Log.e(TAG, "Conversion from array list to byte array error");
			e.printStackTrace();
		}
		return bytes;
	}

	private ArrayList<String> byteArrayToArrayList(byte[] bytes)
	{
		ArrayList<String> list = new ArrayList<String>();
		try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(bais);
			while (in.available() > 0) {
			    String element = in.readUTF();
			    list.add(element);
			}
		}
		catch (IOException ex)
		{
			Log.e(TAG, "Conversion from byte array to array list error");
			ex.printStackTrace();
		}
		return list;
	}
	
	public Data findData() {
		Data data = null;
		try {
			String query = "Select * FROM " + TABLE_PRODUCTS;
			
			SQLiteDatabase db = this.getWritableDatabase();
			
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
				data.setContacts(byteArrayToArrayList(cursor.getBlob(5)));
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
