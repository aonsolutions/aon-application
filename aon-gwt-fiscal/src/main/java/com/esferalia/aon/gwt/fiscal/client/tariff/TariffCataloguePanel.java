package com.esferalia.aon.gwt.fiscal.client.tariff;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.tariff.TariffCatalogue;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class TariffCataloguePanel extends HTMLPanel {
	// Callback
	
	public static interface TariffCatalogueCallback {
		void onAccept(TariffCatalogue tariffCatalogue);
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
	
	private TariffCatalogueCallback callback;
	private List<Catalogue> catalogues;
	private TariffCatalogue tariffCatalogue;
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomListBox catalogue = new AonCustomListBox("Cat\u00e1logo");
	
	private Button okButton;
	
	// Constructor
	
	public TariffCataloguePanel(RegistryModuleOptions options, Tariff tariff, List<Catalogue> catalogues, TariffCatalogueCallback callback) {
		super(EMPTY_STRING);
		
		this.options = options;
		this.callback = callback;
		this.catalogues = catalogues;
		
		this.tariffCatalogue = new TariffCatalogue().setDomain(options.getDomain()).setTariff(tariff);
		
		initializeView();
	}
	
	public TariffCataloguePanel(RegistryModuleOptions options, List<Catalogue> catalogues, TariffCatalogue tariffCatalogue, TariffCatalogueCallback callback) {
		super(EMPTY_STRING);
		
		this.options = options;
		this.callback = callback;
		this.catalogues = catalogues;
		this.tariffCatalogue = tariffCatalogue;
		
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
		
		catalogue.clearItems();
		catalogue.addItem("-", "");
		catalogues.forEach(catalogueIt -> catalogue.addItem(catalogueIt.getName(), catalogueIt.getId().toString()));
		catalogue.addChangeHandler(e -> {
			okButton.setVisible(AonStringUtils.isNotBlank(catalogue.getValue()));
			tariffCatalogue.setCatalogue(new Catalogue().setId(AonStringUtils.isNotBlank(catalogue.getValue()) ? Integer.parseInt(catalogue.getValue()) : null));
		});
		
		row.add(catalogue);
		container.add(row);
		
		if(null != tariffCatalogue.getId()) {
			catalogue.setValue(tariffCatalogue.getCatalogue().getId().toString());
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
    	okButton.setText( null == tariffCatalogue.getId() ? "Crear" : "Actualizar" );
    	okButton.setVisible(false);
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
    		
			commonService.saveTariffCatalogue(options.getDomainName(), options.getDomain(), options.getUser(), tariffCatalogue, new AsyncCallback<TariffCatalogue>() {
				
				@Override
				public void onSuccess(TariffCatalogue result) {
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
