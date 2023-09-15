package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AmortizationTypeDialog extends AonCustomDialog {

	// ------------------------------------------------- Variables (UI)
	
	private HTMLPanel container;
	private HTMLPanel mainPanel;
	
	private HTMLPanel messagePanel;
	
	private HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	private List<Account> fixedAssetAccountList = new LinkedList<>();
	private List<Account> accumulatedAccountList = new LinkedList<>();
	private List<Account> allocationAccountList = new LinkedList<>();

	private com.esferalia.aon.occam.api.model.accounting.AmortizationType amortizationType;

	// ------------------------------------------------- Constructor

	protected AmortizationTypeDialog(List<Account> fixedAssetAccountList, List<Account> accumulatedAccountList, List<Account> allocationAccountList) {
		setCaption("Editor Tipo Amortizaci\u00f3n");
		
		this.fixedAssetAccountList =fixedAssetAccountList;
		this.accumulatedAccountList = accumulatedAccountList;
		this.allocationAccountList = allocationAccountList;
		
		this.amortizationType = new AmortizationType();
		
		initView();
		showDialog();
	}
	
	protected AmortizationTypeDialog(List<Account> fixedAssetAccountList, List<Account> accumulatedAccountList, List<Account> allocationAccountList, com.esferalia.aon.occam.api.model.accounting.AmortizationType amortizationType) {
		setCaption("Editor Tipo Amortizaci\u00f3n");
		
		this.fixedAssetAccountList =fixedAssetAccountList;
		this.accumulatedAccountList = accumulatedAccountList;
		this.allocationAccountList = allocationAccountList;
		
		this.amortizationType = amortizationType;
		
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
		
		// Description
		HTMLPanel descriptionPanel = new HTMLPanel("");
		descriptionPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label descriptionLabel = new Label("Descripci\u00f3n");
		descriptionLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		descriptionLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		
		TextBox descriptionTextBox = new TextBox();
		setWidgetStyle(descriptionTextBox);
		descriptionTextBox.setWidth("100%");
		descriptionTextBox.addValueChangeHandler(e -> amortizationType.setDescription(e.getValue()));
		
		descriptionPanel.add(descriptionLabel);
		descriptionPanel.add(descriptionTextBox);
		
		// FixedAsset
		HTMLPanel fixedAssetItemPanel = new HTMLPanel("");
		fixedAssetItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label fixedAssetItemPanelLabel = new Label("C. Inmovilizado");
		fixedAssetItemPanelLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		fixedAssetItemPanelLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		
		ListBox fixedAssetAccountListBox = new ListBox();
		setWidgetStyle(fixedAssetAccountListBox);
		fixedAssetAccountListBox.setWidth("100%");
		fixedAssetAccountListBox.addItem("-", "");
		fixedAssetAccountList.forEach(account -> fixedAssetAccountListBox.addItem(account.getCode() + " - " + account.getDescription(), account.getCode()));
		fixedAssetAccountListBox.addChangeHandler(e -> amortizationType.setFixedAssetAccount(fixedAssetAccountListBox.getSelectedValue()));
		
		fixedAssetItemPanel.add(fixedAssetItemPanelLabel);
		fixedAssetItemPanel.add(fixedAssetAccountListBox);
		
		// Accumulated
		HTMLPanel accumulatedItemPanel = new HTMLPanel("");
		accumulatedItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label accumulatedItemPanelLabel = new Label("C. Acumulada");
		accumulatedItemPanelLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		accumulatedItemPanelLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		
		ListBox accumulatedAccountListBox = new ListBox();
		setWidgetStyle(accumulatedAccountListBox);
		accumulatedAccountListBox.setWidth("100%");
		accumulatedAccountListBox.addItem("-", "");
		accumulatedAccountList.forEach(account -> accumulatedAccountListBox.addItem(account.getCode() + " - " + account.getDescription(), account.getCode()));
		accumulatedAccountListBox.addChangeHandler(e -> amortizationType.setAccumulatedAccount(accumulatedAccountListBox.getSelectedValue()));
		
		accumulatedItemPanel.add(accumulatedItemPanelLabel);
		accumulatedItemPanel.add(accumulatedAccountListBox);
		
		// Allocation
		HTMLPanel allocationItemPanel = new HTMLPanel("");
		allocationItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label allocationLabel = new Label("C. Dotaci\u00f3n");
		allocationLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		allocationLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		
		ListBox allocationAccountListBox = new ListBox();
		setWidgetStyle(allocationAccountListBox);
		allocationAccountListBox.setWidth("100%");
		allocationAccountListBox.addItem("-", "");
		allocationAccountList.forEach(account -> allocationAccountListBox.addItem(account.getCode() + " - " + account.getDescription(), account.getCode()));
		allocationAccountListBox.addChangeHandler(e -> amortizationType.setAllocationAccount(allocationAccountListBox.getSelectedValue()));
		
		allocationItemPanel.add(allocationLabel);
		allocationItemPanel.add(allocationAccountListBox);
		
		// Percentage / Years
		HTMLPanel percentageYearItemPanel = new HTMLPanel("");
		percentageYearItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label percentageLabel = new Label("Porcentaje");
		percentageLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		percentageLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		
		Label percentageSymLabel = new Label("%");
		percentageSymLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		TextBox percentageTextBox = new TextBox();
		setWidgetStyle(percentageTextBox);
		
		Label yearsLabel = new Label("A\u00f1os");
		yearsLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		TextBox yearseTextBox = new TextBox();
		setWidgetStyle(yearseTextBox);
		
		percentageTextBox.addValueChangeHandler(e -> createYearByPercentage(percentageTextBox, yearseTextBox));
		yearseTextBox.addValueChangeHandler(e -> createPercentageByYear(percentageTextBox, yearseTextBox));
		
		percentageYearItemPanel.add(percentageLabel);
		percentageYearItemPanel.add(percentageTextBox);
		percentageYearItemPanel.add(percentageSymLabel);
		percentageYearItemPanel.add(yearsLabel);
		percentageYearItemPanel.add(yearseTextBox);
		
		// Fill info if needed
		if(amortizationType.getId() != null) {
			descriptionTextBox.setValue(amortizationType.getDescription());
			setSelectedValueLB(fixedAssetAccountListBox, amortizationType.getFixedAssetAccount());
			setSelectedValueLB(accumulatedAccountListBox, amortizationType.getAccumulatedAccount());
			setSelectedValueLB(allocationAccountListBox, amortizationType.getAllocationAccount());
			percentageTextBox.setValue(null == amortizationType.getPercentage() ? "" : NumberFormat.getFormat("0.00").format(amortizationType.getPercentage()));
			Double years = null == amortizationType.getPercentage() ? 0.00 : 100 / amortizationType.getPercentage();
			yearseTextBox.setValue(0.00 == years ? "" : years.intValue() + "");
		}
		
		// Add widgets
		container.add(descriptionPanel);
		container.add(fixedAssetItemPanel);
		container.add(accumulatedItemPanel);
		container.add(allocationItemPanel);
		container.add(percentageYearItemPanel);
		
		getButtonsPanel();
		container.add(buttonsPanel);
		
		mainPanel.add(container);
		
		this.setWidget(mainPanel);
	}
	
	private void createYearByPercentage(TextBox percentageTextBox, TextBox yearseTextBox) {
		double percentage = Double.parseDouble(percentageTextBox.getValue());
		amortizationType.setPercentage(percentage);
		Double years = 100 / percentage;
		yearseTextBox.setValue(years.intValue() + "");
	}

	private void createPercentageByYear(TextBox percentageTextBox, TextBox yearseTextBox) {
		Integer years = Integer.parseInt(yearseTextBox.getValue());
		double percentage = 100.00 / years;
		amortizationType.setPercentage(percentage);
		percentageTextBox.setValue(NumberFormat.getFormat("0.00").format(percentage));
	}
	
	private void setWidgetStyle(Widget widget) {
		widget.setHeight("2em");
		widget.getElement().getStyle().setProperty("padding", "0 5px");
	}

	private void setSelectedValueLB(ListBox lBox, String str) {
		String text = str;
		int indexToFind = 0;
		for (int i = 0; i < lBox.getItemCount(); i++) {
			if (lBox.getValue(i).equals(text)) {
				indexToFind = i;
				break;
			}
		}
		lBox.setSelectedIndex(indexToFind);
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
		
		Button acceptBtnDialog  = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonDialogButton());
		acceptBtnDialog.addStyleName(AON.CSS.aonIconSave());
		acceptBtnDialog.setText(AON.MSG.saveAction());
		acceptBtnDialog.addClickHandler(e -> {
			hide();
			onAccept(amortizationType);
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}

	// ------------------------------------------------- Abstract Methods

	protected abstract void onAccept(AmortizationType amortizationType);

}
