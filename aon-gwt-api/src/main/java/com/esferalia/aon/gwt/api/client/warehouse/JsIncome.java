package com.esferalia.aon.gwt.api.client.warehouse;

import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsIncome extends JavaScriptObject {

	protected JsIncome() {
	}

	public static final ProvidesKey<JsIncome> PROVIDES_KEY = new ProvidesKey<JsIncome>() {
		@Override
		public Object getKey(JsIncome js) {
			return js == null ? null : js.getId();
		}
	};

	public final native Integer getId() /*-{
										return this.id;
										}-*/;

	public final native Integer getDomain() /*-{
											return this.domain;
											}-*/;

	public final native String getReferenceCode() /*-{
												return this.reference_code;
												}-*/;

	public final native JsObject getRegistry() /*-{
												return this.registry;
												}-*/;

	public final native String getIssueDate() /*-{
												return this.issue_date;
												}-*/;
	
}
