package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.List;
import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.ItemTariffType;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ItemTariffPanel  extends HTMLPanel {
	// Callback
	
	public static interface ItemTariffCallback {
		void onAccept(ItemTariff itemTariff);
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
	private Product product;
	private Item item;
	private List<Tariff> tariffList;
	
	private ItemTariffCallback callback;
	
	private ItemTariff itemTariff;
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomListBox tariff = new AonCustomListBox("Tarifa");
	private AonCustomListBox type = new AonCustomListBox("Aplica sobre");
	private AonCustomNumberBox price = new AonCustomNumberBox("Precio");
	private AonCustomNumberBox percent = new AonCustomNumberBox("Porcentaje");
	private AonCustomNumberBox neto = new AonCustomNumberBox("P.Neto");
	private AonCustomNumberBox iva = new AonCustomNumberBox("I.V.A. (%)");
	private AonCustomNumberBox pvp = new AonCustomNumberBox("P.V.P");
	
	private Button okButton;
	
	// Constructor
	
	public ItemTariffPanel(RegistryModuleOptions options, List<Tariff> tariffList, Product product, Item item, ItemTariffCallback callback) {
		super(EMPTY_STRING);
		
		this.options = options;
		this.tariffList = tariffList;
		this.product = product;
		this.item = item;
		
		this.callback = callback;
		
		this.itemTariff = new ItemTariff().setDomain(options.getDomain()).setItem(item.getId());
		
		initializeView();
	}
	
	public ItemTariffPanel(RegistryModuleOptions options, List<Tariff> tariffList, Product product, Item item, ItemTariff itemTariff, ItemTariffCallback callback) {
		super(EMPTY_STRING);
		
		this.options = options;
		this.tariffList = tariffList;
		this.product = product;
		this.item = item;
		
		this.callback = callback;
		
		this.itemTariff = itemTariff;
		
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
		
		tariff.clearItems();
		tariffList.forEach(tariffIt -> tariff.addItem(tariffIt.getName(), tariffIt.getId().toString()));
		tariff.addChangeHandler(e -> {
			Optional<Tariff> tariffOpt = tariffList.stream().filter(tariffIt -> tariffIt.getId().equals(Integer.parseInt(tariff.getValue()))).findFirst();
			percent.setValue(tariffOpt.get().getDiscount());
			createPVP();
		});
		tariff.setEnable(false);
		
		row.add(tariff);
		container.add(row);
		
		// Second Row
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		type.clearItems();
		type.addItem(ItemTariffType.FIXED.getDescription(), ItemTariffType.FIXED.ordinal() + "");
		type.addItem(ItemTariffType.SALE_BASE.getDescription(), ItemTariffType.SALE_BASE.ordinal() + "");
		type.addChangeHandler(e -> {
			okButton.setVisible(true);
			
			if(AonStringUtils.equalsIgnoreCase(type.getValue(), ItemTariffType.FIXED.ordinal() + "")) {
				price.setValue(0.00);
				price.setEnable(true);
			} else if(AonStringUtils.equalsIgnoreCase(type.getValue(), ItemTariffType.SALE_BASE.ordinal() + "")) {
				price.setValue(item.getPrice());
				price.setEnable(false);
			}
			createPVP();
		});
		
		row2.add(type);
		container.add(row2);
		
		// Third Row
		HTMLPanel row3 = new HTMLPanel(EMPTY_STRING);
		row3.setStyleName(AON.CSS.aonItemFlex());
		
		percent.hideNearBy();
		percent.addValueChangeHandler(e -> {
			okButton.setVisible(true);
			createPVP();
		});
		
		price.hideNearBy();
		price.addValueChangeHandler(e -> {
			okButton.setVisible(true);
			createPVP();
		});
		
		neto.setEnable(false);
		neto.hideNearBy();
		
		iva.setEnable(false);
		iva.hideNearBy();
		iva.setValue(product.getVat().getPercentage());
		
		pvp.setEnable(false);
		pvp.hideNearBy();
		
		row3.add(price);
		row3.add(percent);
		row3.add(neto);
		row3.add(iva);
		row3.add(pvp);
		container.add(row3);
		
		tariff.setValue(itemTariff.getTariff().getId().toString());
		type.setValue(itemTariff.getType().ordinal() + "");
		percent.setValue(itemTariff.getProfitPercent());
		price.setValue(itemTariff.getPrice());
		price.setEnable(AonStringUtils.equals(itemTariff.getType().ordinal() + "", ItemTariffType.FIXED.ordinal() + ""));
		
		createPVP();
		
		// Buttons
		container.add(createButtonsPanel());
		add(container);	
	}
	
	private void createPVP() {
		createNeto();
		
		double pvpValue = neto.getValue() + (neto.getValue() * product.getVat().getPercentage() / 100);
		pvp.setValue(pvpValue);
	}
	
	private void createNeto() {
		double netoValue = price.getValue() - (price.getValue() * percent.getValue() / 100);
		neto.setValue(netoValue);
	}

	private Widget createButtonsPanel() {
		HTMLPanel buttonsPanel = new HTMLPanel(EMPTY_STRING);
		buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
    	
    	okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( "Crear Tarifa" );
    	okButton.setVisible(false);
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
    		
    		itemTariff
				.setTariff(tariffList.stream().filter(tariffIt -> tariffIt.getId().equals(Integer.parseInt(tariff.getValue()))).findFirst().get())
				.setType(ItemTariffType.safeValueOf(Integer.parseInt(type.getValue())))
				.setProfitPercent(percent.getValue())
				.setPrice(price.getValue())
    			;
			
			commonService.saveItemTariff(options.getDomainName(), options.getDomain(), options.getUser(), itemTariff, new AsyncCallback<ItemTariff>() {
				
				@Override
				public void onSuccess(ItemTariff result) {
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
