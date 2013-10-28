package ua.cc.cupsfacebook.test;

import java.util.ArrayList;

import ua.cc.cupsfacebook.database.Data;
import ua.cc.cupsfacebook.database.MySQLiteOpenHelper;
import android.test.AndroidTestCase;

/**
 * Tests for class MySQLiteOpenHelper
 * 
 * @version 1.0 28-10-2013
 * @author Taras Melon
 */
public class MySQLiteOpenHelperTests extends AndroidTestCase {

	private MySQLiteOpenHelper db;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		db = new MySQLiteOpenHelper(getContext(), null, null, 1);
		assertNotNull(db);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		db.close();
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link ua.cc.cups.database.MySQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)}
	 * .
	 */
	public void testOnCreateSQLiteDatabase() {
		assertNotNull(db);
	}

	/**
	 * Test method for
	 * {@link ua.cc.cups.database.MySQLiteOpenHelper#addData(ua.cc.cups.database.Data)}
	 * .
	 */
	public void testAddDataAndFindData() {
		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 1; i <= 10; ++i) {
			list.add("Contact" + i);
		}

		Data data = new Data("Taras", "Melon", "Was born in...", "02/05/1992",
				list);

		db.addData(data);

		Data fetchedData = db.findData();
		assertEquals(data.getBio(), fetchedData.getBio());
		assertEquals(data.getDateOfBirth(), fetchedData.getDateOfBirth());
		assertEquals(data.getName(), fetchedData.getName());
		assertEquals(data.getSurname(), fetchedData.getSurname());
		int i = 0;
		for (String actual : fetchedData.getContacts()) {
			assertEquals(list.get(i), actual);
			i++;
		}
	}

	/**
	 * Testing case, when database is not exists, but user try to find data in
	 * database
	 */
	public void testFindDataWithoutAddData() {
		db.getWritableDatabase().execSQL(
				"DROP TABLE IF EXISTS " + MySQLiteOpenHelper.TABLE_USERS);
		db.getWritableDatabase().execSQL(
				"DROP TABLE IF EXISTS " + MySQLiteOpenHelper.TABLE_CONTACTS);

		Data fetchedData = db.findData();

		String createUsersTable = "CREATE TABLE "
				+ MySQLiteOpenHelper.TABLE_USERS + "("
				+ MySQLiteOpenHelper.USER_COLUMN_ID + " INTEGER PRIMARY KEY,"
				+ MySQLiteOpenHelper.USER_COLUMN_NAME + " TEXT,"
				+ MySQLiteOpenHelper.USER_COLUMN_SURNAME + " TEXT,"
				+ MySQLiteOpenHelper.USER_COLUMN_BIO + " TEXT,"
				+ MySQLiteOpenHelper.USER_COLUMN_DATEOFBIRTH + " TEXT" + ")";

		String createContactsTable = "CREATE TABLE "
				+ MySQLiteOpenHelper.TABLE_CONTACTS + "("
				+ MySQLiteOpenHelper.CONTACT_COLUMN_ID
				+ " INTEGER PRIMARY KEY,"
				+ MySQLiteOpenHelper.CONTACT_COLUMN_USER_ID + " INTEGER,"
				+ MySQLiteOpenHelper.CONTACT_COLUMN_FULL_NAME + " TEXT" + ")";

		db.getWritableDatabase().execSQL(createUsersTable);
		db.getWritableDatabase().execSQL(createContactsTable);

		assertNull(fetchedData);
	}

	/**
	 * Testing onUpgrade method
	 */
	public void testOnUpgrade() {
		db.onUpgrade(db.getWritableDatabase(), 1, 2);
		assertEquals(2, db.getWritableDatabase().getVersion());
	}
}
