package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.Dni;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class Enterprise extends ResizeComposite {
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static EnterpriseUiBinder uiBinder = GWT.create(EnterpriseUiBinder.class);

	interface EnterpriseUiBinder extends UiBinder<Widget, Enterprise> {
	}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String warningColor();
	}
	
	// TABLA DATOS EMPRESA
	
	@UiField
	TextBox enterpriseName;
	
	@UiField
	TextBox enterpriseAlias;
	
	@UiField
	Label documentType;
	
	@UiField
	TextBox document;
	
	@UiField 
	Label documentStatus;
	
	@UiField 
	Label nationalityLabel;
	
	@UiField (provided = true)
	SuggestBox nationality;
	
	@UiField
	ListBox streetType;
	
	@UiField
	TextBox address;
	
	@UiField
	TextBox addressNum;
	
	@UiField
	TextBox addressZip;
	
	@UiField
	ListBox addressCity;
	
	@UiField
	ListBox addressProvince;
	
	@UiField
	TextBox mobile;
	
	@UiField
	TextBox phone;
	
	@UiField
	TextBox email;
	
	@UiField
	TextBox enterpriseWeb;
	
	@UiField
	HTMLPanel enterpriseScopePanel;
	
	// TABLA OTRO DATOS
	
	@UiField
	ListBox enterprisePaysheetModel;
	
	@UiField
	ListBox enterpriseCostModel;
	
	@UiField
	ListBox enterprisePaysheetSendType;
	
	@UiField
	HTMLPanel enterprisePaysheetSendPanel;
	
	@UiField
	TextBox enterprisePaysheetSendEmail;
	
	@UiField
	ListBox enterpriseAgreement;

	// ------------------------------------------------ CONSTRUCTOR ------------------------------------------------------

	public Enterprise() {
		//Initialize Nationality SuggestBox
		MultiWordSuggestOracle oracleCountries = new MultiWordSuggestOracle();
		ArrayList<Country> countries = new ArrayList<>(Arrays.asList(Country.values()));
		for (Country c : countries)
			oracleCountries.add(c.getName());
		this.nationality = new SuggestBox(oracleCountries);
		
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		initializeView();
	}

	// ------------------------------------------------- UiHandlers ------------------------------------------------------
	
	@UiHandler("enterpriseName")
	void onEnterpriseNameChangeValue(ChangeEvent event) {
		onEnterpriseNameChange();
	}
	
	@UiHandler("enterpriseAlias")
	void onEnterpriseAliasChangeValue(ChangeEvent event) {
		onEnterpriseAliasChange();
	}

	@UiHandler("document")
	void onEnterpriseDocumentChangeValue(ValueChangeEvent<String> event) {
		onEnterpriseDocumentChange();
	}
	
	@UiHandler("nationality")
	void onEnterpriseNationalityChangeValue(ValueChangeEvent<String> event) {
		onEnterpriseNationalityChange();
	}
	
	@UiHandler("streetType")
	void onEnterpriseStreetTypeChangeValue(ChangeEvent event) {
		onEnterpriseStreetTypeChange();
	}
	
	@UiHandler("address")
	void onEnterpriseAddressChangeValue(ValueChangeEvent<String> event) {
		onEnterpriseAddressChange();
	}
	
	@UiHandler("addressNum")
	void onEnterpriseAddressNumChangeValue(ChangeEvent event) {
		onEnterpriseAddressNumChange();
	}
	
	@UiHandler("addressZip")
	void onEnterpriseAddressZipChangeValue(ChangeEvent event) {
		onEnterpriseAddressZipChange();
	}
	
	@UiHandler("addressCity")
	void onEnterpriseAddressCityChangeValue(ChangeEvent event) {
		onEnterpriseAddressCityChange();
	}
	
	@UiHandler("addressProvince")
	void onEnterpriseAddressProvinceChangeValue(ChangeEvent event) {
		onEnterpriseAddressProvinceChange();
	}
	
	@UiHandler("mobile")
	void onEnterpriseMobileChangeValue(ChangeEvent event) {
		onEnterpriseMobileChange();
	}
	
	@UiHandler("phone")
	void onEnterprisePhoneChangeValue(ChangeEvent event) {
		onEnterprisePhoneChange();
	}
	
	@UiHandler("email")
	void onEnterpriseEmailChangeValue(ChangeEvent event) {
		onEnterpriseEmailChange();
	}
	
	@UiHandler("enterpriseWeb")
	void onEnterpriseWebChangeValue(ChangeEvent event) {
		onEnterpriseWebChange();
	}
	
	@UiHandler("enterprisePaysheetModel")
	void onEnterprisePaysheetModelChangeValue(ChangeEvent event) {
		onEnterprisePaysheetModelChange();
	}
	
	@UiHandler("enterpriseCostModel")
	void onEnterpriseCostModelChangeValue(ChangeEvent event) {
		onEnterpriseCostModelChange();
	}
	
	@UiHandler("enterprisePaysheetSendType")
	void onEnterprisePaysheetSendTypeChangeValue(ChangeEvent event) {
		onEnterprisePaysheetSendTypeChange();
	}
	
	@UiHandler("enterprisePaysheetSendEmail")
	void onEnterprisePaysheetSendEmailChangeValue(ChangeEvent event) {
		onEnterprisePaysheetSendEmailChange();
	}
	
	@UiHandler("enterpriseAgreement")
	void onEnterpriseAgreementChangeValue(ChangeEvent event) {
		onEnterpriseAgreementChange();
	}
	
	// ------------------------------------------------------------------------
	//							Abstraact Methods
	// ------------------------------------------------------------------------
	
	// TABLA DATOS EMPRESA
	
	public abstract void onEnterpriseNameChange();
	public abstract void onEnterpriseAliasChange();
	public abstract void onEnterpriseDocumentChange();
	public abstract void onEnterpriseNationalityChange();
	public abstract void onEnterpriseStreetTypeChange();
	public abstract void onEnterpriseAddressChange();
	public abstract void onEnterpriseAddressNumChange();
	public abstract void onEnterpriseAddressZipChange();
	public abstract void onEnterpriseAddressCityChange();
	public abstract void onEnterpriseAddressProvinceChange();
	public abstract void onEnterpriseMobileChange();
	public abstract void onEnterprisePhoneChange();
	public abstract void onEnterpriseEmailChange();
	public abstract void onEnterpriseWebChange();
	
	// TABLA OTROS DATOS EMPRESA
	
	public abstract void onEnterprisePaysheetModelChange();
	public abstract void onEnterpriseCostModelChange();
	public abstract void onEnterprisePaysheetSendTypeChange();
	public abstract void onEnterprisePaysheetSendEmailChange();
	public abstract void onEnterpriseAgreementChange();
	public abstract void onEnterpriseScopeChange(Integer scopeId);
	
	// ------------------------------------------------------ METODOS DE LA CLASE --------------------------------------------------

	public void initializeView() {
		resetElements();
		initializeListBox();
	}

	private void resetElements() {
		
		this.enterpriseName.setValue(null);
		this.enterpriseAlias.setValue(null);
		this.documentType.setText("");
		this.document.setValue(null);
		this.streetType.clear();
		this.address.setValue(null);
		this.addressNum.setValue(null);
		this.addressZip.setValue(null);
		this.addressCity.clear();
		this.addressProvince.clear();
		this.mobile.setValue(null);
		this.phone.setValue(null);
		this.email.setValue(null);
		this.enterpriseWeb.setValue(null);
		this.enterpriseScopePanel.clear();
		
		this.enterprisePaysheetModel.clear();
		this.enterpriseCostModel.clear();
		this.enterprisePaysheetSendType.clear();
		this.enterprisePaysheetSendEmail.setValue(null);
		this.enterpriseAgreement.clear();
	}

	private void initializeListBox() {
		//STREET TYPE
		for(int i=0; i<StreetType.values().length; i++){
			this.streetType.addItem(StreetType.values()[i].getDescription(), StreetType.values()[i].getShortCode());
		}
		
		//PROVINCE
		this.addressProvince.addItem("-");
		for( Entry<String, String> provinces : ProvinceContract.getProvinces().entrySet())
			this.addressProvince.addItem(provinces.getValue(), provinces.getKey());
		
		//PAYSHEET MODEL
		this.enterprisePaysheetModel.addItem("Estandar", "salary");
		this.enterprisePaysheetModel.addItem("Estandar (2 columnas)", "salary_dualColumn");
		this.enterprisePaysheetModel.addItem("Factura Simple", "salary_invoiceSimple");
		this.enterprisePaysheetModel.addItem("Factura (Agrupada CRA)", "salary_invoiceCraGroup");
		
		//COST MODEL
		this.enterpriseCostModel.addItem("Por defecto", "salaryExpense");
		this.enterpriseCostModel.addItem("Extendida", "salaryExpenseExtended");
		
		//SEND PAYSHEET
		this.enterprisePaysheetSendType.addItem("Email", "EMAIL");
		this.enterprisePaysheetSendType.addItem("Papel", "PAPER");
		this.enterprisePaysheetSendType.addItem("Otro", "OTHERS");
	}
	
	public void checkDocument() {
		String value = document.getValue();
		
		if(AonStringUtils.isNotBlank(value)) {
			String documentTypeValue = checkDocumentType();
		
			documentType.setText(documentTypeValue);
			showNationality();
		
			if(checkDocumentValidation()) {
				documentStatus.removeStyleName(AON.CSS.aonIconValid());
				documentStatus.addStyleName(AON.CSS.aonIconInvalid());
			}else {
				documentStatus.removeStyleName(AON.CSS.aonIconInvalid());
				documentStatus.addStyleName(AON.CSS.aonIconValid());
			}
		}else {
			documentStatus.removeStyleName(AON.CSS.aonIconValid());
			documentStatus.addStyleName(AON.CSS.aonIconInvalid());
		}
	}
	
	private String checkDocumentType() {

		String value = document.getValue();
		
		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");
		RegExp niePattern = RegExp.compile("[A-Z]{1}\\d{7}[A-Z]{1}");
		RegExp cifPattern = RegExp.compile("[A-Z]{1}\\d{8}");

		if (dniPattern.test(value.toUpperCase()))
			return "DNI";
		else if (niePattern.test(value.toUpperCase()))
			return "NIE";
		else if (cifPattern.test(value.toUpperCase()))
			return "CIF";
		else
			return "Pasaporte";
	}
	
	private void showNationality() {
		String documentType = checkDocumentType();
		
		if (	AonStringUtils.equals(documentType, "CIF") || 
				AonStringUtils.equals(documentType, "Pasaporte") || 
				AonStringUtils.equals(documentType, "NIE")) {
			
			nationalityLabel.getElement().getStyle().clearDisplay();
			nationality.getElement().getStyle().clearDisplay();
			
		} else {
			nationalityLabel.getElement().getStyle().setDisplay(Display.NONE);
			nationality.getElement().getStyle().setDisplay(Display.NONE);
			nationality.setValue("ESPA\u00D1A");
		}
	}
	
	private boolean checkDocumentValidation() {
		String value = document.getValue();
		String documentType = checkDocumentType();
		
		if(AonStringUtils.isBlank(value)) {
			return false;
		} else if(AonStringUtils.equals(documentType, "DNI")){
			Dni dni = new Dni(value);
			return dni.checkDNI();
		} else
			return true;
	}	
	
	public void checkPaysheetSendType(String email) {
		String paysheetSendType = String.valueOf(enterprisePaysheetSendType.getSelectedValue());
		
		if(AonStringUtils.isNotBlank(paysheetSendType) && AonStringUtils.equals(paysheetSendType, "EMAIL")) {
			enterprisePaysheetSendPanel.getElement().getStyle().clearDisplay();
			enterprisePaysheetSendEmail.setValue(email);
		}else {
			enterprisePaysheetSendPanel.getElement().getStyle().setDisplay(Display.NONE);
		}	
	}

	public void initializeScopeCell(Map<Integer, String> enterprisecopes) {
		Widget enterpriseScopeWidget;
		
		if(enterprisecopes.size() == 0)
			enterpriseScopeWidget = createEmptyListLabel();
		else {
			ListBox scopeListBox = new ListBox();
			scopeListBox.setStyleName("aon-selectOneMenu");
			scopeListBox.getElement().getStyle().setWidth(100, Unit.PCT);
			
			for(Entry<Integer, String> entry : enterprisecopes.entrySet())
				scopeListBox.addItem(entry.getValue(), entry.getKey().toString());
			
			scopeListBox.addChangeHandler(e -> {
				Integer scopeId = Integer.valueOf(scopeListBox.getSelectedValue());
				onEnterpriseScopeChange(scopeId);
			});
			
			enterpriseScopeWidget = scopeListBox;
		}
		
		enterpriseScopePanel.add(enterpriseScopeWidget);
	}

	public void initializeAgreementCell(List<Agreement> enterpriseAgreements) {
		enterpriseAgreement.addItem("-", "-1");
		
		for (Agreement agreement : enterpriseAgreements)
			enterpriseAgreement.addItem(agreement.getDescription(), String.valueOf(agreement.getId()));
	}
	
	public Label createEmptyListLabel() {
		Label label = new Label();
		
		label.setText("No hay entradas disponibles");
		label.setStyleName("aon-inputText");
		label.addStyleName(style.warningColor());
		label.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		
		return label;
	}

}
