package com.esferalia.aon.gwt.dump.shared;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.google.gwt.core.client.JavaScriptObject;

public final class JsEmployee extends JavaScriptObject {

	public static JsEmployee create() {
		return JavaScriptObject.createObject().cast();
	}

	protected JsEmployee() {
	}

	public native String getName() /*-{
		return this.name;
	}-*/;

	public native String getFirstName() /*-{
		return this.firstName;
	}-*/;

	public native String getSecondName() /*-{
		return this.secondName;
	}-*/;

	public String getFullName() {
		StringBuffer fullName = new StringBuffer();
		if ( !StringUtils.isBlank(getFirstName()) )
			fullName.append(getFirstName());
		
		if ( !StringUtils.isBlank(getSecondName()) ) {
			if ( fullName.length() > 0 )
				fullName.append(" ");
			fullName.append(getSecondName());
		}

		if ( !StringUtils.isBlank(getName()) ) {
			if ( fullName.length() > 0 )
				fullName.append(", ");
			fullName.append(getName());
		}

		return fullName.toString();
	};

}
