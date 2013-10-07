/**
 * 
 */
package ua.cc.cupsfacebook.test;

import ua.cc.cupsfacebook.EditDataActivity;
import ua.cc.cupsfacebook.MainActivity;
import ua.cc.cupsfacebook.R;
import android.annotation.SuppressLint;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * @author Taras
 *
 */
public class MainActivityTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity mActivity;
	private TextView mBio;
	private TextView mDateOfBirth;
	private TextView mFullName;
	private ListView mListView;
	private ImageView mImageView;
	private Button editData;
	private Button logout;
	private TabHost tabs;
	
	/**
	 * @param name
	 */
	@SuppressLint("NewApi")
	public MainActivityTests(String name) {
		super(MainActivity.class);
		setName(name);
	}

	/* (non-Javadoc)
	 * @see android.test.ActivityInstrumentationTestCase2#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		mActivity = getActivity();
		assertNotNull(mActivity);
		
		mBio = (TextView) mActivity.findViewById(R.id.bio);
		assertNotNull(mBio);
		mDateOfBirth = (TextView) mActivity.findViewById(R.id.dateOfBirth);
		assertNotNull(mDateOfBirth);
		mFullName = (TextView) mActivity.findViewById(R.id.fullName);
		assertNotNull(mFullName);
		mListView = (ListView) mActivity.findViewById(R.id.listView);
		assertNotNull(mListView);
		mImageView = (ImageView) mActivity.findViewById(R.id.imageView);
		assertNotNull(mImageView);
		editData = (Button) mActivity.findViewById(R.id.editDataButton);
		assertNotNull(editData);
		logout = (Button) mActivity.findViewById(R.id.logoutButton);
		assertNotNull(logout);
		tabs = (TabHost)mActivity.findViewById(R.id.tabhost); 
		assertNotNull(tabs);
	}

	/* (non-Javadoc)
	 * @see android.test.ActivityInstrumentationTestCase2#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SmallTest
	public void testFieldsOnScreen()
	{
		final View origin =
				mActivity.getWindow().getDecorView();
		
		ViewAsserts.assertOnScreen(origin, mBio);
		ViewAsserts.assertOnScreen(origin, mDateOfBirth);
		ViewAsserts.assertOnScreen(origin, mFullName);
		ViewAsserts.assertOnScreen(origin, mListView);
		ViewAsserts.assertOnScreen(origin, mImageView);
		ViewAsserts.assertOnScreen(origin, editData);
		ViewAsserts.assertOnScreen(origin, logout);
	}
	
	public void testSaveChanges()
	{
		mActivity.runOnUiThread(new Runnable() {
		    public void run() {
		    	
		    	try {
			      Thread.sleep(2000);
			    } catch (InterruptedException e) {
			      e.printStackTrace();
			    }
		  
		    	// register next activity that need to be monitored.
				ActivityMonitor activityMonitor = getInstrumentation().addMonitor(EditDataActivity.class.getName(), null, false);
		    	
				//tests for info tab
				tabs.setCurrentTab(0);
				
				editData.performClick();
				
				try {
				      Thread.sleep(2000);
				    } catch (InterruptedException e) {
				      e.printStackTrace();
				    }
				
				getInstrumentation().waitForIdleSync();
				final EditDataActivity nextActivity = (EditDataActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 10);
				// next activity is opened and captured.
				assertNotNull(nextActivity);
				
				((TextView)nextActivity.findViewById(R.id.editTextName)).setText("Helloman");
				final Button saveChanges = (Button)nextActivity.findViewById(R.id.buttonSaveChanges);
				nextActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// register next activity that need to be monitored.
						ActivityMonitor activityMonitor2 = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
						
						saveChanges.performClick();
						nextActivity.onBackPressed();
						
						try {
						      Thread.sleep(2000);
						    } catch (InterruptedException e) {
						      e.printStackTrace();
						    }
						
						getInstrumentation().waitForIdleSync();
						final MainActivity nextActivity2 = (MainActivity) getInstrumentation().waitForMonitorWithTimeout(activityMonitor2, 10);
						// next activity is opened and captured.
						assertNotNull(nextActivity2);
						
						assertEquals("Helloman", ((TextView)nextActivity2.findViewById(R.id.fullName)).getText().toString().split(" ")[0]);
					}
				});
		    }
		  });
	}
}
