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
	private static final String TAG = "[CUPS_FACEBOOK]";
	
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_SURNAME = "surname";
	public static final String COLUMN_BIO = "bio";
	public static final String COLUMN_DATEOFBIRTH = "dateOfBirth";
	public static final String COLUMN_USERID = "userId";
	public static final String COLUMN_CONTACTS = "contacts";
	public static final String COLUMN_ABOUT = "about";
	
	public MySQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
	             TABLE_PRODUCTS + "("
	             + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME 
	             + " TEXT," + COLUMN_SURNAME + " TEXT," + COLUMN_BIO + " TEXT," 
	             + COLUMN_DATEOFBIRTH + " TEXT," + COLUMN_USERID + " TEXT," + COLUMN_CONTACTS + " BLOB," + COLUMN_ABOUT + " TEXT" + ")";
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
        values.put(COLUMN_CONTACTS, arrayListToByteArray(data.getContacts()));
        values.put(COLUMN_ABOUT, data.getAbout());
        
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
	
	public ArrayList<Data> findData() {
		ArrayList<Data> dataList = new ArrayList<Data>();
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
				data.setContacts(byteArrayToArrayList(cursor.getBlob(6)));
				data.setAbout(cursor.getString(7));
				dataList.add(data);
				
				if (cursor.moveToNext())
				{
					data = new Data();
					data.setId(Integer.parseInt(cursor.getString(0)));
					data.setName(cursor.getString(1));
					data.setSurname(cursor.getString(2));
					data.setBio(cursor.getString(3));
					data.setDateOfBirth(cursor.getString(4));
					data.setUserId(cursor.getString(5));
					data.setContacts(byteArrayToArrayList(cursor.getBlob(6)));
					data.setAbout(cursor.getString(7));
					dataList.add(data);
				}
					
				cursor.close();
			}
			
		    db.close();
		}
		catch (SQLiteException ex)
		{
			Log.e(TAG, ex.getMessage());
		}
		return dataList;
	}

	public boolean updateName(int id, String newName) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_NAME, newName);
			db.update(TABLE_PRODUCTS, cv, COLUMN_ID+"=?", new String[]{Integer.toString(id)});
			
		    db.close();
		}
		catch (SQLiteException ex)
		{
			Log.e(TAG, ex.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean updateSurname(int id, String newSurname) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_SURNAME, newSurname);
			db.update(TABLE_PRODUCTS, cv, COLUMN_ID+"=?", new String[]{Integer.toString(id)});
			
		    db.close();
		}
		catch (SQLiteException ex)
		{
			Log.e(TAG, ex.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean updateAbout(int id, String newAbout) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_ABOUT, newAbout);
			db.update(TABLE_PRODUCTS, cv, COLUMN_ID+"=?", new String[]{Integer.toString(id)});
			
		    db.close();
		}
		catch (SQLiteException ex)
		{
			Log.e(TAG, ex.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean updateDateOfBirth(int id, String newDate) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_DATEOFBIRTH, newDate);
			db.update(TABLE_PRODUCTS, cv, COLUMN_ID+"=?", new String[]{Integer.toString(id)});
			
		    db.close();
		}
		catch (SQLiteException ex)
		{
			Log.e(TAG, ex.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean updateBio(int id, String newBio) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_BIO, newBio);
			db.update(TABLE_PRODUCTS, cv, COLUMN_ID+"=?", new String[]{Integer.toString(id)});
			
		    db.close();
		}
		catch (SQLiteException ex)
		{
			Log.e(TAG, ex.getMessage());
			return false;
		}
		return true;
	}

	public boolean updateContacts(int id, Friend[] items) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ArrayList<String> contacts = new ArrayList<String>();
			for (int i=0; i<items.length; i++)
			{
				Friend friend = items[i];
				contacts.add(friend.getName()+";"+friend.getId()+";"+friend.getPriority());
			}
			
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_CONTACTS, arrayListToByteArray(contacts));
			db.update(TABLE_PRODUCTS, cv, COLUMN_ID+"=?", new String[]{Integer.toString(id)});
			
		    db.close();
		}
		catch (SQLiteException ex)
		{
			Log.e(TAG, ex.getMessage());
			return false;
		}
		return true;
	}
	
}
