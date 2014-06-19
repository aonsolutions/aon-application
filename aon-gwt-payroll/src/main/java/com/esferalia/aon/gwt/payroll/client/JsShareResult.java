package com.esferalia.aon.gwt.payroll.client;

public class JsShareResult extends JsSalaryResult {

	protected JsShareResult() {
	}

	public final native int getId() /*-{
		return this.id;
	}-*/;

	public final native int getSize() /*-{
		return this.size;
	}-*/;

	public final native String getError() /*-{
		return this.error;
	}-*/;

	public final native String getDescription() /*-{
	return this.description;
}-*/;
}
