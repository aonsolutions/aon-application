package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class ModificationPDFDialog extends AonCustomDialog {
	
	// ------------------------------------------------- UIBinder
	
	interface ModificationPDFDialogUIBinder extends UiBinder<Widget, ModificationPDFDialog> {}

	private static final ModificationPDFDialogUIBinder binder = GWT.create(ModificationPDFDialogUIBinder.class);
	
	// ------------------------------------------------- UIFileds
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String footerButton();
	}
	
	@UiField
	HTMLPanel formPanel;
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	TextBox titleTB;
	
	@UiField
	TextArea informationTA;
	
	@UiField
	AonDateBox dateBox;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	// Forms
	FormPanel form;
	FileUpload fileUpload;
	Hidden userLoginHidden = new Hidden("currentUser", Wnd.getCurrentUser());
	Hidden currentDomainHidden = new Hidden("currentDomain", Wnd.getCurrentDomainNameURL());
	Hidden contractHidden = new Hidden("contractId", "");
	
	Hidden titleHidden = new Hidden("title", "Notificaci\u00f3n Laboral");
	Hidden informationHidden = new Hidden("information", "");
	Hidden dateHidden = new Hidden("date", formatDate.format(new Date()));
	
	// Button
	Button exportPDF;
	Button generatePDF;
	
	// ------------------------------------------------- Constructor
	
	protected ModificationPDFDialog(Integer contractId) {
		
		setCaption("Notificaci\u00f3n laboral");
		setWidget(binder.createAndBindUi(this));
		
		showCloseButton(true);
		
		contractHidden.setValue(contractId.toString());
		
		hideMessage();
		initializeForm();
		initElementHandlers();
		getButtonsPanel();
		showDialog();
	}
	
	// ------------------------------------------------- Constructor methods

	private void initElementHandlers() {
		titleTB.getElement().setPropertyString("placeholder", "Notificaci\u00f3n Laboral");
		titleTB.addValueChangeHandler(e -> titleHidden.setValue(e.getValue()));
		informationTA.addValueChangeHandler(e -> informationHidden.setValue(e.getValue()));
		dateBox.addValueChangeHandler(e -> dateHidden.setValue(formatDate.format(e.getValue())));
		dateBox.getElement().setPropertyString("placeholder", formatDate.format(new Date()));
	}

	private void initializeForm() {
		// Create Form Panel
		form = new FormPanel();
		form.setAction(GWT.getModuleBaseURL() + "modification_form/");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
//		form.getElement().setPropertyString("acceptCharset", "ISO-8859-1");
		form.addSubmitCompleteHandler(e -> {
			try {
				hideMessage();
				String jsonStr = e.getResults().split(">")[1].split("<")[0];
				JSONValue json = JSONParser.parseStrict(jsonStr);
				parseJSON(json.isObject());
				hide();
			} catch (NullPointerException | IllegalArgumentException err){
				// Nothing to do
			}
		});
		
		//Add all to FlowPanel to add to FormPanel
		HTMLPanel flowFormPanel = new HTMLPanel("");
		flowFormPanel.add(userLoginHidden);
		flowFormPanel.add(currentDomainHidden);
		flowFormPanel.add(contractHidden);
		flowFormPanel.add(titleHidden);
		flowFormPanel.add(informationHidden);
		flowFormPanel.add(dateHidden);
		form.add(flowFormPanel);
		formPanel.add(form);
	}
	
	private void parseJSON(JSONObject json) {
		JSONValue success = json.get("success");
		if(null == success || AonStringUtils.isBlank(success.toString())) {
			JSONValue error = json.get("error");
			onError(error.toString().replaceAll("\"", ""));
		} else {
			onSuccess(success.toString().replaceAll("\"", ""));
		}
	}
	
	// ------------------------------------------------- Abstract Methods

	protected abstract void onSuccess(String message);

	protected abstract void onError(String message);

	// ------------------------------------------------- Auxiliar Methods
	
	private void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	// ------------------------------------------------- ButtonsPanel
	
	private void getButtonsPanel() {
		exportPDF = new Button();
		exportPDF.setStyleName(AON.CSS.aonIconPdf());
		exportPDF.addStyleName(style.footerButton());
		exportPDF.setText("Exportar PDF");
		exportPDF.addClickHandler(e -> {
			showLoading("Exportando modificaci\u00f3n");
			form.setAction(GWT.getModuleBaseURL() + "modification_form/export/");
			form.submit();
			hideMessageTimer();
		});
		
		buttonsPanel.add(exportPDF);
		
		generatePDF = new Button();
		generatePDF.setStyleName(AON.CSS.aonIconSave());
		generatePDF.addStyleName(style.footerButton());
		generatePDF.setText("Crear y guardar PDF");
		generatePDF.addClickHandler(e -> {
			showLoading("Generando y guardando modificaci\u00f3n");
			form.setAction(GWT.getModuleBaseURL() + "modification_form/generate/");
			form.submit();
		});
		
		buttonsPanel.add(generatePDF);
	}
	
	private void hideMessageTimer() {
		Timer timer = new Timer() {
			@Override
			public void run() {
				hideMessage();
			}
		};
		timer.schedule(2000);
	}
	
	// ------------------------------------------------- Aon Messages panel
	
	private void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}

	private void hideMessage() {
		AonMessagePanel.hideMessage(messagePanel);
	}
}
