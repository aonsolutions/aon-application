package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.JavaScriptObject;

public class PersonDocumentResult extends JavaScriptObject {

	protected PersonDocumentResult() {
		
	}
	
	 public final native String getDocument() /*-{
     return this.document;
 }-*/;
 
 public final native String getNationality() /*-{
     return this.nationality;
 }-*/;

 public final native String getName() /*-{
     return this.name;
 }-*/;

 public final native String getFirstSurname() /*-{
     return this.firstSurname;
 }-*/;

 public final native String getSecondSurname() /*-{
     return this.secondSurname;
 }-*/;
}
