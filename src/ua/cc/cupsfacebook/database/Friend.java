package ua.cc.cupsfacebook.database;

import android.graphics.drawable.Drawable;

public class Friend
{
	private String name;
	private int priority;
	private String id;
	private Drawable drawable = null;
	
	public Friend(String name, int priority, String id) {
		this.name = name;
		this.priority = priority;
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public int getPriority() {
		return priority;
	}
	public String getId() {
		return id;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public Drawable getDrawable() {
		return drawable;
	}
	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}
}
