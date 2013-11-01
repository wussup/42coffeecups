package ua.cc.cupsfacebook.test;

import ua.cc.cupsfacebook.ui.LoginActivity;
import ua.cc.cupsfacebook.R;
import android.annotation.SuppressLint;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.Button;

/**
 * Tests for LoginActivity
 * 
 * @version 1.0 29-10-2013
 * @author Taras Melon
 */
public class LoginActivityTests extends
		ActivityInstrumentationTestCase2<LoginActivity> {

	private LoginActivity mActivity;
	private Button mButton;

	@SuppressLint("NewApi")
	public LoginActivityTests(String name) {
		super(LoginActivity.class);
		setName(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mActivity = getActivity();
		assertNotNull(mActivity);

		mButton = (Button) mActivity.findViewById(R.id.loginButton);
		assertNotNull(mButton);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Testing views on screen
	 */
	@SmallTest
	public void testFieldsOnScreen() {
		final View origin = mActivity.getWindow().getDecorView();

		ViewAsserts.assertOnScreen(origin, mButton);
	}

}
