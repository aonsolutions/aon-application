package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public abstract class CataloguePackBookingCard extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	
	private HTMLPanel content;
	private HTMLPanel contentData;
	private HTMLPanel buttonData;
	
	private Product packProduct;
	private Tariff tariff;
	private ItemTariff itemTariff;
	private List<ItemComposition> itemCompositions;
	private LinkedList<Fee> customerFees;
	
	public CataloguePackBookingCard(Product packProduct, Tariff tariff, List<ItemComposition> itemCompositions, LinkedList<Fee> customerFees) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonCustomCard());
		getElement().getStyle().setProperty("flex", "1");
		getElement().getStyle().setProperty("min-height", "18rem");
		setHeight("100%");
		this.packProduct = packProduct;
		this.tariff = tariff;
		this.itemCompositions = itemCompositions;
		this.customerFees = customerFees;
		
		if(isFeeProduct(packProduct.getItem().getId()))
			getElement().getStyle().setProperty("background-color", "#eee");
			
		
		content = new HTMLPanel(EMPTY_STRING);
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("justify-content", "space-between");
		content.setHeight("100%");
		
		contentData = new HTMLPanel(EMPTY_STRING);
		contentData.addStyleName(AON.CSS.aonFlexColumn());
		
		buttonData = new HTMLPanel(EMPTY_STRING);
		buttonData.addStyleName(AON.CSS.aonFlexColumn());
		buttonData.getElement().getStyle().setProperty("align-items", "center");
		
		content.add(contentData);
		content.add(buttonData);
		
		createTitle();
		
//		if(!itemCompositions.isEmpty())
//			createPackCompositionContent();
		
		createPackContent();
		
		createPrice();
		
		createButton(packProduct);
		
		add(content);
	}
	
	public CataloguePackBookingCard(Product packProduct, ItemTariff itemTariff, List<ItemComposition> itemCompositions, LinkedList<Fee> customerFees) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonCustomCard());
		getElement().getStyle().setProperty("flex", "1");
		getElement().getStyle().setProperty("min-height", "18rem");
		setHeight("100%");
		this.packProduct = packProduct;
		this.itemTariff = itemTariff;
		this.itemCompositions = itemCompositions;
		this.customerFees = customerFees;
		
		content = new HTMLPanel(EMPTY_STRING);
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("justify-content", "space-between");
		content.setHeight("100%");
		
		contentData = new HTMLPanel(EMPTY_STRING);
		contentData.addStyleName(AON.CSS.aonItemFlex());
		contentData.addStyleName(AON.CSS.aonFlexColumn());
		
		content.add(contentData);
		
		createTitle();
		
//		if(!itemCompositions.isEmpty())
//			createPackCompositionContent();
		
		createPackContent();
		
		createPrice();
		
		createButton(packProduct);
		
		add(content);
	}

	private void createTitle() {
		HTMLPanel titlePanel = new HTMLPanel(EMPTY_STRING);
		titlePanel.getElement().getStyle().setProperty("display", "flex");
		titlePanel.getElement().getStyle().setProperty("align-items", "center");
		titlePanel.getElement().getStyle().setProperty("gap", "0.5rem");
		titlePanel.getElement().getStyle().setProperty("margin", "1rem 0");
		titlePanel.getElement().getStyle().setProperty("width", "100%");
		
		HTMLPanel titleLabel = new HTMLPanel(packProduct.getName());
		titleLabel.getElement().getStyle().setProperty("font-size", "1rem");
		titleLabel.getElement().getStyle().setProperty("font-weight", "700");
		titleLabel.getElement().getStyle().setProperty("color", "#5f6368");
		titlePanel.add(titleLabel);
		
		contentData.add(titlePanel);
	}
	
	private void createPrice() {
		HTMLPanel pricePanel = new HTMLPanel(EMPTY_STRING);
		pricePanel.addStyleName(AON.CSS.aonFlexBetween());
		pricePanel.getElement().getStyle().setProperty("margin-bottom", "1rem");
		pricePanel.getElement().getStyle().setProperty("flexDirection", "column-reverse");
		
		String priceValue = formaDouble(packProduct.getItem().getPrice());
		HTMLPanel price = new HTMLPanel("<b>" + priceValue.split("\\.")[0] + "</b>." + priceValue.split("\\.")[1] + "<b> \u20ac </b>" + " al mes *");
		price.getElement().getStyle().setProperty("color", isFeeProduct(packProduct.getItem().getId()) ? "black" : "blue");
		
		if(null != tariff) {
			if(tariff.getDiscount() != 0.00) {
				double tariffPrice = getTariffPrice(packProduct.getItem().getPrice(), tariff.getDiscount());
				Label newPrice = new Label(tariffPrice == 0.00 ? "Gratis" : formaDouble(tariffPrice) + " \u20ac");
				newPrice.getElement().getStyle().setProperty("font-size", "1.2rem");
				newPrice.getElement().getStyle().setColor("#0ea90e");
				pricePanel.add(newPrice);
				
				price.getElement().getStyle().setProperty("font-size", "1rem");
				price.getElement().getStyle().setColor("#848484");
				price.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
			} else {
				price.getElement().getStyle().setProperty("font-size", "1rem");
			}
		} else {
			if(itemTariff.getProfitPercent() != 0.00) {
				double tariffPrice = getTariffPrice(packProduct.getItem().getPrice(), itemTariff.getProfitPercent());
				Label newPrice = new Label(tariffPrice == 0.00 ? "Gratis" : formaDouble(tariffPrice) + " \u20ac");
				newPrice.getElement().getStyle().setProperty("font-size", "1.2rem");
				newPrice.getElement().getStyle().setColor("#0ea90e");
				pricePanel.add(newPrice);
				
				price.getElement().getStyle().setProperty("font-size", "1rem");
				price.getElement().getStyle().setColor("#848484");
				price.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
			} else {
				price.getElement().getStyle().setProperty("font-size", "1rem");
			}
		}
		
		
		pricePanel.add(price);
		
		buttonData.add(pricePanel);
	}

	private void createPackContent() {
		HTMLPanel packContent = new HTMLPanel(EMPTY_STRING);
		packContent.addStyleName(AON.CSS.aonFlexColumn());
		packContent.setWidth("100%");
		packContent.getElement().getStyle().setProperty("margin-bottom", "2rem");
		packContent.getElement().getStyle().setProperty("align-items", "start");
		
		HTMLPanel include = new HTMLPanel(packProduct.getItem().getDescription());
		include.getElement().getStyle().setProperty("padding", "1rem");
		packContent.add(include);
		
		contentData.add(packContent);
		
	}

	private void createPackCompositionContent() {
		HTMLPanel packContent = new HTMLPanel(EMPTY_STRING);
		packContent.addStyleName(AON.CSS.aonItemFlex());
		packContent.addStyleName(AON.CSS.aonFlexColumn());
		packContent.setWidth("100%");
		packContent.getElement().getStyle().setProperty("margin-bottom", "2rem");
		packContent.getElement().getStyle().setProperty("align-items", "start");
		
		Label include = new Label("Pack incluye");
		include.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		packContent.add(include);
		
		itemCompositions.forEach(itemComposition -> {
			Label itemCompositionLabel = new Label(itemComposition.getComposition().getProduct().getName());
			itemCompositionLabel.getElement().getStyle().setProperty("padding-left", "1rem");
			packContent.add(itemCompositionLabel);
		});
		
		contentData.add(packContent);
		
	}
	
	private void createButton(Product packProduct) {
		Button bookBtn = new Button();
		bookBtn.setText(isFeeProduct(packProduct.getItem().getId()) ? "Ya Contratado" : "Contratar");
		bookBtn.setEnabled(!isFeeProduct(packProduct.getItem().getId()));
		
		bookBtn.getElement().getStyle().setProperty("background", "none");
		bookBtn.getElement().getStyle().setProperty("color", isFeeProduct(packProduct.getItem().getId()) ? "green" : "white");
		bookBtn.getElement().getStyle().setProperty("background-color", isFeeProduct(packProduct.getItem().getId()) ? "rgb(181, 180, 180)" : "#ff8f00");
		bookBtn.getElement().getStyle().setProperty("width", "15rem");
		bookBtn.getElement().getStyle().setProperty("height", "2.5rem");
		bookBtn.getElement().getStyle().setProperty("border-radius", "5px");
		bookBtn.getElement().getStyle().setProperty("border", "none");
		
		bookBtn.addClickHandler(e -> {
			AonDialog dialog = new AonDialog(packProduct.getName(), new HTMLPanel("Se va a proceder con la creaci\u00f3n de la cuota del producto <b>" + packProduct.getName() + "</b><br> Al proceder acepta los terminos y condiciones de la contrataci\u00f3n"));
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {}
				
				@Override
				public void onAccept() {
					onCreateCustomerFeeByItem(packProduct);
				}
			});
		});
		
		buttonData.add(bookBtn);
	}
	
	private boolean isFeeProduct(Integer itemId) {
		return customerFees.stream().filter(fee -> fee.getItem().getId().equals(itemId) && (fee.getEndDate() == null || (fee.getEndDate().equals(new Date()) || fee.getEndDate().after(new Date()) ))).findAny().isPresent();
	}
	
	protected abstract void onCreateCustomerFeeByItem(Product product);

	private double getTariffPrice(double price, double discount) {
		return price - (price * discount / 100);
	}
	
	private static String formaDouble(double value) {
        // Round to two decimal places
        long scaledValue = Math.round(value * 100); // Scale to avoid floating-point precision issues
        long integerPart = scaledValue / 100;      // Extract integer part
        long decimalPart = scaledValue % 100;      // Extract decimal part

        // Format the result
        return integerPart + "." + (decimalPart < 10 ? "0" : "") + decimalPart /*+ " \u20ac"*/;
    }

}
