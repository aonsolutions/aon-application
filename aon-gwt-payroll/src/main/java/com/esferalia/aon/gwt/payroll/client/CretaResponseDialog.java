package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CretaResponseDialog extends SelectDialog<Employee> {

	// ------------------------------------------------------------------------

	public static class JsEvent extends JavaScriptObject {
		protected JsEvent() {
		}

		// ----------------------------------- JSNI (Native JavaScript Methods)

		public final native String getMessage() /*-{
			return this.message;
		}-*/;
	}

	public static class JsBasesResult extends JavaScriptObject {

		protected JsBasesResult() {
		}

		public final String getBasesFile() {
			String bases = getFullBases();
			return URL.decodeQueryString(bases);

		}

		public final String getChangedBasesFile() {
			String bases = getDiffBases();
			return URL.decodeQueryString(bases);

		}

		// ----------------------------------- JSNI (Native JavaScript Methods)

		public final native String getFullBases() /*-{
			return this.full_bases;
		}-*/;

		public final native String getDiffBases() /*-{
			return this.diff_bases;
		}-*/;

		public final native JsEvent[] getErrors() /*-{
			return this.errors;
		}-*/;

		public final native JsEvent[] getWarnings() /*-{
			return this.warnings;
		}-*/;

	}

	public static interface Handler {
		void onBases(JsBasesResult result);
	}

	interface Binder extends UiBinder<Widget, CretaResponseDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	InlineLabel fileLabel;
	@UiField
	Button fileButton;

	@UiField
	FormPanel formPanel;
	@UiField
	CheckBox basesCheck;
	@UiField
	FileUpload fileUpload;

	private Handler handler;

	public CretaResponseDialog(CretaService.File file, Handler handler) {
		super();
		exportSubmitComplete();
		this.handler = handler;
		setWidget(binder.createAndBindUi(this));
		formPanel.setAction(CretaService.CRETA_URL + "/" + file.name());
		setCaption("Sistema de Liquidaci\u00F3n Directa (Proyecto Cret@)");
	}

	// ------------------------------------------------------------------------

	@UiHandler("fileButton")
	void onFileClicked(ClickEvent e) {
		fileUpload.click();
	}

	@Override
	@UiHandler("acceptButton")
	void onAcceptClick(ClickEvent e) {
		formPanel.submit();
	}

	@UiHandler("fileUpload")
	void onFileUploadChange(ChangeEvent event) {
		acceptButton.setEnabled(true);

		String fileName = fileUpload.getFilename();
		fileLabel.setTitle(fileName);
		fileLabel.setText(fileName.substring(
				Math.max(0, fileName.length() - 25), fileName.length()));
	}

	// ------------------------------------------------------------------------

	public void onResultComplete() {
	}

	public void onBases(JsBasesResult object) {
		handler.onBases(object);
	}

	// ------------------------------------------------------------------------

	private native void exportSubmitComplete() /*-{
		var that = this;
		$wnd.__onBases = $entry(function(result) {
			that.@com.esferalia.aon.gwt.payroll.client.CretaResponseDialog::onBases(Lcom/esferalia/aon/gwt/payroll/client/CretaResponseDialog$JsBasesResult;)(result);
		});
	}-*/;

}
