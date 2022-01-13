package com.esferalia.aon.gwt.api.client.documental;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsCertificate extends JavaScriptObject{

	public static JavaScriptObject create() {
		return JavaScriptObject.createObject().cast();
	}

	protected JsCertificate() {
		
	}

	public static final ProvidesKey<JsCertificate> PROVIDES_KEY = new ProvidesKey<JsCertificate>() {
		@Override
		public Object getKey(JsCertificate certificate) {
			return certificate == null ? null : certificate.getId();
		}
	};
	
	public final native int getId() /*-{
		return this.id;
	}-*/;
	
	public final native int getDomain() /*-{
		return this.domain;
	}-*/;
	
	public final native String getName() /*-{
		return this.name;
	}-*/;
	
	public final native boolean hasPassword() /*-{
		return this.password;
	}-*/;
	
	public final native Boolean isConfidential() /*-{
		return this.confidential;
	}-*/;
}
