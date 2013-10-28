package ua.cc.cupsfacebook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ua.cc.cupsfacebook.database.Data;
import ua.cc.cupsfacebook.database.MySQLiteOpenHelper;
import ua.cc.cupsfacebook.util.Global;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * MainActivity represents fully functionality of the app.
 * 
 * Task:
 * 
 * Create a tabbed android project that would present your name, surname, date
 * of birth, bio, contacts and photo (128x128 with a towel ;) on the main page.
 * The data should be stored in a sqlite database, created and filled
 * automatically on the first launch or when the user data is cleared.
 * 
 * The application should work on Android 2.x and Android 4.x, portrait and
 * landscape.
 * 
 * Store the last built of the application as attached .apk to the ticket before
 * assigning it to the reviewer.
 * 
 * Place a link to your git repository on Github with the first comment.
 * 
 * And don't forget about tests.
 * 
 * @version 1.0 27-10-2013
 * @author Taras Melon
 * 
 */
public class MainActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Boolean b = checkDataBase();
		if ((b==null) || (!b))
			addDataToDatabase();

		getDataFromDatabaseAndFillTextViews();

		setUpTabWidget();
	}

	/**
	 * Setting up ListView by adding contacts list with adapter
	 * 
	 * @param contacts
	 *            list of user's contacts
	 */
	private void setUpListView(ArrayList<String> contacts) {
		ListView listView = (ListView) findViewById(R.id.listView);

		StableArrayAdapter adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_1, contacts);
		listView.setAdapter(adapter);
	}

	/**
	 * Getting data from database and fill text views and setting up listView
	 */
	private void getDataFromDatabaseAndFillTextViews() {
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, null, null, 1);

		Data data = helper.findData();

		((TextView) findViewById(R.id.fullName)).setText(data.getName() + " "
				+ data.getSurname());
		((TextView) findViewById(R.id.dateOfBirth)).setText(data
				.getDateOfBirth());
		((TextView) findViewById(R.id.bio)).setText(data.getBio());

		setUpListView(data.getContacts());
	}

	/**
	 * Adding data to database
	 */
	private void addDataToDatabase() {
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, null, null,
				Global.DATABASE_VERSION);

		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 1; i <= 10; ++i) {
			list.add("Contact" + i);
		}

		Data data = new Data("Taras", "Melon", "Was born in...", "02/05/1992",
				list);

		helper.addData(data);
	}

	/**
	 * Checking if database exists. Method try to open database by path.
	 * 
	 * @return Boolean value is returned: null - database doesn't exist, false -
	 *         database is in another version, true - all good
	 */
	private Boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		Boolean b = null;
		try {
			checkDB = SQLiteDatabase.openDatabase(Global.DB_FULL_PATH, null,
					SQLiteDatabase.OPEN_READONLY);

			if (checkDB.getVersion() == Global.DATABASE_VERSION)
				b = true;
			else
				b = false;

			checkDB.close();

			Log.i(Global.TAG, "Database exist");
		} catch (SQLiteException e) {
			Log.i(Global.TAG, "Database doesn't exist yet");
		}
		return b;
	}

	/**
	 * Setting up tab widget
	 */
	private void setUpTabWidget() {
		TabHost tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setup();
		TabHost.TabSpec spec = tabs.newTabSpec("tag1");
		spec.setContent(R.id.tab1);
		spec.setIndicator("Info");
		tabs.addTab(spec);
		tabs.setCurrentTab(0);
	}

	/**
	 * ArrayAdapter for ListView
	 * 
	 * @version 1.0 28-10-2013
	 * @author Taras Melon
	 */
	private class StableArrayAdapter extends ArrayAdapter<String> {

		/**
		 * List of contacts
		 */
		private HashMap<String, Integer> mListOfContacts = new HashMap<String, Integer>();

		/**
		 * Constructor
		 * 
		 * @param context
		 *            needed context
		 * @param textViewResourceId
		 *            type of list view
		 * @param objects
		 *            list of contacts
		 */
		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mListOfContacts.put(objects.get(i), i);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mListOfContacts.get(item);
		}

	}
}
