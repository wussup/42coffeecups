package ua.cc.cupsfacebook.database;

public class Data {
	private int id;
	private String name;
	private String surname;
	private String bio;
	private String dateOfBirth;
	private String userId;

	public Data() {
	}

	public Data(int id, String name, String surname, String bio,
			String dateOfBirth, String userId) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.bio = bio;
		this.dateOfBirth = dateOfBirth;
		this.userId = userId;
	}

	public Data(String name, String surname, String bio, String dateOfBirth, String userId) {
		this.name = name;
		this.surname = surname;
		this.bio = bio;
		this.dateOfBirth = dateOfBirth;
		this.userId = userId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String toString()
	{
		return id+";"+bio+";"+dateOfBirth+";"+name+";"+surname+";"+userId;
	}
}
