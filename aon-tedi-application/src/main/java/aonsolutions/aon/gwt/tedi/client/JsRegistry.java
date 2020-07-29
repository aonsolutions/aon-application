package aonsolutions.aon.gwt.tedi.client;

import com.google.gwt.core.client.JavaScriptObject;

public class JsRegistry extends JavaScriptObject{
	
	protected JsRegistry() {
	}

	public final native String getName() /*-{
		return this.name;
	}-*/; 

	public final native String getDocument() /*-{
		return this.document;
	}-*/; 

	public final native String getDocumentCountry() /*-{
		return this.document_country;
	}-*/; 


}