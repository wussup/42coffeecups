package ua.cc.cupsfacebook.ui;

import java.util.ArrayList;

import ua.cc.cupsfacebook.MainActivity;
import ua.cc.cupsfacebook.R;
import ua.cc.cupsfacebook.database.Data;
import ua.cc.cupsfacebook.database.MySQLiteOpenHelper;
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
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;

public class LoginActivity extends FragmentActivity  {

	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private ProgressDialog dialog;
	private final static String TAG = "[CupsFacebook]";
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_empty);
		
		if (!isNetworkAvailable())
		{
			AlertDialog alertDialog = new AlertDialog.Builder(
                    LoginActivity.this).create();
 
            // Setting Dialog Title
            alertDialog.setTitle("No Connection!");
 
            // Setting Dialog Message
            alertDialog.setMessage("You should find Internet connection!");
 
            alertDialog.setCancelable(false);
            
            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
 
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog
                    // closed
                	finish();
                }
            });
 
            // Showing Alert Message
            alertDialog.show();
		}
		
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		
		Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
        else
        {
        	runIfOpened(session);
        }
        
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickLogin();
			}
		});
	}

	private void runIfOpened(Session session) {
		if (session.isOpened()) {
			
			if (isNetworkAvailable())
			{
		    	if (!checkDataBase())
		    	{
		    		dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
		    		makeMeRequest(session);
		    	}
		    	else
		    	{
		    		startMainActivity();
		    	}
			}
		}
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private void makeMeRequest(final Session session) {
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	                if (user != null) {
	                    // Set the id for the ProfilePictureView
	                    // view that in turn displays the profile picture.
	                	
	                    // Set the Textview's text to the user's name.
	                	
	                	addDataToDatabase(user);
	                	
	                	dialog.dismiss();
	                	
	                	startMainActivity();
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
	}
	
	private void addDataToDatabase(GraphUser user) {
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, null, null, 1);
		
		final ArrayList<String> list = new ArrayList<String>();
        for (int i = 1; i <= 10; ++i) {
          list.add("Contact"+i);
        }
		
        Data data = new Data(user.getFirstName(), user.getLastName(), "Homepage: " + user.getLink(), user.getBirthday(), user.getId(), list);
        
        helper.addData(data);
	}
	
	@Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
     
    private void updateView() {
        Session session = Session.getActiveSession();
        runIfOpened(session);
    }

	private void startMainActivity() {
		Intent i = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(i);
		finish();
	}
    
    private boolean checkDataBase() {
	    SQLiteDatabase checkDB = null;
	    try {
	        checkDB = SQLiteDatabase.openDatabase(MainActivity.DB_FULL_PATH, null,
	                SQLiteDatabase.OPEN_READONLY);
	        checkDB.close();
	        Log.i(TAG, "Database exists");
	    } catch (SQLiteException e) {
	    	Log.i(TAG, "Database doesn't exist yet");
	    }
	    return checkDB != null ? true : false;
	}
    
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            updateView();
        }
    }
    
    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }
}
