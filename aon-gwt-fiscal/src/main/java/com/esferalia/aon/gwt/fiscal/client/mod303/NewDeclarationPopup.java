package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Label;

public class NewDeclarationPopup<T extends FiscalModel> extends CustomDialog {
	
	protected int row = 0;

	public NewDeclarationPopup(final Mod303 mod303 ,final Model303Callback callback) {
		setCaption(AON.MSG.newDeclaration());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		FlexTable tab = new FlexTable();
		AdministrationListBox admonList = new AdministrationListBox();
		IntegerBox yearBox = new IntegerBox();	
		CheckBox replacement = new CheckBox();
		CheckBox complementary = new CheckBox();

		FlowPanel rootPanel = new FlowPanel(); 
		tab.setCellPadding(0);
		tab.setCellSpacing(0);
		tab.setStyleName(AON.AON_CSS.aonMarginTop());
		tab.addStyleName(AON.AON_CSS.aonMarginBottom());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		ColumnFormatter cf = tab.getColumnFormatter();
		cf.setWidth(0, "130px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		cf.setWidth(1, "250px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );

		// ADMINISTRATION
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.administration()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		admonList.setSelectedIndex( mod303.getAdministration().ordinal());
		admonList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				mod303.setAdministration( admonList.getValue() );
				replacement.setVisible(mod303.isReplacementDeclarationAvailable());
				complementary.setVisible(mod303.isComplementaryDeclarationAvailable());
			}
		});
		tab.setWidget(row, 1, admonList);
		row++;

		// YEAR
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.year()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		yearBox.setValue(mod303.getYear());
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				mod303.setYear(yearBox.getValue());
			}
		});
		tab.setWidget(row, 1,yearBox);
		row++;
		
		// PERIOD
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.period()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		boolean months = mod303.getModel().isMonthly(mod303.getAdministration());
		final PeriodListBox periodList = new PeriodListBox(months);
		if (mod303.getPeriod() != null) {
			for (int i = 0; i < periodList.getItemCount(); i++) {
				Integer value = AonNumberUtils.toInteger(periodList.getValue(i));
				if (value != null && mod303.getPeriod().ordinal() == value) {
					periodList.setSelectedIndex(i);
					break;
				}
			}
		}
		periodList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				mod303.setPeriod( periodList.getValue() );
			}
		});
		tab.setWidget(row, 1, periodList);
		row++;
		
		// COMPLEMENTARIA
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(""));
		complementary.setText(AON.MSG.complementary());
		complementary.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod303.setComplementary(complementary.getValue());
				replacement.setEnabled(!complementary.getValue());
				if (complementary.getValue()) {
					replacement.setValue(false);
				}
			}
		});
		complementary.setVisible(mod303.isComplementaryDeclarationAvailable());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, complementary);
		row++;
		
		// SUSTITUTIVA
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(""));
		replacement.setText(AON.MSG.replacement());
		replacement.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod303.setReplacement(replacement.getValue());
				complementary.setEnabled(!replacement.getValue());
				if (replacement.getValue()) {
					complementary.setValue(false);
				}
			}
		});
		replacement.setVisible(mod303.isReplacementDeclarationAvailable());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, replacement);
		row++;
		
		rootPanel.add(tab);
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.AON_CSS.aonPadding());
		buttonsPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
				callback.onAccept();
			}
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
    	cancelButton.addStyleName(AON.AON_CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
				callback.onCancel();
			}
			
		});
		buttonsPanel.add(cancelButton);
		rootPanel.add(buttonsPanel);
		add(rootPanel);
	}
}
