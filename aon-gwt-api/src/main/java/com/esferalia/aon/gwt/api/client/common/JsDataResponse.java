package com.esferalia.aon.gwt.api.client.common;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsDataResponse extends JavaScriptObject {

	protected JsDataResponse() {}
	
	public static final ProvidesKey<JsDataResponse> PROVIDES_KEY = new ProvidesKey<JsDataResponse>() {
		@Override
		public Object getKey(JsDataResponse dataResponse) {
			return dataResponse == null ? null : dataResponse.getId();
		}
	};
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;
	
	public final native String getCode() /*-{
		return this.code;
	}-*/;
	
	public final native String getDate() /*-{
		return this.date;
	}-*/;
	
	public final native JsObject getSource() /*-{
		return this.source;
	}-*/;
	
	public final native Integer getSourceId() /*-{
		return this.source_id;	
	}-*/;
	
	public final native AonJsArray<JsDataResponseDetail> getDetail() /*-{
		return this.detail;
	}-*/;
	
	public final native String getCreationUser() /*-{
		return this.creation_user;
	}-*/;

	public final native String getCreationDate() /*-{
		return this.creation_date;
	}-*/;

	public final native String getModificationUser() /*-{
		return this.modification_user;
	}-*/;

	public final native String getModificationDate() /*-{
		return this.modification_date;
	}-*/;
	
	
	@Deprecated
	public final native String getNumber() /*-{
		return this.number;
	}-*/;

	@Deprecated
	public final native String getIssueDate() /*-{
		return this.issue_date;
	}-*/;

	@Deprecated
	public final native String getProduct() /*-{
		return this.product;
	}-*/;
	
	@Deprecated
	public final native JsObject getSupplier() /*-{
		return this.supplier;
	}-*/;

}
