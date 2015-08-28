package com.esferalia.aon.gwt.office.client.models.users;

import com.google.gwt.core.client.JavaScriptObject;

public class User extends JavaScriptObject {
	
	protected User() {}
	
	public final native String getLogin() /*-{ 
		return this.login; 
	}-*/;
	
	public final native String getGravatarId() /*-{ 
		return this.gravatar_id; 
	}-*/;
	
	public final native String getUrl() /*-{ 
		return this.url; 
	}-*/;
	
	public final native String getAvatarUrl() /*-{ 
		return this.avatar_url; 
	}-*/;
	
	public final native String getId() /*-{ 
		return this.id; 
	}-*/;



}
