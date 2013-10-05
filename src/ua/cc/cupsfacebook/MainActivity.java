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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.facebook.Session;

public class MainActivity extends Activity {

	public final static String DB_FULL_PATH = "//data/data/ua.cc.cupsfacebook/databases/infoDB.db";
	private ImageView user_picture;
	private final static String TAG = "[CupsFacebook]";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		user_picture = (ImageView)findViewById(R.id.imageView);
		
		if (checkDataBase())
			getDataFromDatabaseAndFillTextViews();
		
		setUpTabWidget();
		
		findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickLogout();
			}
		});
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

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
            android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
	}

	private void getDataFromDatabaseAndFillTextViews() {
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, null, null, 1);
        
        Data data = helper.findData();
        
		((TextView)findViewById(R.id.fullName)).setText(data.getName()+" "+data.getSurname());
		if (data.getDateOfBirth()!=null)
			((TextView)findViewById(R.id.dateOfBirth)).setText(data.getDateOfBirth());
		else
			((TextView)findViewById(R.id.dateOfBirth)).setText("Inaccessible");
		((TextView)findViewById(R.id.bio)).setText(data.getBio());

		setUpListView(data.getContacts());
		
		new RetreiveFeedTask().execute(data.getUserId());
		
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
        tabs.setup(); 
        TabHost.TabSpec spec=tabs.newTabSpec("tag1"); 
        spec.setContent(R.id.tab1); 
        spec.setIndicator("Info"); 
        tabs.addTab(spec);
     /*   spec=tabs.newTabSpec("tag2"); 
        spec.setContent(R.id.tab2); 
        spec.setIndicator("About"); 
        tabs.addTab(spec);
        spec=tabs.newTabSpec("tag3"); 
        spec.setContent(R.id.tab3); 
        spec.setIndicator("Tab3"); 
        tabs.addTab(spec);*/ 
        tabs.setCurrentTab(0);
	}
	
}
