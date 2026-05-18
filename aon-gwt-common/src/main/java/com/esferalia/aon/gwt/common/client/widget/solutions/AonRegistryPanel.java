package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.BankSwift;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Iban;
import com.esferalia.aon.occam.api.model.Municipalities;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonRegistryPanel extends HTMLPanel {
	
	// Callback
	
	public static interface AonCustomerPanelCallback {
		void onAccept(CustomerFull customerFull);
		void onAccept(CreditorFull creditorFull);
		void onAccept(SupplierFull SupplierFull);
		void onCancel();
	}

	// CommonService
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// CommonService
	
	static RegistryServiceAsync registryService;
	
	private static void initializeRegistryService() {
		if (registryService == null) {
			RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
			registryService = new RegistryServiceAsyncDecorator(registryServiceRaw);
		}
	}
	
	// Variables
	
	private final static String EMPTY_STRING = "";
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private RegistrySource registrySource;
	
	private AonCustomerPanelCallback callback;
	private CustomerFull customerFull;
	private CreditorFull creditorFull;
	private SupplierFull supplierFull;
	
	private LinkedList<Scope> scopes;
	private LinkedList<GeoZone> geozones;
	private LinkedList<PayMethod> paymethods;
	private LinkedList<RegistryBank> comapnyBanks = new LinkedList<RegistryBank>();
	private Municipalities municipalities = new Municipalities();
	
	// Wrokplace Info
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomListBox documentNationality = new AonCustomListBox("Pa\u00eds");
	private AonCustomListBox documentType = new AonCustomListBox("Tipo Doc.");
	private AonCustomTextBox document = new AonCustomTextBox("N\u00ba Documento");
	private AonCustomTextBox name = new AonCustomTextBox(AON.MSG.enterpriseName());
	private AonCustomTextBox firstSurname = new AonCustomTextBox("Apellido");
	private AonCustomTextBox secondSurname = new AonCustomTextBox("Apellido 2");
	
	private AonCustomTextBox alias = new AonCustomTextBox(AON.MSG.alias());
	private AonCustomListBox scopeLB = new AonCustomListBox("\u00c1mbito");
	private AonCustomListBox transactionLB = new AonCustomListBox("Tipo Transacci\u00f3n");
	
	private AonCustomTextArea observation = new AonCustomTextArea("Observaciones");
	
	private AonCustomListBox type = new AonCustomListBox("Tipo V\u00eda");
	private AonCustomTextBox addressTB = new AonCustomTextBox("Direcci\u00f3n");
	private AonCustomTextBox number = new AonCustomTextBox("N\u00b0");
	
	private AonCustomTextBox address2TB = new AonCustomTextBox("Rest. Direcci\u00f3n");
	private AonCustomTextBox address3TB = new AonCustomTextBox("Rest. Direcci\u00f3n 2");
	
	private AonCustomTextBox zip = new AonCustomTextBox("C. Postal");
	private AonCustomListBox province = new AonCustomListBox("Provincia");
	private AonCustomListBox municipality = new AonCustomListBox("Municipio");
	private AonCustomTextBox city = new AonCustomTextBox("Localidad");
	
	private AonCustomTextBox phoneValue = new AonCustomTextBox("T\u00e9lefono");
	private AonCustomTextBox phoneComment = new AonCustomTextBox("Comentarios Tlf.");
	private AonCustomTextBox emailValue = new AonCustomTextBox("Email");
	private AonCustomTextBox emailComment = new AonCustomTextBox("Comentarios Email");
	
	private AonCustomListBox paymethod = new AonCustomListBox("Forma de pago");
	private AonCustomListBox banks = new AonCustomListBox("Cuenta Bancaria");
	private AonCustomTextBox iban = new AonCustomTextBox("IBAN");
	private AonCustomTextBox bic = new AonCustomTextBox("BIC");
	private AonCustomTextBox bankAlias = new AonCustomTextBox("Alias");
	
	
	// Constructor
	
	public AonRegistryPanel(String domainName, Integer domain, String user, CustomerFull customerFull, LinkedList<Scope> scopes, LinkedList<GeoZone> geozones, LinkedList<PayMethod> paymethods, AonCustomerPanelCallback callback) {
		super(EMPTY_STRING);
		
		initializeCommonService();
		initializeRegistryService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.registrySource = RegistrySource.CUSTOMER;
		
		this.customerFull = customerFull;
		this.scopes = scopes;
		this.geozones = geozones;
		this.paymethods = paymethods;

		this.callback = callback;
		
		show();
	}
	
	public AonRegistryPanel(String domainName, Integer domain, String user, CreditorFull creditorFull, LinkedList<Scope> scopes, LinkedList<GeoZone> geozones, LinkedList<PayMethod> paymethods, AonCustomerPanelCallback callback) {
		super(EMPTY_STRING);
		
		initializeCommonService();
		initializeRegistryService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.registrySource = RegistrySource.CREDITOR;
		
		this.creditorFull = creditorFull;
		this.scopes = scopes;
		this.geozones = geozones;
		this.paymethods = paymethods;

		this.callback = callback;
		
		show();
	}
	
	public AonRegistryPanel(String domainName, Integer domain, String user, SupplierFull supplierFull, LinkedList<Scope> scopes, LinkedList<GeoZone> geozones,  LinkedList<PayMethod> paymethods, AonCustomerPanelCallback callback) {
		super(EMPTY_STRING);
		
		initializeCommonService();
		initializeRegistryService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.registrySource = RegistrySource.SUPPLIER;
		
		this.supplierFull = supplierFull;
		this.scopes = scopes;
		this.geozones = geozones;
		this.paymethods = paymethods;

		this.callback = callback;
		
		show();
	}
	
	public void show() {
		// Message Panel
		setStyleName(AON.CSS.aonFlexColumn2());
		getElement().getStyle().setProperty("padding", "1rem 0");
		getElement().getStyle().setProperty("width", "850px");
		add(messagePanel);
		
		HTMLPanel container = new HTMLPanel(EMPTY_STRING);
		container.setStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		container.getElement().getStyle().setProperty("min-width", "25rem");
		
		// Row 1
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		row.setStyleName(AON.CSS.aonItemFlex());
		
		documentType.clearItems();
		for (int i = 0; i < DocumentType.values().length; i++)
			documentType.addItem(DocumentType.values()[i].getDescription(), DocumentType.values()[i].name());
		documentType.setValue(DocumentType.CIF.name());
		
		documentNationality.clearItems();
		for (int i = 0; i < Country.values().length; i++)
			documentNationality.addItem(Country.values()[i].getIso2(), Country.values()[i].getIso2());
		documentNationality.setValue(Country.ES.getIso2());
		
		documentType.getElement().getStyle().setProperty("max-width", "5rem");
		documentNationality.getElement().getStyle().setProperty("max-width", "4rem");
		document.getElement().getStyle().setProperty("max-width", "7rem");
		
		documentType.addChangeHandler(e -> checkNameByDocumentType());
		
		row.add(documentType);
		row.add(documentNationality);
		row.add(document);
		row.add(name);
		row.add(firstSurname);
		row.add(secondSurname);
		container.add(row);
		
		// Row 3
		HTMLPanel row3 = new HTMLPanel(EMPTY_STRING);
		row3.setStyleName(AON.CSS.aonItemFlex());
		
		scopeLB.clearItems();
		this.scopes.forEach(s -> scopeLB.addItem(s.getDescription(), s.getId().toString()));
		
		transactionLB.clearItems();
		for(int i=0; i < InvoiceTransactionType.values().length; i++)
			transactionLB.addItem(InvoiceTransactionType.values()[i].getDescription(), InvoiceTransactionType.values()[i].name());
			
		row3.add(alias);
		row3.add(scopeLB);
		row3.add(transactionLB);
		container.add(row3);
		
		// Row 4
		HTMLPanel row4 = new HTMLPanel(EMPTY_STRING);
		row4.setStyleName(AON.CSS.aonItemFlex());
		
		observation.getTextBox().setVisibleLines(3);
		
		row4.add(observation);
		container.add(row4);
		
		// First Row
		HTMLPanel row5 = new HTMLPanel(EMPTY_STRING);
		row5.setStyleName(AON.CSS.aonItemFlex());

		StreetType.getSpanishTypes().forEach(streetTypeValue -> type.addItem(capitalizeFirstLetterOfEachWord(streetTypeValue.getDescription()), streetTypeValue.getAeatCode()));
		type.setValue("CL");
		type.getElement().getStyle().setProperty("max-width", "6rem");
		number.getElement().getStyle().setProperty("max-width", "3rem");
		
		row5.add(type);
		row5.add(addressTB);
		row5.add(number);
		container.add(row5);
		
		// Second Row
		HTMLPanel row6 = new HTMLPanel(EMPTY_STRING);
		row6.setStyleName(AON.CSS.aonItemFlex());
		
		row6.add(address2TB);
		row6.add(address3TB);
		container.add(row6);
		
		// Second Row
		HTMLPanel row7 = new HTMLPanel(EMPTY_STRING);
		row7.setStyleName(AON.CSS.aonItemFlex());
	
		initializeGeozones();
		zip.getTextBox().addValueChangeHandler(e -> {
			if(AonStringUtils.isNotBlank(zip.getValue()) && zip.getValue().length() >= 2) {
				province.setValue(AonStringUtils.substring(zip.getValue(), 0, 2));
				
				municipality.clearItems();
				municipalities.getMunicipalitiesByProvinceCode(province.getValue()).entrySet().forEach(entry -> municipality.addItem(entry.getValue(), entry.getKey()));
				
				if(AonStringUtils.isNotBlank( municipalities.getMunicipalityByZip(zip.getValue()) ))
					municipality.setValue(zip.getValue());
			}
		});
		zip.getElement().getStyle().setProperty("max-width", "6rem");
		province.addChangeHandler(e -> {
			municipality.clearItems();
			municipalities.getMunicipalitiesByProvinceCode(province.getValue()).entrySet().forEach(entry -> municipality.addItem(entry.getValue(), entry.getKey()));
			
			if(AonStringUtils.isNotBlank( municipalities.getMunicipalityByZip(zip.getValue()) ))
				municipality.setValue(zip.getValue());
		});
		
		row7.add(zip);
		row7.add(province);
		row7.add(municipality);
		row7.add(city);
		container.add(row7);
		
		HTMLPanel row8 = new HTMLPanel(EMPTY_STRING);
		row8.setStyleName(AON.CSS.aonItemFlex());
		
		row8.add(phoneValue);
		row8.add(phoneComment);
		row8.add(emailValue);
		row8.add(emailComment);
		container.add(row8);
		
		HTMLPanel row9 = new HTMLPanel(EMPTY_STRING);
		row9.setStyleName(AON.CSS.aonItemFlex());
		
		paymethod.clearItems();
		paymethod.addItem("-" , "");
		this.paymethods.sort(
			    Comparator
			        .comparing((PayMethod pm) -> pm.getType() == PayMethodType.NEGOTIABLE_DOCUMENT ? 0 : 1)
			        .thenComparing(pm -> pm.getType().name())
			);

		this.paymethods.forEach(pm -> paymethod.addItem(pm.getName(), pm.getId().toString()));
		paymethod.getElement().getStyle().setProperty("max-width", "12rem");
		
		paymethod.addChangeHandler(e -> checkPayMethodBankType() );
		
		banks.clearItems();
		banks.addItem("-" , "");
		
		iban.addValueChangeHandler(e -> {
			String accountStr = this.iban.getValue();
			accountStr = accountStr.replaceAll("\\W+", "");
			accountStr = accountStr.toUpperCase();
			iban.setValue(accountStr);

			if (accountStr.length() > 0 && !Iban.validateIBAN(accountStr))
				AonMessagePanel.showError(messagePanel, "IBAN no valido");
		
			String bankAliasValue = getBankAlias(accountStr);
			if(AonStringUtils.isNotBlank(bankAliasValue))
				bankAlias.setValue(bankAliasValue);
			
			String bankSwift = getBankSwift(accountStr);
			if(AonStringUtils.isNotBlank(bankSwift))
				bic.setValue(bankSwift);
		});
		
		iban.getElement().getStyle().setDisplay(Display.NONE);
		bic.getElement().getStyle().setDisplay(Display.NONE);
		bankAlias.getElement().getStyle().setDisplay(Display.NONE);
		banks.getElement().getStyle().clearDisplay();
		
		bic.getElement().getStyle().setProperty("max-width", "9rem");
		
		row9.add(paymethod);
		row9.add(banks);
		row9.add(iban);
		row9.add(bic);
		row9.add(bankAlias);
		container.add(row9);
		
		// Check name by document
		checkNameByDocumentType();
		
		// Remove placeholders
		removePlaceHolders();
		
		// Buttons
		container.add(createButtonsPanel());
		add(container);	
	}

	private void initializeGeozones() {
		province.addItem("-", "");
		province.addItem("Pais", "");

		province.getElement()
		        .getElementsByTagName("option")
		        .getItem(province.getListBox().getItemCount() - 1)
		        .setAttribute("disabled", "disabled");

		boolean provinceAdded = false;

		for (GeoZone geozone : geozones) {

		    if (geozone.getCode().length() == 2 && canBeCastToInt(geozone.getCode())) {

		        if (!provinceAdded && "01".equals(geozone.getCode())) {

		            province.addItem("Provincia", "");

		            province.getElement()
		                    .getElementsByTagName("option")
		                    .getItem(province.getListBox().getItemCount() - 1)
		                    .setAttribute("disabled", "disabled");

		            provinceAdded = true;
		        }

		        province.addItem(
		            capitalizeFirstLetterOfEachWord(geozone.getName()),
		            geozone.getCode()
		        );
		    }
		}
	}

	private void checkPayMethodBankType() {
		if(AonStringUtils.isBlank(paymethod.getValue())) return;
		
		PayMethod paymethodObj = this.paymethods.stream().filter(pm -> pm.getId().equals(Integer.parseInt(paymethod.getValue()))).findFirst().orElse(null);
		if(null != paymethodObj) {
			if(this.registrySource == RegistrySource.CUSTOMER) {
				if(paymethodObj.getType().equals(PayMethodType.BANK_TRANSFER)) {
					// Bancos de la empresa
					getCompanyBanks(end -> {
						banks.clearItems();
						comapnyBanks.forEach(b -> banks.addItem(b.getFullName(), b.getId().toString()));
				
						iban.getElement().getStyle().setDisplay(Display.NONE);
						bic.getElement().getStyle().setDisplay(Display.NONE);
						bankAlias.getElement().getStyle().setDisplay(Display.NONE);
						banks.getElement().getStyle().clearDisplay();
					});
				} else if(paymethodObj.getType().equals(PayMethodType.NEGOTIABLE_DOCUMENT)) {
					// Nuevo banco del cliente
					banks.getElement().getStyle().setDisplay(Display.NONE);
					iban.getElement().getStyle().clearDisplay();
					bic.getElement().getStyle().clearDisplay();
					bankAlias.getElement().getStyle().clearDisplay();
				} 
			} else {
				if(paymethodObj.getType().equals(PayMethodType.BANK_TRANSFER)) {
					// Nuevo banco del cliente
					banks.getElement().getStyle().setDisplay(Display.NONE);
					iban.getElement().getStyle().clearDisplay();
					bic.getElement().getStyle().clearDisplay();
					bankAlias.getElement().getStyle().clearDisplay();
					
				} else if(paymethodObj.getType().equals(PayMethodType.NEGOTIABLE_DOCUMENT)) {
					// Bancos de la empresa
					getCompanyBanks(end -> {
						banks.clearItems();
						comapnyBanks.forEach(b -> banks.addItem(b.getFullName(), b.getId().toString()));
				
						iban.getElement().getStyle().setDisplay(Display.NONE);
						bic.getElement().getStyle().setDisplay(Display.NONE);
						bankAlias.getElement().getStyle().setDisplay(Display.NONE);
						banks.getElement().getStyle().clearDisplay();
					});
				} 
			}
		}
		
	}
	
	private void removePlaceHolders() {
		document.getTextBox().getElement().setPropertyString("placeholder", "");
		name.getTextBox().getElement().setPropertyString("placeholder", "");
		firstSurname.getTextBox().getElement().setPropertyString("placeholder", "");
		secondSurname.getTextBox().getElement().setPropertyString("placeholder", "");
		alias.getTextBox().getElement().setPropertyString("placeholder", "");
		observation.getTextBox().getElement().setPropertyString("placeholder", "");
		addressTB.getTextBox().getElement().setPropertyString("placeholder", "");
		number.getTextBox().getElement().setPropertyString("placeholder", "");
		address2TB.getTextBox().getElement().setPropertyString("placeholder", "");
		address3TB.getTextBox().getElement().setPropertyString("placeholder", "");
		zip.getTextBox().getElement().setPropertyString("placeholder", "");
		city.getTextBox().getElement().setPropertyString("placeholder", "");
		phoneValue.getTextBox().getElement().setPropertyString("placeholder", "");
		phoneComment.getTextBox().getElement().setPropertyString("placeholder", "");
		emailValue.getTextBox().getElement().setPropertyString("placeholder", "");
		emailComment.getTextBox().getElement().setPropertyString("placeholder", "");
		iban.getTextBox().getElement().setPropertyString("placeholder", "");
		bic.getTextBox().getElement().setPropertyString("placeholder", "");
		bankAlias.getTextBox().getElement().setPropertyString("placeholder", "");
	}

	private void checkNameByDocumentType() {
		if(DocumentType.safeValueOf(documentType.getValue()).equals(DocumentType.CIF)) {
			firstSurname.getElement().getStyle().setDisplay(Display.NONE);
			secondSurname.getElement().getStyle().setDisplay(Display.NONE);
			name.setVisibleTitle(AON.MSG.enterpriseName());
		} else {
			firstSurname.getElement().getStyle().clearDisplay();
			secondSurname.getElement().getStyle().clearDisplay();
			name.setVisibleTitle("Nombre");
		}
	}
	
	private static String capitalizeFirstLetterOfEachWord(String str) {
		if(AonStringUtils.isBlank(str)) return str;
		
	    StringBuilder sb = new StringBuilder();
	    boolean capitalizeNext = true;

	    for (char c : str.toCharArray()) {
	        if (Character.isWhitespace(c)) {
	            capitalizeNext = true;
	        } else if (capitalizeNext) {
	            sb.append(Character.toUpperCase(c));
	            capitalizeNext = false;
	        } else {
	            sb.append(Character.toLowerCase(c));
	        }
	    }

	    return sb.toString();
	}

	private boolean canBeCastToInt(String str) {
	    try {
	        Integer.parseInt(str);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}

	private Widget createButtonsPanel() {
		HTMLPanel buttonsPanel = new HTMLPanel(EMPTY_STRING);
		buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
		buttonsPanel.getElement().getStyle().setProperty("margin-top", "1rem");
    	
    	Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
    		
    		if(this.registrySource == RegistrySource.CUSTOMER) saveCustomerFull(okButton);
    		else if(this.registrySource == RegistrySource.CREDITOR) saveCreditorFull(okButton);
    		else if(this.registrySource == RegistrySource.SUPPLIER) saveSupplierFull(okButton);
    		
    	});
    	buttonsPanel.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addClickHandler(e -> {
    		cancelButton.setEnabled(false);
			callback.onCancel();
    	});
    	buttonsPanel.add(cancelButton);
    	
    	return buttonsPanel;
	}
	
	private void saveCustomerFull(Button okButton) {
		customerFull.getRegistry().setDocumentType(DocumentType.safeValueOf(documentType.getValue()));
		customerFull.getRegistry().setLegalPerson(customerFull.getRegistry().getDocumentType().equals(DocumentType.CIF));
		customerFull.getRegistry().setDocumentCountry(Country.safeValueOf(documentNationality.getValue()));
		customerFull.getRegistry().setDocument(document.getValue());
		customerFull.getRegistry().setName(
				customerFull.getRegistry().getDocumentType().equals(DocumentType.CIF)
					? name.getValue()
					: ensureRegistryNameByPerson(name.getValue(), firstSurname.getValue(), secondSurname.getValue())
		);
		
		customerFull.getRegistry().setAlias(alias.getValue());
		customerFull.getRegistry().setStatus(RegistryStatus.ACTIVE);
		customerFull.getRegistry().setScope(new Scope().setId(Integer.parseInt(scopeLB.getValue())));
		customerFull.getRegistry().setTransaction(InvoiceTransactionType.safeValueOf(transactionLB.getValue()));
		
		if(AonStringUtils.isNotBlank(observation.getValue()))
			customerFull.getRegistry().setObservation(observation.getValue());
	
		if(AonStringUtils.isNotBlank(addressTB.getValue())) {
			Optional<GeoZone> geozoneOpt = geozones.stream().filter(geozone -> geozone.getCode().length() == 2 && canBeCastToInt(geozone.getCode()) && province.getValue() == geozone.getCode()).findFirst();
			
			RegistryAddress registryAddress = new RegistryAddress()
				.setMain(true)
				.setStreetType(StreetType.getForAeatCode(type.getValue(), AonLanguage.SPANISH))
				.setAddress(addressTB.getValue())
				.setAddress2(address2TB.getValue())
				.setAddress3(address3TB.getValue())
				.setNumber(number.getValue())
				.setZip(zip.getValue())
				.setGeozone(geozoneOpt.isPresent() ? geozoneOpt.get().getId() : null)
				.setGeozoneCode(province.getValue())
				.setGeozoneName(geozoneOpt.isPresent() ? geozoneOpt.get().getName() : null)
				.setMunicipalityCode(municipality.getValue())
				.setCity(city.getValue())
				;
			
			customerFull.addAddress(registryAddress);
		}
		
		if(AonStringUtils.isNotBlank(phoneValue.getValue())) {
			RegistryMedia phoneMedia = new RegistryMedia()
					.setDomain(customerFull.getDomain())
					.setRegistry(customerFull.getId())
					.setMedia(MediaType.FIXED_PHONE)
					.setValue(phoneValue.getValue())
					.setComment(phoneComment.getValue())
					.setAdministrative(true)
					.setCommercial(true)
					.setTechnical(true)
					;
	
			customerFull.addMedia(phoneMedia);
		}
		
		if(AonStringUtils.isNotBlank(emailValue.getValue())) {
			RegistryMedia emailMedia = new RegistryMedia()
					.setDomain(customerFull.getDomain())
					.setRegistry(customerFull.getId())
					.setMedia(MediaType.EMAIL)
					.setValue(emailValue.getValue())
					.setComment(emailComment.getValue())
					.setAdministrative(true)
					.setCommercial(true)
					.setTechnical(true)
					;
			
			customerFull.addMedia(emailMedia);
		}
		
		if(!AonStringUtils.isBlank(iban.getValue())) {
			RegistryBank registryBank = new RegistryBank()
				.setDomain(customerFull.getDomain())
				.setRegistry(customerFull.getId())
				.setAlias(bankAlias.getValue())
				.setActive(true)
				.setBankAccount(AonStringUtils.isBlank(iban.getValue()) ? null : new BankAccount(iban.getValue()))
				.setBic(bic.getValue())
				;
			
			customerFull.addBank(registryBank);
		}
		
		AonMessagePanel.showLoading(messagePanel, "Guardando informaci\u00f3n...");
		
		registryService.save(domainName, domainId, user, customerFull, new AsyncCallback<CustomerFull>() {
			
			@Override
			public void onSuccess(CustomerFull result) {
				if(AonStringUtils.isNotBlank(paymethod.getValue())) {
					
					RegistryBank selectedBank = null;
					if(!AonStringUtils.isBlank(iban.getValue())) {
						selectedBank = result.getBanks().isEmpty() ? null : result.getBanks().get(0);
					} else if(!AonStringUtils.isBlank(banks.getValue()) ) {
						selectedBank = comapnyBanks.stream().filter(b -> b.getId().equals(Integer.parseInt(banks.getValue()))).findFirst().orElse(null);
					}
					
					RegistryPayMethod rPayMethod = new RegistryPayMethod()
							.setDomain(result.getDomain())
							.setRegistry(result.getId())
							.setPayMethod(AonStringUtils.isBlank(paymethod.getValue()) ? null : paymethods.stream().filter(pm -> pm.getId().equals(Integer.parseInt(paymethod.getValue()))).findFirst().orElse(null))
							.setRbank(selectedBank)
							.setNumberOfPymnts((short)1)
							.setDaysToFirstPymnt((short)0)
							.setDaysBetwenPymnts((short)0)
							.setPymntDays("")
							;
							
					AonMessagePanel.showLoading(messagePanel, "Guardando forma de pago...");
					
					commonService.saveRegistryPayMethod(domainName, domainId, user, rPayMethod, new AsyncCallback<RegistryPayMethod>() {

						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error forma de pago : " + caught.getMessage());
							okButton.setEnabled(true);
						}

						@Override
						public void onSuccess(RegistryPayMethod registryPayMethod) {
							callback.onAccept(result);
						}
					});
				} else
					callback.onAccept(result);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, "Error cliente : " + error.getMessage());
				okButton.setEnabled(true);
			}
			
		});
	}

	private void saveCreditorFull(Button okButton) {
		creditorFull.getRegistry().setDocumentType(DocumentType.safeValueOf(documentType.getValue()));
		creditorFull.getRegistry().setLegalPerson(creditorFull.getRegistry().getDocumentType().equals(DocumentType.CIF));
		creditorFull.getRegistry().setDocumentCountry(Country.safeValueOf(documentNationality.getValue()));
		creditorFull.getRegistry().setDocument(document.getValue());
		creditorFull.getRegistry().setName(
				creditorFull.getRegistry().getDocumentType().equals(DocumentType.CIF)
					? name.getValue()
					: ensureRegistryNameByPerson(name.getValue(), firstSurname.getValue(), secondSurname.getValue())
		);
		
		creditorFull.getRegistry().setAlias(alias.getValue());
		creditorFull.getRegistry().setStatus(RegistryStatus.ACTIVE);
		creditorFull.getRegistry().setScope(new Scope().setId(Integer.parseInt(scopeLB.getValue())));
		creditorFull.getRegistry().setTransaction(InvoiceTransactionType.safeValueOf(transactionLB.getValue()));
		
		if(AonStringUtils.isNotBlank(observation.getValue()))
			creditorFull.getRegistry().setObservation(observation.getValue());
	
		if(AonStringUtils.isNotBlank(addressTB.getValue())) {
			Optional<GeoZone> geozoneOpt = geozones.stream().filter(geozone -> geozone.getCode().length() == 2 && canBeCastToInt(geozone.getCode()) && province.getValue() == geozone.getCode()).findFirst();
			
			RegistryAddress registryAddress = new RegistryAddress()
				.setMain(true)
				.setStreetType(StreetType.getForAeatCode(type.getValue(), AonLanguage.SPANISH))
				.setAddress(addressTB.getValue())
				.setAddress2(address2TB.getValue())
				.setAddress3(address3TB.getValue())
				.setNumber(number.getValue())
				.setZip(zip.getValue())
				.setGeozone(geozoneOpt.isPresent() ? geozoneOpt.get().getId() : null)
				.setGeozoneCode(province.getValue())
				.setGeozoneName(geozoneOpt.isPresent() ? geozoneOpt.get().getName() : null)
				.setMunicipalityCode(municipality.getValue())
				.setCity(city.getValue())
				;
			
			creditorFull.addAddress(registryAddress);
		}
			
		if(AonStringUtils.isNotBlank(phoneValue.getValue())) {
			RegistryMedia phoneMedia = new RegistryMedia()
					.setDomain(creditorFull.getDomain())
					.setRegistry(creditorFull.getId())
					.setMedia(MediaType.FIXED_PHONE)
					.setValue(phoneValue.getValue())
					.setComment(phoneComment.getValue())
					.setAdministrative(true)
					.setCommercial(true)
					.setTechnical(true)
					;
	
			creditorFull.addMedia(phoneMedia);
		}
		
		if(AonStringUtils.isNotBlank(emailValue.getValue())) {
			RegistryMedia emailMedia = new RegistryMedia()
					.setDomain(creditorFull.getDomain())
					.setRegistry(creditorFull.getId())
					.setMedia(MediaType.EMAIL)
					.setValue(emailValue.getValue())
					.setComment(emailComment.getValue())
					.setAdministrative(true)
					.setCommercial(true)
					.setTechnical(true)
					;
			
			creditorFull.addMedia(emailMedia);
		}
		
		if(!AonStringUtils.isBlank(iban.getValue())) {
			RegistryBank registryBank = new RegistryBank()
				.setDomain(creditorFull.getDomain())
				.setRegistry(creditorFull.getId())
				.setAlias(bankAlias.getValue())
				.setActive(true)
				.setBankAccount(AonStringUtils.isBlank(iban.getValue()) ? null : new BankAccount(iban.getValue()))
				.setBic(bic.getValue())
				;
			
			creditorFull.addBank(registryBank);
		}
		
		AonMessagePanel.showLoading(messagePanel, "Guardando informaci\u00f3n...");
		
		registryService.save(domainName, domainId, user, creditorFull, new AsyncCallback<CreditorFull>() {
			
			@Override
			public void onSuccess(CreditorFull result) {
				if(AonStringUtils.isNotBlank(paymethod.getValue())) {
					
					RegistryBank selectedBank = null;
					if(!AonStringUtils.isBlank(iban.getValue())) {
						selectedBank = result.getBanks().isEmpty() ? null : result.getBanks().get(0);
					} else if(!AonStringUtils.isBlank(banks.getValue()) ) {
						selectedBank = comapnyBanks.stream().filter(b -> b.getId().equals(Integer.parseInt(banks.getValue()))).findFirst().orElse(null);
					}
					
					RegistryPayMethod rPayMethod = new RegistryPayMethod()
							.setDomain(result.getDomain())
							.setRegistry(result.getId())
							.setPayMethod(AonStringUtils.isBlank(paymethod.getValue()) ? null : paymethods.stream().filter(pm -> pm.getId().equals(Integer.parseInt(paymethod.getValue()))).findFirst().orElse(null))
							.setRbank(selectedBank)
							.setNumberOfPymnts((short)1)
							.setDaysToFirstPymnt((short)0)
							.setDaysBetwenPymnts((short)0)
							.setPymntDays("")
							;
							
					AonMessagePanel.showLoading(messagePanel, "Guardando forma de pago...");
					
					commonService.saveRegistryPayMethod(domainName, domainId, user, rPayMethod, new AsyncCallback<RegistryPayMethod>() {

						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error forma de pago : " + caught.getMessage());
							okButton.setEnabled(true);
						}

						@Override
						public void onSuccess(RegistryPayMethod registryPayMethod) {
							callback.onAccept(result);
						}
					});
				} else
					callback.onAccept(result);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, "Error acreedor : " + error.getMessage());
				okButton.setEnabled(true);
			}
			
		});
	}

	private void saveSupplierFull(Button okButton) {
		supplierFull.getRegistry().setDocumentType(DocumentType.safeValueOf(documentType.getValue()));
		supplierFull.getRegistry().setLegalPerson(supplierFull.getRegistry().getDocumentType().equals(DocumentType.CIF));
		supplierFull.getRegistry().setDocumentCountry(Country.safeValueOf(documentNationality.getValue()));
		supplierFull.getRegistry().setDocument(document.getValue());
		supplierFull.getRegistry().setName(
				supplierFull.getRegistry().getDocumentType().equals(DocumentType.CIF)
					? name.getValue()
					: ensureRegistryNameByPerson(name.getValue(), firstSurname.getValue(), secondSurname.getValue())
		);
		
		supplierFull.getRegistry().setAlias(alias.getValue());
		supplierFull.getRegistry().setStatus(RegistryStatus.ACTIVE);
		supplierFull.getRegistry().setScope(new Scope().setId(Integer.parseInt(scopeLB.getValue())));
		supplierFull.getRegistry().setTransaction(InvoiceTransactionType.safeValueOf(transactionLB.getValue()));
		
		if(AonStringUtils.isNotBlank(observation.getValue()))
			supplierFull.getRegistry().setObservation(observation.getValue());
	
		if(AonStringUtils.isNotBlank(addressTB.getValue())) {
			Optional<GeoZone> geozoneOpt = geozones.stream().filter(geozone -> geozone.getCode().length() == 2 && canBeCastToInt(geozone.getCode()) && province.getValue() == geozone.getCode()).findFirst();
			
			RegistryAddress registryAddress = new RegistryAddress()
				.setMain(true)
				.setStreetType(StreetType.getForAeatCode(type.getValue(), AonLanguage.SPANISH))
				.setAddress(addressTB.getValue())
				.setAddress2(address2TB.getValue())
				.setAddress3(address3TB.getValue())
				.setNumber(number.getValue())
				.setZip(zip.getValue())
				.setGeozone(geozoneOpt.isPresent() ? geozoneOpt.get().getId() : null)
				.setGeozoneCode(province.getValue())
				.setGeozoneName(geozoneOpt.isPresent() ? geozoneOpt.get().getName() : null)
				.setMunicipalityCode(municipality.getValue())
				.setCity(city.getValue())
				;
			
			supplierFull.addAddress(registryAddress);
		}
		
		if(AonStringUtils.isNotBlank(phoneValue.getValue())) {
			RegistryMedia phoneMedia = new RegistryMedia()
					.setDomain(supplierFull.getDomain())
					.setRegistry(supplierFull.getId())
					.setMedia(MediaType.FIXED_PHONE)
					.setValue(phoneValue.getValue())
					.setComment(phoneComment.getValue())
					.setAdministrative(true)
					.setCommercial(true)
					.setTechnical(true)
					;
	
			supplierFull.addMedia(phoneMedia);
		}
		
		if(AonStringUtils.isNotBlank(emailValue.getValue())) {
			RegistryMedia emailMedia = new RegistryMedia()
					.setDomain(supplierFull.getDomain())
					.setRegistry(supplierFull.getId())
					.setMedia(MediaType.EMAIL)
					.setValue(emailValue.getValue())
					.setComment(emailComment.getValue())
					.setAdministrative(true)
					.setCommercial(true)
					.setTechnical(true)
					;
			
			supplierFull.addMedia(emailMedia);
		}
		
		Window.alert("iban.getValue() : " + iban.getValue());
		if(!AonStringUtils.isBlank(iban.getValue())) {
			Window.alert("Add bank to supplier");
			RegistryBank registryBank = new RegistryBank()
				.setDomain(supplierFull.getDomain())
				.setRegistry(supplierFull.getId())
				.setAlias(bankAlias.getValue())
				.setActive(true)
				.setBankAccount(AonStringUtils.isBlank(iban.getValue()) ? null : new BankAccount(iban.getValue()))
				.setBic(bic.getValue())
				;
			
			supplierFull.addBank(registryBank);
			
			Window.alert("Added bank");
			
			Window.alert("Banks size : " + supplierFull.getBanks().size());
		}
		
		AonMessagePanel.showLoading(messagePanel, "Guardando informaci\u00f3n...");
		
		registryService.save(domainName, domainId, user, supplierFull, new AsyncCallback<SupplierFull>() {
			
			@Override
			public void onSuccess(SupplierFull result) {
				if(AonStringUtils.isNotBlank(paymethod.getValue())) {
					
					RegistryBank selectedBank = null;
					if(!AonStringUtils.isBlank(iban.getValue())) {
						selectedBank = result.getBanks().isEmpty() ? null : result.getBanks().get(0);
					} else if(!AonStringUtils.isBlank(banks.getValue()) ) {
						selectedBank = comapnyBanks.stream().filter(b -> b.getId().equals(Integer.parseInt(banks.getValue()))).findFirst().orElse(null);
					}
					
					RegistryPayMethod rPayMethod = new RegistryPayMethod()
							.setDomain(result.getDomain())
							.setRegistry(result.getId())
							.setPayMethod(AonStringUtils.isBlank(paymethod.getValue()) ? null : paymethods.stream().filter(pm -> pm.getId().equals(Integer.parseInt(paymethod.getValue()))).findFirst().orElse(null))
							.setRbank(selectedBank)
							.setNumberOfPymnts((short)1)
							.setDaysToFirstPymnt((short)0)
							.setDaysBetwenPymnts((short)0)
							.setPymntDays("")
							;
							
					AonMessagePanel.showLoading(messagePanel, "Guardando forma de pago...");
					
					commonService.saveRegistryPayMethod(domainName, domainId, user, rPayMethod, new AsyncCallback<RegistryPayMethod>() {

						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error forma de pago : " + caught.getMessage());
							okButton.setEnabled(true);
						}

						@Override
						public void onSuccess(RegistryPayMethod registryPayMethod) {
							callback.onAccept(result);
						}
					});
				} else
					callback.onAccept(result);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, "Error proveedor : " + error.getMessage());
				okButton.setEnabled(true);
			}
			
		});
	}

	private String getBankSwift(String account) {
		if (AonStringUtils.isNotBlank(account)) {
			BankSwift bankSwiftEntry = BankSwift.safeValueOf("B" + AonStringUtils.substring(account, 4, 8));
			return null == bankSwiftEntry ? null : bankSwiftEntry.getSwift();
		}
		return null;
	}

	private String getBankAlias(String account) {
		if (AonStringUtils.isNotBlank(account)) {
			BankSwift bankSwiftEntry = BankSwift.safeValueOf("B" + AonStringUtils.substring(account, 4, 8));
			return null == bankSwiftEntry ? null : bankSwiftEntry.getBankName();
		}
		return null;
	}

	private String ensureRegistryNameByPerson(String name, String firstSurname, String secondSurname) {
		StringBuilder sb = new StringBuilder();

	    if (!AonStringUtils.isBlank(firstSurname)) {
	        sb.append(firstSurname.trim());
	    }

	    if (!AonStringUtils.isBlank(secondSurname)) {
	        if (sb.length() > 0) sb.append(" ");
	        sb.append(secondSurname.trim());
	    }

	    if (!AonStringUtils.isBlank(name)) {
	        if (sb.length() > 0) sb.append(", ");
	        sb.append(name.trim());
	    }

	    return sb.toString();
	}
	
	private void getCompanyBanks(Consumer<LinkedList<RegistryBank>> end) {
		if(comapnyBanks.isEmpty()) {
			commonService.getCompanyBanks(domainName, domainId, user, new AsyncCallback<LinkedList<RegistryBank>>() {
	
				@Override
				public void onFailure(Throwable caught) {
					AonMessagePanel.showError(messagePanel, "Error bancos: " + caught.getMessage());
				}
	
				@Override
				public void onSuccess(LinkedList<RegistryBank> rBanksDB) {
					comapnyBanks = rBanksDB.stream().filter(b -> b.isActive()).collect(Collectors.toCollection(LinkedList::new));
					end.accept(rBanksDB);
				}
			});
		} else end.accept(comapnyBanks);
	}

}
