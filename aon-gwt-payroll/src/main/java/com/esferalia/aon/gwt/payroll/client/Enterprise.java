package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.common.shared.Dni;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.SelectionEvent;
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
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class Enterprise extends ResizeComposite {
	
	// -------------------------------------------------- UiBinder

	private static EnterpriseUiBinder uiBinder = GWT.create(EnterpriseUiBinder.class);

	interface EnterpriseUiBinder extends UiBinder<Widget, Enterprise> {
	}

	// -------------------------------------------------- UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String documentError();
		String inputPadding();
		String inputLBHeight();
		String warningColor();
		String warningTB();
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
	
	@UiField (provided = true)
	AonToolbarSmallButton documentStatus;
	
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
	ListBox enterprisePaysheetSendType;
	
	@UiField
	HTMLPanel enterprisePaysheetSendPanel;
	
	@UiField
	TextBox enterprisePaysheetSendEmail;
	
	@UiField
	SuggestBox enterpriseAgreement;
	
	// TABLA TGSS
	
	@UiField
	TextBox enterprisePayAuthorizationKey;
	
	@UiField
	ListBox enterprisePaySsMutual;
	
	// -------------------------------------------------- Variables
	
	private List<Agreement> enterpriseAgreements;

	// -------------------------------------------------- Constructor

	protected Enterprise() {
		//Initialize Nationality SuggestBox
		MultiWordSuggestOracle oracleCountries = new MultiWordSuggestOracle();
		ArrayList<Country> countries = new ArrayList<>(Arrays.asList(Country.values()));
		countries.forEach(c -> oracleCountries.add(c.getName()));
		this.nationality = new SuggestBox(oracleCountries);
		
		documentStatus = new AonToolbarSmallButton("", AON.CSS.aonIconValid());
		
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		initializeView();
	}
	
	// -------------------------------------------------- initializeView
	
	public void initializeView() {
		removeWarning(enterpriseName);
		
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
		this.enterprisePaysheetSendType.clear();
		this.enterprisePaysheetSendEmail.setValue(null);
		this.enterpriseAgreement.setValue(null);
		
		this.enterprisePayAuthorizationKey.setValue(null);
		this.enterprisePaySsMutual.clear();
	}

	private void initializeListBox() {
		//STREET TYPE
		for(int i=0; i<StreetType.values().length; i++)
			if(null != StreetType.values()[i].getLanguage() && StreetType.values()[i].getLanguage().equals(AonLanguage.SPANISH))
				this.streetType.addItem(StreetType.values()[i].getDescription(), StreetType.values()[i].getIneCode());
		
		//PROVINCE
		this.addressProvince.addItem("-");
		for(int i=0; i<Province.values().length; i++)
			this.addressProvince.addItem(Province.values()[i].getName(), AonStringUtils.leftPad(i + "", 2, '0'));
		
		//PAYSHEET MODEL
		this.enterprisePaysheetModel.addItem("Est\u00E1ndar", "salary");
		this.enterprisePaysheetModel.addItem("Est\u00E1ndar (2 columnas)", "salary_dualColumn");
		this.enterprisePaysheetModel.addItem("Factura Simple", "salary_invoiceSimple");
		this.enterprisePaysheetModel.addItem("Factura (Agrupada CRA)", "salary_invoiceCraGroup");
		this.enterprisePaysheetModel.addItem("Detallada (new)", "salary_connorMacleod");
		this.enterprisePaysheetModel.addItem("Est\u00E1ndar (new)", "salary_connorMacleod_classic");
		
		disableDeprecatedPaysheetModels();
		
		//SEND PAYSHEET
		this.enterprisePaysheetSendType.addItem("Email", "EMAIL");
		this.enterprisePaysheetSendType.addItem("Papel", "PAPER");
		this.enterprisePaysheetSendType.addItem("Otro", "OTHERS");
		
		this.enterprisePaySsMutual.addItem("-", "");
		this.enterprisePaySsMutual.addItem("CNP PARTNERS SEGUROS Y REASEGUROS SA", "G0001");
		this.enterprisePaySsMutual.addItem("ABANCA VIDA Y PENSIONES DE SEGUROS Y REASEGUROS S.A.U.", "G0002");
		this.enterprisePaySsMutual.addItem("UNICORP VIDA, COMPA\u00d1IA DE SEGUROS Y REASEGUROS, S.A.", "G0003");
		this.enterprisePaySsMutual.addItem("BANKINTER SEGUROS DE VIDA", "G0006");
		this.enterprisePaySsMutual.addItem("FIATC, MUTUA DE SEGUROS Y REASEGUROS", "G0010");
		this.enterprisePaySsMutual.addItem("SA NOSTRA COMPA\u00d1IA DE SEGUROS DE VIDA SA", "G0012");
		this.enterprisePaySsMutual.addItem("VIDA-CAIXA SA DE SEGUROS Y REASEGUROS", "G0021");
		this.enterprisePaySsMutual.addItem("GENERALI ESPA\u00d1A, S.A. DE SEGUROS Y REASEGUROS", "G0037");
		this.enterprisePaySsMutual.addItem("CCM VIDA Y PENSIONES S.A. DE SEG.Y REAS", "G0048");
		this.enterprisePaySsMutual.addItem("GESPENSION CAMINOS, S.A. E.G.F.P.", "G0067");
		this.enterprisePaySsMutual.addItem("IBERCAJA PENSION,E.G.F.P. , S.A.U.", "G0079");
		this.enterprisePaySsMutual.addItem("SANTANDER PENSIONES, S.A., E.G.F.P.", "G0080");
		this.enterprisePaySsMutual.addItem("BBVA PENSIONES S.A. EGFP", "G0082");
		this.enterprisePaySsMutual.addItem("BANSABADELL PENSIONES, E.G.F.P., S.A.", "G0085");
		this.enterprisePaySsMutual.addItem("MEDIOLANUM PENSIONES, S.A., S.G.F.P.", "G0091");
		this.enterprisePaySsMutual.addItem("MUTUALITAT DELS ENGINYERS MPS", "G0105");
		this.enterprisePaySsMutual.addItem("GVC GAESCO PENSIONES,S.A. S.G.F.P.", "G0111");
		this.enterprisePaySsMutual.addItem("MAPFRE VIDA PENSIONES,E.G.F.P.,S.A", "G0121");
		this.enterprisePaySsMutual.addItem("LORETO MUTUA, MUTUALIDAD DE PREVISION SOCIAL", "G0124");
		this.enterprisePaySsMutual.addItem("RGA RURAL PENSIONES S.A. EGFP", "G0131");
		this.enterprisePaySsMutual.addItem("GESTION DE PREVISION Y PENSIONES EGFP S.A.", "G0133");
		this.enterprisePaySsMutual.addItem("MUTUACTIVOS PENSIONES SGFP S.A.U", "G0135");
		this.enterprisePaySsMutual.addItem("ARQUIPENSIONES EGFP, S.A.", "G0137");
		this.enterprisePaySsMutual.addItem("PREVISION SANITARIA NACIONAL, MUTUA DE SEGUROS A PRIMA FIJA", "G0148");
		this.enterprisePaySsMutual.addItem("MERCHBANC, E.G.F.P., S.A.", "G0153");
		this.enterprisePaySsMutual.addItem("SEGUROS EL CORTE INGLES VIDA PENSIONES Y REASEGUROS S.A.U.", "G0154");
		this.enterprisePaySsMutual.addItem("FONDITEL PENSIONES E.G.F.P.,S.A.", "G0162");
		this.enterprisePaySsMutual.addItem("TARGOPENSIONES ENTIDAD GESTORA DE FONDOS DE PENSIONES, S.A.U", "G0172");
		this.enterprisePaySsMutual.addItem("AXA PENSIONES,S.A., E.G.F.P.", "G0177");
		this.enterprisePaySsMutual.addItem("BESTINVER PENSIONES E.G.F.P. ,S.A", "G0179");
		this.enterprisePaySsMutual.addItem("LIBERBANK PENSIONES SGFP SA", "G0180");
		this.enterprisePaySsMutual.addItem("RENTA 4 PENSIONES, S.A., E.G.F.P.", "G0185");
		this.enterprisePaySsMutual.addItem("DEUTSCHE ZURICH PENSIONES, ENTIDAD GESTO ", "G0187");
		this.enterprisePaySsMutual.addItem("NATIONALE-NEDERLANDEN VIDA COMPA\u00d1IA DE SEGUROS Y REASEGUROS, S.A.E", "G0190");
		this.enterprisePaySsMutual.addItem("MARCH GESTION DE PENSIONES SGFP, S.A.", "G0197");
		this.enterprisePaySsMutual.addItem("PUEYO PENSIONES E.G.F.P., S.A.", "G0198");
		this.enterprisePaySsMutual.addItem("TREA PENSIONES, E.G.F.P., S.AU.", "G0202");
		this.enterprisePaySsMutual.addItem("FINECO PREVISION", "G0207");
		this.enterprisePaySsMutual.addItem("SURNE MUTUA DE SEGUROS Y REASEGUROS A PRIMA FIJA", "G0211");
		this.enterprisePaySsMutual.addItem("CAJAMARVIDA, S.A. DE SEGUROS Y REASEGUROS", "G0214");
		this.enterprisePaySsMutual.addItem("CAJA LABORAL PENSIONES S.A., G.F.P.", "G0217");
		this.enterprisePaySsMutual.addItem("CASER PENSIONES ENTIDAD GESTORA DE FONDOS DE PENSIONES SA", "G0219");
		this.enterprisePaySsMutual.addItem("DUNAS CAPITAL PENSIONES, S.G.F.P., S.A.U.", "G0224");
		this.enterprisePaySsMutual.addItem("CAJA INGENIEROS VIDA, COMPA\u00d1IA DE SEGUROS Y REASEGUROS, S.A.", "G0225");
		this.enterprisePaySsMutual.addItem("AEGON ESPA\u00d1A, S.A. DE SEGUROS Y REASEGUROS", "G0230");
		this.enterprisePaySsMutual.addItem("LIBERBANK VIDA Y PENSIONES, DE SEGUROS Y REASEGUROS, S.A.", "G0231");
		this.enterprisePaySsMutual.addItem("HERMANDAD NACIONAL DE ARQUITECTOS, ARQUITECTOS TECNICOS Y QUIMICOS, MPS", "G0232");
		this.enterprisePaySsMutual.addItem("Abante Pensiones EGFP S.A.", "G0233");
		this.enterprisePaySsMutual.addItem("KUTXABANK PENSIONES, E.G.F.P., S.A.U.", "G0234");
		this.enterprisePaySsMutual.addItem("GCO GESTORA DE PENSIONES, EGFP, S.A.", "G0236");
		this.enterprisePaySsMutual.addItem("UNION DEL DUERO, COMPA\u00d1IA DE SEGUROS DE VIDA, S.A.", "G0237");
		this.enterprisePaySsMutual.addItem("COBAS PENSIONES SGFP SA", "G0238");
		this.enterprisePaySsMutual.addItem("ALLIANZ, COMPA\u00d1IA DE SEGUROS Y REASEGUROS, S.A.", "G0239");
		this.enterprisePaySsMutual.addItem("SANTA LUCIA, S.A. COMPA\u00d1IA DE SEGUROS Y REASEGUROS", "G0240");
	}

	// ------------------------------------------------- UiHandlers
	
	@UiHandler("enterpriseName")
	void onEnterpriseNameChangeValue(ChangeEvent event) {
		if(AonStringUtils.isNotBlank(enterpriseName.getValue())) {
			removeWarning(enterpriseName);
			onEnterpriseNameChange();
		} else {
			addWarning(enterpriseName);
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("Nombre obligatorio", "Este campo debe ser rellenado obligatoriamente");
			fireErrorMessage(errorMap);
		}
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
	void onEnterpriseNationalitySelection(SelectionEvent<Suggestion> event) {
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
	
	@UiHandler("enterprisePaysheetSendType")
	void onEnterprisePaysheetSendTypeChangeValue(ChangeEvent event) {
		onEnterprisePaysheetSendTypeChange();
	}
	
	@UiHandler("enterprisePaysheetSendEmail")
	void onEnterprisePaysheetSendEmailChangeValue(ChangeEvent event) {
		onEnterprisePaysheetSendEmailChange();
	}
	
	@UiHandler("enterpriseAgreement")
	void onEnterpriseAgreementSelection(SelectionEvent<Suggestion> event) {
		String agreementDescription = enterpriseAgreement.getValue();
		for(Agreement agreement : this.enterpriseAgreements)
			if(AonStringUtils.equalsIgnoreCase(agreement.getDescription(), agreementDescription))
				onEnterpriseAgreementChange(agreement.getId());		
	}
	
	@UiHandler("enterpriseAgreement")
	void onEnterpriseAgreementValueChange(ValueChangeEvent<String> event) {
		String agreementDescription = enterpriseAgreement.getValue();
		if(AonStringUtils.isBlank(agreementDescription))
			onEnterpriseAgreementChange(null);
	}
	
	@UiHandler("enterprisePaySsMutual")
	void onEnterprisePaySsMutualChangeValue(ChangeEvent event) {
		onEnterprisePaySsMutualChange();
	}
	
	@UiHandler("enterprisePayAuthorizationKey")
	void onEnterprisePayAuthorizationKeyChangeValue(ChangeEvent event) {
		onnterprisePayAuthorizationKeyChange();
	}
	
	// ------------------------------------------------- Abstract Methods
	
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
	public abstract void onEnterprisePaysheetSendTypeChange();
	public abstract void onEnterprisePaysheetSendEmailChange();
	public abstract void onEnterpriseAgreementChange(Integer agreementId);
	public abstract void onEnterpriseScopeChange(Integer scopeId);
	
	public abstract void fireErrorMessage(Map<String, String> messages);
	public abstract void fireWarningMessage(Map<String, String> messages);
	
	// TABLA TGSS EMPRESA
	
	public abstract void onEnterprisePaySsMutualChange();
	public abstract void onnterprisePayAuthorizationKeyChange();
	
	// ------------------------------------------------- Auxiliar Methods	
	
	public void checkDocument(boolean fireMessage) {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("Formato documento", "El documento no est\u00E1 definido o tiene un formato err\u00F3neo");
		
		if(AonStringUtils.isNotBlank(document.getValue())) {
			String documentTypeValue = checkDocumentType();
		
			documentType.setText(documentTypeValue);
			showNationality();
		
			if(checkDocumentValidation()) {
				showDocumentError();
				if(Boolean.TRUE.equals(fireMessage))
					fireWarningMessage(infoMap);
			}else {
				hideDocumentError();
			}
		}else {
			showDocumentError();
			if(Boolean.TRUE.equals(fireMessage))
				fireWarningMessage(infoMap);
		}
	}
	
	private void showDocumentError() {
		documentStatus.removeStyleName(AON.CSS.aonIconValid());
		documentStatus.addStyleName(AON.CSS.aonIconInvalid());
		document.addStyleName(style.documentError());
		document.setTitle("Documento no definido o formato err\u00F3neo");
		documentStatus.setTitle("Documento no definido o formato err\u00F3neo");
	}
	
	private void hideDocumentError() {
		documentStatus.removeStyleName(AON.CSS.aonIconInvalid());
		document.removeStyleName(style.documentError());
		documentStatus.addStyleName(AON.CSS.aonIconValid());
		document.setTitle("");
		documentStatus.setTitle("");
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
		String documentTypeStr = checkDocumentType();
		
		if (	AonStringUtils.equals(documentTypeStr, "CIF") || 
				AonStringUtils.equals(documentTypeStr, "Pasaporte") || 
				AonStringUtils.equals(documentTypeStr, "NIE")) {
			
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
		String documentTypeStr = checkDocumentType();
		
		if(AonStringUtils.isBlank(value))
			return false;
		else if(AonStringUtils.equals(documentTypeStr, "DNI"))
			return !Dni.checkDNI(value);
		else
			return false;
	}
	
	// ------------------------------------------------- Initialize ListBoxes

	public void initializeScopeCell(Map<Integer, String> enterprisecopes) {
		Widget enterpriseScopeWidget;
		
		if(enterprisecopes.size() == 0)
			enterpriseScopeWidget = createEmptyListLabel();
		else {
			ListBox scopeListBox = new ListBox();
			scopeListBox.setStyleName("aon-selectOneMenu");
			scopeListBox.addStyleName(style.inputLBHeight());
			scopeListBox.addStyleName(style.inputPadding());
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
		this.enterpriseAgreements = enterpriseAgreements;
		
		List<String> agreementDescriptions = new ArrayList<>();
		
		for (Agreement agreement : this.enterpriseAgreements)
			agreementDescriptions.add(agreement.getDescription());
		
		MultiWordSuggestOracle orclAgreements = (MultiWordSuggestOracle) enterpriseAgreement.getSuggestOracle();
		orclAgreements.addAll(agreementDescriptions);
		orclAgreements.setDefaultSuggestionsFromText(agreementDescriptions);
		enterpriseAgreement.setAutoSelectEnabled(true);
		enterpriseAgreement.getElement().setPropertyString("placeholder", "Escriba el nombre del convenio... (Ctrl + espacio para ver sugerencias)");
		
		enterpriseAgreement.getValueBox().addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				enterpriseAgreement.setText("");
				enterpriseAgreement.showSuggestionList();
			} else if(e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				enterpriseAgreement.hideSuggestionList();
		});
	}
	
	public Label createEmptyListLabel() {
		Label label = new Label();
		
		label.setText("No hay entradas disponibles");
		label.setStyleName("aon-inputText");
		label.addStyleName(style.warningColor());
		label.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		
		return label;
	}
	
	// ------------------------------------------------- checkPaysheetSendType
	
	public void checkPaysheetSendType(String email) {
		String paysheetSendType = String.valueOf(enterprisePaysheetSendType.getSelectedValue());
		
		if(AonStringUtils.isNotBlank(paysheetSendType) && AonStringUtils.equals(paysheetSendType, "EMAIL")) {
			enterprisePaysheetSendPanel.getElement().getStyle().clearDisplay();
			enterprisePaysheetSendEmail.setValue(email);
			enterprisePaysheetSendType.setWidth("115px");
		}else {
			enterprisePaysheetSendPanel.getElement().getStyle().setDisplay(Display.NONE);
			enterprisePaysheetSendType.setWidth("100%");
		}	
	}
	
	// ------------------------------------------------- Warning Styles
	
	private void addWarning(Widget widget) {
		widget.addStyleName(style.warningTB());
	}
	
	private void removeWarning(Widget widget) {
		widget.removeStyleName(style.warningTB());
	}


	private void disableDeprecatedPaysheetModels() {
	    NodeList<OptionElement> enterprisePaysheetOptions = ((SelectElement)this.enterprisePaysheetModel.getElement().cast()).getOptions();
	    for (int i = 0; i < enterprisePaysheetOptions.getLength(); i++ ) {
	        OptionElement enterprisePaysheetOption =  enterprisePaysheetOptions.getItem(i);
	        if ( !AonStringUtils.startsWith(enterprisePaysheetOption.getValue(), "salary_connorMacleod") ) {
		        enterprisePaysheetOption.setDisabled(true);
	        }
	    }
	}
}
