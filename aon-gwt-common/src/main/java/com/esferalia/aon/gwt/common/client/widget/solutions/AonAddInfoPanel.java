package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

public class AonAddInfoPanel extends HTMLPanel {
	
	// Callback
	
	public static interface AonAddInfoPanelCallback {
		void onAccept(RegistryAddInfo rAddInfo);
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
	
	private AonAddInfoPanelCallback callback;
	private RegistryAddInfo registryAddInfo;
	
	// Address Info
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomSuggestBox type = new AonCustomSuggestBox("Parametro");
	private List<String> types = new ArrayList<>();
	
	private AonCustomTextBox value = new AonCustomTextBox("Valor");
	private AonCustomDateBox date = new AonCustomDateBox("Fecha");
	
	// Constructor
	
	public AonAddInfoPanel(String domainName, Integer domain, String user, Integer registry, AonAddInfoPanelCallback callback) {
		super(EMPTY_STRING);
		this.registryAddInfo = new RegistryAddInfo().setDomain(domain).setRegistry(registry);
		aonAddInfoPanel(domainName, domain, user, callback);
	}
	
	public AonAddInfoPanel(String domainName, Integer domain, String user, RegistryAddInfo registryAddInfo, AonAddInfoPanelCallback callback) {
		super(EMPTY_STRING);
		this.registryAddInfo = registryAddInfo;
		aonAddInfoPanel(domainName, domain, user, callback);
	}
	
	private void aonAddInfoPanel(String domainName, Integer domain, String user, AonAddInfoPanelCallback callback) {
		initializeCommonService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.callback = callback;
		
		show();
	}

	private void show() {
		// Message Panel
		setStyleName(AON.CSS.aonFlexColumn());
		getElement().getStyle().setProperty("padding", "1rem 0");
		add(messagePanel);
		
		HTMLPanel container = new HTMLPanel(EMPTY_STRING);
		container.setStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		container.getElement().getStyle().setProperty("min-width", "25rem");
		
		// First Row
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		row.setStyleName(AON.CSS.aonItemFlex());
		
		type.setAutoSelectEnabled(false);
		type.setPlaceHolder("Cuota: ctrl + espacio para ver sugerencias");
		type.getSuggestBox().addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				type.showSuggestionList();
			}
		});
		getRAddInfoAviableAttributes();
		
		row.add(type);
		container.add(row);
		
		// Second Row
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		date.getElement().getStyle().setProperty("max-width", "5rem");
		
		row2.add(value);
		row2.add(date);
		container.add(row2);
		
		// Fill info
		if(registryAddInfo.getId() != null) {
			type.setValue(registryAddInfo.getAttribute());
			value.setValue(registryAddInfo.getValue());
			date.setValue(registryAddInfo.getDate());
		}
		
		// Buttons
		container.add(createButtonsPanel());
		add(container);
	}
	
	private HTMLPanel createButtonsPanel(){
		HTMLPanel buttonsPanel = new HTMLPanel(EMPTY_STRING);
		buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
    	
    	Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
			
    		registryAddInfo
				.setAttribute(type.getValue())
				.setValue(value.getValue())
				.setDate(date.getValue())
			;
			
			commonService.saveRegistryAddInfo(domainName, domainId, user, registryAddInfo, new AsyncCallback<RegistryAddInfo>() {
				
				@Override
				public void onSuccess(RegistryAddInfo result) {
					callback.onAccept(result);
				}
				
				@Override
				public void onFailure(Throwable error) {
					AonMessagePanel.showError(messagePanel, error.getMessage());
				}
				
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
	
	private void getRAddInfoAviableAttributes() {
		commonService.getRAddInfoAviableAttributes(domainName, domainId, user, new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> newsSuggestion) {
				types = newsSuggestion;
				
				List<String> suggestions = new ArrayList<String>();
				types.forEach(typeIt -> suggestions.add(typeIt));
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) type.getSuggestBox().getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(suggestions);
				orclSb.setDefaultSuggestionsFromText(suggestions);
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, error.getMessage());
			}
			
		});
	}

}
