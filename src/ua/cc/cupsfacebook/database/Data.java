package ua.cc.cupsfacebook.database;

import java.util.ArrayList;

/**
 * Simple POJO class represents information about user (name, surname,
 * biography, date of birth and contacts)
 * 
 * @version 1.0 28-10-2013
 * @author Taras Melon
 */
public class Data {
	private int mId;
	private String mName;
	private String mSurname;
	private String mBio;
	private String mDateOfBirth;
	private ArrayList<String> mContacts;

	public Data() {
	}

	public Data(int id, String name, String surname, String bio,
			String dateOfBirth, ArrayList<String> contacts) {
		this.mId = id;
		this.mName = name;
		this.mSurname = surname;
		this.mBio = bio;
		this.mDateOfBirth = dateOfBirth;
		this.mContacts = contacts;
	}

	public Data(String name, String surname, String bio, String dateOfBirth,
			ArrayList<String> contacts) {
		this.mName = name;
		this.mSurname = surname;
		this.mBio = bio;
		this.mDateOfBirth = dateOfBirth;
		this.mContacts = contacts;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getSurname() {
		return mSurname;
	}

	public void setSurname(String surname) {
		this.mSurname = surname;
	}

	public String getBio() {
		return mBio;
	}

	public void setBio(String bio) {
		this.mBio = bio;
	}

	public String getDateOfBirth() {
		return mDateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.mDateOfBirth = dateOfBirth;
	}

	public ArrayList<String> getContacts() {
		return mContacts;
	}

	public void setContacts(ArrayList<String> contacts) {
		this.mContacts = contacts;
	}

}
