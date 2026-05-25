package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Municipalities;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;

public class AonAddressPanel extends HTMLPanel {
	
	// Callback
	
	public static interface AonAddressPanelCallback {
		void onAccept(RegistryAddress address);
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
	
	// Variables
	
	private final static String EMPTY_STRING = "";
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private AonAddressPanelCallback callback;
	private RegistryAddress registryAddress;
	private LinkedList<GeoZone> aviableGeozones;
	private Municipalities municipalities = new Municipalities();
	
	// Address Info
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomTextBox addressTB = new AonCustomTextBox("Direcci\u00f3n");
	private AonCustomTextBox number = new AonCustomTextBox("N\u00b0");
	
	private AonCustomTextBox address2TB = new AonCustomTextBox("Rest. Direcci\u00f3n");
	private AonCustomTextBox address3TB = new AonCustomTextBox("Rest. Direcci\u00f3n 2");
	
	private AonCustomTextBox zip = new AonCustomTextBox("C.P.");
	private AonCustomListBox province = new AonCustomListBox("Provincia");
	private AonCustomListBox municipality = new AonCustomListBox("Municipio");
	private AonCustomTextBox city = new AonCustomTextBox("Localidad");
	
	// Constructor
	
	public AonAddressPanel(String domainName, Integer domain, String user, LinkedList<GeoZone> aviableGeozones, Integer registry, AonAddressPanelCallback callback) {
		super(EMPTY_STRING);
		this.registryAddress = new RegistryAddress().setDomain(domain).setRegistry(registry);
		aonAddressPanel(domainName, domain, user, aviableGeozones, callback);
	}
	
	public AonAddressPanel(String domainName, Integer domain, String user, LinkedList<GeoZone> aviableGeozones, RegistryAddress registryAddress, AonAddressPanelCallback callback) {
		super(EMPTY_STRING);
		this.registryAddress = registryAddress;
		aonAddressPanel(domainName, domain, user, aviableGeozones, callback);
	}
	
	private void aonAddressPanel(String domainName, Integer domain, String user, LinkedList<GeoZone> aviableGeozones, AonAddressPanelCallback callback) {
		initializeCommonService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.callback = callback;
		
		this.aviableGeozones = aviableGeozones;
		
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
		
		// First Row
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		row.setStyleName(AON.CSS.aonItemFlex());
		row.getElement().getStyle().setProperty("gap", "1rem");

		StreetType.getSpanishTypes().forEach(streetTypeValue -> type.addItem(capitalizeFirstLetterOfEachWord(streetTypeValue.getDescription()), streetTypeValue.getAeatCode()));
		type.setValue("CL");
		type.getElement().getStyle().setProperty("max-width", "6rem");
		number.getElement().getStyle().setProperty("max-width", "3rem");
		
		row.add(type);
		row.add(addressTB);
		row.add(number);
		container.add(row);
		
		// Second Row
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());
		row2.getElement().getStyle().setProperty("gap", "1rem");
		
		row2.add(address2TB);
		row2.add(address3TB);
		container.add(row2);
		
		// Second Row
		HTMLPanel row3 = new HTMLPanel(EMPTY_STRING);
		row3.setStyleName(AON.CSS.aonItemFlex());
		row3.getElement().getStyle().setProperty("gap", "1rem");
	
		aviableGeozones.stream().filter(geozone -> geozone.getCode().length() == 2 && canBeCastToInt(geozone.getCode())).forEach(geozone -> province.addItem(capitalizeFirstLetterOfEachWord(geozone.getName()), geozone.getCode()));
		zip.getTextBox().addValueChangeHandler(e -> {
			if(AonStringUtils.isNotBlank(zip.getValue()) && zip.getValue().length() >= 2) {
				province.setValue(AonStringUtils.substring(zip.getValue(), 0, 2));
				
				municipality.clearItems();
				municipality.addItem("-", "");
				municipalities.getMunicipalitiesByProvinceCode(province.getValue()).entrySet().forEach(entry -> municipality.addItem(entry.getValue(), entry.getKey()));
				
				if(AonStringUtils.isNotBlank( municipalities.getMunicipalityByZip(zip.getValue()) ))
					municipality.setValue(zip.getValue());
			}
		});
		zip.getElement().getStyle().setProperty("max-width", "6rem");
		province.addChangeHandler(e -> {
			municipality.clearItems();
			municipality.addItem("-", "");
			municipalities.getMunicipalitiesByProvinceCode(province.getValue()).entrySet().forEach(entry -> municipality.addItem(entry.getValue(), entry.getKey()));
			
			if(AonStringUtils.isNotBlank( municipalities.getMunicipalityByZip(zip.getValue()) ))
				municipality.setValue(zip.getValue());
		});
		
		row3.add(zip);
		row3.add(province);
		row3.add(municipality);
		row3.add(city);
		container.add(row3);
		
		if(registryAddress.getId() != null) {
			type.setValue(registryAddress.getStreetType().getAeatCode());
			addressTB.setValue(registryAddress.getAddress());
			address2TB.setValue(registryAddress.getAddress2());
			address3TB.setValue(registryAddress.getAddress3());
			number.setValue(registryAddress.getNumber());
			zip.setValue(registryAddress.getZip());
			province.setValue(registryAddress.getGeozoneCode());
			city.setValue(registryAddress.getCity());
			
			if(AonStringUtils.isNotBlank(registryAddress.getGeozoneCode())) {
				municipality.clearItems();
				municipality.addItem("-", "");
				municipalities.getMunicipalitiesByProvinceCode(registryAddress.getGeozoneCode()).entrySet().forEach(entry -> municipality.addItem(entry.getValue(), entry.getKey()));
				municipality.setValue(registryAddress.getMunicipalityCode());
			}
		}
		
		// Buttons
		container.add(createButtonsPanel());
		add(container);	
	}
	
	private HTMLPanel createButtonsPanel(){
		HTMLPanel buttonsPanel = new HTMLPanel(EMPTY_STRING);
		buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
		buttonsPanel.getElement().getStyle().setProperty("margin-top", "1rem");
    	
    	Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
			
			Optional<GeoZone> geozoneOpt = aviableGeozones.stream().filter(geozone -> geozone.getCode().length() == 2 && canBeCastToInt(geozone.getCode()) && province.getValue() == geozone.getCode()).findFirst();
			
			registryAddress
				.setMain(false)
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
			
			commonService.saveRegistryAddress(domainName, domainId, user, registryAddress, new AsyncCallback<RegistryAddress>() {
				
				@Override
				public void onSuccess(RegistryAddress result) {
					callback.onAccept(result);
				}
				
				@Override
				public void onFailure(Throwable caught) {}
				
			});
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

}
