package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.shared.Dni;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class Enterprise extends ScrollPanel {
	
	// -------------------------------------------------- UiFields
	
	private HTMLPanel content = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel gridPanel = new HTMLPanel(AonStringUtils.EMPTY);
	
	// General Info
	private AonCustomTextBox name = new AonCustomTextBox("Nombre");
	private AonCustomTextBox alias = new AonCustomTextBox("Alias");
	private AonCustomTextBox documentType = new AonCustomTextBox("Tipo");
	private AonCustomTextBox document = new AonCustomTextBox("Documento");
	private AonCustomSuggestBox documentCountry = new AonCustomSuggestBox("P. Emisi\u00f3n");
	
	private AonCustomListBox streetType = new AonCustomListBox("Tipo V\u00eda");
	private AonCustomTextBox address = new AonCustomTextBox("Direcci\u00f3n");
	private AonCustomTextBox addressNum = new AonCustomTextBox("N\u00famero");
	
	private AonCustomTextBox addressZip = new AonCustomTextBox("C\u00f3digo Postal");
	private AonCustomListBox addressProvince = new AonCustomListBox("Provincia");
	private AonCustomListBox addressMunicipality = new AonCustomListBox("Localidad");
	
	private AonCustomTextBox mobile = new AonCustomTextBox("M\u00f3vil");
	private AonCustomTextBox phone = new AonCustomTextBox("Tel\u00e9fono");
	private AonCustomTextBox email = new AonCustomTextBox("Email");
	
	private AonCustomTextBox web = new AonCustomTextBox("Web");
	private AonCustomListBox scope = new AonCustomListBox("Ambito");
	
	// Other Info
	private AonCustomListBox enterprisePaysheetModel = new AonCustomListBox("Mod. Recibo Salarial");
	private AonCustomListBox enterprisePaysheetSendType = new AonCustomListBox("Envio N\u00f3minas");
	private AonCustomTextBox payrollEmail = new AonCustomTextBox("Email Envio N\u00f3minas");
	private AonCustomSuggestBox agreement = new AonCustomSuggestBox("Convenio");
	
	// Sistema RED
	private AonCustomTextBox authKey = new AonCustomTextBox("Clave Autorizaci\u00f3n");
	private AonCustomListBox paySSMutual = new AonCustomListBox("Entidad Gestora Plan de Pensiones");
	
	// -------------------------------------------------- Variables
	
	private List<Agreement> enterpriseAgreements;
	private Municipalities municipalities = new Municipalities();

	// -------------------------------------------------- Constructor

	protected Enterprise() {
		initializeView();
	}
	
	// -------------------------------------------------- initializeView
	
	public void initializeView() {
		clear();
		content.clear();
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.add(messagePanel);
		
		initDocumentContry();
		initStreetType();
		initAddressProvince();
		initPaysheetModel();
		initPaysheetSendType();
		initPaySSMutual();
		
		initHandlers();
		
		initView();
		
		Scheduler.get().scheduleDeferred(() -> {
			setWidget(content);
		});
	}

	private void initDocumentContry() {
		List<Country> countries = new ArrayList<>(Arrays.asList(Country.values()));
		List<String> countriesSuggestions = countries.stream().map(country -> country.getName()).collect(Collectors.toList());
		MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) documentCountry.getSuggestBox().getSuggestOracle();
		orclNames.addAll(countriesSuggestions);
		
		documentCountry.setAutoSelectEnabled(true);
	}
	
	private void initStreetType() {
		streetType.clearItems();
		for(int i=0; i<StreetType.values().length; i++)
			if(null != StreetType.values()[i].getLanguage() && StreetType.values()[i].getLanguage().equals(AonLanguage.SPANISH))
				streetType.addItem(StreetType.values()[i].getDescription(), StreetType.values()[i].getIneCode());
		
	}
	
	private void initAddressProvince() {
		addressProvince.clearItems();
		addressProvince.addItem("-", "");
		for(int i=0; i<Province.values().length; i++)
			this.addressProvince.addItem(Province.values()[i].getName(), AonStringUtils.leftPad(i + "", 2, '0'));
		
	}
	
	private void initPaysheetModel() {
		enterprisePaysheetModel.clearItems();
		enterprisePaysheetModel.addItem("Est\u00E1ndar", "salary");
		enterprisePaysheetModel.addItem("Est\u00E1ndar (2 columnas)", "salary_dualColumn");
		enterprisePaysheetModel.addItem("Factura Simple", "salary_invoiceSimple");
		enterprisePaysheetModel.addItem("Factura (Agrupada CRA)", "salary_invoiceCraGroup");
//		enterprisePaysheetModel.addItem("Detallada (new)", "salary_connorMacleod");
//		enterprisePaysheetModel.addItem("Est\u00E1ndar (new)", "salary_connorMacleod_classic");
		
	}
	
	private void initPaysheetSendType() {
		enterprisePaysheetSendType.clearItems();
		enterprisePaysheetSendType.addItem("Email", "EMAIL");
		enterprisePaysheetSendType.addItem("Papel", "PAPER");
		enterprisePaysheetSendType.addItem("Otro", "OTHERS");
	}

	private void initPaySSMutual() {
		paySSMutual.clearItems();
		paySSMutual.addItem("-", "");
		paySSMutual.addItem("CNP PARTNERS SEGUROS Y REASEGUROS SA", "G0001");
		paySSMutual.addItem("ABANCA VIDA Y PENSIONES DE SEGUROS Y REASEGUROS S.A.U.", "G0002");
		paySSMutual.addItem("UNICORP VIDA, COMPA\u00d1IA DE SEGUROS Y REASEGUROS, S.A.", "G0003");
		paySSMutual.addItem("BANKINTER SEGUROS DE VIDA", "G0006");
		paySSMutual.addItem("SA NOSTRA COMPA\u00d1IA DE SEGUROS DE VIDA SA", "G0012");
		paySSMutual.addItem("VIDA-CAIXA SA DE SEGUROS Y REASEGUROS", "G0021");
		paySSMutual.addItem("GENERALI ESPA\u00d1A, S.A. DE SEGUROS Y REASEGUROS", "G0037");
		paySSMutual.addItem("CCM VIDA Y PENSIONES S.A. DE SEG.Y REAS", "G0048");
		paySSMutual.addItem("IBERCAJA PENSION,E.G.F.P. , S.A.U.", "G0079");
		paySSMutual.addItem("SANTANDER PENSIONES, S.A., E.G.F.P.", "G0080");
		paySSMutual.addItem("BBVA PENSIONES S.A. EGFP", "G0082");
		paySSMutual.addItem("BANSABADELL PENSIONES, E.G.F.P., S.A.", "G0085");
		paySSMutual.addItem("MEDIOLANUM PENSIONES, S.A., S.G.F.P.", "G0091");
		paySSMutual.addItem("GVC GAESCO PENSIONES,S.A. S.G.F.P.", "G0111");
		paySSMutual.addItem("MAPFRE VIDA PENSIONES,E.G.F.P.,S.A", "G0121");
		paySSMutual.addItem("LORETO MUTUA, MUTUALIDAD DE PREVISION SOCIAL", "G0124");
		paySSMutual.addItem("RGA RURAL PENSIONES S.A. EGFP", "G0131");
		paySSMutual.addItem("GESTION DE PREVISION Y PENSIONES EGFP S.A.", "G0133");
		paySSMutual.addItem("MUTUACTIVOS PENSIONES SGFP S.A.U", "G0135");
		paySSMutual.addItem("PREVISION SANITARIA NACIONAL, MUTUA DE SEGUROS A PRIMA FIJA", "G0148");
		paySSMutual.addItem("MERCHBANC, E.G.F.P., S.A.", "G0153");
		paySSMutual.addItem("FONDITEL PENSIONES E.G.F.P.,S.A.", "G0162");
		paySSMutual.addItem("AXA PENSIONES,S.A., E.G.F.P.", "G0177");
		paySSMutual.addItem("BESTINVER PENSIONES E.G.F.P. ,S.A", "G0179");
		paySSMutual.addItem("LIBERBANK PENSIONES SGFP SA", "G0180");
		paySSMutual.addItem("RENTA 4 PENSIONES, S.A., E.G.F.P.", "G0185");
		paySSMutual.addItem("DEUTSCHE ZURICH PENSIONES, ENTIDAD GESTO ", "G0187");
		paySSMutual.addItem("NATIONALE-NEDERLANDEN VIDA COMPA\u00d1IA DE SEGUROS Y REASEGUROS, S.A.E", "G0190");
		paySSMutual.addItem("TREA PENSIONES, E.G.F.P., S.AU.", "G0202");
		paySSMutual.addItem("SURNE MUTUA DE SEGUROS Y REASEGUROS A PRIMA FIJA", "G0211");
		paySSMutual.addItem("CAJAMARVIDA, S.A. DE SEGUROS Y REASEGUROS", "G0214");
		paySSMutual.addItem("CASER PENSIONES ENTIDAD GESTORA DE FONDOS DE PENSIONES SA", "G0219");
		paySSMutual.addItem("DUNAS CAPITAL PENSIONES, S.G.F.P., S.A.U.", "G0224");
		paySSMutual.addItem("CAJA INGENIEROS VIDA, COMPA\u00d1IA DE SEGUROS Y REASEGUROS, S.A.", "G0225");
		paySSMutual.addItem("AEGON ESPA\u00d1A, S.A. DE SEGUROS Y REASEGUROS", "G0230");
		paySSMutual.addItem("LIBERBANK VIDA Y PENSIONES, DE SEGUROS Y REASEGUROS, S.A.", "G0231");
		paySSMutual.addItem("KUTXABANK PENSIONES, E.G.F.P., S.A.U.", "G0234");
		paySSMutual.addItem("GCO GESTORA DE PENSIONES, EGFP, S.A.", "G0236");
		paySSMutual.addItem("UNION DEL DUERO, COMPA\u00d1IA DE SEGUROS DE VIDA, S.A.", "G0237");
		paySSMutual.addItem("COBAS PENSIONES SGFP SA", "G0238");
		paySSMutual.addItem("ALLIANZ, COMPA\u00d1IA DE SEGUROS Y REASEGUROS, S.A.", "G0239");
		paySSMutual.addItem("SANTA LUCIA, S.A. COMPA\u00d1IA DE SEGUROS Y REASEGUROS", "G0240");
		
		paySSMutual.addItem("MONTEPIO DEL IGUALATORIO, ENTIDAD DE PREVISION SOCIAL VOLUNTARIA DE EMPLEO", "00008");
		paySSMutual.addItem("ELKARKIDETZA EPSV DE EMPLEO PREFERENTE", "00053");
		paySSMutual.addItem("LAGUNARO, ENTIDAD DE PREVISION SOCIAL VOLUNTARIA DE EMPLEO PREFERENTE", "00069");
		paySSMutual.addItem("CAJA JUAN URRUTIA, EPSV DE EMPLEO", "00081");
		paySSMutual.addItem("BIHARKO, EPSV DE EMPLEO", "00087");
		paySSMutual.addItem("ENTIDAD DE PREVISION SOCIAL VOLUNTARIA DE EMPLEO ALEJANDRO ECHEVARRIA", "00088");
		paySSMutual.addItem("BIDEPENSION, EPSV DE LA MODALIDAD DE EMPLEO", "00101");
		paySSMutual.addItem("KUTXABANK EMPLEO, EPSV DE EMPLEO", "00105");
		paySSMutual.addItem("ETORPENSION, EPSV DE LA MODALIDAD DE EMPLEO", "00110");
		paySSMutual.addItem("NORPYME, EPSV DE EMPLEO", "00111");
		paySSMutual.addItem("ARABA ETA GASTEIZ AURREZKI KUTXA I EPSV DE EMPLEO PARA EL COLECTIVO DE BENEFICIARIOS POR PENSIONES CAUSADAS", "00121");
		paySSMutual.addItem("EPSV DE EMPLEO ARABA ETA GASTEIZ AURREZKI KUTXA II PARA EL COLECTIVO DE EMPLEADOS EN ACTIVO E INCORPORADOS ANTES DEL 1 DE ABRIL DE 1990", "00122");
		paySSMutual.addItem("EPSV DE EMPLEO ARABA ETA GASTEIZ AURREZKI KUTXA III PARA EL COLECTIVO DE EMPLEADOS DE LA CAJA DE AHORROS DE VITORIA Y ÁLAVA INGRESADOS DURANTE EL PERÍODO COMPRENDIDO ENTRE EL 01.04.1990 Y EL 25.10.96", "00123");
		paySSMutual.addItem("LANAUR BAT, ENTIDAD DE PREVISION SOCIAL VOLUNTARIA DE EMPLEO", "00124");
		paySSMutual.addItem("LANAUR BI, ENTIDAD DE PREVISIÓN SOCIAL VOLUNTARIA DE EMPLEO", "00125");
		paySSMutual.addItem("LANAUR HIRU, ENTIDAD DE PREVISION SOCIAL OLUNTARIA DE EMPLEO", "00126");
		paySSMutual.addItem("GRUPO DE EMPRESAS TUBACEX, ENTIDAD DE PREVISION SOCIAL VOLUNTARIA DE EMPLEO", "00127");
		paySSMutual.addItem("GAUZATU EPSV DE EMPLEO", "00129");
		paySSMutual.addItem("HAZIA-BBK EPSV DE EMPLEO", "00130");
		paySSMutual.addItem("ZAINTZA EPSV DE EMPLEO", "00132");
		paySSMutual.addItem("TUBOS REUNIDOS EPSV DE EMPLEO", "00133");
		paySSMutual.addItem("EPSV DE EMPLEO DEL COLECTIVO DE LOS ANTIGUOS TRABAJADORES DEL BANCO DE VITORIA", "00137");
		paySSMutual.addItem("E.P.S.V. GERTAKIZUN", "00138");
		paySSMutual.addItem("IZARPENSION, EPSV DE LA MODALIDAD DE EMPLEO", "00139");
		paySSMutual.addItem("GEROA PENTSIOAK EPSV DE EMPLEO PREFERENTE", "00178");
		paySSMutual.addItem("ENTIDAD DE PREVISIÓN SOCIAL VOLUNTARIA GARAIZ PREVISION DE EMPLEO", "00199");
		paySSMutual.addItem("PREVISION VIVIENDAS DE VIZCAYA GIZARTEA, EPSV DE EMPLEO", "00207");
		paySSMutual.addItem("AROGESTION AHORRO-JUBILACION, ENTIDAD DE PREVISION SOCIAL VOLUNTARIA DE EMPLEO", "00218");
		paySSMutual.addItem("EPSV DE EMPLEO ARABA ETA GASTEIZ AURREZKI KUTXA IV PARA EL COLECTIVO DE EMPLEADOS Y EMPLEADAS DE LA CAJA DE AHORROS DE VITORIA Y ÁLAVA INGRESADOS INGRESADOS/AS A PARTIR DEL 25 OCTUBRE DE 1996", "00222");
		paySSMutual.addItem("EPSV DE EMPLEO TRABAJADORES DE PRODUCTOS TUBULARES", "00226");
		paySSMutual.addItem("GENERALI EMPLEO, EPSV DE EMPLEO", "00234");
		paySSMutual.addItem("GEROCAIXA PYME, EPSV DE EMPLEO", "00241");
		paySSMutual.addItem("SVRNE LAN, EPSV DE EMPLEO", "00242");
		paySSMutual.addItem("RAUPEN-ARRETA, ENTIDAD DE PREVISION SOCIAL VOLUNTARIA DE EMPLEO", "00245");
		paySSMutual.addItem("SANTANDER PREVISION COLECTIVA, ENTIDAD DE PREVISION SOCIAL VOLUNTARIA DE EMPLEO", "00246");
		paySSMutual.addItem("GEROKOA LAN, EPSV DE EMPLEO", "00256");
		paySSMutual.addItem("ITZARRI, EPSV DE EMPLEO", "00260");
		paySSMutual.addItem("RURAL PENSION XXI EMPLEO, EPSV", "00281");
		paySSMutual.addItem("MAPFRE VIDA EMPLEO, EPSV", "00282");
		paySSMutual.addItem("BANSABADELL PREVISION EMPRESAS, EPSV DE EMPLEO", "00283");
		paySSMutual.addItem("ETORKIZUMA EPVS DE EMPLEO", "0285B");
	}

	private void initHandlers() {
		name.addValueChangeHandler(e -> {
			String nameValue = e.getValue();
			if(AonStringUtils.isBlank(nameValue)) {
				name.addError();
				showError("El campo nombre es obligatorio");
			} else {
				name.removeError();
				onEnterpriseNameChange(nameValue);
			}
		});
		
		alias.addValueChangeHandler(e -> onEnterpriseAliasChange(e.getValue()));
		document.addValueChangeHandler(e -> {
			checkDocument(true);
			onEnterpriseDocumentChange(e.getValue());
		});
		documentCountry.getSuggestBox().addSelectionHandler(e -> onEnterpriseNationalityChange(documentCountry.getValue()));
		
		streetType.addChangeHandler(e -> onEnterpriseStreetTypeChange(streetType.getValue()));
		address.addValueChangeHandler(e -> onEnterpriseAddressChange(e.getValue()));
		addressNum.addValueChangeHandler(e -> onEnterpriseAddressNumChange(e.getValue()));
		addressZip.addValueChangeHandler(e -> {
			updateProvince();
			updateMunicipalities();
			onEnterpriseAddressZipChange(e.getValue());
		});
		addressProvince.addChangeHandler(e -> {
			onEnterpriseAddressProvinceChange(addressProvince.getValue());
			updateMunicipalities();
		});
		addressMunicipality.addChangeHandler(e -> onEnterpriseAddressCityChange(addressMunicipality.getValue()));
		
		mobile.addValueChangeHandler(e -> onEnterpriseMobileChange(e.getValue()));
		phone.addValueChangeHandler(e -> onEnterprisePhoneChange(e.getValue()));
		email.addValueChangeHandler(e -> onEnterpriseEmailChange(e.getValue()));
		
		web.addValueChangeHandler(e -> onEnterpriseWebChange(e.getValue()));
		scope.addChangeHandler(e -> onEnterpriseScopeChange(AonStringUtils.isBlank(scope.getValue()) ? null : Integer.parseInt(scope.getValue())));
		
		enterprisePaysheetModel.addChangeHandler(e -> onEnterprisePaysheetModelChange(enterprisePaysheetModel.getValue()));
		enterprisePaysheetSendType.addChangeHandler(e -> {
			checkPaysheetSendType();
			onEnterprisePaysheetSendTypeChange(enterprisePaysheetSendType.getValue());
		});
		payrollEmail.addValueChangeHandler(e -> onEnterprisePaysheetSendEmailChange(e.getValue()));
		
		agreement.getSuggestBox().addValueChangeHandler(e -> {
			String agreementValue = e.getValue();
			if(AonStringUtils.isBlank(agreementValue))
				onEnterpriseAgreementChange(null);
			else if(AonStringUtils.containsIgnoreCase(agreementValue, " - "))
				for(Agreement agreement : this.enterpriseAgreements)
					if(agreement.getId().equals(Integer.parseInt(AonStringUtils.substringBefore(agreementValue, " -")))
							/*AonStringUtils.equalsIgnoreCase(agreement.getDescription(), agreementValue)*/)
						onEnterpriseAgreementChange(agreement.getId());	
		});
		
		authKey.addValueChangeHandler(e -> onnterprisePayAuthorizationKeyChange(authKey.getValue()));
		paySSMutual.addChangeHandler(e -> onEnterprisePaySsMutualChange(paySSMutual.getValue()));
	}
	
	private void initView() {
		gridPanel.clear();
		gridPanel.setStyleName(AON.CSS.aonGridTwoCols());
		gridPanel.getElement().getStyle().setProperty("padding", "0 1rem");
		
		AonCustomCard infoCard = new AonCustomCard("Informaci\u00f3n General");
		
		HTMLPanel tableInfo = createTable();
		
		documentType.setWidth("8rem");
		streetType.setWidth("8rem");
		addressZip.setWidth("8rem");
		
		tableInfo.add(createRow(name, alias, null));
		tableInfo.add(createRow(documentType, document, documentCountry));
		tableInfo.add(createRow(streetType, address, addressNum));
		tableInfo.add(createRow(addressZip, addressProvince, addressMunicipality));
		tableInfo.add(createRow(mobile, phone, email));
		tableInfo.add(createRow(web, scope, null));
		
		infoCard.add(tableInfo);
		gridPanel.add(infoCard);
		
		AonCustomCard otherDataCard = new AonCustomCard("Otros Datos");
		
		HTMLPanel tableOther = createTable();
		tableOther.setHeight("100%");
		
		enterprisePaysheetModel.setWidth("10em");
		enterprisePaysheetSendType.setWidth("10rem");
		
		tableOther.add(createRow(agreement, enterprisePaysheetModel, null));
		tableOther.add(createRow(enterprisePaysheetSendType, payrollEmail, null));
		
		otherDataCard.add(tableOther);
		gridPanel.add(otherDataCard);
		
		AonCustomCard sistemREDCard = new AonCustomCard("Sistema RED");
		
		HTMLPanel tableSistemRED = createTable();
		
		paySSMutual.getElement().getStyle().setProperty("max-width", "30rem");
		tableSistemRED.add(createRow(authKey, paySSMutual, null));
		
		sistemREDCard.add(tableSistemRED);
		gridPanel.add(sistemREDCard);
		
		content.remove(gridPanel);
		content.add(gridPanel);
	}
	
	private HTMLPanel createTable() {
		HTMLPanel table = new HTMLPanel("");
		table.setStyleName(AON.CSS.aonFlexColumn());
		return table;
	}
	
	private HTMLPanel createRow(Widget w1, Widget w2, Widget w3) {
		HTMLPanel panel = new HTMLPanel("");
		panel.setStyleName(AON.CSS.aonItemFlex());
		
		panel.add(w1);
		if(null != w2) panel.add(w2);
		if(null != w3) panel.add(w3);
		
		return panel;
	}
	

	// ------------------------------------------------- Abstract Methods
	
	// General Info
	public abstract void onEnterpriseNameChange(String name);
	public abstract void onEnterpriseAliasChange(String alias);
	public abstract void onEnterpriseDocumentChange(String document);
	public abstract void onEnterpriseNationalityChange(String nationality);
	public abstract void onEnterpriseStreetTypeChange(String streetType);
	public abstract void onEnterpriseAddressChange(String address);
	public abstract void onEnterpriseAddressNumChange(String number);
	public abstract void onEnterpriseAddressZipChange(String zip);
	public abstract void onEnterpriseAddressCityChange(String city);
	public abstract void onEnterpriseAddressProvinceChange(String province);
	public abstract void onEnterpriseMobileChange(String mobile);
	public abstract void onEnterprisePhoneChange(String phone);
	public abstract void onEnterpriseEmailChange(String email);
	public abstract void onEnterpriseWebChange(String web);
	
	// Other Info
	public abstract void onEnterprisePaysheetModelChange(String paysheetModel);
	public abstract void onEnterprisePaysheetSendTypeChange(String sendType);
	public abstract void onEnterprisePaysheetSendEmailChange(String sendEmail);
	public abstract void onEnterpriseAgreementChange(Integer agreementId);
	public abstract void onEnterpriseScopeChange(Integer scopeId);
	
	// Sistema RED
	public abstract void onEnterprisePaySsMutualChange(String paySSMutual);
	public abstract void onnterprisePayAuthorizationKeyChange(String authKey);
	
	// ------------------------------------------------- Auxiliar Methods	
	
	public void checkDocument(boolean fireMessage) {
		if(AonStringUtils.isNotBlank(document.getValue())) {
			String documentTypeValue = checkDocumentType();
		
			documentType.setValue(documentTypeValue);
			showNationality();
		
			if(checkDocumentValidation()) {
				document.addError();
				if(Boolean.TRUE.equals(fireMessage))
					showError("El documento no est\u00E1 definido o tiene un formato err\u00F3neo");
			}else {
				document.removeError();
			}
		}else {
			document.addError();
			if(Boolean.TRUE.equals(fireMessage))
				showError("El documento no est\u00E1 definido o tiene un formato err\u00F3neo");
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
		String documentTypeStr = checkDocumentType();
		documentCountry.setVisible(
				AonStringUtils.equals(documentTypeStr, "CIF") || 
				AonStringUtils.equals(documentTypeStr, "Pasaporte") || 
				AonStringUtils.equals(documentTypeStr, "NIE")
		);
		
		if(!documentCountry.isVisible()) documentCountry.setValue("ESPA\u00D1A");
	}
	
	private boolean checkDocumentValidation() {
		String value = document.getValue();
		String documentTypeStr = checkDocumentType();
		
		if(AonStringUtils.isBlank(value))
			return false;
		else if(AonStringUtils.equals(documentTypeStr, "DNI"))
			return !Dni.checkDNI(value);
		else if(AonStringUtils.equals(documentTypeStr, "NIE"))
			return !Dni.checkNIE(value);
		else
			return false;
	}
	
	// ------------------------------------------------- Initialize ListBoxes
	
	public void setScopes(Map<Integer, String> enterprisecopes) {
		scope.clearItems();
		scope.addItem("-", "");
		
		for(Entry<Integer, String> entry : enterprisecopes.entrySet())
			scope.addItem(entry.getValue(), entry.getKey().toString());
	}
	
	public void setAgreements(List<Agreement> enterpriseAgreementsDb) {
		enterpriseAgreements = enterpriseAgreementsDb;
		
		List<String> agreementSuggestions = new ArrayList<>();
		enterpriseAgreements.forEach(agreement -> agreementSuggestions.add(agreement.getDescription()));
		
		MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) agreement.getSuggestBox().getSuggestOracle();
		orclNames.addAll(agreementSuggestions);
		
		agreement.setAutoSelectEnabled(true);
		agreement.setPlaceHolder("Escriba el nombre del convenio... (Ctrl + espacio para ver sugerencias)");
		
		agreement.getSuggestBox().addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				agreement.setValue("");
				agreement.showSuggestionList();
			} else if(e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				agreement.hideSuggestionList();
		});
	}
	
	public void fillEnterprise(com.esferalia.aon.occam.api.model.payroll.Enterprise enterprise) {
		name.setValue(enterprise.getName());
		alias.setValue(enterprise.getAlias());
		documentType.setValue(null == enterprise.getDocumentType() ? null : enterprise.getDocumentType().getDescription());
		document.setValue(enterprise.getDocument());
		documentCountry.setValue(null == enterprise.getDocumentCountry() ? null : enterprise.getDocumentCountry().getName());
		checkDocument(false);
		
		if(null != enterprise.getAddress()){
			RegistryAddress addressObj = enterprise.getAddress();
			streetType.setValue(addressObj.getStreetType().getIneCode());
			address.setValue(addressObj.getAddress());
			addressNum.setValue(addressObj.getNumber());
			addressZip.setValue(addressObj.getZip());
			
			updateProvince();
			
			addressProvince.setValue(addressObj.getGeozoneCode());
			
			if(null != addressObj.getGeozoneCode()) {
				updateMunicipalities();
				addressMunicipality.setValue(addressObj.getMunicipalityCode());
			}
		}
		
		Optional<RegistryMedia> mobileOpt = enterprise.getMedias().stream().filter(f -> f.getMedia() == MediaType.CELLULAR).findFirst();
		mobile.setValue(mobileOpt.isEmpty() ? null : mobileOpt.get().getValue());
		
		Optional<RegistryMedia> phoneOpt = enterprise.getMedias().stream().filter(f -> f.getMedia() == MediaType.FIXED_PHONE).findFirst();
		phone.setValue(phoneOpt.isEmpty() ? null : phoneOpt.get().getValue());
		
		Optional<RegistryMedia> emailOpt = enterprise.getMedias().stream().filter(f -> f.getMedia() == MediaType.EMAIL).findFirst();
		email.setValue(emailOpt.isEmpty() ? null : emailOpt.get().getValue());
		
		Optional<RegistryMedia> webOpt = enterprise.getMedias().stream().filter(f -> f.getMedia() == MediaType.WEB).findFirst();
		web.setValue(webOpt.isEmpty() ? null : webOpt.get().getValue());
		
		scope.setValue(null == enterprise.getScope() ? null : enterprise.getScope().toString());
		
		Optional<EnterpriseData> paySheetModel = enterprise.getDatas().stream().filter(f -> f.getName().equals("PAY_REPORT_salary_PAY")).findFirst();
		enterprisePaysheetModel.setValue(paySheetModel.isEmpty() ? null : paySheetModel.get().getExpression());
		
		Optional<EnterpriseData> paysheetSend = enterprise.getDatas().stream().filter(f -> f.getName().equals("PAY_salarySendingMethod_PAY")).findFirst();
		enterprisePaysheetSendType.setValue(paysheetSend.isEmpty() ? null : paysheetSend.get().getExpression());
		
		Optional<EnterpriseData> paysheetSendEmail = enterprise.getDatas().stream().filter(f -> f.getName().equals("PAY_salarySending_email_PAY")).findFirst();
		payrollEmail.setValue(paysheetSendEmail.isEmpty() ? null : paysheetSendEmail.get().getExpression());
		
		Optional<EnterpriseData> agreementOpt = enterprise.getDatas().stream().filter(f -> f.getName().equals("agreement")).findFirst();
		if(!agreementOpt.isEmpty()) {
			Optional<Agreement> agreemntOpt = enterpriseAgreements.stream().filter(a -> a.getId().equals(Integer.parseInt(agreementOpt.get().getExpression()))).findFirst();
			agreement.setValue(agreemntOpt.isEmpty() ? null : (agreemntOpt.get().getId() + " - " + agreemntOpt.get().getDescription()));
		}
		
		Optional<EnterpriseData> payAuthorizationKey = enterprise.getDatas().stream().filter(f -> f.getName().equals("PAY_authorization_key_PAY")).findFirst();
		authKey.setValue(payAuthorizationKey.isEmpty() ? null : payAuthorizationKey.get().getExpression());
		
		Optional<EnterpriseData> paySsMutual = enterprise.getDatas().stream().filter(f -> f.getName().equals("PAY_ss_pension_plan_mutual_PAY")).findFirst();
		paySSMutual.setValue(paySsMutual.isEmpty() ? null : paySsMutual.get().getExpression());
		
	}
	
	private void updateProvince() {
		String zip = addressZip.getValue();
		if(AonStringUtils.isNotBlank(zip)) {
			String zipCode = zip.substring(0, 2);
			addressProvince.setValue(AonStringUtils.leftPad(zipCode, 2, '0'));
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), addressProvince);
		}
	}
	
	private void updateMunicipalities() {
		String provinceCode = addressProvince.getValue();
		addressMunicipality.clearItems();
		addressMunicipality.addItem("-", "");
		HashMap<String, String> municipalitiesOfProvince = municipalities.getMunicipalitiesByProvinceCode(provinceCode);
		municipalitiesOfProvince.entrySet().forEach(e -> addressMunicipality.addItem(e.getValue(), e.getKey()));
	}

	// ------------------------------------------------- checkPaysheetSendType
	
	public void checkPaysheetSendType() {
		String paysheetSendType = String.valueOf(enterprisePaysheetSendType.getValue());
		payrollEmail.setVisible(AonStringUtils.isNotBlank(paysheetSendType) && AonStringUtils.equals(paysheetSendType, "EMAIL")) ;
	}
	
	public void showError(String message) {
		AonMessagePanel.showError(messagePanel, message);
	}
	
	public void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}
	
	public void showSuccess(String message) {
		AonMessagePanel.showSuccess(messagePanel, message);
	}
	
}
