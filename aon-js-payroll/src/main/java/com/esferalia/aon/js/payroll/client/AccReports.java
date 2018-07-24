package com.esferalia.aon.js.payroll.client;

import com.google.gwt.core.client.JavaScriptObject;

public class AccReports extends Reports {

	public static native void journal( JavaScriptObject metadata, JavaScriptObject entries, Callback callback) /*-{
		var stream = payroll.blobStream();
		payroll.accReports.journal(metadata, entries, stream);
		stream.on('finish', function() {
			callback.@com.esferalia.aon.js.payroll.client.Reports.Callback::onSuccess(Ljava/lang/String;)(this.toBlobURL('application/pdf'));
		});
	}-*/;

}
