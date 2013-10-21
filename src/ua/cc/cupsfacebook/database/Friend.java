package ua.cc.cupsfacebook.database;

import android.graphics.drawable.Drawable;

public class Friend
{
        private String name;
        private String id;
        private Drawable drawable = null;
        
        public Friend(String name, String id) {
                this.name = name;
                this.id = id;
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
}
