/**
 * 
 */
package ua.cc.cupsfacebook.test;

import java.util.ArrayList;

import ua.cc.cupsfacebook.database.Data;
import ua.cc.cupsfacebook.database.MySQLiteOpenHelper;
import android.test.AndroidTestCase;

/**
 * @author Taras
 *
 */
public class MySQLiteOpenHelperTests extends AndroidTestCase {

	private MySQLiteOpenHelper db;
	//private static final String TABLE_PRODUCTS = "user_info";

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
		ArrayList<String> list = new ArrayList<String>();
		for (int i=1; i<=10; i++)
			list.add("Contact"+i);
		
        Data data = new Data("Taras", "Melon", "Was born in...", "02/05/1992", "1", list, "");
        
        db.addData(data);
        
        ArrayList<Data> dataList = db.findData();
        Data fetchedData = dataList.get(1);
        assertEquals(data.getBio(), fetchedData.getBio());
        assertEquals(data.getDateOfBirth(), fetchedData.getDateOfBirth());
        assertEquals(data.getName(), fetchedData.getName());
        assertEquals(data.getSurname(), fetchedData.getSurname());
        assertEquals(data.getUserId(), fetchedData.getUserId());
        int i=0;
        for (String el : fetchedData.getContacts())
        {
        	assertEquals(list.get(i), el);
        	i++;
        }
        assertEquals(data.getAbout(), fetchedData.getAbout());
	}
}
