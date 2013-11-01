package ua.cc.cupsfacebook.test;

import ua.cc.cupsfacebook.EditDataActivity;
import ua.cc.cupsfacebook.R;
import android.annotation.SuppressLint;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

/**
 * Testing EditDataActivity
 * 
 * @version 1.0 01-11-2013
 * @author Taras Melon
 */
public class EditDataActivityTests extends
		ActivityInstrumentationTestCase2<EditDataActivity> {

	private TextView mName;
	private TextView mSurname;
	private TextView mBio;
	private TextView mDateOfBirth;
	private EditDataActivity mActivity;

	@SuppressLint("NewApi")
	public EditDataActivityTests(String name) {
		super(EditDataActivity.class);
		setName(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mActivity = getActivity();
		assertNotNull(mActivity);

		mName = (TextView) mActivity.findViewById(R.id.editTextName);
		mSurname = (TextView) mActivity.findViewById(R.id.editTextSurname);
		mBio = (TextView) mActivity.findViewById(R.id.editTextBio);
		mDateOfBirth = (TextView) mActivity
				.findViewById(R.id.editTextDateOfBirth);

		assertNotNull(mName);
		assertNotNull(mSurname);
		assertNotNull(mBio);
		assertNotNull(mDateOfBirth);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
