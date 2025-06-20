package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;

public abstract class CustomerFeeDuplicateDialog extends AonCustomDialog {

	// ------------------------------------------------- Variables (UI)
	
	private HTMLPanel container;
	private HTMLPanel mainPanel;
	
	private HTMLPanel messagePanel;
	
	private HTMLPanel quantityPricePanel;
	private TextBox discountTextBox;
	
	private HTMLPanel datesPanel;
	private AonDateBox startDateBox;
	private AonDateBox endDateBox;
	
	private HTMLPanel periodicityPanel;
	private ListBox monthListBox;
	private TextBox yearTextBox;
	private Label periodLabel;
	private ListBox periodListBox;
	
	private HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Constructor

	protected CustomerFeeDuplicateDialog() {
		setCaption("Duplicar Cuota");
		initView();
		showDialog();
	}

	// ------------------------------------------------- Auxiliar Methods

	private void initView() {
		mainPanel = new HTMLPanel("");
		mainPanel.addStyleName(AON.CSS.aonFlexColumn());
		mainPanel.getElement().getStyle().setProperty("margin-top", ".5rem");
		
		messagePanel = new HTMLPanel("");
		mainPanel.add(messagePanel);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("margin", "0 1rem 1rem");
		
		createQuantityPricePanel();
		createDatesPanel();
		createPeriodicityPanel();
		
		getButtonsPanel();
		container.add(buttonsPanel);
		
		mainPanel.add(container);
		
		this.setWidget(mainPanel);
	}

	private void createQuantityPricePanel() {
		quantityPricePanel = new HTMLPanel("");
		quantityPricePanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label discountLabel = new Label("Descuento");
		discountLabel.setWidth("5.5rem");
		discountLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		discountTextBox = new TextBox();
		discountTextBox.setHeight("2em");
		discountTextBox.setWidth("8.8em");
		discountTextBox.getElement().getStyle().setProperty("padding", "0 5px");
		
		quantityPricePanel.add(discountLabel);
		quantityPricePanel.add(discountTextBox);
		
		container.add(quantityPricePanel);
	}

	private void createDatesPanel() {
		datesPanel = new HTMLPanel("");
		datesPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label startDateLabel = new Label("F. Inicio");
		startDateLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		startDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		startDateBox = new AonDateBox();
		startDateBox.setHeight("2em");
		startDateBox.setWidth("8.8em");
		startDateBox.getElement().getStyle().setProperty("padding", "0 5px");
		startDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		startDateBox.addStyleName("gwt-TextBox");
		
		Label endDateLabel = new Label("F. Fin");
		endDateLabel.setWidth("3.5rem");
		endDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		endDateBox = new AonDateBox();
		endDateBox.setHeight("2em");
		endDateBox.setWidth("8.8em");
		endDateBox.getElement().getStyle().setProperty("padding", "0 5px");
		endDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		endDateBox.addStyleName("gwt-TextBox");
		
		datesPanel.add(startDateLabel);
		datesPanel.add(startDateBox);
		datesPanel.add(endDateLabel);
		datesPanel.add(endDateBox);
		
		container.add(datesPanel);
	}

	private void createPeriodicityPanel() {
		periodicityPanel = new HTMLPanel("");
		periodicityPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label billingLabel = new Label("F. Facturaci\u00f3n");
		billingLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		billingLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		monthListBox = createMonthListBox();
		yearTextBox = createYearTextBox();
		
		periodicityPanel.add(billingLabel);
		periodicityPanel.add(monthListBox);
		periodicityPanel.add(yearTextBox);
		
		periodLabel = new Label("Periodo");
		periodLabel.setWidth("3.5rem");
		periodLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		periodListBox = new ListBox();
		periodListBox.setHeight("2em");
		periodListBox.setWidth("9em");
		periodListBox.addItem("Sin periodo", "0");
		periodListBox.addItem("Mensual", "1");
		periodListBox.addItem("Bimensual", "2");
		periodListBox.addItem("Trimestral", "3");
		periodListBox.addItem("Cuatrimestral", "4");
		periodListBox.addItem("Semestral", "5");
		periodListBox.addItem("Anual", "6");
		
		periodicityPanel.add(periodLabel);
		periodicityPanel.add(periodListBox);
		
		container.add(periodicityPanel);
	}
	
	private TextBox createYearTextBox() {
		TextBox tb = new TextBox();
		tb.setMaxLength(4);
		tb.setHeight("2em");
		tb.setWidth("4em");
		tb.setAlignment(TextAlignment.CENTER);
		tb.getElement().getStyle().setProperty("padding", "0 5px");
		tb.getElement().getStyle().setProperty("placeholder", "aaaa");
		
		return tb;
	}

	private Date createBillingDate() {
		if(AonStringUtils.isBlank(monthListBox.getSelectedValue()) || AonStringUtils.isBlank(yearTextBox.getValue())) return null;
		
		return new Date(Integer.parseInt(yearTextBox.getValue()) - 1900, Integer.parseInt(monthListBox.getSelectedValue()), 1);
	}

	private ListBox createMonthListBox() {
		ListBox lb = new ListBox();
		lb.setHeight("2em");
		lb.getElement().getStyle().setProperty("padding", "0 5px");

		lb.addItem("-", "");
		lb.addItem("Ene.", "0");
		lb.addItem("Feb.", "1");
		lb.addItem("Mar.", "2");
		lb.addItem("Abr.", "3");
		lb.addItem("May.", "4");
		lb.addItem("Jun.", "5");
		lb.addItem("Jul.", "6");
		lb.addItem("Ago.", "7");
		lb.addItem("Sep.", "8");
		lb.addItem("Oct.", "9");
		lb.addItem("Nov.", "10");
		lb.addItem("Dic.", "11");

		return lb;
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
		acceptBtnDialog.setText("Duplicar");
		acceptBtnDialog.addClickHandler(e -> {
			if(startDateBox.getValue() == null || createBillingDate() == null) {
				AonMessagePanel.showError(messagePanel, "Las fechas desde y facturaci\u00f3n son obligatorias");
				return;
			}
			
			Double discount = 0.00;
			try {
				discount = Double.parseDouble(discountTextBox.getValue());
			} catch (Exception exception) {}
			
			hide();
			onAccept(discount, startDateBox.getValue(), endDateBox.getValue(), createBillingDate(), BillingPeriod.values()[Integer.parseInt(periodListBox.getSelectedValue())]);
		});

		buttonsPanel.add(acceptBtnDialog);
	}

	// ------------------------------------------------- Abstract Methods

	protected abstract void onAccept(Double discount, Date startDate, Date endDate, Date billingDate, BillingPeriod period);

}
