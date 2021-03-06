package ua.cc.cupsfacebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import ua.cc.cupsfacebook.database.Data;
import ua.cc.cupsfacebook.database.Friend;
import ua.cc.cupsfacebook.database.MySQLiteOpenHelper;
import ua.cc.cupsfacebook.util.Global;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * MainActivity represents fully functionality of the app.
 * 
 * Description of functionality you can find in file README
 * 
 * @version 1.4 27-10-2013
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
	private ItemsAdapter mItemsAdapter;
	private boolean mCheckboxesAreVisible = false;

	private ImageLoader mImageLoader;

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

		setUpListViewListener();

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

		setUpImageLoader();
	}

	/**
	 * Setting up Image Loader for downloading avatars
	 */
	private void setUpImageLoader() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).threadPoolSize(20).build();

		ImageLoader.getInstance().init(config);
		mImageLoader = ImageLoader.getInstance();
	}

	/**
	 * Setting up list view on scroll listener
	 */
	private void setUpListViewListener() {
		final ListView listView = (ListView) findViewById(R.id.listView);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(final AbsListView view,
					int scrollState) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
					onScrollStateIdle(listView);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	/**
	 * Method download avatars for each visible in list view friend
	 * 
	 * @param listView
	 *            list view with user contacts
	 */
	private synchronized void onScrollStateIdle(final ListView listView) {

		int first = listView.getFirstVisiblePosition();
		int last = listView.getLastVisiblePosition();
		for (int i = first; i <= last; i++) {
			final int num = i;

			if (mItemsAdapter.items[num].getDrawable() == null) {

				int firstPosition = listView.getFirstVisiblePosition()
						- listView.getHeaderViewsCount();
				int wantedChild = i - firstPosition;
				final TextView textView = (TextView) ((LinearLayout) listView
						.getChildAt(wantedChild)).findViewById(R.id.desc);

				final ImageView imageView = (ImageView) ((LinearLayout) listView
						.getChildAt(wantedChild)).findViewById(R.id.avatar);

				mImageLoader.loadImage("http://graph.facebook.com/"
						+ textView.getContentDescription().toString()
						+ "/picture?type=square",
						new MySimpleImageLoadingListener(num, imageView));
			}
		}

	}

	/**
	 * Different actions for on click listener on edit data button
	 */
	private void currentTabActions() {
		if (mCurrentTab == 1) {
			startEditDataActivity();
		} else if (mCurrentTab == 3) {
			mCheckboxesAreVisible = !mCheckboxesAreVisible;
			if (!mCheckboxesAreVisible) {
				ArrayList<Friend> tmpFriends = new ArrayList<Friend>(
						Arrays.asList(mItemsAdapter.items));
				sortListOfFriends(tmpFriends);

				mItemsAdapter.items = tmpFriends.toArray(new Friend[tmpFriends
						.size()]);

				mEditDataButton.setText("Edit Priorities");

				AsyncTask<String, Void, Boolean> task = new UpdateContactsAsyncTask();
				task.execute();
			} else {
				mEditDataButton.setText("Save Priorities");
			}

			mItemsAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Sorting list of friends via priority
	 * 
	 * @param tmpFriends
	 *            list of friends
	 */
	private void sortListOfFriends(ArrayList<Friend> tmpFriends) {
		Collections.sort(tmpFriends, new Comparator<Friend>() {

			@Override
			public int compare(Friend lhs, Friend rhs) {
				if (rhs.getPriority() > lhs.getPriority())
					return 1;
				else if (rhs.getPriority() < lhs.getPriority())
					return -1;
				else
					return 0;
			}

		});
	}

	/**
	 * Starting EditDataActivity with extras and finish MainActivity
	 */
	private void startEditDataActivity() {
		Intent i = new Intent(MainActivity.this, EditDataActivity.class);
		i.putExtra("NAME", mCurrentData.getName());
		i.putExtra("SURNAME", mCurrentData.getSurname());
		i.putExtra("BIO", mCurrentData.getBio());
		i.putExtra("DATE_OF_BIRTH", mCurrentData.getDateOfBirth());
		i.putExtra("ID", mId);
		startActivity(i);
		MainActivity.this.finish();
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

		ArrayList<Friend> friendList = new ArrayList<Friend>();

		if (contacts == null) {
			contacts = new ArrayList<String>();
			contacts.add("Error;1;0");
			Log.e(Global.TAG,
					"NullPointerException: value 'contacts' in method setUpListView is null");
		}

		for (String listString : contacts) {
			String[] splitted = listString.split(";");
			friendList.add(new Friend(splitted[0], splitted[1], Integer
					.valueOf(splitted[2])));
		}

		mItemsAdapter = new ItemsAdapter(MainActivity.this, R.layout.list_item,
				friendList.toArray(new Friend[friendList.size()]));
		listView.setAdapter(mItemsAdapter);
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
			Log.e(Global.TAG,
					"NullPointerException: value 'list' in method setUpListView is null");
		}

		final StableArrayAdapter adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	/**
	 * Setting up list view item on click listener. Listener load contact's
	 * Facebook page
	 * 
	 * @param convertView
	 *            list view item
	 * @param friend
	 *            contact info
	 */
	private void setUpListViewItemOnClickListener(View convertView,
			final Friend friend) {
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startFriendsPage(friend.getId());
			}

			private void startFriendsPage(String friendId) {
				final String urlFb = "fb://profile/" + friendId;
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(urlFb));

				final PackageManager packageManager = getPackageManager();
				List<ResolveInfo> list = packageManager.queryIntentActivities(
						intent, PackageManager.MATCH_DEFAULT_ONLY);
				if (list.size() == 0) {
					final String urlBrowser = "https://www.facebook.com/pages/"
							+ friendId;
					intent.setData(Uri.parse(urlBrowser));
				}

				startActivity(intent);
			}
		});
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
	 * Setting up tab widget
	 */
	private void setUpTabWidget() {
		TabHost tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				if (tabId.compareToIgnoreCase("tag1") == 0) {
					mEditDataButton.setEnabled(true);
					mEditDataButton.setText("Edit Data");
					mCurrentTab = 1;
				} else if (tabId.compareTo("tag2") == 0) {
					mEditDataButton.setEnabled(false);
					mCurrentTab = 2;
				} else {
					mEditDataButton.setEnabled(true);
					if (!mCheckboxesAreVisible)
						mEditDataButton.setText("Edit Priorities");
					else
						mEditDataButton.setText("Save Priorities");
					mCurrentTab = 3;
				}
			}
		});
		tabs.setup();
		TabHost.TabSpec spec = tabs.newTabSpec("tag1");
		spec.setContent(R.id.tab1);
		spec.setIndicator("Main");
		tabs.addTab(spec);
		spec = tabs.newTabSpec("tag3");
		spec.setContent(R.id.tab3);
		spec.setIndicator("Friends");
		tabs.addTab(spec);
		spec = tabs.newTabSpec("tag2");
		spec.setContent(R.id.tab2);
		spec.setIndicator("About");
		tabs.addTab(spec);
		tabs.setCurrentTab(0);
	}

	/**
	 * Setting up checkBox and checkBox's listener in adapter
	 * 
	 * @param convertView
	 *            list view item
	 * @param friend
	 *            contact info
	 */
	private void setUpCheckBox(ViewHolder viewHolder, final Friend friend) {
		final CheckBox box = viewHolder.checkBox;
		if (friend.getPriority() == 0)
			box.setChecked(false);
		else
			box.setChecked(true);
		box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (box.isPressed()) {
					Log.i(Global.TAG, friend.getName());
					if (friend.getPriority() == 1 && !isChecked)
						friend.setPriority(0);
					else if (friend.getPriority() == 0 && isChecked)
						friend.setPriority(1);
				}
			}
		});
		if (mCheckboxesAreVisible)
			box.setVisibility(View.VISIBLE);
		else {
			box.setVisibility(View.INVISIBLE);
		}
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
	 * ViewHolder pattern class
	 * 
	 * @version 1.2 02-11-2013
	 * @author Taras Melon
	 */
	private static class ViewHolder {
		TextView textView;
		CheckBox checkBox;
		ImageView avatar;
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

	/**
	 * Adapter for list view with user contacts
	 * 
	 * @version 1.2 02-11-2013
	 * @author Taras Melon
	 */
	private class ItemsAdapter extends BaseAdapter {

		/** The items. */
		Friend[] items;

		/**
		 * Instantiates a new items adapter.
		 * 
		 * @param context
		 *            the context
		 * @param textViewResourceId
		 *            the text view resource id
		 * @param items
		 *            the items
		 */
		public ItemsAdapter(Context context, int textViewResourceId,
				Friend[] items) {
			this.items = items;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder viewHolder;

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.list_item, null);

				viewHolder = new ViewHolder();
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.desc);
				viewHolder.checkBox = (CheckBox) convertView
						.findViewById(R.id.checkBoxPriority);

				viewHolder.avatar = (ImageView) convertView
						.findViewById(R.id.avatar);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final Friend friend;

			friend = items[position];

			viewHolder.textView.setText(friend.getName());
			viewHolder.textView.setContentDescription(friend.getId());

			if (friend.getDrawable() != null)
				viewHolder.avatar.setImageDrawable(friend.getDrawable());
			else
				viewHolder.avatar.setImageDrawable(MainActivity.this
						.getResources().getDrawable(R.drawable.ic_launcher));

			setUpListViewItemOnClickListener(convertView, friend);

			setUpCheckBox(viewHolder, friend);

			return convertView;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		public int getCount() {
			return items.length;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		public Object getItem(int position) {
			return items[position];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		public long getItemId(int position) {
			return Long.valueOf(items[position].getId());
		}
	}

	/**
	 * AsyncTask update user's friends list in database after priority changing
	 * 
	 * @version 1.0 02-11-2013
	 * @author Taras Melon
	 */
	private class UpdateContactsAsyncTask extends
			AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			MySQLiteOpenHelper helper = new MySQLiteOpenHelper(
					MainActivity.this, null, null, Global.DATABASE_VERSION);
			return helper.updateContacts(mId, mItemsAdapter.items);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(MainActivity.this,
						"Your data has been successfully saved!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MainActivity.this,
						"Your data has not been successfully saved!",
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	/**
	 * Listener waits for fetched friend's avatar and set it to friend item in
	 * list
	 * 
	 * @version 1.0 08-11-2013
	 * @author Taras Melon
	 */
	private class MySimpleImageLoadingListener extends
			SimpleImageLoadingListener {
		private int pos;
		private ImageView imageView;

		public MySimpleImageLoadingListener(int pos, ImageView imageView) {
			this.pos = pos;
			this.imageView = imageView;
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			imageView.setImageBitmap(loadedImage);
			Drawable drawable = new BitmapDrawable(getResources(), loadedImage);
			mItemsAdapter.items[pos].setDrawable(drawable);
		}
	}
}