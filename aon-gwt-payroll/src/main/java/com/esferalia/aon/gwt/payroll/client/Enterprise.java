package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;

import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class Enterprise extends ResizeComposite implements ContextMenuHandler {
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static EnterpriseUiBinder uiBinder = GWT.create(EnterpriseUiBinder.class);

	interface EnterpriseUiBinder extends UiBinder<Widget, Enterprise> {
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

	// TABLA DATOS EMPRESA
	
	@UiField
	TableElement enterpriseDataTable;
	
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
	
	// TABLA LOGOS EMPRESA
	
	@UiField
	HorizontalPanel enterpriseLogoPanel;
	
	@UiField
	Image enterpriseLogo;
	
	@UiField 
	Button uploadLogo;
	
	@UiField
	Button deleteLogo;
	
	@UiField
	Image enterpriseSign;
	
	@UiField
	Button uploadSign;
	
	@UiField
	Button deleteSign;
	
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
	
//	@UiHandler("workplaceDescription")
//	void onWorkplaceDescriptionChangeValue(ChangeEvent event) {
//		onWorkplaceDescriptionChange();
//	}
//
//	@UiHandler("workplaceEconomicConcert")
//	void onWorkplaceEconomicConcertChangeValue(ChangeEvent event) {
//		onWorkplaceEconomicConcertChange();
//	}
//	
//	@UiHandler("workpalceAgreement")
//	void onContractAgreementChangeValue(ChangeEvent event) {
//		onWorkplaceAgreementChange();
//	}
	
	// ------------------------------------------------------------------------
	//							Abstraact Methods
	// ------------------------------------------------------------------------
	
	// TABLA DATOS CENTRO DE TRABAJO
	
//	public abstract void onWorkplaceDescriptionChange();
//	public abstract void onWorkplaceEconomicConcertChange();
	
	// TABLA DATOS CENTRO DE TRABAJO (LABORAL)
	
//	public abstract void onWorkplaceAgreementChange();

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
			this.streetType.addItem(StreetType.values()[i].getDescription());
		}
		
		//PROVINCE
		this.addressProvince.addItem("-");
		for(String province : ProvinceContract.getProvinces().values())
			this.addressProvince.addItem(province);
		
		//PAYSHEET MODEL
		this.enterprisePaysheetModel.addItem("Estandar");
		this.enterprisePaysheetModel.addItem("Estandar (2 columnas)");
		this.enterprisePaysheetModel.addItem("Factura Simple");
		this.enterprisePaysheetModel.addItem("Factura (Agrupada CRA)");
		
		//COST MODEL
		this.enterpriseCostModel.addItem("Por defecto");
		this.enterpriseCostModel.addItem("Extendida");
		
		//SEND PAYSHEET
		this.enterprisePaysheetSendType.addItem("Email");
		this.enterprisePaysheetSendType.addItem("Papel");
		this.enterprisePaysheetSendType.addItem("Otro");
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// TODO Auto-generated method stub
	}

}
