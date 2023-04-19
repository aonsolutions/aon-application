package com.esferalia.aon.gwt.fiscal.client.registry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public abstract class CustomerMassiveFeeDialog extends AonCustomDialog {

	// ------------------------------------------------- Variables
	
	private HTMLPanel container;
	
	private TextBox priceTextBox;
	private TextBox discountTextBox;
	private AonDateBox billingDateBox;
	private AonDateBox startDateBox;
	private AonDateBox endDateBox;
	
	private HTMLPanel buttonsPanel;

	private Fee fee;

	// ------------------------------------------------- Constructor

	protected CustomerMassiveFeeDialog() {
		setCaption("Editor Cuotas");
		this.fee = new Fee();
		initView();
		showDialog();
	}

	// ------------------------------------------------- Auxiliar Methods

	private void initView() {
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("margin", "1rem");
		
		createFields();
		
		getButtonsPanel();
		container.add(buttonsPanel);
		
		this.setWidget(container);
	}

	private void createFields() {
		HTMLPanel pricePanel = new HTMLPanel("");
		pricePanel.addStyleName(AON.CSS.aonFlexBetween());
		Label priceLabel = new Label("Precio");
		priceLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		priceTextBox = new TextBox();
		priceTextBox.setHeight("20px");
		pricePanel.add(priceLabel);
		pricePanel.add(priceTextBox);
		
		HTMLPanel discountPanel = new HTMLPanel("");
		discountPanel.addStyleName(AON.CSS.aonFlexBetween());
		Label discountLabel = new Label("Descuento");
		discountLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		discountTextBox = new TextBox();
		discountTextBox.setHeight("20px");
		discountPanel.add(discountLabel);
		discountPanel.add(discountTextBox);
		
		HTMLPanel billingDatePanel = new HTMLPanel("");
		billingDatePanel.addStyleName(AON.CSS.aonFlexBetween());
		Label billingDateLabel = new Label("F. Facturaci\u00f3n");
		billingDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		billingDateBox = new AonDateBox();
		billingDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		billingDateBox.addStyleName("gwt-TextBox");
		billingDateBox.setHeight("20px");
		billingDateBox.setWidth("140px");
		billingDateBox.addValueChangeHandler(e -> {if(e.getValue() != null) billingDateBox.setValue(DateUtils.getFirstDayOfMonth(e.getValue()));});
		billingDatePanel.add(billingDateLabel);
		billingDatePanel.add(billingDateBox);
		
		HTMLPanel startDatePanel = new HTMLPanel("");
		startDatePanel.addStyleName(AON.CSS.aonFlexBetween());
		Label startDateLabel = new Label("F. Desde");
		startDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		startDateBox = new AonDateBox();
		startDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		startDateBox.addStyleName("gwt-TextBox");
		startDateBox.setHeight("20px");
		startDateBox.setWidth("140px");
		startDatePanel.add(startDateLabel);
		startDatePanel.add(startDateBox);
		
		HTMLPanel endDatePanel = new HTMLPanel("");
		endDatePanel.addStyleName(AON.CSS.aonFlexBetween());
		Label endDateLabel = new Label("F. Hasta");
		endDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		endDateBox = new AonDateBox();
		endDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		endDateBox.addStyleName("gwt-TextBox");
		endDateBox.setHeight("20px");
		endDateBox.setWidth("140px");
		endDatePanel.add(endDateLabel);
		endDatePanel.add(endDateBox);
		
		container.add(pricePanel);
		container.add(discountPanel);
		container.add(billingDatePanel);
		container.add(startDatePanel);
		container.add(endDatePanel);
	}

	private void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	// ------------------------------------------------- ButtonsPanel

	private void getButtonsPanel() {
		buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText(AON.MSG.cancelAction());
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		closeBtnDialog.addClickHandler(e -> hide());

		buttonsPanel.add(closeBtnDialog);

		Button acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonDialogButton());
		acceptBtnDialog.addStyleName(AON.CSS.aonIconSave());
		acceptBtnDialog.setText(AON.MSG.saveAction());
		acceptBtnDialog.addClickHandler(e -> {
			hide();
			acceptDialog();
		});

		buttonsPanel.add(acceptBtnDialog);
	}

	private void acceptDialog() {
		if(null == fee) fee = new Fee();
		
		// Protect againt 0.0 default value
		fee.setDiscountExpr(null);
		
		if(AonStringUtils.isNotBlank(priceTextBox.getValue())) fee.setPrice(Double.parseDouble(priceTextBox.getValue()));
		if(AonStringUtils.isNotBlank(discountTextBox.getValue())) fee.setDiscountExpr(discountTextBox.getValue());
		
		if(null != billingDateBox.getValue()) fee.setBillingDate(billingDateBox.getValue());
		if(null != startDateBox.getValue()) fee.setStartDate(startDateBox.getValue());
		if(null != endDateBox.getValue()) fee.setEndDate(endDateBox.getValue());
		
		onAccept(fee);
	}

	// ------------------------------------------------- Abstract Methods

	protected abstract void onAccept(Fee fee);

}
