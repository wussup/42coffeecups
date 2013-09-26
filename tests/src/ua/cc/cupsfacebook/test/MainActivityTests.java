/**
 * 
 */
package ua.cc.cupsfacebook.test;

import ua.cc.cupsfacebook.R;
import ua.cc.cupsfacebook.MainActivity;
import android.annotation.SuppressLint;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
	private static final String DATABASE_NAME = "infoDB.db";
	
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
	}
	
	@SmallTest
	public void testAlignment()
	{
		ViewAsserts.assertRightAligned(mFullName, mDateOfBirth);
		ViewAsserts.assertRightAligned(mDateOfBirth,mListView);
		ViewAsserts.assertRightAligned(mListView, mBio);
		ViewAsserts.assertLeftAligned(mImageView, mBio);
		ViewAsserts.assertLeftAligned(mFullName, mDateOfBirth);
		ViewAsserts.assertLeftAligned(mDateOfBirth, mListView);
	}
	
	@SmallTest
	public void testAddDataToDatabase()
	{
		mActivity.deleteDatabase(DATABASE_NAME);
		
		mActivity = getActivity();
	}
}
