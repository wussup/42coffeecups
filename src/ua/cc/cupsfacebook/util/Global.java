package ua.cc.cupsfacebook.util;

/**
 * Class represents all app's global variables that are used from many classes
 * 
 * @version 1.2 27-10-2013
 * @author Taras Melon
 */
public class Global {

	/**
	 * Database name string
	 */
	public static final String DATABASE_NAME = "infoDB.db";

	/**
	 * Full path to database placement
	 */
	public static final String DB_FULL_PATH = "//data/data/ua.cc.cupsfacebook/databases/"
			+ DATABASE_NAME;

	/**
	 * Tag for logging
	 */
	public static final String TAG = "[CupsFacebook]";

	/**
	 * Database current version
	 */
	public static final int DATABASE_VERSION = 3;

}
