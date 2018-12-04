package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class EnterpriseDraft extends Composite implements ContextMenuHandler {
	
	private class EnterpriseImplementation extends Enterprise{
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static EnterpriseDraftUiBinder uiBinder = GWT.create(EnterpriseDraftUiBinder.class);

	interface EnterpriseDraftUiBinder extends UiBinder<Widget, EnterpriseDraft> {
	}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hide();
		String paddingEnableDisable();
		String maxWidth();
		String fontDisableStyle();
		String fontEnableStyle();
		String warningColor();
		String maxWidthTextBox();
		String borderNone();
	}
	
	@UiField (provided = true)
	Enterprise enterprise;

	// ------------------------------------------------------ VARIABLES DE LA CLASE --------------------------------------------------

	private EnterpriseDraftObject enterpriseDraftObject;

	// ------------------------------------------------ CONSTRUCTOR ------------------------------------------------------

	public EnterpriseDraft() {
		enterprise = new EnterpriseImplementation();
		
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
	}

	// ------------------------------------------------- UiHandlers ------------------------------------------------------
	
	

	// ------------------------------------------------------ METODOS DE LA CLASE --------------------------------------------------

	public void setEnterpriseDraftObject(EnterpriseDraftObject enterpriseDraftObject) {
		this.enterpriseDraftObject = enterpriseDraftObject;
		this.enterpriseDraftObject.initializeEnterprise(
				s -> {
					initilizeView();
				},
				f -> {}
		);
	}

	private void initilizeView() {
		enterprise.initializeView();
		initializeListBox();
		fillEnterpriseData();
		
		enterprise.enterpriseOthersTable.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
	}

	private void initializeListBox() {
		//SCOPE
		initializeScopeCell();
		
		// CONVENIO
		enterprise.enterpriseAgreement.addItem("-");
		List<Agreement> agreements = enterpriseDraftObject.getActiveAgreements();
		for (Agreement a : agreements) {
			enterprise.enterpriseAgreement.addItem(a.getDescription());
		}
		
		//CALENDARIO
		initializeCalendarCell();		
	}

	private void initializeScopeCell() {
		Widget enterpriseScopeWidget;
		if(enterpriseDraftObject.getEnterprisecopes().values().size() == 1){
			Integer scopeId = (Integer) enterpriseDraftObject.getEnterprisecopes().keySet().toArray()[0];
			enterpriseScopeWidget = new Label((null == scopeId) ? "" : enterpriseDraftObject.getEnterprisecopes().get(scopeId));
			enterpriseScopeWidget.setStyleName("aon-inputText");
			enterpriseScopeWidget.addStyleName(style.maxWidthTextBox());
			enterprise.enterpriseScopePanel.add(enterpriseScopeWidget);
			enterpriseDraftObject.setScope(scopeId); //AutoSeleccion
		}else{
			enterpriseScopeWidget = new ListBox();
			for(String scope : enterpriseDraftObject.getEnterprisecopes().values()){
				((ListBox) enterpriseScopeWidget).addItem(scope);
			}
			enterpriseScopeWidget.setStyleName("aon-selectOneMenu");
			enterpriseScopeWidget.addStyleName(style.maxWidth());
			((ListBox) enterpriseScopeWidget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
//					onScopeChange();
					//save();
				}
			});
			if(((ListBox) enterpriseScopeWidget).getItemCount() != 0){
				((ListBox) enterpriseScopeWidget).setSelectedIndex(enterpriseDraftObject.getScopeIndex());
			}
			enterprise.enterpriseScopePanel.add(enterpriseScopeWidget);
		}
	}

	private void initializeCalendarCell() {
		Widget enterpriseCalendarWidget;
		if(enterpriseDraftObject.getEnterpriseCalendars().values().size() == 0){
			enterpriseCalendarWidget = new Label("No hay calendarios disponibles");
			enterpriseCalendarWidget.setStyleName("aon-inputText");
			enterpriseCalendarWidget.addStyleName(style.maxWidthTextBox());
			enterpriseCalendarWidget.addStyleName(style.warningColor());
			enterpriseCalendarWidget.addStyleName(style.borderNone());
			enterprise.enterpriseCalendarPanel.add(enterpriseCalendarWidget);
		}else if(enterpriseDraftObject.getEnterpriseCalendars().values().size() == 1){
			Integer calendarId = enterpriseDraftObject.getCalendar();
			enterpriseCalendarWidget = new Label(enterpriseDraftObject.getEnterpriseCalendars().get(calendarId));
			enterpriseCalendarWidget.setStyleName("aon-inputText");
			enterpriseCalendarWidget.addStyleName(style.maxWidthTextBox());
			enterprise.enterpriseCalendarPanel.add(enterpriseCalendarWidget);
		}else{
			enterpriseCalendarWidget = new ListBox();
			for(String calendar : enterpriseDraftObject.getEnterpriseCalendars().values()){
				if(null != calendar)
					((ListBox) enterpriseCalendarWidget).addItem(calendar);
			}
			enterpriseCalendarWidget.setStyleName("aon-selectOneMenu");
			enterpriseCalendarWidget.addStyleName(style.maxWidth());
			((ListBox) enterpriseCalendarWidget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
//					String calendar = ((ListBox) workplaceCalendarWidget).getSelectedItemText();
//					Integer calendarId = -1;
//					for(Entry<Integer, String> entry : workplaceDraftObject.getWorkplacesCalendars().entrySet()){
//						if(calendar.equals(entry.getValue())){
//							calendarId = entry.getKey();
//							break;
//						}
//					}
//					workplaceDraftObject.setWorkplaceCalendar(calendarId);
					//save();
					
				}
			});
			enterprise.enterpriseCalendarPanel.add(enterpriseCalendarWidget);
			if(((ListBox) enterpriseCalendarWidget).getItemCount() != 0){
				((ListBox) enterpriseCalendarWidget).setSelectedIndex(enterpriseDraftObject.getCalendarIndex());
			}
		}	
	}
	
	private void fillEnterpriseData() {
		enterprise.enterpriseName.setValue(enterpriseDraftObject.getName());
		enterprise.enterpriseAlias.setValue(enterpriseDraftObject.getAlias());
		enterprise.document.setValue(enterpriseDraftObject.getDocument());
		onEnterpriseDocumentChange();
		enterprise.nationality.setValue(enterpriseDraftObject.getDocumentCountry());
		enterprise.streetType.setSelectedIndex(enterpriseDraftObject.getAddressStreetTypeIndex());
		enterprise.address.setValue(enterpriseDraftObject.getAddress());
		enterprise.addressNum.setValue(enterpriseDraftObject.getAddressNum());
		enterprise.addressZip.setValue(enterpriseDraftObject.getAddressZip());
		enterprise.addressCity.setValue(enterpriseDraftObject.getAddressCity());
		enterprise.addressProvince.setSelectedIndex(enterpriseDraftObject.getAddressProvinceIndex());
		enterprise.mobile.setValue(enterpriseDraftObject.getMobile());
		enterprise.phone.setValue(enterpriseDraftObject.getPhone());
		enterprise.email.setValue(enterpriseDraftObject.getEmail());
		enterprise.enterpriseWeb.setValue(enterpriseDraftObject.getWeb());
		
		//Crear Imagen
//		Image img = new Image("data:image/png;base64," + enterpriseDraftObject.getLogo());
//		img.setPixelSize(100, 100);
//		
//		enterprise.enterpriseLogoPanel.add(img);
		
//		enterprise.enterpriseLogo = new Image(enterpriseDraftObject.getLogo());
//		enterprise.enterpriseSign = new Image(enterpriseDraftObject.getSignature());
		
		enterprise.enterprisePaysheetModel.setSelectedIndex(enterpriseDraftObject.getPaySheetModelIndex());
		enterprise.enterpriseCostModel.setSelectedIndex(enterpriseDraftObject.getCostsModelIndex());
		enterprise.enterprisePaysheetSendType.setSelectedIndex(enterpriseDraftObject.getPaysheetSendIndex());
		checkPaysheetSendType();
		if(enterprise.enterpriseAgreement.getItemCount() != 0) {
			enterprise.enterpriseAgreement.setSelectedIndex(enterpriseDraftObject.getAgreementIndex() + 1);
		}
	}


	private void checkPaysheetSendType() {
		if(enterpriseDraftObject.getPaysheetSendIndex() == 0) {
			enterprise.enterprisePaysheetSendPanel.removeStyleName(style.hide());
			enterprise.enterprisePaysheetSendEmail.setValue(enterpriseDraftObject.getPaysheetSendEmail());
		}else {
			enterprise.enterprisePaysheetSendPanel.setStyleName(style.hide());
		}
		
	}

	private void onEnterpriseDocumentChange() {
		String document = enterpriseDraftObject.getDocument();
		String document_type = checkDocumentType(document);
		
		enterprise.documentType.setText(document_type);
		if(enterpriseDraftObject.checkDocumentValidation(document_type, document)) {
			enterpriseDraftObject.setDocument(document);
			enterpriseDraftObject.setDocumentType(document_type);
			enterprise.documentStatus.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
			enterprise.documentStatus.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
		}else {
			enterprise.documentStatus.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
			enterprise.documentStatus.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
			
		}

		showNationality(document_type);
	}
	
	public String checkDocumentType(String document) {

		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");
		RegExp niePattern = RegExp.compile("[A-Z]{1}\\d{7}[A-Z]{1}");
		RegExp cifPattern = RegExp.compile("[A-Z]{1}\\d{8}");

		if (dniPattern.test(document.toUpperCase()))
			return "DNI";
		else if (niePattern.test(document.toUpperCase()))
			return "NIE";
		else if (cifPattern.test(document.toUpperCase()))
			return "CIF";
		else
			return "Pasaporte";
	}
	
	private void showNationality(String document_type_str) {
		if (document_type_str == "CIF" || document_type_str == "Pasaporte" || document_type_str == "NIE") {
			enterprise.nationalityLabelCell.getStyle().clearDisplay();
		} else {
			enterprise.nationalityLabelCell.getStyle().setDisplay(Display.NONE);
			enterprise.nationality.setValue("ESPA\u00D1A");
		}
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// TODO Auto-generated method stub
	}

}
