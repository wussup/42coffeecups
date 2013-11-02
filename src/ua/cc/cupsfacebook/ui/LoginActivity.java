package ua.cc.cupsfacebook.ui;

import java.util.ArrayList;
import java.util.List;

import ua.cc.cupsfacebook.MainActivity;
import ua.cc.cupsfacebook.R;
import ua.cc.cupsfacebook.database.Data;
import ua.cc.cupsfacebook.database.MySQLiteOpenHelper;
import ua.cc.cupsfacebook.util.Global;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;

/**
 * Activity represents functionality for log in Facebook account via Facebook
 * app or web-browser
 * 
 * @version 1.3 28-10-2013
 * @author Taras Melon
 */
public class LoginActivity extends FragmentActivity {

	private Session.StatusCallback mStatusCallback = new SessionStatusCallback();
	private ProgressDialog mDialog;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_empty);

		if (!isNetworkAvailable()) {
			createAndShowAlertDialog();
		}

		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		sessionOpen(savedInstanceState);

		findViewById(R.id.loginButton).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						onClickLogin();
					}
				});
	}

	/**
	 * Trying to get active session. If success, then starts MainActivity, else
	 * open session and go through all functions needed for start MainActivity
	 * 
	 * @param savedInstanceState
	 *            bundle from onCreate method
	 */
	private void sessionOpen(Bundle savedInstanceState) {
		Session session = Session.getActiveSession();

		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, mStatusCallback,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				ArrayList<String> permissions = new ArrayList<String>();
				permissions.add("user_birthday");
				permissions.add("user_friends");

				session.openForRead(new Session.OpenRequest(this).setCallback(
						mStatusCallback).setPermissions(permissions));
			}
		} else {
			runIfOpened(session);
		}
	}

	/**
	 * Adding data about me to database
	 */
	private void addDataToDatabase() {
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, null, null,
				Global.DATABASE_VERSION);

		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 1; i <= 10; ++i) {
			list.add("Contact" + i);
		}

		Data data = new Data("Taras", "Melon", "Was born in...", "02/05/1992",
				"1", list);

		helper.addData(data);
	}

	/**
	 * Creating and showing AlertDialog, if network connection is unaccessible
	 */
	@SuppressWarnings("deprecation")
	private void createAndShowAlertDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
				.create();

		alertDialog.setTitle("No Connection!");

		alertDialog.setMessage("You should find Internet connection!");

		alertDialog.setCancelable(false);

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		alertDialog.show();
	}

	/**
	 * If session is opened and network connection is available, method checking
	 * database for errors and in good case starts the MainActivity, but in bad
	 * case - fix errors
	 * 
	 * @param session
	 *            current session info
	 */
	private void runIfOpened(Session session) {
		if (session.isOpened()) {

			if (isNetworkAvailable()) {
				Boolean b = checkDataBase();
				if ((b == null) || (!b)) {
					mDialog = ProgressDialog.show(this, "Loading",
							"Please wait...", true);
					makeMeRequest(session);
				} else {
					startMainActivity();
				}
			}
		}
	}

	/**
	 * Checking if network connection is available
	 * 
	 * @return true - if available, false - in other case
	 */
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/**
	 * Making request for user data and friends list from Facebook account,
	 * adding fetched data to database and starting MainActivity
	 * 
	 * @param session
	 *            current session info
	 */
	private void makeMeRequest(final Session session) {
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(final GraphUser user,
							Response response) {
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (user != null) {
								Request friendsRequest = Request
										.newMyFriendsRequest(session,
												new MyGraphUserListCallback(
														session, user));
								Bundle params = new Bundle();
								params.putString("fields", "id, name");
								friendsRequest.setParameters(params);

								friendsRequest.executeAsync();
							}
						}
						if (response.getError() != null) {
							Log.e(Global.TAG, response.getError()
									.getErrorMessage());
						}
					}
				});
		request.executeAsync();
	}

	/**
	 * Adding user data to database
	 * 
	 * @param user
	 *            current Facebook user
	 * @param friends
	 *            user friends list
	 */
	private void addDataToDatabase(GraphUser user, List<GraphUser> friends) {
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, null, null,
				Global.DATABASE_VERSION);

		final ArrayList<String> list = new ArrayList<String>();
		for (GraphUser friend : friends) {
			Log.i(Global.TAG, friend.toString());

			list.add(friend.getProperty("name").toString() + ";"
					+ friend.getProperty("id") + ";0");
		}

		Data data = new Data(user.getFirstName(), user.getLastName(),
				"Homepage: " + user.getLink(), user.getBirthday(),
				user.getId(), list);

		helper.addData(data);
	}

	@Override
	public void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(mStatusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(mStatusCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	/**
	 * Starting runIfOpened method with session object
	 */
	private void updateView() {
		Session session = Session.getActiveSession();
		runIfOpened(session);
	}

	/**
	 * Starting MainActivity
	 */
	private void startMainActivity() {
		Intent i = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(i);
		finish();
	}

	/**
	 * Checking if database exists. Method tries to open database by path.
	 * 
	 * @return Boolean value is returned: null - database doesn't exist, false -
	 *         database is in another version, true - all good
	 */
	private Boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		Boolean b = null;
		try {
			checkDB = SQLiteDatabase.openDatabase(Global.DB_FULL_PATH, null,
					SQLiteDatabase.OPEN_READONLY);

			if (checkDB.getVersion() == Global.DATABASE_VERSION)
				b = true;
			else
				b = false;

			checkDB.close();

			Log.i(Global.TAG, "Database exists");
		} catch (SQLiteException e) {
			Log.i(Global.TAG, "Database doesn't exist yet");
		}
		return b;
	}

	/**
	 * Opening session onClick Login button
	 */
	private void onClickLogin() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			ArrayList<String> permissions = new ArrayList<String>();
			permissions.add("user_birthday");
			permissions.add("user_friends");

			session.openForRead(new Session.OpenRequest(this).setCallback(
					mStatusCallback).setPermissions(permissions));
		} else {
			Session.openActiveSession(this, true, mStatusCallback);
		}
	}

	/**
	 * Class represents callback from Facebook API
	 * 
	 * @version 1.1 28-10-2013
	 * @author Taras Melon
	 */
	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			updateView();
		}
	}

	/**
	 * Callback with user friends list
	 * 
	 * @version 1.0 02-11-2013
	 * @author Taras melon
	 */
	private class MyGraphUserListCallback implements GraphUserListCallback {

		private Session session;
		private GraphUser user;

		public MyGraphUserListCallback(Session session, GraphUser user) {
			this.session = session;
			this.user = user;
		}

		@Override
		public void onCompleted(List<GraphUser> users, Response response) {
			if (session == Session.getActiveSession()) {
				addDataToDatabase(user, users);

				addDataToDatabase();

				if (isNetworkAvailable())
					startMainActivity();
			}
			if (response.getError() != null) {
				Log.e(Global.TAG, response.getError().getErrorMessage());
			}
			mDialog.dismiss();
		}

	}
}
