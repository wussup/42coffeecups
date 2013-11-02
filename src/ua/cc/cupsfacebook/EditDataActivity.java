package ua.cc.cupsfacebook;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ua.cc.cupsfacebook.database.MySQLiteOpenHelper;
import ua.cc.cupsfacebook.util.Global;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity for editing and saving user data in database
 * 
 * @version 1.0 01-11-2013
 * @author Taras Melon
 */
public class EditDataActivity extends Activity {

	private TextView mName;
	private TextView mSurname;
	private TextView mBio;
	private TextView mDateOfBirth;
	private String mOldStringName;
	private String mOldStringSurname;
	private String mOldStringBio;
	private String mOldStringDateOfBirth;
	private int mId;
	private Calendar mMyCalendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_data);

		setUpFields();

		setUpOldValues();

		final DatePickerDialog.OnDateSetListener date = setUpCalendar();

		findViewById(R.id.buttonSaveChanges).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						checkForChanges();
					}
				});

		mName.requestFocus();

		mDateOfBirth.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					onClickAndFocusMethod(date);
				}
			}
		});
	}

	/**
	 * Checking for changes in TextViews and insert new data to database
	 */
	private void checkForChanges() {
		String newStringName = mName.getText().toString();
		String newStringSurname = mSurname.getText().toString();
		String newStringBio = mBio.getText().toString();
		String newStringDateOfBirth = mDateOfBirth.getText().toString();

		boolean success = false;

		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(
				EditDataActivity.this, null, null, Global.DATABASE_VERSION);

		String regxName = "^[\\p{L} .'-]+$";

		Pattern pattern = Pattern.compile(regxName, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(newStringName + " "
				+ newStringSurname);

		if (matcher.find()) {
			if (newStringName.compareTo(mOldStringName) != 0) {
				if (helper.updateName(mId, newStringName)) {
					mOldStringName = newStringName;
				}
				success = true;
			}
			if (newStringSurname.compareTo(mOldStringSurname) != 0) {
				if (helper.updateSurname(mId, newStringSurname)) {
					mOldStringSurname = newStringSurname;
				}
				success = true;
			}
			if (newStringBio.compareTo(mOldStringBio) != 0) {
				if (helper.updateBio(mId, newStringBio)) {
					mOldStringBio = newStringBio;
				}
				success = true;
			}
			if (newStringDateOfBirth.compareTo(mOldStringDateOfBirth) != 0) {
				if (helper.updateDateOfBirth(mId, newStringDateOfBirth)) {
					mOldStringDateOfBirth = newStringDateOfBirth;
				}
				success = true;
			}

			if (success)
				Toast.makeText(EditDataActivity.this,
						"Your data has been successfully saved!",
						Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(EditDataActivity.this,
						"You did not change data!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(EditDataActivity.this,
					"You should write properly name and surname!",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Setting up Calendar DatePicker
	 * 
	 * @return date picker
	 */
	private DatePickerDialog.OnDateSetListener setUpCalendar() {
		mMyCalendar = Calendar.getInstance();

		mDateOfBirth.setText(mOldStringDateOfBirth);

		final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				mMyCalendar.set(Calendar.YEAR, year);
				mMyCalendar.set(Calendar.MONTH, monthOfYear);
				mMyCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateLabel();
			}
		};

		mDateOfBirth.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickAndFocusMethod(date);
			}

		});
		return date;
	}

	/**
	 * Setting up old values, fetched from extras
	 */
	private void setUpOldValues() {
		Bundle extras = getIntent().getExtras();

		mOldStringName = extras.getString("NAME");
		mOldStringSurname = extras.getString("SURNAME");
		mOldStringBio = extras.getString("BIO");
		mOldStringDateOfBirth = extras.getString("DATE_OF_BIRTH");
		if (mOldStringDateOfBirth == null)
			mOldStringDateOfBirth = "";
		mId = extras.getInt("ID");

		mName.setText(mOldStringName);
		mSurname.setText(mOldStringSurname);
		mBio.setText(mOldStringBio);
	}

	/**
	 * Setting up fields
	 */
	private void setUpFields() {
		mName = (TextView) findViewById(R.id.editTextName);
		mSurname = (TextView) findViewById(R.id.editTextSurname);
		mBio = (TextView) findViewById(R.id.editTextBio);
		mDateOfBirth = (TextView) findViewById(R.id.editTextDateOfBirth);
	}

	/**
	 * Starting DatePickerDialog on click or on focus
	 * 
	 * @param date
	 *            datePicker object
	 */
	private void onClickAndFocusMethod(
			final DatePickerDialog.OnDateSetListener date) {
		String dateString = mDateOfBirth.getText().toString();
		if (dateString.compareTo("") != 0) {
			String[] stringDate = dateString.split("/");

			new DatePickerDialog(EditDataActivity.this, date,
					Integer.valueOf(stringDate[2]),
					Integer.valueOf(stringDate[0]) - 1,
					Integer.valueOf(stringDate[1])).show();
		} else {
			new DatePickerDialog(EditDataActivity.this, date,
					mMyCalendar.get(Calendar.YEAR),
					mMyCalendar.get(Calendar.MONTH),
					mMyCalendar.get(Calendar.DAY_OF_MONTH)).show();
		}
	}

	/**
	 * Setting new date of birth after change
	 */
	private void updateLabel() {
		String myFormat = "MM/dd/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		mDateOfBirth.setText(sdf.format(mMyCalendar.getTime()));
	}

	/*
	 * Start Main Activity and finish current activity
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		Intent i = new Intent(EditDataActivity.this, MainActivity.class);
		startActivity(i);

		finish();
	}

}
