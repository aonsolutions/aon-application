package com.esferalia.aon.gwt.fiscal.client.accounting;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class CostCenterDialog extends AonCustomDialog {

	// ------------------------------------------------- Variables (UI)
	
	private HTMLPanel container;
	private HTMLPanel mainPanel;
	
	private TextBox descriptionTextBox;
	
	private HTMLPanel messagePanel;
	
	private HTMLPanel buttonsPanel;
	private Button acceptBtnDialog;

	// ------------------------------------------------- Constructor

	protected CostCenterDialog() {
		setCaption("Centro de costes");
		
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
		
		descriptionTextBox = new TextBox();
		setWidgetStyle(descriptionTextBox);
		descriptionTextBox.setWidth("100%");
		descriptionTextBox.addValueChangeHandler(e -> acceptBtnDialog.setEnabled(AonStringUtils.isNotBlank(e.getValue())));
		
		descriptionPanel.add(descriptionLabel);
		descriptionPanel.add(descriptionTextBox);
		
		// Add widgets
		container.add(descriptionPanel);
		
		getButtonsPanel();
		container.add(buttonsPanel);
		
		mainPanel.add(container);
		
		this.setWidget(mainPanel);
	}
	
	private void setWidgetStyle(Widget widget) {
		widget.setHeight("2em");
		widget.getElement().getStyle().setProperty("padding", "0 5px");
	}

	private void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
			descriptionTextBox.setFocus(true);
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
		
		acceptBtnDialog  = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonDialogButton());
		acceptBtnDialog.addStyleName(AON.CSS.aonIconSave());
		acceptBtnDialog.setText(AON.MSG.saveAction());
		acceptBtnDialog.setEnabled(false);
		acceptBtnDialog.addClickHandler(e -> {
			hide();
			onAccept(descriptionTextBox.getValue());
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}

	// ------------------------------------------------- Abstract Methods

	protected abstract void onAccept(String description);

}
