package com.esferalia.aon.gwt.api.client.finance;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsInvoiceDetail extends JavaScriptObject {

	public static final ProvidesKey<JsInvoiceDetail> PROVIDES_KEY = new ProvidesKey<JsInvoiceDetail>() {
		@Override
		public Object getKey(JsInvoiceDetail js) {
			return js == null ? null : js.getId();
		}
	};
	
	protected JsInvoiceDetail() {
	}

	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;
	
	public final native JsInvoice getInvoice() /*-{
		return this.invoice;
	}-*/;
	
	public final native Double getQuantity() /*-{
		return this.quantity;
	}-*/;
	
}
