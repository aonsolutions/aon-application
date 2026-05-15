package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Municipalities;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.MediaType;
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

public class AonCustomerPanel extends HTMLPanel {
	
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
	
	private AonCustomerPanelCallback callback;
	private CustomerFull customerFull;
	private CreditorFull creditorFull;
	private SupplierFull supplierFull;
	
	private LinkedList<Scope> scopes;
	private LinkedList<GeoZone> geozones;
	private Municipalities municipalities = new Municipalities();
	
	// Wrokplace Info
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomTextBox alias = new AonCustomTextBox(AON.MSG.alias());
	private AonCustomListBox documentNationality = new AonCustomListBox("Pais");
	private AonCustomListBox documentType = new AonCustomListBox("Tipo");
	private AonCustomTextBox document = new AonCustomTextBox("Documento");
	private AonCustomTextBox name = new AonCustomTextBox(AON.MSG.enterpriseName());
	private AonCustomTextBox firstSurname = new AonCustomTextBox("Apellido");
	private AonCustomTextBox secondSurname = new AonCustomTextBox("Apellido 2");
	
	private AonCustomListBox statusLB = new AonCustomListBox("Estado");
	private AonCustomListBox scopeLB = new AonCustomListBox("Ambito");
	private AonCustomListBox transactionLB = new AonCustomListBox("T. Transacci\u00f3n");
	
	private AonCustomTextArea observation = new AonCustomTextArea("Observaciones");
	
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomTextBox addressTB = new AonCustomTextBox("Direcci\u00f3n");
	private AonCustomTextBox number = new AonCustomTextBox("N\u00b0");
	
	private AonCustomTextBox address2TB = new AonCustomTextBox("Rest. Direcci\u00f3n");
	private AonCustomTextBox address3TB = new AonCustomTextBox("Rest. Direcci\u00f3n 2");
	
	private AonCustomTextBox zip = new AonCustomTextBox("C.P.");
	private AonCustomListBox province = new AonCustomListBox("Provincia");
	private AonCustomListBox municipality = new AonCustomListBox("Municipion");
	private AonCustomTextBox city = new AonCustomTextBox("Localidad");
	
	private AonCustomTextBox phoneValue = new AonCustomTextBox("Telefono");
	private AonCustomTextBox phoneComment = new AonCustomTextBox("Comentarios");
	private AonCustomToogleButton phoneAdmin = new AonCustomToogleButton("Administrativo");
	private AonCustomToogleButton phoneCommercial = new AonCustomToogleButton("Comercial");
	private AonCustomToogleButton phoneTecnical = new AonCustomToogleButton("Tecnico");
	
	private AonCustomTextBox mobileValue = new AonCustomTextBox("M\u00f3vil");
	private AonCustomTextBox mobileComment = new AonCustomTextBox("Comentarios");
	private AonCustomToogleButton mobileAdmin = new AonCustomToogleButton("Administrativo");
	private AonCustomToogleButton mobileCommercial = new AonCustomToogleButton("Comercial");
	private AonCustomToogleButton mobileTecnical = new AonCustomToogleButton("Tecnico");
	
	private AonCustomTextBox emailValue = new AonCustomTextBox("Email");
	private AonCustomTextBox emailComment = new AonCustomTextBox("Comentarios");
	private AonCustomToogleButton emailAdmin = new AonCustomToogleButton("Administrativo");
	private AonCustomToogleButton emailCommercial = new AonCustomToogleButton("Comercial");
	private AonCustomToogleButton emailTecnical = new AonCustomToogleButton("Tecnico");
	
	// Constructor
	
	public AonCustomerPanel(String domainName, Integer domain, String user, CustomerFull customerFull, LinkedList<Scope> scopes, LinkedList<GeoZone> geozones, AonCustomerPanelCallback callback) {
		super(EMPTY_STRING);
		
		initializeCommonService();
		initializeRegistryService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.customerFull = customerFull;
		this.scopes = scopes;
		this.geozones = geozones;

		this.callback = callback;
		
		show();
	}
	
	public AonCustomerPanel(String domainName, Integer domain, String user, CreditorFull creditorFull, LinkedList<Scope> scopes, LinkedList<GeoZone> geozones, AonCustomerPanelCallback callback) {
		super(EMPTY_STRING);
		
		initializeCommonService();
		initializeRegistryService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.creditorFull = creditorFull;
		this.scopes = scopes;
		this.geozones = geozones;

		this.callback = callback;
		
		show();
	}
	
	public AonCustomerPanel(String domainName, Integer domain, String user, SupplierFull supplierFull, LinkedList<Scope> scopes, LinkedList<GeoZone> geozones, AonCustomerPanelCallback callback) {
		super(EMPTY_STRING);
		
		initializeCommonService();
		initializeRegistryService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.supplierFull = supplierFull;
		this.scopes = scopes;
		this.geozones = geozones;

		this.callback = callback;
		
		show();
	}
	
	public void show() {
		// Message Panel
		setStyleName(AON.CSS.aonFlexColumn2());
		getElement().getStyle().setProperty("padding", "1rem 0");
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
			documentType.addItem(DocumentType.values()[i].getDescription(), DocumentType.values()[i].toString());
		
		documentNationality.clearItems();
		for (int i = 0; i < Country.values().length; i++)
			documentNationality.addItem(Country.values()[i].getIso2(), Country.values()[i].getIso2());
		
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
		
		// Row 2
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		row2.add(alias);
		container.add(row2);
		
		// Row 3
		HTMLPanel row3 = new HTMLPanel(EMPTY_STRING);
		row3.setStyleName(AON.CSS.aonItemFlex());
		
		statusLB.clearItems();
		for(int i=0; i < RegistryStatus.values().length; i++)
			statusLB.addItem(RegistryStatus.values()[i].getDescription(), RegistryStatus.values()[i].name());
		
		scopeLB.clearItems();
		this.scopes.forEach(s -> scopeLB.addItem(s.getDescription(), s.getId().toString()));
		
		transactionLB.clearItems();
		for(int i=0; i < InvoiceTransactionType.values().length; i++)
			transactionLB.addItem(InvoiceTransactionType.values()[i].getDescription(), InvoiceTransactionType.values()[i].name());
			
		row3.add(statusLB);
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
	
		geozones.stream().filter(geozone -> geozone.getCode().length() == 2 && canBeCastToInt(geozone.getCode())).forEach(geozone -> province.addItem(capitalizeFirstLetterOfEachWord(geozone.getName()), geozone.getCode()));
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
		row8.add(phoneAdmin);
		row8.add(phoneCommercial);
		row8.add(phoneTecnical);
		container.add(row8);
		
		HTMLPanel row9 = new HTMLPanel(EMPTY_STRING);
		row9.setStyleName(AON.CSS.aonItemFlex());
		
		row9.add(mobileValue);
		row9.add(mobileComment);
		row9.add(mobileAdmin);
		row9.add(mobileCommercial);
		row9.add(mobileTecnical);
		container.add(row9);
		
		HTMLPanel row10 = new HTMLPanel(EMPTY_STRING);
		row10.setStyleName(AON.CSS.aonItemFlex());
		
		row10.add(emailValue);
		row10.add(emailComment);
		row10.add(emailAdmin);
		row10.add(emailCommercial);
		row10.add(emailTecnical);
		container.add(row10);
		
		// Check name by document
		checkNameByDocumentType();
		
		// Buttons
		container.add(createButtonsPanel());
		add(container);	
	}
	
	private void checkNameByDocumentType() {
		if(DocumentType.safeValueOf(documentType.getValue()).equals(DocumentType.CIF)) {
			firstSurname.getElement().getStyle().setDisplay(Display.NONE);
			secondSurname.getElement().getStyle().setDisplay(Display.NONE);
		} else {
			firstSurname.getElement().getStyle().clearDisplay();
			secondSurname.getElement().getStyle().clearDisplay();
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
    		
    		if(null != customerFull) saveCustomerFull(okButton);
    		else if(null != creditorFull) saveCreditorFull(okButton);
    		else if(null != supplierFull) saveSupplierFull(okButton);
    		
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
		
		customerFull.getRegistry().setStatus(RegistryStatus.safeValueOf(statusLB.getValue()));
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
					.setAdministrative(phoneAdmin.getValue())
					.setCommercial(phoneCommercial.getValue())
					.setTechnical(phoneTecnical.getValue())
					;
	
			customerFull.addMedia(phoneMedia);
		}
		
		if(AonStringUtils.isNotBlank(mobileValue.getValue())) {
			RegistryMedia mobileMedia = new RegistryMedia()
					.setDomain(customerFull.getDomain())
					.setRegistry(customerFull.getId())
					.setMedia(MediaType.CELLULAR)
					.setValue(mobileValue.getValue())
					.setComment(mobileComment.getValue())
					.setAdministrative(mobileAdmin.getValue())
					.setCommercial(mobileCommercial.getValue())
					.setTechnical(mobileTecnical.getValue())
					;
	
			customerFull.addMedia(mobileMedia);
		}
		
		if(AonStringUtils.isNotBlank(emailValue.getValue())) {
			RegistryMedia emailMedia = new RegistryMedia()
					.setDomain(customerFull.getDomain())
					.setRegistry(customerFull.getId())
					.setMedia(MediaType.EMAIL)
					.setValue(emailValue.getValue())
					.setComment(emailComment.getValue())
					.setAdministrative(emailAdmin.getValue())
					.setCommercial(emailCommercial.getValue())
					.setTechnical(emailTecnical.getValue())
					;
			
			customerFull.addMedia(emailMedia);
		}
		
		registryService.save(domainName, domainId, user, customerFull, new AsyncCallback<CustomerFull>() {
			
			@Override
			public void onSuccess(CustomerFull result) {
				callback.onAccept(result);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, error.getMessage());
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
		
		creditorFull.getRegistry().setStatus(RegistryStatus.safeValueOf(statusLB.getValue()));
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
					.setAdministrative(phoneAdmin.getValue())
					.setCommercial(phoneCommercial.getValue())
					.setTechnical(phoneTecnical.getValue())
					;
	
			creditorFull.addMedia(phoneMedia);
		}
		
		if(AonStringUtils.isNotBlank(mobileValue.getValue())) {
			RegistryMedia mobileMedia = new RegistryMedia()
					.setDomain(creditorFull.getDomain())
					.setRegistry(creditorFull.getId())
					.setMedia(MediaType.CELLULAR)
					.setValue(mobileValue.getValue())
					.setComment(mobileComment.getValue())
					.setAdministrative(mobileAdmin.getValue())
					.setCommercial(mobileCommercial.getValue())
					.setTechnical(mobileTecnical.getValue())
					;
	
			creditorFull.addMedia(mobileMedia);
		}
		
		if(AonStringUtils.isNotBlank(emailValue.getValue())) {
			RegistryMedia emailMedia = new RegistryMedia()
					.setDomain(creditorFull.getDomain())
					.setRegistry(creditorFull.getId())
					.setMedia(MediaType.EMAIL)
					.setValue(emailValue.getValue())
					.setComment(emailComment.getValue())
					.setAdministrative(emailAdmin.getValue())
					.setCommercial(emailCommercial.getValue())
					.setTechnical(emailTecnical.getValue())
					;
			
			creditorFull.addMedia(emailMedia);
		}
		
		registryService.save(domainName, domainId, user, creditorFull, new AsyncCallback<CreditorFull>() {
			
			@Override
			public void onSuccess(CreditorFull result) {
				callback.onAccept(result);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, error.getMessage());
				okButton.setEnabled(true);
			}
			
		});
	}

	private void saveSupplierFull(Button okButton) {
		Window.alert("Save Supplier");
		
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
		
		supplierFull.getRegistry().setStatus(RegistryStatus.safeValueOf(statusLB.getValue()));
		supplierFull.getRegistry().setScope(new Scope().setId(Integer.parseInt(scopeLB.getValue())));
		supplierFull.getRegistry().setTransaction(InvoiceTransactionType.safeValueOf(transactionLB.getValue()));
		
		Window.alert("Save Supplier 2");
		if(AonStringUtils.isNotBlank(observation.getValue()))
			supplierFull.getRegistry().setObservation(observation.getValue());
	
		Window.alert("Save Supplier 3");
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
		
		Window.alert("Save Supplier 4");
		if(AonStringUtils.isNotBlank(phoneValue.getValue())) {
			RegistryMedia phoneMedia = new RegistryMedia()
					.setDomain(supplierFull.getDomain())
					.setRegistry(supplierFull.getId())
					.setMedia(MediaType.FIXED_PHONE)
					.setValue(phoneValue.getValue())
					.setComment(phoneComment.getValue())
					.setAdministrative(phoneAdmin.getValue())
					.setCommercial(phoneCommercial.getValue())
					.setTechnical(phoneTecnical.getValue())
					;
	
			supplierFull.addMedia(phoneMedia);
		}
		
		Window.alert("Save Supplier 5");
		if(AonStringUtils.isNotBlank(mobileValue.getValue())) {
			RegistryMedia mobileMedia = new RegistryMedia()
					.setDomain(supplierFull.getDomain())
					.setRegistry(supplierFull.getId())
					.setMedia(MediaType.CELLULAR)
					.setValue(mobileValue.getValue())
					.setComment(mobileComment.getValue())
					.setAdministrative(mobileAdmin.getValue())
					.setCommercial(mobileCommercial.getValue())
					.setTechnical(mobileTecnical.getValue())
					;
	
			supplierFull.addMedia(mobileMedia);
		}
		
		Window.alert("Save Supplier 6");
		if(AonStringUtils.isNotBlank(emailValue.getValue())) {
			RegistryMedia emailMedia = new RegistryMedia()
					.setDomain(supplierFull.getDomain())
					.setRegistry(supplierFull.getId())
					.setMedia(MediaType.EMAIL)
					.setValue(emailValue.getValue())
					.setComment(emailComment.getValue())
					.setAdministrative(emailAdmin.getValue())
					.setCommercial(emailCommercial.getValue())
					.setTechnical(emailTecnical.getValue())
					;
			
			supplierFull.addMedia(emailMedia);
		}
		
		Window.alert("Save Supplier End");
		registryService.save(domainName, domainId, user, supplierFull, new AsyncCallback<SupplierFull>() {
			
			@Override
			public void onSuccess(SupplierFull result) {
				callback.onAccept(result);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, error.getMessage());
				okButton.setEnabled(true);
			}
			
		});
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

}
