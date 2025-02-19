package com.esferalia.aon.gwt.fiscal.client.tariff;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.tariff.TariffAddInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class TariffAddInfoPanel extends HTMLPanel {
	// Callback
	
	public static interface TariffAddInfoCallback {
		void onAccept(TariffAddInfo tariffAddInfo);
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
	
	private RegistryModuleOptions options;
	
	private TariffAddInfoCallback callback;
	
	private TariffAddInfo tariffAddInfo;
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomTextBox name = new AonCustomTextBox("Nombre");
	private AonCustomTextBox value = new AonCustomTextBox("Valor");
	private AonCustomDateBox date = new AonCustomDateBox("Fecha");
	
	private Button okButton;
	
	// Constructor
	
	public TariffAddInfoPanel(RegistryModuleOptions options, Tariff tariff, TariffAddInfoCallback callback) {
		super(EMPTY_STRING);
		
		this.options = options;
		this.callback = callback;
		
		this.tariffAddInfo = new TariffAddInfo().setDomain(options.getDomain()).setTariff(tariff);
		
		initializeView();
	}
	
	public TariffAddInfoPanel(RegistryModuleOptions options, TariffAddInfo tariffAddInfo, TariffAddInfoCallback callback) {
		super(EMPTY_STRING);
		
		this.options = options;
		this.callback = callback;
		this.tariffAddInfo = tariffAddInfo;
		
		initializeView();
	}
	
	private void initializeView() {
		initializeCommonService();
		show();
	}
	
	public void show() {
		// Message Panel
		setStyleName(AON.CSS.aonFlexColumn());
		getElement().getStyle().setProperty("padding", "1rem 0");
		add(messagePanel);
		
		HTMLPanel container = new HTMLPanel(EMPTY_STRING);
		container.setStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		container.getElement().getStyle().setProperty("min-width", "35rem");
		
		// First Row
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		row.setStyleName(AON.CSS.aonItemFlex());
		
		name.getTextBox().setMaxLength(32);
		name.addValueChangeHandler(e -> {
			okButton.setVisible(true);
			tariffAddInfo.setAttribute(name.getValue());
		});
		value.addValueChangeHandler(e -> {
			okButton.setVisible(true);
			tariffAddInfo.setValue(value.getValue());
		});
		date.addValueChangeHandler(e -> {
			okButton.setVisible(true);
			tariffAddInfo.setDate(date.getValue());
		});
		
		row.add(name);
		row.add(value);
		row.add(date);
		container.add(row);
		
		if(null != tariffAddInfo.getId()) {
			name.setValue(tariffAddInfo.getAttribute());
			value.setValue(tariffAddInfo.getValue());
			date.setValue(tariffAddInfo.getDate());
		}
		
		// Buttons
		container.add(createButtonsPanel());
		add(container);	
	}
	
	private Widget createButtonsPanel() {
		HTMLPanel buttonsPanel = new HTMLPanel(EMPTY_STRING);
		buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
    	
    	okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( null == tariffAddInfo.getId() ? "Crear" : "Actualizar" );
    	okButton.setVisible(false);
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
    		
			commonService.saveTariffAddInfo(options.getDomainName(), options.getDomain(), options.getUser(), tariffAddInfo, new AsyncCallback<TariffAddInfo>() {
				
				@Override
				public void onSuccess(TariffAddInfo result) {
					callback.onAccept(result);
				}
				
				@Override
				public void onFailure(Throwable error) {
					AonMessagePanel.showError(messagePanel, error.getMessage());
					okButton.setEnabled(true);
				}
				
			});
    	});
    	buttonsPanel.add(okButton);
    	
    	return buttonsPanel;
	}
}
