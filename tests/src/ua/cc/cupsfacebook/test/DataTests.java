/**
 * 
 */
package ua.cc.cupsfacebook.test;

import ua.cc.cupsfacebook.database.Data;
import junit.framework.TestCase;

/**
 * @author Taras
 *
 */
public class DataTests extends TestCase {

	/**
	 * @param name
	 */
	public DataTests(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link ua.cc.cups.database.Data#Data(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	public void testDataIntStringStringStringString() {
		int id=1;
		Data data = new Data(id, "name", "surname", "bio", "dateOfBirth", "1");
		assertEquals(data.getId(), id);
	}

}
