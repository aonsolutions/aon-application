package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AmortizationPeriodBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAmortizationTypeBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonInvestAssetBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.amortization.AmortizationPanel.AmortizationPanelCallback;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.type.AmortizationPeriod;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class AmortizationFormPanel extends DockLayoutPanel {
	interface AmortizationFormPanelCallback {
		Amortization getAmortization();
		void onChange(Amortization am);
		void onError(String message);
	}
	
	AmortizationFormPanel(AmortizationModuleOptions opts, AmortizationPanelCallback callback ) {
		super(Unit.PX);
		setStyleName(AON.CSS.aonScrollArea());
		paint(opts, callback);
	}

	private void paint(AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		this.clear();
		callback.paintSaveButton();
		callback.paintDeleteButton();
		if (callback.getAmortization().detailStream().anyMatch( d -> d.isPending() )) {
			callback.paintCalculateButton();
		}
		if (callback.getAmortization().detailStream().anyMatch( d -> d.isPending() )) {
			callback.paintSaleButton();
		}
		if (callback.getAmortization().getId() != null) {
			callback.paintExcelButton();
		}
		AmortizationFormPanelCallback cbk = new AmortizationFormPanelCallback() {
			
			@Override
			public void onChange(Amortization amortization) {
				paint(opts, callback);
			}
			
			@Override
			public Amortization getAmortization() {
				return callback.getAmortization();
			}
			
			@Override
			public void onError(String message) {
				callback.showError(message);
			}
		};
		
		addNorth( getHeader(opts, callback), 220 );
		AmortizationDetailTable detailPanel = getDetails(opts, callback);
		if (callback.getAmortization().getFeePeriod() == AmortizationPeriod.YEARLY) {
			SimpleLayoutPanel detailWrapper = new SimpleLayoutPanel();
			detailWrapper.setWidget(detailPanel);
			add( detailWrapper );
		} else {
			AonTabLayoutPanel tabPanel = new AonTabLayoutPanel(26, Unit.PX);
			tabPanel.add( getSummary(opts, cbk), AON.MSG.yearSummary() );
			tabPanel.add( detailPanel, AON.MSG.amortizationDetails());
			add( tabPanel );
		}
	}

	private Widget getHeader(AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		FlowPanel header = new FlowPanel();

		AonDisplayTable grid = new AonDisplayTable();
		grid.addStyleName( AON.CSS.aonBlockCenter());

		AonIntegerBox idBox = new AonIntegerBox();
		CheckBox confidentialBox = new CheckBox(AON.MSG.confidential());
		AonTextBox descriptionBox = new AonTextBox();
		
		AonDateBox initialDateBox = new AonDateBox();
		AonAccountBox fixedAccountBox = new AonAccountBox(opts.getOccam());
		FlowPanel fixedAccountListBoxPanel = new FlowPanel();
		AonAccountListBox fixedAccountListBox = null;

		AmortizationPeriodBox periodBox = new AmortizationPeriodBox();
		AonAccountBox accumulatedAccountBox = new AonAccountBox(opts.getOccam());
		FlowPanel accumulatedAccountListBoxPanel = new FlowPanel();
		AonAccountListBox accumulatedAccountListBox = null;
		
		AonDoubleBox coefficientBox = new AonDoubleBox();
		AonAccountBox allocationAccountBox = new AonAccountBox(opts.getOccam());
		FlowPanel allocationAccountListBoxPanel = new FlowPanel();
		AonAccountListBox allocationAccountListBox = null;
		
		AonInvestAssetBox investAssetBox = new AonInvestAssetBox(opts,"");
		investAssetBox.removeStyleName(AON.CSS.aonCustomTextBox());
		AonTextBox commentsBox = new AonTextBox();
		commentsBox.setVisibleLength(80);
		
		// ID Panel
		Label idLabel = new Label(AON.MSG.code());
		
		idBox.setVisibleLength(4);
		idBox.setValue(callback.getAmortization().getId());
		idBox.setEnabled(false);
		
		
		confidentialBox.getElement().getStyle().setDisplay( Display.INLINE_BLOCK );
		confidentialBox.setValue(callback.getAmortization().isConfidential());
		
		FlowPanel idPanel = new FlowPanel();
		idPanel.setStyleName(AON.CSS.aonNowrap());
		idPanel.add(idLabel);
		idPanel.add(idBox);
		idPanel.add(confidentialBox);
		
		// Description
		Label descriptionLabel = new Label(AON.MSG.description());
		descriptionBox.setVisibleLength(80);
		descriptionBox.setMaxLength(64);
		descriptionBox.setValue(callback.getAmortization().getDescription());
		descriptionBox.addValueChangeHandler(e -> callback.getAmortization().setDescription(e.getValue()));
		
		grid.addRow()
			.addCell(idLabel, AON.CSS.aonWidth200())
			.addCell(idPanel, AON.CSS.aonWidth200())
			.addCell(descriptionLabel, AON.CSS.aonWidth150())
			.addCell(descriptionBox, AON.CSS.aonWidthAuto());
		
		// Initial Date
		Label initialDateLabel = new Label(AON.MSG.assetInitialDate());
		initialDateBox.setValue(callback.getAmortization().getInitialDate());
		initialDateBox.addValueChangeHandler(e -> callback.getAmortization().setInitialDate(e.getValue()));
		
		AonDisplayTableRow row = grid.addRow()
			.addCell(initialDateLabel, AON.CSS.aonNowrap())
			.addCell(initialDateBox);
		
		if (isNew(callback.getAmortization())) {
			// Amortization type
			Label amortizationTypeLabel = new Label(AON.MSG.amortizationType());
			
			AonAmortizationTypeBox amortizationTypeBox = new AonAmortizationTypeBox( opts , null);
			amortizationTypeBox.setAmortizationType( callback.getAmortization().getAmortizationType());
			amortizationTypeBox.addSelectionHandler(e -> {
				AmortizationType type = e.getSelectedItem();
				callback.getAmortization().setAmortizationType(type);
				callback.getAmortization().setPercentage(type.getPercentage());
				coefficientBox.setValue(type.getPercentage());
				
				initializeAccountListBox(opts, fixedAccountListBoxPanel, fixedAccountListBox, type.getFixedAssetAccount());
				initializeAccountListBox(opts, accumulatedAccountListBoxPanel, accumulatedAccountListBox, type.getAccumulatedAccount());
				initializeAccountListBox(opts, allocationAccountListBoxPanel, allocationAccountListBox, type.getAllocationAccount());
			});
			
			row.addCell(amortizationTypeLabel)
				.addCell(amortizationTypeBox);
		} else {
			row.addEmptyCell()
				.addEmptyCell();
		}
	
		// Amount
		Label amountLabel = new Label(AON.MSG.amount());
		AonDoubleBox amountBox = new AonDoubleBox();
		amountBox.addValueChangeHandler(e -> callback.getAmortization().setAmount(e.getValue()));
		amountBox.setValue(callback.getAmortization().getAmount());
		
		row = grid.addRow()
			.addCell(amountLabel)
			.addCell(amountBox);
		
		Label fixedAccountLabel = new Label(AON.MSG.fixedAssetAccount());
		if (isNew(callback.getAmortization())) {
			row.addCell(fixedAccountLabel)
				.addCell(fixedAccountListBoxPanel);
		} else {
			// FixedAccount
			fixedAccountBox.setAccount(callback.getAmortization().getFixedAssetAccount());
			row.addCell(fixedAccountLabel)
				.addCell(fixedAccountBox);
		}
		
		// Period
		Label periodLabel = new Label(AON.MSG.periodicity());
		periodBox.setValue(callback.getAmortization().getFeePeriod());
		periodBox.addChangeHandler(e -> callback.getAmortization().setFeePeriod(periodBox.getValue()));
		
		row = grid.addRow()
			.addCell(periodLabel)
			.addCell(periodBox);
		// AccumulatedAccount
		Label accumulatedAccountLabel = new Label(AON.MSG.accumulatedAccount());
		if (isNew(callback.getAmortization())) {
			row.addCell(accumulatedAccountLabel)
				.addCell(accumulatedAccountListBoxPanel);
		} else {
			accumulatedAccountBox.setAccount(callback.getAmortization().getAccumulatedAccount());
			row.addCell(accumulatedAccountLabel)
				.addCell(accumulatedAccountBox);
		}
		
		// Coeficiente
		Label coefficientLabel = new Label(AON.MSG.coefficient());
		coefficientBox.setValue(callback.getAmortization().getPercentage());
		coefficientBox.setEnabled(false);
		
		row = grid.addRow()
			.addCell(coefficientLabel)
			.addCell(coefficientBox);
		// Allocation Account
		Label allocationAccountLabel = new Label(AON.MSG.allocationAccount());
		if (isNew(callback.getAmortization())) {
			row.addCell(allocationAccountLabel)
			.addCell(allocationAccountListBoxPanel);
		} else {
			allocationAccountBox.setAccount(callback.getAmortization().getAllocationAccount());
			row.addCell(allocationAccountLabel)
			.addCell(allocationAccountBox);
		}
		
		// Deadline
		AonDateBox deadlineBox = new AonDateBox();
		deadlineBox.setValue(callback.getAmortization().getDeadline());
		deadlineBox.setEnabled(false);
		Label deadlineLabel = new Label(AON.MSG.saleDate());
		
		// Sale amount
		AonDoubleBox saleAmountBox = new AonDoubleBox();
		saleAmountBox.setValue(callback.getAmortization().getSaleAmount());
		saleAmountBox.setEnabled(false);
		Label saleAmountLabel = new Label(AON.MSG.saleAmount());

		grid.addRow()
			.addCell(deadlineLabel)
			.addCell(deadlineBox)
			.addCell(saleAmountLabel)
			.addCell(saleAmountBox);

		
		// Bien afecto
		Label investAsset = new Label(AON.MSG.investAsset());
		investAssetBox.addStyleName(AON.CSS.aonWidth170());
		investAssetBox.setInvestAsset(callback.getAmortization().getInvestAsset());
		investAssetBox.addSelectionHandler(e -> callback.getAmortization().setInvestAsset(e.getSelectedItem()));
		
		// Comments
		Label commentsLabel = new Label(AON.MSG.comments());
		commentsBox.setMaxLength(255);
		commentsBox.setValue(callback.getAmortization().getComments());
		commentsBox.addValueChangeHandler(e -> callback.getAmortization().setComments(e.getValue()));
		
		grid.addRow()
			.addCell(investAsset)
			.addCell(investAssetBox)
			.addCell(commentsLabel)
			.addCell(commentsBox)
		;
		
		header.add(grid);
		return header;
	}
	
	private void initializeAccountListBox(AmortizationModuleOptions opts, FlowPanel container, AonAccountListBox accountListBox, String prefix) {
		container.clear();
		accountListBox = new AonAccountListBox(opts, prefix, null);
		container.add(accountListBox);
	}

	private boolean isNew(Amortization am) {
		return am.getId() == null;
	}
	private AmortizationDetailTable getDetails(AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		return new AmortizationDetailTable( opts, callback ) ;
	}

	private AmortizationSummaryTable getSummary(AmortizationModuleOptions opts, AmortizationFormPanelCallback cbk) {
		return new AmortizationSummaryTable( opts, cbk ) ;
	}
	
}
