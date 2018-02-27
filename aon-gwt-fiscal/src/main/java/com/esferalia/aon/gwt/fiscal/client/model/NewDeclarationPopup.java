package com.esferalia.aon.gwt.fiscal.client.model;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
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
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class NewDeclarationPopup<T extends FiscalModel> extends CustomDialog {
	
	final protected FlexTable tab = new FlexTable();
	final protected AdministrationListBox admonList = new AdministrationListBox();
	final protected IntegerBox yearBox = new IntegerBox();	
	final protected InlineLabel previousLabel = new InlineLabel();
	final protected TextBox previous = new TextBox();
	final protected CheckBox replacement = new CheckBox();
	final protected CheckBox complementary = new CheckBox();
	final protected CheckBox diffCalculation = new CheckBox();
		
	protected int row = 0;
	protected IFiscalModelCallback<T> callback;

	public NewDeclarationPopup(final IFiscalModelCallback<T> callback) {
		this.callback = callback;
		setCaption(AON.MSG.newDeclaration());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		FlowPanel rootPanel = new FlowPanel(); 
		initializeTable(tab);
		paintAdministration();
		paintYear();
		paintPeriod();
		paintVariablePanel();
		paintModelSpecificPanel();
		rootPanel.add(tab);
		rootPanel.add(getButtonsPanels());
		add(rootPanel);
	}

	protected void paintModelSpecificPanel() {
	}

	private Widget getPreviousNumberPanel() {
		FlowPanel prevPanel = new FlowPanel();
		prevPanel.setStyleName(AON.AON_CSS.aonTextCenter());
		prevPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		previousLabel.setText(AON.MSG.previousDeclaration());
		previousLabel.setStyleName(AON.AON_CSS.aonMarginRight());
		previousLabel.setVisible(callback.getFiscalModel().isReplacedNumberAvailable());
		prevPanel.add(previousLabel);
		previous.setStyleName(AON.AON_CSS.aonInputText());
		previous.setVisible(callback.getFiscalModel().isReplacedNumberAvailable());
		previous.setMaxLength(13);
		previous.setVisibleLength(13);
		previous.setValue(callback.getFiscalModel().getReplacedNumber());
		previous.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				callback.getFiscalModel().setReplacedNumber( previous.getValue() );
			}
		});
		prevPanel.add(previous);
		return prevPanel;
	}

	protected void initializeTable(FlexTable table) {
		table.setCellPadding(0);
		table.setCellSpacing(0);
		table.setStyleName(AON.AON_CSS.aonMarginTop());
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		table.addStyleName(AON.AON_CSS.aonPanelGrid());
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(0, "130px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		cf.setWidth(1, "250px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
	}

	protected void paintAdministration() {
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.administration()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		admonList.setSelectedIndex( callback.getFiscalModel().getAdministration().ordinal());
		admonList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				callback.getFiscalModel().setAdministration( admonList.getValue() );
				replacement.setVisible(callback.getFiscalModel().isReplacementDeclarationAvailable());
				complementary.setVisible(callback.getFiscalModel().isComplementaryDeclarationAvailable());
				previousLabel.setVisible(callback.getFiscalModel().isReplacedNumberAvailable());
				previous.setVisible(callback.getFiscalModel().isReplacedNumberAvailable());
			}
		});
		tab.setWidget(row, 1, admonList);
		row++;
	}

	private void paintYear() {
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.year()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		yearBox.setValue(callback.getFiscalModel().getYear());
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				callback.getFiscalModel().setYear(yearBox.getValue());
			}
		});
		tab.setWidget(row, 1,yearBox);
		row++;
	}

	protected void paintPeriod() {
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.period()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		boolean months = callback.getFiscalModel().getModel().isMonthly(callback.getFiscalModel().getAdministration());
		final PeriodListBox periodList = new PeriodListBox(months);
		if (callback.getFiscalModel().getPeriod() != null) {
			for (int i = 0; i < periodList.getItemCount(); i++) {
				Integer value = AonNumberUtils.toInteger(periodList.getValue(i));
				if (value != null && callback.getFiscalModel().getPeriod().ordinal() == value) {
					periodList.setSelectedIndex(i);
					break;
				}
			}
		}
		periodList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				callback.getFiscalModel().setPeriod( periodList.getValue() );
			}
		});
		tab.setWidget(row, 1, periodList);
		row++;
	}

	private void paintVariablePanel() {
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		FlowPanel decPanel = new FlowPanel();
		decPanel.add(getDiffCalculationPanel());		
		decPanel.add(getComplementaryPanel());
		decPanel.add(getReplacementPanel());		
		tab.setWidget(row, 0, decPanel);
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, getPreviousNumberPanel());
		row++;
	}
	
	private FlowPanel getReplacementPanel() {
		FlowPanel replPanel = new FlowPanel();
		//replPanel.setStyleName(AON.AON_CSS.aonTextCenter());
		replacement.setText(AON.MSG.replacement());
		replacement.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				callback.getFiscalModel().setReplacement(replacement.getValue());
				complementary.setEnabled(!replacement.getValue());
				previousLabel.setVisible(callback.getFiscalModel().isReplacedNumberAvailable());
				previous.setVisible(callback.getFiscalModel().isReplacedNumberAvailable());
				if (replacement.getValue()) {
					complementary.setValue(false);
				}
			}
		});
		replacement.setVisible(callback.getFiscalModel().isReplacementDeclarationAvailable());
		replPanel.add(replacement);
		return replPanel;
	}

	private Widget getComplementaryPanel() {
		FlowPanel compPanel = new FlowPanel();
		//compPanel.setStyleName(AON.AON_CSS.aonTextCenter());
		complementary.setText(AON.MSG.complementary());
		complementary.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				callback.getFiscalModel().setComplementary(complementary.getValue());
				replacement.setEnabled(!complementary.getValue());
				previousLabel.setVisible(callback.getFiscalModel().isReplacedNumberAvailable());
				previous.setVisible(callback.getFiscalModel().isReplacedNumberAvailable());
				if (complementary.getValue()) {
					replacement.setValue(false);
				}
			}
		});
		complementary.setVisible(callback.getFiscalModel().isComplementaryDeclarationAvailable());
		compPanel.add(complementary);
		return compPanel;
	}
	
	private Widget getDiffCalculationPanel() {
		FlowPanel compPanel = new FlowPanel();
		//compPanel.setStyleName(AON.AON_CSS.aonTextCenter());
		diffCalculation.setText(AON.MSG.diffCalculation());
		diffCalculation.setValue(!callback.getFiscalModel().isDiffCalculationDisabled());
		diffCalculation.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				callback.getFiscalModel().setDiffCalculationDisabled(!diffCalculation.getValue());				
			}
		});
		diffCalculation.setVisible(callback.getFiscalModel().isDiffCalculationAvailable());
		compPanel.add(diffCalculation);
		return compPanel;
	}
	
	private Widget getButtonsPanels() {
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
		return buttonsPanel;
	}
	
}
