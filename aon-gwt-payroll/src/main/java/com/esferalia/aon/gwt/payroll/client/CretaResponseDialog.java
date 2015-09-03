package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;

public class CretaResponseDialog extends SelectDialog<Employee> {

	public static interface Callback {
	}

	interface Binder extends UiBinder<Widget, CretaResponseDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	private Callback callback;

	@UiField FormPanel formPanel;
	@UiField FileUpload fileUpload;
	@UiField CheckBox basesCheck;
	
	public CretaResponseDialog(CretaService.File file, Callback callback) {
		super();
		this.callback = callback;
		setWidget(binder.createAndBindUi(this));
		setCaption("Sistema de Liquidaci\u00F3n Directa (Proyecto Cret@)");
		formPanel.setAction(CretaService.CRETA_URL+"/"+file.name());
	}
	
	// ------------------------------------------------------------------------
	
//	@UiHandler("acceptButton")
//	void  onAcceptClicked(ClickEvent e){
//		formPanel.submit();
//		hide();
//	}
	
	@UiHandler("acceptButton")
	void  onCancelClicked(ClickEvent e){
		hide();
	}
	
	// ------------------------------------------------------------------------
	
	@UiHandler("fileUpload")
	void onFileUploadChange(ChangeEvent event) {
		acceptButton.setEnabled(true);
	}
}
