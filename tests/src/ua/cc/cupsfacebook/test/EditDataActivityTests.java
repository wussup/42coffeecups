package ua.cc.cupsfacebook.test;

import ua.cc.cupsfacebook.EditDataActivity;
import ua.cc.cupsfacebook.R;
import android.annotation.SuppressLint;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.TextView;

public class EditDataActivityTests extends
		ActivityInstrumentationTestCase2<EditDataActivity> {

	private TextView name;
	private TextView surname;
	private TextView bio;
	private TextView dateOfBirth;
	private EditDataActivity mActivity;
	
	@SuppressLint("NewApi")
	public EditDataActivityTests(String name) {
		super(EditDataActivity.class);
		setName(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		mActivity = getActivity();
		assertNotNull(mActivity);
		
		name = (TextView) mActivity.findViewById(R.id.editTextName);
		surname = (TextView) mActivity.findViewById(R.id.editTextSurname);
		bio = (TextView) mActivity.findViewById(R.id.editTextBio);
		dateOfBirth = (TextView) mActivity.findViewById(R.id.editTextDateOfBirth);
		
		assertNotNull(name);
		assertNotNull(surname);
		assertNotNull(bio);
		assertNotNull(dateOfBirth);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
}
