package ua.cc.cupsfacebook.database;

import android.graphics.drawable.Drawable;

/**
 * POJO class represents friend in user contacts list
 * 
 * @version 1.1 02-11-2013
 * @author Taras Melon
 */
public class Friend {
	/**
	 * Full name
	 */
	private String name;
	/**
	 * Facebook ID
	 */
	private String id;
	/**
	 * Avatar
	 */
	private Drawable drawable = null;

	/**
	 * Priority
	 */
	private int priority;

	public Friend(String name, String id) {
		this.name = name;
		this.id = id;
	}

	public Friend(String name, String id, int priority) {
		this.name = name;
		this.id = id;
		this.priority = priority;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

}
