package ua.cc.cupsfacebook.test;

import java.util.ArrayList;

import junit.framework.TestCase;
import ua.cc.cupsfacebook.database.Data;

/**
 * Tests for class Data
 * 
 * @version 1.0 28-10-2013
 * @author Taras Melon
 */
public class DataTests extends TestCase {

	public DataTests(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link ua.cc.cups.database.Data#Data(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	public void testDataIntStringStringStringString() {
		int id = 1;

		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 1; i <= 10; ++i) {
			list.add("Contact" + i);
		}

		Data data = new Data(id, "name", "surname", "bio", "dateOfBirth", list);
		assertEquals(data.getId(), id);
	}

}
