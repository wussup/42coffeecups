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
import ua.cc.cupsfacebook.database.MySQLiteOpenHelper;
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
import android.view.Menu;
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

import com.facebook.Session;

public class MainActivity extends Activity {

	public final static String DB_FULL_PATH = "//data/data/ua.cc.cupsfacebook/databases/infoDB.db";
	private ImageView user_picture;
	private final static String TAG = "[CupsFacebook]";
	private TextView fullName;
	private TextView myFullName;
	private int id = -1;
	private Button editDataButton;
	private int currentTab = 1;
	private Data currentData;
	private ItemsAdapter itemsAdapter;
	private boolean checkboxesAreVisible = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		editDataButton = (Button) findViewById(R.id.editDataButton);
		user_picture = (ImageView)findViewById(R.id.imageView);
		fullName = (TextView) findViewById(R.id.fullName);
		
		myFullName = (TextView) findViewById(R.id.fullNameMine);
		
		if (checkDataBase())
			getDataFromDatabaseAndFillTextViews();
		
		setUpTabWidget();
		
		findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickLogout();
			}
		});
		
		final ListView listView = (ListView)findViewById(R.id.listView);
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(final AbsListView view, int scrollState) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
				{
					int first = listView.getFirstVisiblePosition();
					int last = listView.getLastVisiblePosition();
					for (int i=first; i<=last; i++)
					{
						int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount(); // This is the same as child #0
						int wantedChild = i - firstPosition;
						final TextView textView = (TextView)((LinearLayout)listView.getChildAt(wantedChild)).findViewById(R.id.desc); 
						
						final AsyncTask<String, Void, Bitmap> task =  new AsyncTask<String, Void, Bitmap> () {
							
							@Override
							protected Bitmap doInBackground(String... urls) {
						    	try {
						         	URL img_value = null;
						         	 
									img_value = new URL("http://graph.facebook.com/"+urls[0]+"/picture?type=square");
										
						         	Bitmap mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
						         	return mIcon1;
						    	} 
								catch (MalformedURLException e) {
									e.printStackTrace();
								}
						        catch (IOException e) {
									e.printStackTrace();
								}
						    	return null;
						    }
						    
						    protected void onPostExecute(Bitmap result) {
						    	if (result!=null)
						    	{
						    		Drawable drawable = new BitmapDrawable(getResources(), result);
						    		textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
						    	}
						    }
						   };
						   task.execute(textView.getContentDescription().toString());
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) 
			{
			}
		});
		
		findViewById(R.id.editDataButton).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*if (currentTab==2)
				{
					String newAbout = about.getText().toString();
					if (newAbout.compareTo(oldAbout)!=0)
					{
						MySQLiteOpenHelper helper = new MySQLiteOpenHelper(MainActivity.this, null, null, 1);
						
						if (helper.updateAbout(id, newAbout))
						{
							oldAbout = newAbout;
						
							Toast.makeText(MainActivity.this, "Your data has been successfully saved!", Toast.LENGTH_SHORT).show();
						}
					}
					else
					{
						Toast.makeText(MainActivity.this, "You did not change data!", Toast.LENGTH_SHORT).show();
					}
				}
				else */if (currentTab==1)
				{
					Intent i = new Intent(MainActivity.this, EditDataActivity.class);
					i.putExtra("NAME", currentData.getName());
					i.putExtra("SURNAME", currentData.getSurname());
					i.putExtra("BIO", currentData.getBio());
					i.putExtra("DATE_OF_BIRTH", currentData.getDateOfBirth());
					i.putExtra("ID", id);
					startActivity(i);
					MainActivity.this.finish();
				}
				else if (currentTab==3)
				{
					checkboxesAreVisible=!checkboxesAreVisible;
					if (!checkboxesAreVisible)
					{
						ArrayList<Friend> tmpFriends = new ArrayList<MainActivity.Friend>(Arrays.asList(itemsAdapter.items));
						Collections.sort(tmpFriends, new Comparator<Friend>() {

							@Override
							public int compare(Friend lhs, Friend rhs) {
								if (rhs.getPriority()>lhs.getPriority())
									return 1;
								else if (rhs.getPriority()<lhs.getPriority())
									return -1;
								else
									return 0;
							}
							
						});
						
						itemsAdapter.items = tmpFriends.toArray(new Friend[tmpFriends.size()]);
						
						editDataButton.setText("Edit Priorities");
					}
					else
					{
						/*int count = listView.getChildCount();
						for (int i=0; i<count; i++)
						{
							CheckBox box = (CheckBox)((LinearLayout)listView.getChildAt(i)).findViewById(R.id.checkBoxPriority);
							
							box.setVisibility(View.VISIBLE);
						}*/
						
						editDataButton.setText("Save Priorities");
					}
					
					//int first = listView.getFirstVisiblePosition();
					itemsAdapter.notifyDataSetChanged();
				}
			}
		});
		
		
    }

	private class Friend
	{
		private String name;
		private int priority;
		private String id;
		
		public Friend(String name, int priority, String id) {
			this.name = name;
			this.priority = priority;
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public int getPriority() {
			return priority;
		}
		public String getId() {
			return id;
		}
		public void setPriority(int priority) {
			this.priority = priority;
		}
	}
	
	/*private String[] getSortedFriendList(ArrayList<Friend> list)
	{
		ArrayList<String> tmpList = new ArrayList<String>();
		for (Friend friend: list)
		{
			tmpList.add(friend.getName()+";"+friend.getId());
		}
		return tmpList.toArray(new String[tmpList.size()]);
	}*/
	
	private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
            finish();
        }
    }
	
	private boolean checkDataBase() {
	    SQLiteDatabase checkDB = null;
	    try {
	        checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null,
	                SQLiteDatabase.OPEN_READONLY);
	        checkDB.close();
	        Log.i(TAG, "Database exists");
	    } catch (SQLiteException e) {
	    	Log.i(TAG, "Database doesn't exist yet");
	    }
	    return checkDB != null ? true : false;
	}

	private void setUpListView(ArrayList<String> list) {
		final ListView listview = (ListView) findViewById(R.id.listView);
		
		ArrayList<Friend> friendList = new ArrayList<MainActivity.Friend>();
		
		for (String listString: list)
		{
			String[] splitted = listString.split(";");
			friendList.add(new Friend(splitted[0],0, splitted[1]));
		}
		
		itemsAdapter = new ItemsAdapter(
		    MainActivity.this, R.layout.list_item,
		    friendList.toArray(new Friend[friendList.size()]));
		listview.setAdapter(itemsAdapter);
	}

	private void setUpMyListView(ArrayList<String> list) {
		final ListView listview = (ListView) findViewById(R.id.listViewMine);

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
            android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
	}

	/**
	 * The Class ItemsAdapter.
	 */
	private class ItemsAdapter extends BaseAdapter {
		  
  		/** The items. */
  		Friend[] items;

		  /**
  		 * Instantiates a new items adapter.
  		 *
  		 * @param context the context
  		 * @param textViewResourceId the text view resource id
  		 * @param items the items
  		 */
  		public ItemsAdapter(Context context, int textViewResourceId,
		    Friend[] items) {
		   this.items = items;
		  }
  		
		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		  public View getView(final int position, View convertView,
		    ViewGroup parent) {
		   final TextView mDescription;
		   View view = convertView;
		   if (view == null) {
		    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    view = vi.inflate(R.layout.list_item, null);
		   }
		   
		   mDescription = (TextView) view.findViewById(R.id.desc);
		   
		   //final String[] nameAndId = items[position].split(";");
		   final Friend friend = items[position];
		   mDescription.setText(/*nameAndId[0]*/friend.getName());
		   
		   mDescription.setContentDescription(/*nameAndId[1]*/friend.getId());
		   
		   view.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) {
					startFriendsPage(/*nameAndId[1]*/friend.getId());
				}

				private void startFriendsPage(String friendId) {
					final String urlFb = "fb://page/"+friendId;
			        Intent intent = new Intent(Intent.ACTION_VIEW);
			        intent.setData(Uri.parse(urlFb));

			        // If Facebook application is installed, use that else launch a browser
			        final PackageManager packageManager = getPackageManager();
			        List<ResolveInfo> list =
			            packageManager.queryIntentActivities(intent,
			            PackageManager.MATCH_DEFAULT_ONLY);
			        if (list.size() == 0) {
			            final String urlBrowser = "https://www.facebook.com/pages/"+friendId;
			            intent.setData(Uri.parse(urlBrowser));
			        }

			        startActivity(intent);
				}
		   });
		   
		   final CheckBox box = (CheckBox) view.findViewById(R.id.checkBoxPriority);
		   if (friend.getPriority()==0)
			   box.setChecked(false);
		   else
			   box.setChecked(true);
		   box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//box.setChecked(isChecked);
				if (isChecked)
					friend.setPriority(1);
				else
					friend.setPriority(0);
			}
		});
		   if (checkboxesAreVisible)
			   box.setVisibility(View.VISIBLE);
		   else
		   {
			   box.setVisibility(View.INVISIBLE);
		   }
		   
		   /*int count = listView.getChildCount();
			ArrayList<Friend> tmpFriends = new ArrayList<MainActivity.Friend>();
			for (int i=0; i<count; i++)
			{
				LinearLayout layout = (LinearLayout)listView.getChildAt(i);
				TextView textView = (TextView)layout.findViewById(R.id.desc);
				CheckBox box = (CheckBox)layout.findViewById(R.id.checkBoxPriority);
				
				tmpFriends.add(new Friend(textView.getText().toString(), box.isChecked() ? 1:0, textView.getContentDescription().toString()));
				
				box.setVisibility(View.INVISIBLE);
			}
			
			Collections.sort(tmpFriends, new Comparator<Friend>() {

				@Override
				public int compare(Friend lhs, Friend rhs) {
					if (rhs.getPriority()>lhs.getPriority())
						return 1;
					else if (rhs.getPriority()<lhs.getPriority())
						return -1;
					else
						return 0;
				}
				
			});
			
			itemsAdapter.items = getSortedFriendList(tmpFriends); */
		   
		   return view;
		  }

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		public int getCount() {
		   return items.length;
		  }

		  /* (non-Javadoc)
  		 * @see android.widget.Adapter#getItem(int)
  		 */
  		public Object getItem(int position) {
		   return position;
		  }

		  /* (non-Javadoc)
  		 * @see android.widget.Adapter#getItemId(int)
  		 */
  		public long getItemId(int position) {
		   return position;
		  }
		 
	}
	
	private void getDataFromDatabaseAndFillTextViews() {
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, null, null, 1);
        
        ArrayList<Data> dataList = helper.findData();
        
        Data data = dataList.get(0);
        
        currentData = data;
        
        String fullNameString = data.getName()+" "+data.getSurname();
		fullName.setText(fullNameString);
		
		id = data.getId();
		
		if (data.getDateOfBirth()!=null)
			((TextView)findViewById(R.id.dateOfBirth)).setText(data.getDateOfBirth());
		else
			((TextView)findViewById(R.id.dateOfBirth)).setText("Inaccessible");
		((TextView)findViewById(R.id.bio)).setText(data.getBio());
		
		setUpListView(data.getContacts());
		
		new RetreiveFeedTask().execute(data.getUserId());
		
		data = dataList.get(1);
        
        fullNameString = data.getName()+" "+data.getSurname();
		myFullName.setText(fullNameString);
		
		if (data.getDateOfBirth()!=null)
			((TextView)findViewById(R.id.dateOfBirthMine)).setText(data.getDateOfBirth());
		else
			((TextView)findViewById(R.id.dateOfBirthMine)).setText("Inaccessible");
		((TextView)findViewById(R.id.bioMine)).setText(data.getBio());
		
		setUpMyListView(data.getContacts());
	}
	
	private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
            List<String> objects) {
          super(context, textViewResourceId, objects);
          for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
          }
        }

        @Override
        public long getItemId(int position) {
          String item = getItem(position);
          return mIdMap.get(item);
        }

      }
	
	class RetreiveFeedTask extends AsyncTask<String, Void, Bitmap> {

	    protected Bitmap doInBackground(String... urls) {
	    	try {
	         	URL img_value = null;
	         	 
				img_value = new URL("http://graph.facebook.com/"+urls[0]+"/picture?type=large");
					
	         	Bitmap mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
	         	return mIcon1;
	    	} 
			catch (MalformedURLException e) {
				e.printStackTrace();
			}
	        catch (IOException e) {
				e.printStackTrace();
			}
	    	return null;
	    }
	    
	    protected void onPostExecute(Bitmap result) {
	    	if (result!=null)
	    		user_picture.setImageBitmap(result);
	     }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ma, menu);
		return true;
	}

	private void setUpTabWidget() {
		TabHost tabs=(TabHost)findViewById(R.id.tabhost); 
		tabs.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				if (tabId.compareToIgnoreCase("tag1")==0)
				{
					editDataButton.setEnabled(true);
					currentTab = 1;
				}
				else if (tabId.compareTo("tag2")==0)
				{
					editDataButton.setEnabled(false);
					currentTab = 2;
				}
				else 
				{
					editDataButton.setEnabled(true);
					if (!checkboxesAreVisible)
						editDataButton.setText("Edit Priorities");
					else
						editDataButton.setText("Save Priorities");
					currentTab = 3;
				}
			}
		});
        tabs.setup(); 
        TabHost.TabSpec spec=tabs.newTabSpec("tag1"); 
        spec.setContent(R.id.tab1); 
        spec.setIndicator("Main"); 
        tabs.addTab(spec);
        spec=tabs.newTabSpec("tag3"); 
        spec.setContent(R.id.tab3); 
        spec.setIndicator("Friends"); 
        tabs.addTab(spec);
        spec=tabs.newTabSpec("tag2"); 
        spec.setContent(R.id.tab2); 
        spec.setIndicator("About"); 
        tabs.addTab(spec);
        tabs.setCurrentTab(0);
	}
	
}
