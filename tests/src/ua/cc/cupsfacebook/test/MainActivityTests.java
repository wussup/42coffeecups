package ua.cc.cupsfacebook.test;

import ua.cc.cupsfacebook.MainActivity;
import ua.cc.cupsfacebook.R;
import ua.cc.cupsfacebook.util.Global;
import android.annotation.SuppressLint;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Tests for class MainActivity
 * 
 * @version 1.1 28-10-2013
 * @author Taras Melon
 */
public class MainActivityTests extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity mActivity;
	private TextView mBio;
	private TextView mDateOfBirth;
	private TextView mFullName;
	private ListView mListView;
	private ImageView mImageView;

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
		mListView = (ListView) mActivity.findViewById(R.id.listView);
		assertNotNull(mListView);
		mImageView = (ImageView) mActivity.findViewById(R.id.imageView);
		assertNotNull(mImageView);
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
		ViewAsserts.assertOnScreen(origin, mListView);
		ViewAsserts.assertOnScreen(origin, mImageView);
	}

	/**
	 * Testing views alignment
	 */
	@SmallTest
	public void testAlignment() {
		ViewAsserts.assertRightAligned(mFullName, mDateOfBirth);
		ViewAsserts.assertRightAligned(mDateOfBirth, mListView);
		ViewAsserts.assertRightAligned(mListView, mBio);
		ViewAsserts.assertLeftAligned(mImageView, mBio);
		ViewAsserts.assertLeftAligned(mFullName, mDateOfBirth);
		ViewAsserts.assertLeftAligned(mDateOfBirth, mListView);
	}

	/**
	 * Testing adding data to database
	 */
	@SmallTest
	public void testAddDataToDatabase() {
		mActivity.deleteDatabase(Global.DATABASE_NAME);

		mActivity = getActivity();
	}
}
