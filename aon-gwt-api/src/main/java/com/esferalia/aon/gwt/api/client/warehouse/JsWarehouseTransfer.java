package com.esferalia.aon.gwt.api.client.warehouse;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsWarehouseTransfer extends JavaScriptObject {

	protected JsWarehouseTransfer() {
	}

	public static final ProvidesKey<JsWarehouseTransfer> PROVIDES_KEY = new ProvidesKey<JsWarehouseTransfer>() {
		@Override
		public Object getKey(JsWarehouseTransfer js) {
			return js == null ? null : js.getId();
		}
	};

	public final native Integer getId() /*-{
										return this.id;
										}-*/;

	public final native Integer getDomain() /*-{
											return this.domain;
											}-*/;

	public final native String getSeries() /*-{
											return this.series;
											}-*/;

	public final native Integer getNumber() /*-{
											return this.number;
											}-*/;

	public final native String getIssueTime() /*-{
												return this.issue_time;
												}-*/;

}