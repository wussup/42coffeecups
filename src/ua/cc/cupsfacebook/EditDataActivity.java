package ua.cc.cupsfacebook;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ua.cc.cupsfacebook.database.MySQLiteOpenHelper;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class EditDataActivity extends Activity {

	private TextView name;
	private TextView surname;
	private TextView bio;
	private TextView dateOfBirth;
	private String oldStringName;
	private String oldStringSurname;
	private String oldStringBio;
	private String oldStringDateOfBirth;
	private int id;
	private Calendar myCalendar;
	private static final String TAG = "[CupsFacebook]";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_data);
		
		name = (TextView) findViewById(R.id.editTextName);
		surname = (TextView) findViewById(R.id.editTextSurname);
		bio = (TextView) findViewById(R.id.editTextBio);
		dateOfBirth = (TextView) findViewById(R.id.editTextDateOfBirth);
		
		Bundle extras = getIntent().getExtras();
		
		oldStringName = extras.getString("NAME");
		oldStringSurname = extras.getString("SURNAME");
		oldStringBio = extras.getString("BIO");
		oldStringDateOfBirth = extras.getString("DATE_OF_BIRTH");
		id = extras.getInt("ID");
		
		name.setText(oldStringName);
		surname.setText(oldStringSurname);
		bio.setText(oldStringBio);
		dateOfBirth.setText(oldStringDateOfBirth);
		
		myCalendar = Calendar.getInstance();

		final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

		    @Override
		    public void onDateSet(DatePicker view, int year, int monthOfYear,
		            int dayOfMonth) {
		        myCalendar.set(Calendar.YEAR, year);
		        myCalendar.set(Calendar.MONTH, monthOfYear);
		        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		        updateLabel();
		    }
		};

	   dateOfBirth.setOnClickListener(new View.OnClickListener() {

	        @Override
	        public void onClick(View v) {
	        	
	        	String[] stringDate = dateOfBirth.getText().toString().split("/");
	        	
	        	Log.i(TAG, Integer.valueOf(stringDate[2])+";"+Integer.valueOf(stringDate[0])+";"+Integer.valueOf(stringDate[1]));
	        	
	            new DatePickerDialog(EditDataActivity.this, date, Integer.valueOf(stringDate[2]),
	            		Integer.valueOf(stringDate[0])-1,
	            		Integer.valueOf(stringDate[1])).show();
	        }
	    });    
		
		findViewById(R.id.buttonSaveChanges).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String newStringName = name.getText().toString();
				String newStringSurname = surname.getText().toString();
				String newStringBio = bio.getText().toString();
				String newStringDateOfBirth = dateOfBirth.getText().toString();
				
				boolean success = false;
				
				MySQLiteOpenHelper helper = new MySQLiteOpenHelper(EditDataActivity.this, null, null, 1);
				
				String regxName = "^[\\p{L} .'-]+$";
				
				Pattern pattern = Pattern.compile(regxName,Pattern.CASE_INSENSITIVE);
			    Matcher matcher = pattern.matcher(newStringName + " " + newStringSurname);
				
			    if (matcher.find())
			    {
					if (newStringName.compareTo(oldStringName)!=0)
					{
						if (helper.updateName(id, newStringName))
						{
							oldStringName = newStringName;
						}
						success = true;
					}
					if (newStringSurname.compareTo(oldStringSurname)!=0)
					{
						if (helper.updateSurname(id, newStringSurname))
						{
							oldStringSurname = newStringSurname;
						}
						success = true;
					}
					if (newStringBio.compareTo(oldStringBio)!=0)
					{
						if (helper.updateBio(id, newStringBio))
						{
							oldStringBio = newStringBio;
						}
						success = true;
					}
					if (newStringDateOfBirth.compareTo(oldStringDateOfBirth)!=0)
					{
						if (helper.updateDateOfBirth(id, newStringDateOfBirth))
						{
							oldStringDateOfBirth = newStringDateOfBirth;
						}
						success = true;
					}
					
					if (success)
						Toast.makeText(EditDataActivity.this, "Your data has been successfully saved!", Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(EditDataActivity.this, "You did not change data!", Toast.LENGTH_SHORT).show();
			    }
			    else
			    {
			    	Toast.makeText(EditDataActivity.this, "You should write properly name and surname!", Toast.LENGTH_SHORT).show();
			    }
			}
		});
	}
	
	private void updateLabel() 
	{
	    String myFormat = "MM/dd/yyyy"; //In which you need put here
	    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
	
	    dateOfBirth.setText(sdf.format(myCalendar.getTime()));
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(EditDataActivity.this, MainActivity.class);
		startActivity(i);
		
		finish();
	}
	
}
