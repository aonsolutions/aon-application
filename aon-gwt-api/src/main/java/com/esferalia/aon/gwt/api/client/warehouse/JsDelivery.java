package com.esferalia.aon.gwt.api.client.warehouse;

import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsDelivery extends JavaScriptObject {

	protected JsDelivery() {
	}

	public static final ProvidesKey<JsDelivery> PROVIDES_KEY = new ProvidesKey<JsDelivery>() {
		@Override
		public Object getKey(JsDelivery js) {
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

	public final native JsObject getRegistry() /*-{
												return this.registry;
												}-*/;

	public final native String getIssueDate() /*-{
												return this.issue_date;
												}-*/;

	

}
