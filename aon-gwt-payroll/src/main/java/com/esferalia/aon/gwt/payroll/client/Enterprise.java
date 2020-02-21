package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
		String hide();
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
	SuggestBox document;
	
	@UiField 
	Label documentStatus;
	
	@UiField 
	TableCellElement nationalityLabelCell;
	
	@UiField 
	TableCellElement nationalityCell;
	
	@UiField (provided = true)
	SuggestBox nationality;
	
	@UiField
	ListBox streetType;
	
	@UiField
	SuggestBox address;
	
	@UiField
	TextBox addressNum;
	
	@UiField
	TextBox addressZip;
	
	@UiField
	TextBox addressCity;
	
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
	HorizontalPanel enterpriseScopePanel;
	
	// TABLA OTRO DATOS
	
	@UiField
	TableElement enterpriseOthersTable;
	
	@UiField
	Label enterpriseActivity;
	
	@UiField
	Label enterpriseWorkplace;
	
	@UiField
	ListBox enterprisePaysheetModel;
	
	@UiField
	ListBox enterpriseCostModel;
	
	@UiField
	ListBox enterprisePaysheetSendType;
	
	@UiField
	HorizontalPanel enterprisePaysheetSendPanel;
	
	@UiField
	TextBox enterprisePaysheetSendEmail;
	
	@UiField
	ListBox enterpriseAgreement;
	
	@UiField
	HorizontalPanel enterpriseCalendarPanel;

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
		this.addressCity.setValue(null);
		this.addressProvince.clear();
		this.mobile.setValue(null);
		this.phone.setValue(null);
		this.email.setValue(null);
		this.enterpriseWeb.setValue(null);
		this.enterpriseScopePanel.clear();
		
		this.enterpriseActivity.setText("");
		this.enterpriseWorkplace.setText("");
		this.enterprisePaysheetModel.clear();
		this.enterpriseCostModel.clear();
		this.enterprisePaysheetSendType.clear();
		this.enterprisePaysheetSendEmail.setValue(null);
		this.enterpriseAgreement.clear();
		this.enterpriseCalendarPanel.clear();
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
		this.enterprisePaysheetModel.addItem("Estandar", "0");
		this.enterprisePaysheetModel.addItem("Estandar (2 columnas)", "1");
		this.enterprisePaysheetModel.addItem("Factura Simple", "2");
		this.enterprisePaysheetModel.addItem("Factura (Agrupada CRA)", "3");
		
		//COST MODEL
		this.enterpriseCostModel.addItem("Por defecto", "0");
		this.enterpriseCostModel.addItem("Extendida", "1");
		
		//SEND PAYSHEET
		this.enterprisePaysheetSendType.addItem("Email", "0");
		this.enterprisePaysheetSendType.addItem("Papel", "1");
		this.enterprisePaysheetSendType.addItem("Otro", "2");
	}

}
