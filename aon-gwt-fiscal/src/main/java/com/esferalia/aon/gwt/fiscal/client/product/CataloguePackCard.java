package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public class CataloguePackCard extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	
	private HTMLPanel content;
	private HTMLPanel contentData;
	
	private Product packProduct;
	private Tariff tariff;
	private ItemTariff itemTariff;
	private List<ItemComposition> itemCompositions;
	
	public CataloguePackCard(Product packProduct, Tariff tariff, List<ItemComposition> itemCompositions) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonCustomCard());
		getElement().getStyle().setProperty("min-width", "18rem");
		getElement().getStyle().setProperty("min-height", "18rem");
		
		this.packProduct = packProduct;
		this.tariff = tariff;
		this.itemCompositions = itemCompositions;
		
		content = new HTMLPanel(EMPTY_STRING);
		content.addStyleName(AON.CSS.aonItemFlex());
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("justify-content", "space-between");
		
		contentData = new HTMLPanel(EMPTY_STRING);
		contentData.addStyleName(AON.CSS.aonItemFlex());
		contentData.addStyleName(AON.CSS.aonFlexColumn());
		
		content.add(contentData);
		
		createTitle();
		createPrice();
		
		if(!itemCompositions.isEmpty())
			createPackContent();
		
		createButton(packProduct);
		
		add(content);
	}
	
	public CataloguePackCard(Product packProduct, ItemTariff itemTariff, List<ItemComposition> itemCompositions) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonCustomCard());
		getElement().getStyle().setProperty("min-width", "18rem");
		getElement().getStyle().setProperty("min-height", "18rem");
		
		this.packProduct = packProduct;
		this.itemTariff = itemTariff;
		this.itemCompositions = itemCompositions;
		
		content = new HTMLPanel(EMPTY_STRING);
		content.addStyleName(AON.CSS.aonItemFlex());
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("justify-content", "space-between");
		
		contentData = new HTMLPanel(EMPTY_STRING);
		contentData.addStyleName(AON.CSS.aonItemFlex());
		contentData.addStyleName(AON.CSS.aonFlexColumn());
		
		content.add(contentData);
		
		createTitle();
		createPrice();
		
		if(!itemCompositions.isEmpty())
			createPackContent();
		
		createButton(packProduct);
		
		add(content);
	}

	private void createTitle() {
		HTMLPanel titlePanel = new HTMLPanel(EMPTY_STRING);
		titlePanel.getElement().getStyle().setProperty("display", "flex");
		titlePanel.getElement().getStyle().setProperty("align-items", "center");
		titlePanel.getElement().getStyle().setProperty("gap", "0.5rem");
		titlePanel.getElement().getStyle().setProperty("margin-bottom", "1rem");
		
		HTMLPanel titleLabel = new HTMLPanel(packProduct.getName());
		titleLabel.getElement().getStyle().setProperty("font-size", "1rem");
		titleLabel.getElement().getStyle().setProperty("font-weight", "700");
		titleLabel.getElement().getStyle().setProperty("color", "#5f6368");
		titlePanel.add(titleLabel);
		
		contentData.add(titlePanel);
	}
	
	private void createPrice() {
		HTMLPanel pricePanel = new HTMLPanel(EMPTY_STRING);
		pricePanel.addStyleName(AON.CSS.aonItemFlex());
		pricePanel.addStyleName(AON.CSS.aonFlexBetween());
		pricePanel.getElement().getStyle().setProperty("margin-bottom", "1rem");
		pricePanel.getElement().getStyle().setProperty("flexDirection", "column-reverse");
		
		Label price = new Label(formaDouble(packProduct.getItem().getPrice()) + " \u20ac");
		
		if(null != tariff) {
			if(tariff.getDiscount() != 0.00) {
				double tariffPrice = getTariffPrice(packProduct.getItem().getPrice(), tariff.getDiscount());
				Label newPrice = new Label(tariffPrice == 0.00 ? "Gratis" : formaDouble(tariffPrice) + " \u20ac");
				newPrice.getElement().getStyle().setProperty("font-size", "2rem");
				newPrice.getElement().getStyle().setColor("#0ea90e");
				pricePanel.add(newPrice);
				
				price.getElement().getStyle().setProperty("font-size", "1.3rem");
				price.getElement().getStyle().setColor("#848484");
				price.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
			} else {
				price.getElement().getStyle().setProperty("font-size", "2rem");
			}
		} else {
			if(itemTariff.getProfitPercent() != 0.00) {
				double tariffPrice = getTariffPrice(packProduct.getItem().getPrice(), itemTariff.getProfitPercent());
				Label newPrice = new Label(tariffPrice == 0.00 ? "Gratis" : formaDouble(tariffPrice) + " \u20ac");
				newPrice.getElement().getStyle().setProperty("font-size", "2rem");
				newPrice.getElement().getStyle().setColor("#0ea90e");
				pricePanel.add(newPrice);
				
				price.getElement().getStyle().setProperty("font-size", "1.3rem");
				price.getElement().getStyle().setColor("#848484");
				price.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
			} else {
				price.getElement().getStyle().setProperty("font-size", "2rem");
			}
		}
		
		
		pricePanel.add(price);
		
		contentData.add(pricePanel);
	}

	private void createPackContent() {
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
		bookBtn.setText("Contratar");
		
		bookBtn.getElement().getStyle().setProperty("background", "none");
		bookBtn.getElement().getStyle().setProperty("background-color", "#3d76d6");
		bookBtn.getElement().getStyle().setProperty("width", "90%");
		bookBtn.getElement().getStyle().setProperty("height", "3rem");
		bookBtn.getElement().getStyle().setProperty("border-radius", "2rem");
		bookBtn.getElement().getStyle().setProperty("color", "white");
		bookBtn.getElement().getStyle().setProperty("border", "none");
		
		content.add(bookBtn);
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
