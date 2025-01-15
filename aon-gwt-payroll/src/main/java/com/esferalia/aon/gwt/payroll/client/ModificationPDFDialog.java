package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextArea;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;

public abstract class ModificationPDFDialog extends AonCustomDialog {
	
	// ------------------------------------------------- Variables
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private HTMLPanel container = new HTMLPanel("");
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonCustomTextBox title = new AonCustomTextBox("Titulo");
	private AonCustomTextArea info = new AonCustomTextArea("Informaci\u00f3n");
	private AonCustomDateBox date = new AonCustomDateBox("Fecha");
	
	private HTMLPanel buttonsPanel = new HTMLPanel("");
	
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
		
		showCloseButton(true);
		
		contractHidden.setValue(contractId.toString());
		
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem");
		container.getElement().getStyle().setProperty("min-width", "35rem");
		
		container.add(messagePanel);
		
		title.addValueChangeHandler(e -> titleHidden.setValue(e.getValue()));
		container.add(title);
		
		info.addValueChangeHandler(e -> informationHidden.setValue(e.getValue()));
		container.add(info);
		
		date.setValue(new Date());
		date.addValueChangeHandler(e -> dateHidden.setValue(formatDate.format(e.getValue())));
		container.add(date);
		
		form = new FormPanel();
		form.setAction(GWT.getModuleBaseURL() + "modification_form/");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.addSubmitCompleteHandler(e -> {
			AonMessagePanel.hideMessage(messagePanel);
			
			try {
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
		container.add(form);
		
		buttonsPanel.addStyleName(AON.CSS.aonItemFlex());
		buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		
		exportPDF = new Button();
		exportPDF.setStyleName(AON.CSS.aonIconPdf());
		exportPDF.addStyleName(AON.CSS.aonButtonDialog());
		exportPDF.setText("Exportar PDF");
		exportPDF.addClickHandler(e -> {
			AonMessagePanel.showLoading(messagePanel, "Exportando modificaci\u00f3n ...");

			form.setAction(GWT.getModuleBaseURL() + "modification_form/export/");
			form.submit();
			
			new Timer() {
				@Override
				public void run() {
					AonMessagePanel.hideMessage(messagePanel);
				}
			}.schedule(3000);
		});
		
		buttonsPanel.add(exportPDF);
		
		generatePDF = new Button();
		generatePDF.setStyleName(AON.CSS.aonIconSave());
		generatePDF.addStyleName(AON.CSS.aonButtonDialog());
		generatePDF.setText("Crear y guardar PDF");
		generatePDF.addClickHandler(e -> {
			AonMessagePanel.showLoading(messagePanel, "Generando y guardando modificaci\u00f3n ...");

			form.setAction(GWT.getModuleBaseURL() + "modification_form/generate/");
			form.submit();
		});
		
		buttonsPanel.add(generatePDF);
		
		container.add(buttonsPanel);
		
		setWidget(container);
		
		showDialog();
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
	
}
