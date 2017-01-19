package com.esferalia.aon.gwt.api.client.warehouse;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsCarrierPacking extends JavaScriptObject {

	protected JsCarrierPacking() {}	public static final ProvidesKey<JsCarrierPacking> PROVIDES_KEY = new ProvidesKey<JsCarrierPacking>() {
		@Override
		public Object getKey(JsCarrierPacking carrierPacking) {
			return carrierPacking == null ? null : carrierPacking.getId();
		}
	};
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;
	
	public final native String getType() /*-{
		return this.type;
	}-*/;
	
	public final native String getDate() /*-{
		return this.date;
	}-*/;

}
