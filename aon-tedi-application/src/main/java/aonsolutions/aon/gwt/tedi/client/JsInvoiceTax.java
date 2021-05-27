package aonsolutions.aon.gwt.tedi.client;

import com.google.gwt.core.client.JavaScriptObject;

public class JsInvoiceTax extends JavaScriptObject{
	
	protected JsInvoiceTax() {
	}

	public final native String getType() /*-{
		return this.type;
	}-*/; 

	public final native Double getBase() /*-{
		return this.base;
	}-*/; 

	public final native Double getPercentage() /*-{
		return this.percentage;
	}-*/; 

	public final native Double getQuota() /*-{
		return this.quota;
	}-*/; 

	public final native Double getSurcharge() /*-{
		return this.surcharge;
	}-*/; 
	
	public final native Double getSurchargeQuota() /*-{
		return this.surcharge_quota;
	}-*/; 

}