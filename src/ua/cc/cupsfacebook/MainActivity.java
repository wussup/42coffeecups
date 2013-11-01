package ua.cc.cupsfacebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ua.cc.cupsfacebook.database.Data;
import ua.cc.cupsfacebook.database.MySQLiteOpenHelper;
import ua.cc.cupsfacebook.util.Global;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.facebook.Session;

/**
 * MainActivity represents fully functionality of the app.
 * 
 * Description of functionality you can find in file README
 * 
 * @version 1.2 27-10-2013
 * @author Taras Melon
 * 
 */
public class MainActivity extends Activity {

	private TextView mFullName;
	private TextView mMyFullName;
	private int mId = -1;
	private Button mEditDataButton;
	private int mCurrentTab = 1;
	private Data mCurrentData;
	private ImageView mUserPicture;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setUpFields();

		Boolean b = checkDataBase();
		if ((b != null) && (b))
			getDataFromDatabaseAndFillTextViews();

		setUpTabWidget();

		findViewById(R.id.logoutButton).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						onClickLogout();
					}
				});

		mEditDataButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				currentTabActions();
			}
		});
	}

	/**
	 * Different actions for on click listener on edit data button
	 */
	private void currentTabActions() {
		if (mCurrentTab == 1) {
			Intent i = new Intent(MainActivity.this, EditDataActivity.class);
			i.putExtra("NAME", mCurrentData.getName());
			i.putExtra("SURNAME", mCurrentData.getSurname());
			i.putExtra("BIO", mCurrentData.getBio());
			i.putExtra("DATE_OF_BIRTH", mCurrentData.getDateOfBirth());
			i.putExtra("ID", mId);
			startActivity(i);
			MainActivity.this.finish();
		}
	}

	/**
	 * Setting up activity fields
	 */
	private void setUpFields() {
		mUserPicture = (ImageView) findViewById(R.id.imageView);
		mEditDataButton = (Button) findViewById(R.id.editDataButton);
		mFullName = (TextView) findViewById(R.id.fullName);
		mMyFullName = (TextView) findViewById(R.id.fullNameMine);
	}

	/**
	 * Closing session and finish activity
	 */
	private void onClickLogout() {
		Session session = Session.getActiveSession();
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
			finish();
		}
	}

	/**
	 * Setting up ListView by adding contacts list with adapter
	 * 
	 * @param contacts
	 *            list of user's contacts
	 */
	private void setUpListView(ArrayList<String> contacts) {
		ListView listView = (ListView) findViewById(R.id.listView);

		if (contacts == null) {
			contacts = new ArrayList<String>();
			contacts.add("Error");
		}

		StableArrayAdapter adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_1, contacts);
		listView.setAdapter(adapter);
	}

	/**
	 * Getting data from database and fill text views and setting up listView
	 */
	private void getDataFromDatabaseAndFillTextViews() {
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, null, null,
				Global.DATABASE_VERSION);

		ArrayList<Data> dataList = helper.findData();

		Data data = dataList.get(0);
		mCurrentData = data;

		String fullNameString = data.getName() + " " + data.getSurname();
		mFullName.setText(fullNameString);

		mId = data.getId();

		((TextView) findViewById(R.id.dateOfBirth)).setText(data
				.getDateOfBirth());
		((TextView) findViewById(R.id.bio)).setText(data.getBio());

		setUpListView(data.getContacts());

		new RetreiveFeedTask().execute(data.getUserId());

		data = dataList.get(1);

		fullNameString = data.getName() + " " + data.getSurname();
		mMyFullName.setText(fullNameString);

		((TextView) findViewById(R.id.dateOfBirthMine)).setText(data
				.getDateOfBirth());
		((TextView) findViewById(R.id.bioMine)).setText(data.getBio());

		setUpMyListView(data.getContacts());
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

			Log.i(Global.TAG, "Database exists");
		} catch (SQLiteException e) {
			Log.i(Global.TAG, "Database doesn't exist yet");
		}
		return b;
	}

	/**
	 * Setting up my contacts list view
	 * 
	 * @param list
	 *            my contacts list
	 */
	private void setUpMyListView(ArrayList<String> list) {
		final ListView listview = (ListView) findViewById(R.id.listViewMine);

		if (list == null) {
			list = new ArrayList<String>();
			list.add("Error");
		}

		final StableArrayAdapter adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);
	}

	/**
	 * Setting up tab widget
	 */
	private void setUpTabWidget() {
		TabHost tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				if (tabId.compareToIgnoreCase("tag1") == 0) {
					mEditDataButton.setEnabled(true);
					mCurrentTab = 1;
				} else if (tabId.compareTo("tag2") == 0) {
					mEditDataButton.setEnabled(false);
					mCurrentTab = 2;
				}
			}
		});
		tabs.setup();
		TabHost.TabSpec spec = tabs.newTabSpec("tag1");
		spec.setContent(R.id.tab1);
		spec.setIndicator("Main");
		tabs.addTab(spec);
		spec = tabs.newTabSpec("tag2");
		spec.setContent(R.id.tab2);
		spec.setIndicator("About");
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

	/**
	 * AsyncTask for download user's avatar
	 * 
	 * @version 1.0 01-11-2013
	 * @author Taras Melon
	 */
	class RetreiveFeedTask extends AsyncTask<String, Void, Bitmap> {

		protected Bitmap doInBackground(String... urls) {
			try {
				URL img_value = null;

				img_value = new URL("http://graph.facebook.com/" + urls[0]
						+ "/picture?type=large");

				Bitmap mIcon1 = BitmapFactory.decodeStream(img_value
						.openConnection().getInputStream());
				return mIcon1;
			} catch (MalformedURLException e) {
				Log.e(Global.TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(Global.TAG, e.getMessage());
			}
			return null;
		}

		protected void onPostExecute(Bitmap result) {
			if (result != null)
				mUserPicture.setImageBitmap(result);
		}

	}

	@Override
	public void onBackPressed() {
		finish();
	}

}
