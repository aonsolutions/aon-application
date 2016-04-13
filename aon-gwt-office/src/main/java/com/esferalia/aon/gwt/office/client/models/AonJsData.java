package com.esferalia.aon.gwt.office.client.models;

import com.esferalia.aon.gwt.office.client.models.repos.JsRMedia;
import com.esferalia.aon.gwt.office.client.models.repos.JsRegistry;
import com.esferalia.aon.gwt.office.client.models.users.JsUser;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class AonJsData extends JavaScriptObject {

	protected AonJsData(){}
	
	public final native boolean isNotFound() /*-{ return this.message == "Not Found"; }-*/;
	
	public final native JsUser getCurrentUser() /*-{ return this.user; }-*/;
	
	public final native JsArray<JsUser> getUsers() /*-{ return this.users; }-*/;
	
	public final native JsArray<JsRegistry> getRegistries() /*-{ return this.registries; }-*/;
	
	public final native JsArray<JsRMedia> getRMedias() /*-{ return this.rmedias; }-*/;

}
