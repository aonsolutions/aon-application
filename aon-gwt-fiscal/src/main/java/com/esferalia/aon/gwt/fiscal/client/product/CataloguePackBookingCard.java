package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductBooking;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public abstract class CataloguePackBookingCard extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	
	private HTMLPanel content;
	private HTMLPanel contentData;
	private HTMLPanel buttonData;
	
	private ProductBooking packProduct;
	private Tariff tariff;
	private ItemTariff itemTariff;
	private LinkedList<Fee> customerFees;
	
	public CataloguePackBookingCard(ProductBooking packProduct, Tariff tariff, LinkedList<Fee> customerFees) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonCustomCard());
		getElement().getStyle().setProperty("flex", "1");
		getElement().getStyle().setProperty("min-height", "18rem");
		setHeight("100%");
		this.packProduct = packProduct;
		this.tariff = tariff;
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
		
		createPackContent();
		
		createPrice();
		
		createButton(packProduct);
		
		add(content);
	}
	
	public CataloguePackBookingCard(ProductBooking packProduct, ItemTariff itemTariff, LinkedList<Fee> customerFees) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonCustomCard());
		getElement().getStyle().setProperty("flex", "1");
		getElement().getStyle().setProperty("min-height", "18rem");
		setHeight("100%");
		this.packProduct = packProduct;
		this.itemTariff = itemTariff;
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
		HTMLPanel price = new HTMLPanel("<b>" + priceValue.split("\\.")[0] + "</b>.<small>" + priceValue.split("\\.")[1] + "</small> \u20ac" + " al mes *");
		price.getElement().getStyle().setProperty("color", isFeeProduct(packProduct.getItem().getId()) ? "black" : "#002469");
		
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
	
	private void createButton(Product packProduct) {
		Button bookBtn = new Button();
		bookBtn.setText(isFeeProduct(packProduct.getItem().getId()) ? "Ya Contratado" : "Contratar");
		bookBtn.setEnabled(!isFeeProduct(packProduct.getItem().getId()));
		
		bookBtn.getElement().getStyle().setProperty("background", "none");
		bookBtn.getElement().getStyle().setProperty("color", "white");
		bookBtn.getElement().getStyle().setProperty("background-color", isFeeProduct(packProduct.getItem().getId()) ? "green" : "#ff8f00");
		bookBtn.getElement().getStyle().setProperty("width", "15rem");
		bookBtn.getElement().getStyle().setProperty("height", "2.5rem");
		bookBtn.getElement().getStyle().setProperty("border-radius", "5px");
		bookBtn.getElement().getStyle().setProperty("border", "none");
		
		bookBtn.addClickHandler(e -> {
			
			AonCustomDialog dialog = new AonCustomDialog();
			
			HTMLPanel dialogContent = new HTMLPanel("");
			dialogContent.addStyleName(AON.CSS.aonFlexColumn());
			dialogContent.getElement().getStyle().setProperty("padding", "1rem");
			
			HTMLPanel messageDialogPanel = new HTMLPanel("");
			dialogContent.add(messageDialogPanel);
			
			HTMLPanel buttonsPanel = new HTMLPanel("");
			buttonsPanel.addStyleName(AON.CSS.aonItemFlex());
			buttonsPanel.getElement().getStyle().setProperty("justify-content", "center");
			buttonsPanel.getElement().getStyle().setProperty("margin-top", "1rem");
			buttonsPanel.setWidth("100%");
			
			Button closeBtnDialog = new Button();
			closeBtnDialog.setStyleName(AON.CSS.aonCancelButton());
			closeBtnDialog.setText(AON.MSG.cancelAction());
			closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
			closeBtnDialog.addClickHandler(ev -> dialog.hide());
			buttonsPanel.add(closeBtnDialog);
			
			Button acceptBtnDialog = new Button();
			acceptBtnDialog.setStyleName(AON.CSS.aonOkButton());
			acceptBtnDialog.setText("Contratar");
			acceptBtnDialog.getElement().getStyle().setProperty("background-color", "#eee");
			acceptBtnDialog.setEnabled(false);
			buttonsPanel.add(acceptBtnDialog);
			
			HTMLPanel message = new HTMLPanel(!isFeeProduct(packProduct.getItem().getId()) 
					? "Se va a proceder con la contrataci\u00f3n del producto <b>" + packProduct.getName() + "</b>" 
					: "Se va a proceder a descontratar el producto <b>" + packProduct.getName() + "</b>" 
					);
			dialogContent.add(message);
			
			HTMLPanel terms = new HTMLPanel("");
			terms.addStyleName(AON.CSS.aonItemFlex());
			
			CheckBox acceptTerms = new CheckBox();
			acceptTerms.addValueChangeHandler(ev -> {
				acceptBtnDialog.setEnabled(acceptTerms.getValue());
				acceptBtnDialog.getElement().getStyle().setProperty("background-color", acceptTerms.getValue() ? "transparent" : "#eee");
			});
			Label temrsMessage = new Label("He leido y acepto los ");
			Anchor termsAnchor = new Anchor("Terminos y Condiciones", "https://ayudatpymes.com/aviso-legal/terminos-condiciones/", "_blank");
			termsAnchor.getElement().getStyle().setProperty("color", "#002469");
			Label temrsMessage_2 = new Label(" de contrataci\u00f3n de Aon");
					
			terms.add(acceptTerms);
			terms.add(temrsMessage);
			terms.add(termsAnchor);
			terms.add(temrsMessage_2);
			dialogContent.add(terms);
			
			acceptBtnDialog.addClickHandler(ev -> {
				if(acceptTerms.getValue()) {
					onCreateCustomerFeeByItem(packProduct);
					dialog.hide();
				} else
					AonMessagePanel.showError(messageDialogPanel, "Debe aceptar los terminos y condiciones para poder aceptar");
			});
			
			dialogContent.add(buttonsPanel);
			
			dialog.setCaption(packProduct.getName());
			dialog.add(dialogContent);
			dialog.center();
			dialog.show();
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
