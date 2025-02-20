package com.esferalia.aon.gwt.fiscal.client.tariff;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class TariffPanel extends HTMLPanel {
	
	public static interface AonTariffPanelCallback {
		void onAccept(Tariff tariff);
	}
	
	// ------------------------------------------------- CommonServiceAsync
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- Variables
	
	private final String EMPTY_STRING = "";
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomTextBox code = new AonCustomTextBox("C\u00f3digo");
	private AonCustomTextBox name = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomNumberBox discount = new AonCustomNumberBox("Descuento");
	private AonCustomListBox status = new AonCustomListBox("Estado");
	
	private AonTariffPanelCallback callback;
	private RegistryModuleOptions options;
	private Tariff tariff;
	
	public TariffPanel(RegistryModuleOptions options, AonTariffPanelCallback callback) {
		super("");
		
		this.options = options;
		this.callback = callback;
		
		this.tariff = new Tariff().setDomain(options.getDomain());
		
		initializeView();
	}

	public TariffPanel(RegistryModuleOptions options, Tariff tariff, AonTariffPanelCallback callback) {
		super("");
		
		this.options = options;
		this.callback = callback;
		
		this.tariff = tariff;
		
		initializeView();
	}
	
	private void initializeView() {
		initializeCommonService();
		
		addStyleName(AON.CSS.aonItemFlex());
		addStyleName(AON.CSS.aonFlexColumn());
		getElement().getStyle().setProperty("padding", "0 1rem");
		
		messagePanel.addStyleName(AON.CSS.aonWidthAll());
		
		add(messagePanel);
		
		add(createGeneralCard());
		
		add(createButtons());
	}

	private FlowPanel createGeneralCard() {
		FlowPanel table = createFlexColumnPanel();
		
		code.setValue(tariff.getCode());
		name.setValue(tariff.getName());
		
		table.add(createRow(code, name));
		
		type.clearItems();
		type.addItem("Compras", "1");
		type.addItem("Ventas", "0");
		
		discount.hideNearBy();
		
		table.add(createRow(type, discount));
		
		status.clearItems();
		status.addItem("Activo", "1");
		status.addItem("Inactive", "0");
		
		table.add(createRow(status, null));
		
		return table;
	}
	
	private Widget createButtons() {
		Button acceptDialog = new Button();
		acceptDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptDialog.setText(tariff.getId() == null ? "Crear Tarifa" : "Guardar");
		acceptDialog.addClickHandler(e -> saveTariff());
		
		FlowPanel buttonsPanel = createRow(acceptDialog, null);
		buttonsPanel.getElement().getStyle().setProperty("margin", ".5rem 0 1rem 0");
		buttonsPanel.getElement().getStyle().setProperty("justify-content", "end");
			
		return buttonsPanel;
	}

	private FlowPanel createFlexColumnPanel() {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName(AON.CSS.aonItemFlex());
        panel.addStyleName(AON.CSS.aonFlexColumn());
        panel.setWidth("100%");
        return panel;
    }
	
	private FlowPanel createRow(Widget widget1, Widget widget2) {
        FlowPanel row = createFlexPanel();
        row.add(widget1);
        if (widget2 != null) {
            row.add(widget2);
        }
        return row;
    }
	
	private FlowPanel createFlexPanel() {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName(AON.CSS.aonItemFlex());
        panel.setWidth("100%");
        return panel;
    }
	
	private void saveTariff() {
		tariff.setCode(code.getValue());
		tariff.setName(name.getValue());
		tariff.setPurchase(AonStringUtils.equalsIgnoreCase(type.getValue(), "1"));
		
		AonMessagePanel.showLoading(messagePanel, "Creando Tarifa " + tariff.getName());
		
		commonService.saveTariff(options.getDomainName(), options.getDomain(), options.getUser(), tariff, new AsyncCallback<Tariff>() {
			
			@Override
			public void onSuccess(Tariff tariffDB) {
				tariff = tariffDB;
				
				AonMessagePanel.showSuccess(messagePanel, "Tarifa " + tariff.getName()+ " creada correctamente");
				
				Timer timer = new Timer() {
				     @Override
				     public void run() { callback.onAccept(tariff);  }
				};
				timer.schedule(2500);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error guardado tarifa: " + caught.getMessage());
			}
		});
	}
	
	public void focusCode() {
		code.setFocus(true);
	}

}
