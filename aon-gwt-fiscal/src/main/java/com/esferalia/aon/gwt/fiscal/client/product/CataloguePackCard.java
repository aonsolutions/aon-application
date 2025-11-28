package com.esferalia.aon.gwt.fiscal.client.product;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.ProductBooking;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public class CataloguePackCard extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	
	private HTMLPanel content;
	private HTMLPanel contentData;
	private HTMLPanel buttonData;
	
	private ProductBooking packProduct;
	private Tariff tariff;
	private ItemTariff itemTariff;
	
	public CataloguePackCard(ProductBooking packProduct, Tariff tariff) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonCustomCard());
		getElement().getStyle().setProperty("flex", "1");
		getElement().getStyle().setProperty("min-height", "18rem");
		setHeight("100%");
		
		this.packProduct = packProduct;
		this.tariff = tariff;
		
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
	
	public CataloguePackCard(ProductBooking packProduct, ItemTariff itemTariff) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonCustomCard());
		getElement().getStyle().setProperty("flex", "1");
		getElement().getStyle().setProperty("min-height", "18rem");
		setHeight("100%");
		
		this.packProduct = packProduct;
		this.itemTariff = itemTariff;
		
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
		titlePanel.getElement().getStyle().setProperty("margin", "1rem 0 .5rem 0");
		titlePanel.getElement().getStyle().setProperty("width", "100%");
		
		HTMLPanel titleLabel = new HTMLPanel(packProduct.getCode());
		titleLabel.getElement().getStyle().setProperty("font-size", "1rem");
		titleLabel.getElement().getStyle().setProperty("font-weight", "700");
		titleLabel.getElement().getStyle().setProperty("color", "#5f6368");
		titlePanel.add(titleLabel);
		
		contentData.add(titlePanel);
	}
	
	private void createPrice() {
		HTMLPanel pricePanel = new HTMLPanel(EMPTY_STRING);
		pricePanel.addStyleName(AON.CSS.aonFlexBetween());
		pricePanel.getElement().getStyle().setProperty("flexDirection", "column-reverse");
		
		String priceValue = formaDouble(packProduct.getItem().getPrice());
		HTMLPanel price = new HTMLPanel("<b>" + priceValue.split("\\.")[0] + "</b>." + priceValue.split("\\.")[1] + "<b> \u20ac </b>" + " al mes *");
		price.getElement().getStyle().setProperty("color", "black");
		
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
		packContent.getElement().getStyle().setProperty("align-items", "start");
		
		HTMLPanel include = new HTMLPanel(packProduct.getDescriptionTemplate());
		include.getElement().getStyle().setProperty("padding", ".5rem");
		include.getElement().getStyle().setProperty("margin", ".5rem 1rem");
		packContent.add(include);
		
		contentData.add(packContent);
		
	}

	private void createButton(ProductBooking packProduct) {
		Button bookBtn = new Button();
		bookBtn.setText(packProduct.isNoBooking() ? "Solicitar Informaci\u00f3n" : "Contratar");
		
		bookBtn.getElement().getStyle().setProperty("background", "none");
		bookBtn.getElement().getStyle().setProperty("color", "white");
		bookBtn.getElement().getStyle().setProperty("background-color", "#ff8f00");
		bookBtn.getElement().getStyle().setProperty("width", "15rem");
		bookBtn.getElement().getStyle().setProperty("height", "2.5rem");
		bookBtn.getElement().getStyle().setProperty("border-radius", "5px");
		bookBtn.getElement().getStyle().setProperty("border", "none");
		
		buttonData.add(bookBtn);
	}
	
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
