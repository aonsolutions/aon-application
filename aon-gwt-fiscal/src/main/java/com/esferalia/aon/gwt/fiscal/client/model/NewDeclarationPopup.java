package com.esferalia.aon.gwt.fiscal.client.model;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class NewDeclarationPopup<T extends FiscalModel> extends CustomDialog {
	
	public static interface INewDeclarationCallback {
	  void onAccept();
	  void onCancel();
	}
	
	public NewDeclarationPopup(final T fm, final INewDeclarationCallback callback) {
		setCaption(AON.MSG.newDeclaration());
		setGlassEnabled(true);
		setAnimationEnabled(true);

		FlexTable tab = new FlexTable();
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
		
		FlexCellFormatter fmt = tab.getFlexCellFormatter();
		final InlineLabel previousLabel = new InlineLabel(AON.MSG.previousDeclaration());
		previousLabel.setStyleName(AON.AON_CSS.aonMarginRight());
		previousLabel.setVisible(fm.isReplacedNumberAvailable());
		final TextBox previous = new TextBox();
		previous.setStyleName(AON.AON_CSS.aonInputText());
		previous.setVisible(fm.isReplacedNumberAvailable());
		
		final CheckBox replacement = new CheckBox(AON.MSG.replacement());
		final CheckBox complementary = new CheckBox(AON.MSG.complementary());
		replacement.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fm.setReplacement(replacement.getValue());
				complementary.setEnabled(!replacement.getValue());
				previousLabel.setVisible(fm.isReplacedNumberAvailable());
				previous.setVisible(fm.isReplacedNumberAvailable());
				if (replacement.getValue()) {
					complementary.setValue(false);
				}
			}
		});
		replacement.setVisible(fm.isReplacementDeclarationAvailable());
		
		complementary.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fm.setComplementary(complementary.getValue());
				replacement.setEnabled(!complementary.getValue());
				previousLabel.setVisible(fm.isReplacedNumberAvailable());
				previous.setVisible(fm.isReplacedNumberAvailable());
				if (complementary.getValue()) {
					replacement.setValue(false);
				}
			}
		});
		complementary.setVisible(fm.isComplementaryDeclarationAvailable());
		
		int row = 0;
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.administration()));
		fmt.addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		final AdministrationListBox admonList = new AdministrationListBox();
		admonList.setSelectedIndex( fm.getAdministration().ordinal());
		admonList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				fm.setAdministration( admonList.getValue() );
				replacement.setVisible(fm.isReplacementDeclarationAvailable());
				complementary.setVisible(fm.isComplementaryDeclarationAvailable());
				previousLabel.setVisible(fm.isReplacedNumberAvailable());
				previous.setVisible(fm.isReplacedNumberAvailable());
			}
		});
		tab.setWidget(row, 1, admonList);
		row++;
		
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.year()));
		fmt.addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		final IntegerBox yearBox = new IntegerBox();
		yearBox.setValue(fm.getYear());
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				fm.setYear(yearBox.getValue());
			}
		});
		tab.setWidget(row, 1,yearBox);
		row++;
		
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.period()));
		fmt.addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		final PeriodListBox periodList = new PeriodListBox();
		if (fm.getPeriod() != null) {
			periodList.setSelectedIndex(fm.getPeriod().ordinal() + 1);
		}
		
		periodList.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				fm.setPeriod( periodList.getValue() );
			}
		});
		tab.setWidget(row, 1, periodList);
		row++;
		
		FlowPanel endPanel = new FlowPanel();
		fmt.setColSpan(row, 0, 2);
		fmt.addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 0, endPanel);
		
		FlowPanel decPanel = new FlowPanel();
		
		FlowPanel replPanel = new FlowPanel();
		replPanel.setStyleName(AON.AON_CSS.aonTextCenter());
		replPanel.add(replacement);
		decPanel.add(replPanel);
		
		FlowPanel compPanel = new FlowPanel();
		compPanel.setStyleName(AON.AON_CSS.aonTextCenter());
		compPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		compPanel.add(complementary);
		decPanel.add(compPanel);
		
		FlowPanel prevPanel = new FlowPanel();
		prevPanel.setStyleName(AON.AON_CSS.aonTextCenter());
		prevPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		prevPanel.add(previousLabel);
		prevPanel.add(previous);
		decPanel.add(prevPanel);
		
		previous.setMaxLength(13);
		previous.setVisibleLength(13);
		previous.setValue(fm.getReplacedNumber());
		previous.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				fm.setReplacedNumber( previous.getValue() );
			}
		});

		endPanel.add(decPanel);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.setStyleName(AON.AON_CSS.aonPadding());
		flowPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		flowPanel.addStyleName(AON.AON_CSS.aonTextCenter());
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
		flowPanel.add(acceptButton);
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
		flowPanel.add(cancelButton);
		endPanel.add(flowPanel);
		
		add(tab);
	}

}
