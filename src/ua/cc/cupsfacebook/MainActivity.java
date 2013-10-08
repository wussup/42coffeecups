package ua.cc.cupsfacebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;

public class MainActivity extends Activity {

	public final static String DB_FULL_PATH = "//data/data/ua.cc.cupsfacebook/databases/infoDB.db";
	private ImageView user_picture;
	private final static String TAG = "[CupsFacebook]";
	private TextView fullName;
	private TextView myFullName;
	private String oldAbout = "";
	private int id = -1;
	private Button editDataButton;
	private int currentTab = 1;
	private TextView about;
	private Data currentData;
	private ItemsAdapter itemsAdapter;
	private int first=-1;
	
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
		
		findViewById(R.id.editDataButton).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (currentTab==2)
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
				else if (currentTab==1)
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
			}
		});
		
		/*final ListView listView = (ListView)findViewById(R.id.listView);
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(final AbsListView view, int scrollState) {
				if (first!=-1)
				{
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
					    		Log.i(TAG, view.getItemAtPosition(first).toString());
					    		((TextView)listView.getChildAt(first).findViewById(R.id.desc)).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
					    		//counter--;
					    	}
					    }
					   };
					   task.execute("1");
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) 
			{
				first = firstVisibleItem;
			}
		});*/
    }

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

		String[] friends=list.toArray(new String[list.size()]);
		
		itemsAdapter = new ItemsAdapter(
		    MainActivity.this, R.layout.list_item,
		    friends);
		listview.setAdapter(itemsAdapter);
		
        /*final StableArrayAdapter adapter = new StableArrayAdapter(this,
            android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);*/
	}

	private void setUpMyListView(ArrayList<String> list) {
		final ListView listview = (ListView) findViewById(R.id.listViewMine);

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
            android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
	}
	
	/*@Override
	public void onBackPressed() {
		finish();
	}*/

	/**
	 * The Class ItemsAdapter.
	 */
	private class ItemsAdapter extends BaseAdapter {
		  
  		/** The items. */
  		String[] items;

		  /**
  		 * Instantiates a new items adapter.
  		 *
  		 * @param context the context
  		 * @param textViewResourceId the text view resource id
  		 * @param items the items
  		 */
  		public ItemsAdapter(Context context, int textViewResourceId,
		    String[] items) {
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
		   
		   final String[] nameAndId = items[position].split(";");
		   
		   mDescription.setText(nameAndId[0]);
		   
		   //mDescription.setContentDescription(nameAndId[1]);
		   
		   view.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) {
					startFriendsPage(nameAndId[1]);
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
		   
		   /*final AsyncTask<String, Void, Bitmap> task =  new AsyncTask<String, Void, Bitmap> () {
			
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
		    		mDescription.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
		    		counter--;
		    	}
		    }
		   };
		   new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean done = false;
				while (!done)
				{
					if (counter<=100)
					{
						counter++;
						task.execute(nameAndId[1]);
						done = true;
					}
					else
					{
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();*/
		   
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
					//editDataButton.setText("Edit Data");
					currentTab = 1;
				}
				else if (tabId.compareTo("tag2")==0)
				{
//					editDataButton.setText("Save Info");
					editDataButton.setEnabled(false);
					currentTab = 2;
				}
				else 
				{
					editDataButton.setEnabled(false);
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
