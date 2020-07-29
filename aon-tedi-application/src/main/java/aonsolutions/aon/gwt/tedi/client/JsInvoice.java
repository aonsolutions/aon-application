package aonsolutions.aon.gwt.tedi.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JsInvoice extends JavaScriptObject{
	
	protected JsInvoice() {
	}
	// ----------------------------------- JSNI (Native JavaScript Methods)
	
	public final native String getType() /*-{
		return this.type;
	}-*/; 

	public final native String getUuid() /*-{
		return this.uuid;
	}-*/; 

	public final native Integer getNumber() /*-{
		return this.number;
	}-*/; 

	public final native String getCompany() /*-{
		return this.company;
	}-*/; 

	public final native String getSeries() /*-{
		return this.series;
	}-*/; 
	
	public final native String getReference() /*-{
		return this.reference;
	}-*/; 

	public final native Double getTotal() /*-{
		return this.total;
	}-*/; 
	
	public final native JsRegistry getSender() /*-{
		return this.sender;
	}-*/; 
	
	public final native JsRegistry getReceiver() /*-{
		return this.receiver;
	}-*/; 

	public final native JsArray<JsInvoiceTax> getTaxes() /*-{
		return this.taxes;
	}-*/; 
	
	public final native String getCategory() /*-{
		return this.category;
	}-*/; 

	public final native String getStatus() /*-{
		return this.oldStatus;
	}-*/; 
	
	public final native String getDate() /*-{
	return this.oldStatus;
}-*/; 


}