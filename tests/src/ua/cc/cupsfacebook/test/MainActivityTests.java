package ua.cc.cupsfacebook.test;

import ua.cc.cupsfacebook.EditDataActivity;
import ua.cc.cupsfacebook.MainActivity;
import ua.cc.cupsfacebook.R;
import ua.cc.cupsfacebook.util.Global;
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
 * Tests for class MainActivity
 * 
 * @version 1.2 28-10-2013
 * @author Taras Melon
 */
public class MainActivityTests extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity mActivity;
	private TextView mBio;
	private TextView mDateOfBirth;
	private TextView mFullName;
	private ListView mListViewMine;
	private ImageView mImageView;
	private Button mEditData;
	private Button mLogout;
	private TabHost mTabs;
	private ListView mListView;

	/**
	 * @param name
	 */
	@SuppressLint("NewApi")
	public MainActivityTests(String name) {
		super(MainActivity.class);
		setName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
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
		mListViewMine = (ListView) mActivity.findViewById(R.id.listViewMine);
		assertNotNull(mListViewMine);
		mImageView = (ImageView) mActivity.findViewById(R.id.imageView);
		assertNotNull(mImageView);
		mEditData = (Button) mActivity.findViewById(R.id.editDataButton);
		assertNotNull(mEditData);
		mLogout = (Button) mActivity.findViewById(R.id.logoutButton);
		assertNotNull(mLogout);
		mTabs = (TabHost) mActivity.findViewById(R.id.tabhost);
		assertNotNull(mTabs);
		mListView = (ListView) mActivity.findViewById(R.id.listView);
		assertNotNull(mListView);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.test.ActivityInstrumentationTestCase2#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Testing views on screen
	 */
	@SmallTest
	public void testFieldsOnScreen() {
		final View origin = mActivity.getWindow().getDecorView();

		ViewAsserts.assertOnScreen(origin, mBio);
		ViewAsserts.assertOnScreen(origin, mDateOfBirth);
		ViewAsserts.assertOnScreen(origin, mFullName);
		ViewAsserts.assertOnScreen(origin, mListViewMine);
		ViewAsserts.assertOnScreen(origin, mImageView);
	}

	/**
	 * Testing adding data to database
	 */
	@SmallTest
	public void testAddDataToDatabase() {
		mActivity.deleteDatabase(Global.DATABASE_NAME);

		mActivity = getActivity();
	}

	/**
	 * Testing editing and saving user data in database
	 */
	@SmallTest
	public void testSaveChanges() {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				ActivityMonitor activityMonitor = getInstrumentation()
						.addMonitor(EditDataActivity.class.getName(), null,
								false);

				//mTabs.setCurrentTab(0);

				mEditData.performClick();

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				getInstrumentation().waitForIdleSync();
				final EditDataActivity nextActivity = (EditDataActivity) getInstrumentation()
						.waitForMonitorWithTimeout(activityMonitor, 10);

				assertNotNull(nextActivity);

				((TextView) nextActivity.findViewById(R.id.editTextName))
						.setText("Helloman");
				final Button saveChanges = (Button) nextActivity
						.findViewById(R.id.buttonSaveChanges);
				nextActivity.runOnUiThread(new SaveChangesAndBackPressedThread(
						saveChanges, nextActivity));
			}
		});
	}

	/**
	 * Thread perform click "Save Changes" and perform back click
	 * 
	 * @version 1.0 01-11-2013
	 * @author Taras Melon
	 */
	class SaveChangesAndBackPressedThread implements Runnable {

		private Button mSaveChanges;
		private EditDataActivity mNextActivity;

		public SaveChangesAndBackPressedThread(Button mSaveChanges,
				EditDataActivity mNextActivity) {
			this.mSaveChanges = mSaveChanges;
			this.mNextActivity = mNextActivity;
		}

		@Override
		public void run() {
			ActivityMonitor activityMonitor2 = getInstrumentation().addMonitor(
					MainActivity.class.getName(), null, false);

			mSaveChanges.performClick();
			mNextActivity.onBackPressed();

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			getInstrumentation().waitForIdleSync();
			final MainActivity nextActivity2 = (MainActivity) getInstrumentation()
					.waitForMonitorWithTimeout(activityMonitor2, 10);
			assertNotNull(nextActivity2);

			assertEquals("Helloman",
					((TextView) nextActivity2.findViewById(R.id.fullName))
							.getText().toString().split(" ")[0]);
		}

	}
}
