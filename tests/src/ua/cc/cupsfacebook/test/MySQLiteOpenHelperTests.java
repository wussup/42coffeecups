/**
 * 
 */
package ua.cc.cupsfacebook.test;

import ua.cc.cupsfacebook.database.Data;
import ua.cc.cupsfacebook.database.MySQLiteOpenHelper;
import android.test.AndroidTestCase;

/**
 * @author Taras
 *
 */
public class MySQLiteOpenHelperTests extends AndroidTestCase {

	private MySQLiteOpenHelper db;
	private static final String TABLE_PRODUCTS = "user_info";

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		db = new MySQLiteOpenHelper(getContext(), null, null, 1);
		assertNotNull(db);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		db.close();
		super.tearDown();
	}

	/**
	 * Test method for {@link ua.cc.cups.database.MySQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)}.
	 */
	public void testOnCreateSQLiteDatabase() {
		assertNotNull(db);
	}

	/**
	 * Test method for {@link ua.cc.cups.database.MySQLiteOpenHelper#addData(ua.cc.cups.database.Data)}.
	 */
	public void testAddDataAndFindData() {
        Data data = new Data("Taras", "Melon", "Was born in...", "05-02-1992", "1");
        
        db.addData(data);
        
        Data fetchedData = db.findData();
        assertEquals(data.getBio(), fetchedData.getBio());
        assertEquals(data.getDateOfBirth(), fetchedData.getDateOfBirth());
        assertEquals(data.getName(), fetchedData.getName());
        assertEquals(data.getSurname(), fetchedData.getSurname());
        assertEquals(data.getUserId(), fetchedData.getUserId());
	}
	
	public void testFindDataWithoutAddData()
	{
		db.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
		
		Data fetchedData = db.findData();
		
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
	             TABLE_PRODUCTS + "("
	             + MySQLiteOpenHelper.COLUMN_ID + " INTEGER PRIMARY KEY," + MySQLiteOpenHelper.COLUMN_NAME 
	             + " TEXT," + MySQLiteOpenHelper.COLUMN_SURNAME + " TEXT," + MySQLiteOpenHelper.COLUMN_BIO + " TEXT," + MySQLiteOpenHelper.COLUMN_DATEOFBIRTH + " TEXT" + ")";
		db.getWritableDatabase().execSQL(CREATE_PRODUCTS_TABLE);
		
        assertNull(fetchedData);
	}

	public void testOnUpgrade()
	{
		db.onUpgrade(db.getWritableDatabase(), 1, 2);
		assertEquals(2, db.getWritableDatabase().getVersion());
	}
}
