package com.esferalia.aon.gwt.dump.shared;

import com.google.gwt.core.client.JavaScriptObject;

public final class JsEmpres extends JavaScriptObject {

	public static JsEmpres create() {
		return JavaScriptObject.createObject().cast();
	}

	protected JsEmpres() {
	}

	public native String getDB() /*-{
		return this.db;
	}-*/;

	public native JsEmpres setDB(String db) /*-{
		this.db = db;
		return this;
	}-*/;

	public native String getSScod() /*-{
		return this.sscod;
	}-*/;

	public native JsEmpres setSScod(String sscod) /*-{
		this.sscod = sscod;
		return this;
	}-*/;

	public native String getSSnum() /*-{
		return this.ssnum;
	}-*/;

	public native JsEmpres setSSnum(String ssnum) /*-{
		this.sscod = ssnum;
		return this;
	}-*/;

	public native String getNif() /*-{
		return this.nif;
	}-*/;

	public native JsEmpres setNif(String nif) /*-{
		this.nif = nif;
		return this;
	}-*/;

	public native String getRSocial() /*-{
		return this.rsocial;
	}-*/;

	public native JsEmpres setRSocial(String rsocial) /*-{
		this.rsocial = rsocial;
		return this;
	}-*/;
	
	/**
	 *  MINE
	 */
	
	public native JsEmpres setFirstSalary(String fnomina) /*-{
		this.fnomina = fnomina;
		return this;
	}-*/;

	
	public native String getFirstSalary() /*-{
		return this.fnomina;
	}-*/;
	
	public native JsEmpres setLastSalary(String lnomina) /*-{
		this.lnomina = lnomina;
		return this;
	}-*/;

	public native String getLastSalary() /*-{
		return this.lnomina;
	}-*/;

}
